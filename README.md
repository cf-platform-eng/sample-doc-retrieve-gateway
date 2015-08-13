# sample-doc-retrieve-gateway

This is a sample of a rest style gateway service acting as a soap client to another existing backend service. Information about the backend service is retrieved via a service registry broker and the app parses the VCAP_SERVICES env variable to arrive at the endpoint of its related service.

There needs to be a backend document retrieval service implementing the WSDL interface.
This endpoint in turn would be made available via a service broker binding. The [Service Registry repo] (https://github.com/cf-platform-eng/service-registry-broker) as the service registry and allows services to be exposed to client apps that bind to the underlying services. The sample-doc-retrieve-gateway would act as a client of the service registry by binding to the specific backend service and getting location, credentials information of the backend service via the service registry.

# Steps to run the sample-doc-retrieve client:

* Deploy the backend or simulated service. The simulated service is available at [document-service] (https://github.com/cf-platform-eng/document-service)
* Deploy the service-registry-broker application to CF and follow the instructions to build, deploy, make the plans and services available
* Build the gateway client code using `mvn clean install `, followed by app push either via a manifest.yml file or cf command line. Make sure the app is bound to the exposed service from the service registry broker

# Sample steps:

```
# Manifest.yml refers to the app as sample-registry-client
cf push # Using manifest.yml

# The name of the service exposed via the service-registry service broker is EDMSRetrieveInterface-basic
# Change as appropriate
cf services
cf bind-service sample-registry-client EDMSRetreiveInterface-basic

# Either restage the app if bind was done via command line or no need if manifest.yml refers to the service already
cf restage sample-registry-client
# Check the VCAP_SERVICES env entry to see if the credentials section got filled in with the service uri
```

# Sample output of VCAP_SERVICES on binding to a service
cf env sample-registry-client
```
System-Provided:
{
 "VCAP_SERVICES": {
  "EDMSRetreiveInterface": [
   {
    "credentials": {
     "uri": "http://document-service.xyz.com/soap/RetrieveService"
    },
    "label": "EDMSRetreiveInterface",
    "name": "EDMSRetreiveInterface-basic",
    "plan": "basic",
    "tags": []
   }
  ]
 }
}
{
 "VCAP_APPLICATION": {
  "application_name": "sample-registry-client",
  "application_uris": [
   "sample-registry-client.xyz.com"
  ],
  "application_version": "2e36be42-8762-4cab-adc4-1d8ec7b402f3",
  "limits": {
   "disk": 1024,
   "fds": 16384,
   "mem": 512
  },
  "name": "sample-registry-client",
  "space_id": "6867846f-76e4-46a3-af60-aee185c81a31",
  "space_name": "development",
  "uris": [
   "sample-registry-client.xyz.com"
  ],
  "users": null,
  "version": "2e36be42-8762-4cab-adc4-1d8ec7b402f3"
 }
}
```
