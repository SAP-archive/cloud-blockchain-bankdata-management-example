package com.mycompany.bankdata.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.bankdetail.BankDetail;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBankDetailService;
import com.sap.cloud.sdk.services.blockchain.multichain.service.MultichainService;
import com.sap.cloud.sdk.services.blockchain.multichain.model.MultichainResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BankDataController {
    private static final String BANKCOUNTRY = "DE";
       
    private static final String SANDBOXAPIKEY = System.getenv("SANDBOX_APIKEY");
    private static final Header HEADER = new Header("APIKey", SANDBOXAPIKEY);
    private static final String MULTICHAIN_STREAM_NAME = "root";    
    private final DefaultHttpDestination destination = DefaultHttpDestination.builder("https://sandbox.api.sap.com/s4hanacloud").header(HEADER).build();   
		
    @RequestMapping( value = "/replicate", method = RequestMethod.GET )
    public ResponseEntity<String> replicateBankData( ) throws ODataException
    {
    	
    	// create MultiChain service instance
    	MultichainService mcService = MultichainService.create();    

    	// Check if environment variable for APIKey of the sandbox system is set
    	if (SANDBOXAPIKEY == null)
    		return ResponseEntity.ok("please maintain the environment variable SANDBOX_APIKEY for your application");    	      
    	
    	// Read all bank data from S/4HANA
    	List<BankDetail> bankDataAll = new DefaultBankDetailService().getAllBankDetail().execute(destination);

        if (bankDataAll.size() > 0) {
        	int cntBanks = 0, cntExists = 0, cntWrite2Blockchain = 0;
        			
        	for (BankDetail bankDetail : bankDataAll) {
        		// get only bank account for country BANKCOUNTRY_DE
        		if (! bankDetail.getBankCountry().equals(BANKCOUNTRY))
        			continue;
        				
        		cntBanks++;
        				
        		// Check if entry already exists by querying the blockchain
            		String streamEntryKey = BANKCOUNTRY + "_" + bankDetail.getBankInternalID();         			
            	      List<MultichainResult> queryResult = mcService.listStreamKeyItems(MULTICHAIN_STREAM_NAME, streamEntryKey, false, 1, -1, false);
            	      if (queryResult.size() > 0) {
            	      	// Entry already exists, don't overwrite
            	        	cntExists ++;
            	        	continue;    	        	
            	      }    				
            	        
            	      // Write to blockchain
                    List<String> streamKeys = Arrays.asList(BANKCOUNTRY + "_" + bankDetail.getBankInternalID() );
                    Map<String,Object> value = new HashMap<String,Object>() {{
                            put("NAME", bankDetail.getBankName());
                            put("NUMBER", bankDetail.getBankNumber());
                            put("SWIFT_CODE", bankDetail.getSWIFTCode());
                    }};        
                    mcService.publishJson(MULTICHAIN_STREAM_NAME, streamKeys, value, null);    
                    cntWrite2Blockchain++;
        	}

        return ResponseEntity.ok(cntBanks + " banks(s) read from S/4HANA for country code " + BANKCOUNTRY + ".\n" + cntExists + " entries already existed on Blockchain.\n" + cntWrite2Blockchain + " entries written to blockchain.");
        }

    	
    	/* end of replace the following if clause in step 4 */  				    
    	return ResponseEntity.notFound().build();		
    }
    
    @RequestMapping( value = "/read", method = RequestMethod.GET )
    public ResponseEntity<String> getBankData( @RequestParam( defaultValue = "" ) final String id ) throws ODataException
    {   
    	// Check if environment variable for APIKey of the sandbox system is set
    	if (SANDBOXAPIKEY == null)
    		return ResponseEntity.ok("please maintain the environment variable SANDBOX_APIKEY for your application");

    	// Check if bank id is provided
    	if (id.equals(""))
    		return ResponseEntity.ok("please provide the internal S/4HANA ID for the bank number in the Http-Get parameter id");

    	// create MultiChain service instance
    	MultichainService mcService = MultichainService.create();
    	
    	// Read bank ID
    	String streamEntryKey = BANKCOUNTRY + "_" + id;
        List<MultichainResult> queryResult = mcService.listStreamKeyItems(MULTICHAIN_STREAM_NAME, streamEntryKey, false, 1, -1, false);
        if (queryResult.size() > 0) {
        	// Entry found. Since query parameter count is set to 1, only the latest enrty is provided
        	MultichainResult resultEntry = queryResult.get(0);
        	HashMap<String, String> payload = (HashMap<String, String>) resultEntry.getJsonData().get("json");
        	
        	// Read business information
        	String bankName = payload.get("NAME");
        	String bankNumber = payload.get("NUMBER");
        	String swiftCode = payload.get("SWIFT_CODE");

        	// Read blockchain information
        	String publisher = queryResult.get(0).getPublishers().get(0);
	        String blocktime = queryResult.get(0).getBlocktime().toString();
	        
	        return ResponseEntity.ok("Bank data: " + bankNumber + "(" + swiftCode + ") at " + bankName + " written to blockchain by Multichain address" + publisher + " (" + blocktime + ")" );
        }    				    	
    	
    	return ResponseEntity.notFound().build();
    }
    
}    
