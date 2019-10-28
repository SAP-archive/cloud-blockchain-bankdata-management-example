# SAP Cloud SDK Blockchain Tutorial: Bankdata Management

Replicating master data for banks from S/4Hana to MultiChain and querying it from blockchain

## Description

In this tutorial example you will use SAP Cloud SDK and MultiChain blockchain technology to develop and deploy a basic bankdata cloud application in Java. This showcases how the SAP Cloud SDK can be used to develop blockchain applications in SAP Cloud Platform with an integration to S/4HANA.

## Requirements 

To deploy this application you should have the following:
- A paid commerical SAP Cloud Platform Account, and a [MultiChain subscription](https://github.com/SAP/cloud-blockchain-odometer-example/archive/master.zip). Alterntively you may use the trial account. 
- A familiarity with [creating MultiChain service instances](https://help.sap.com/viewer/15cb4580694c4d119793f0d3e9b8a32b/BLOCKCHAIN/en-US/0183c6479c47427ab6257bd37ab8bee3.html)
- An understanding of MultiChain streams and how they can be viewed on the MultiChain dashboard. 
- Downloaded and install the [Cloud Foundry command-line tool](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)

## Download and Installation

Download the Maven sample project from https://github.com/SAP/cloud-blockchain-bankdata-management-example

1. Compile and make this Maven project using maven command mvn package

2. Use Cloud Foundry command line tools to push the application to SAP Cloud Platform

3. Add the environment variable SANDBOX_APIKEY with the API Key from https://api.sap.com

4. Create a Multichain service instance in the same cloud platform account where your application is deployed to

5. Bind the MultiChain service to your cloud application

6. Restart your application 

## Support

There is no support provided for this application.

## License

Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the SAP Sample Code License except as noted otherwise in the [LICENSE](LICENSE) file.
