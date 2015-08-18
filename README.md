# sample-doc-retrieve-gateway

This is a sample of a rest style gateway service acting as a soap client to another existing backend service. Information about the backend service is retrieved via a service registry broker and the app parses the VCAP_SERVICES env variable to arrive at the endpoint of its related service.

There needs to be a backend document retrieval service implementing the WSDL interface.
This endpoint in turn would be made available via a service broker binding. The [Service Registry repo] (https://github.com/cf-platform-eng/service-registry-broker) as the service registry and allows services to be exposed to client apps that bind to the underlying services. The sample-doc-retrieve-gateway would act as a client of the service registry by binding to the specific backend service and getting location, credentials information of the backend service via the service registry.

# Steps to run the sample-doc-retrieve client:

* Deploy the backend or simulated service. The simulated service is available at [document-service] (https://github.com/cf-platform-eng/document-service)
* Deploy the service-registry-broker application to CF and follow the instructions to build, deploy, make the plans and services available
* Build the gateway client code using **`mvn clean install `**, followed by app push either via a manifest.yml file or cf command line. Make sure the app is bound to the exposed service from the service registry broker

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

# Notes
* Modify the value of SERVICE_NAME inside the EDMSRetrieveContentClient.java as per the service exposed
```
// Edit the service name as necessary.
private static final String SERVICE_NAME = "EDMSRetreiveInterface";
```

This is not the same as the name of the actual service instance bound to the application.
Example: `EDMSRetreiveInterface` is the service bucket or category under which EDMS Service is exposed.
The actual service instance is named `EDMSRetreiveInterface-basic` (a combination of service and plan to arrive at final service instance. 
Please check the VCAP_SERVICES env variable sample for reference.

* If there are multiple service instances all exposed/bound to the app in the same service bucket or category (like **`EDMSRetreiveInterface-basic`** and **`EDMSRetreiveInterface-premium`** both under the **`EDMSRetreiveInterface`** service category, only the first service defn would be returned.

* The package name of the generated sources from the wsdl specified inside the pom.xml should match the CONTEXT_PATH within the Marshaller

Inside maven pom.xml file:
```
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!-- tag::wsdl[] -->
            <plugin>
                <groupId>org.jvnet.jaxb2.maven2</groupId>
                <artifactId>maven-jaxb2-plugin</artifactId>
                <version>0.12.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
             .........
                <configuration>
                    <forceRegenerate>true</forceRegenerate>
                    <schemaLanguage>WSDL</schemaLanguage>
                    <generatePackage>org.cf.restgateway.edms.wsdl</generatePackage>
                    <schemaDirectory>src/main/resources</schemaDirectory>
                    <schemaIncludes>
                <include>*.wsdl</include>
              </schemaIncludes>
             .........
            </plugin>
```

Inside Marshaller class:
```
@Configuration
public class EDMSJaxb2Marshaller {
  
    private static final String CONTEXT_PATH = "org.cf.restgateway.edms.wsdl";
```

* Verify the URI gets switched to the correct endpoint based on the service binding during the invocation

* Sanity Testing
Requests can be passed to the /EDMSContent via two ways:

** Via Get with path parameters
Sample GET Request: 
```
curl http://sample-registry-client.xyz.com/EDMSContent/aa/aasd1112/testuser/134i9321103
```

Sample response:
```
{"csn_id":"aa","corr_id":"134i9321103","content_id":"testuser","file_length_number":0,
 "fileName":null,"fileEncoded":"The quick brown fox jumps over the lazy dog.",
 "application_meta_data":null,"revision_timestamp":"null","creation_timestamp":"null",
 "store_timestamp":"null","author_name":null,"description":"Hello world!",
 "sensitivity_code":null,"csnId":"aa","corrId":"134i9321103",
 "contentId":"testuser","descrp":"Hello world!","author":null}
```

** As POST JSON Payload 
Sample Request: 
```
curl http://sample-registry-client.xyz.com/EDMSContent \
       -d '{ "csn_id":"aa", "corr_id": "aasd1112", "user_id": "testuser", "content_id": "134i9321103" }' \
       -H "Content-type: application/json" -X POST
```

Sample response:
```
{"csn_id":"aa","corr_id":"134i9321103","content_id":"testuser","file_length_number":0,
 "fileName":null,"fileEncoded":"The quick brown fox jumps over the lazy dog.",
 "application_meta_data":null,"revision_timestamp":"null","creation_timestamp":"null",
 "store_timestamp":"null","author_name":null,"description":"Hello world!",
 "sensitivity_code":null,"csnId":"aa","corrId":"134i9321103",
 "contentId":"testuser","descrp":"Hello world!","author":null}
```
