# Getting Started

### Creating Tapir API

Create Tapir endpoints definitions (sttp.tapir.Endpoint) using previously defined DTOs classes.
Define query parameters, request path, output data format and output status codes.
For example com.fourthwall.googlemembersapi.api

Remember about OpenAPI limitation:
OpenAPI defines a unique operation as a combination of a path and an HTTP method. 
This means that two GET or two POST methods for the same path are not allowed – even if they have different parameters 
(parameters have no effect on uniqueness).

To omit this limitation you can add additional slashes in endpoint definitions.

### Generating Retrofit API based on OpenAPI yaml using openapi-generator

https://openapi-generator.tech/#try

Install via homebrew:
brew install openapi-generator

Then, generate a client from a valid yaml doc:
openapi-generator generate -i swagger-generated.yaml -g kotlin -o /tmp/test/ --library=jvm-retrofit2

more about kotlin generator options:
https://openapi-generator.tech/docs/generators/kotlin

### Generating Retrofit API based on OpenAPI yaml using swagger-codegen

https://swagger.io/tools/swagger-codegen/

Install via homebrew:
brew install swagger-codegen

Then, generate a client from a valid yaml doc (this generator does not support retrofit for kotlin):
swagger-codegen generate -i swagger-generated.yaml -l java -o /tmp/test/ --library=retrofit2

more about generator options:
https://github.com/swagger-api/swagger-codegen
