# ticket-manager

Ticket Manager is a distributed microservice system for managing events and ticketing. Each microservice runs as an independent application built on Java 17 and Spring Boot, which allows for clear separation of responsibilities, independent implementation, and easy scaling depending on needs.

The system architecture is based on several key pillars:

🌐 **Communication** and integration The system uses a hybrid architecture, combining synchronous REST communication with asynchronous message exchange.

Spring Web is used to share and consume REST APIs between microservices. OpenFeign is used to create an HTTP client between services, which allows declaratively defining calls to other services.

In cases where loose dependency and tolerance to errors and delays are required, RabbitMQ is used as a message broker. Events such as ticket reservations are sent to other services (e.g. Notification Service) via message queues.

🚪 **API Gateway** and security All external requests first go to the API Gateway, built on the Spring Cloud Gateway. It is responsible for:

routing requests to the appropriate microservices,

validating and verifying JWT tokens,

simplifying communication with the frontend (one entry point to many services).

Keycloak is responsible for authentication and authorization - an external identity server that supports user login, account management and role assignment (USER, ADMIN). Authentication is based on JWT tokens, which are checked by the gateway and microservices using Spring Security.

The security model is based on RBAC - Role-Based Access Control - which allows for precise control of access to resources depending on the user's permissions.

🗄️ **Data layer** and versioning Each microservice has its own, isolated database, in accordance with the Database per service principle. The system uses PostgreSQL as a relational database engine, and access to data is provided using Spring Data JPA (ORM).

Changes to the database structure are managed using Flyway, which enables:

automatic versioning of the database schema,

performing migrations at service startup,

consistent control of the history of changes in the database.

📦 **Containerization** and launching All system components - microservices, RabbitMQ, Keycloak, databases - are launched and managed in Docker containers, which provides a repeatable and easy-to-configure runtime environment. This facilitates local launch, testing and implementation of the system in various environments.

🔍 **Observability** and monitoring To monitor system operation and track request flow, the following were used:

Spring Cloud Sleuth – automatic labeling of requests with unique identifiers (traceId, spanId),

Zipkin – distributed tracing enabling analysis of processing time and localization of problems in the service chain.

Microservice logs are also prepared for integration with tools such as ELK Stack, which allows for their central analysis and searching.

✅ **Testing** The project has been covered with unit and integration tests using:

JUnit 5 and Spring Test – for testing components within the application,

Testcontainers – for running temporary containers with PostgreSQL or RabbitMQ in integration tests, which allows testing microservices in an isolated and realistic environment.

📚 **API documentation** Each microservice automatically generates documentation for its REST API using Swagger / OpenAPI (Springdoc). The interface is available at the endpoint /swagger-ui.html and allows for easy testing and viewing of the API structure.

👤 Sample order process
![image](https://github.com/user-attachments/assets/b5372393-d368-4d25-86d0-b38ad562fb08)

![image](https://github.com/user-attachments/assets/291a0177-e75a-46e3-9326-4b36e691990a)
