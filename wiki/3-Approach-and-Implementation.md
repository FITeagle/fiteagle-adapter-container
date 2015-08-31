In order to expose the functionality of a lightweight virtualization service such as Docker, within the FITeagle system, one has to address several architectual components and interoperability issues.

## Docker Client
To be able to control containers from the FITeagle Adapter, we need an application programming interface to talk to. The Docker service exposes an API which is available via HTTP. It is possible to access through a Unix or TCP socket. Since most HTTP client libraries for Java lack support for HTTP through a Unix socket, we chose to design our API client with TCP sockets in mind.

Our API client's goal is to provide simple abstraction over Docker's API design.

Each method consists of 2 core components: a request generator and a response parser.

The request generator takes care of generating an appropriate HTTP request which if the API operation requires it, contains a JSON object as request body. Both request URL and body are generated from the method's input parameters.

Counterpart to the request generator is the response parser. Its job is to retrieve JSON objects from the response body. Additionally, response objects are validated and wrapped inside a corresponding class instance in order to make accessing properties easier.

Both components are combined by our Docker client implementation. Each method follows a simple instruction pattern:

1. Serialize input parameters and HTTP request
2. Send request
3. Await response
4. Parse and validate response
5. Wrap response
6. Inform user about result or failure

For a more detailed look of each API client method, look at the attached JavaDocs.

## Resource Description
FITeagle does not understand containers. Therefore we must describe the concept of containers to it. For that we use the Resource Description Framework. This project provides a description of containers. 

Provided with an instance of a `Container` class, our FITeagle adapter can interact with the Docker API to make sure the described container will become reality. 

## FITeagle Adapter
Core to this project is the FITeagle adapter. It implements the interface provided by `AbstractAdapter`, which allows us to simplify the interactions with FITeagle.

There are three core directives sent to our adapter implementation.

Name | Corresponding method | Description
-----|----------------------|------------
Create | `createInstance(String uri, Model model)` | Create an instance described by the resource identified by `uri`.
Update | `updateInstance(String uri, Model model)` | Update an instance described by the resource identified by `uri`.
Delete | `deleteInstance(String uri)` | Delete an instance identified by `uri`

### `create`
The incoming resource description is interpreted by generating a container configuration. Said configuration gets directed to our Docker client which will delegate a `create` request to the Docker service. On success, a container has been created which matches the provided resource description.

### `update`
An `update` message results in almost the same instructions as a `create` message. They mainly differ in the deletion which preceeds the container creation process. Such message can only be received once the resource has been created. A resource creation usually means that a container has also been created. Before commiting the changes proposed to a resource, the old container associated with the resource has to be deleted, because Docker containers are immutable. Once the old container has been erased, a new one can be configured.

### `delete`
The resource and its associated container are deleted.