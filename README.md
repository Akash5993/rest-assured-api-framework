# API Automation Framework: REST Assured + Java + TestNG

An API test automation framework covering two public practice APIs: [Restful Booker](https://restful-booker.herokuapp.com) (booking CRUD with cookie-token auth) and [DummyJSON](https://dummyjson.com) (real JWT authentication with access and refresh tokens).

## Tech stack

| | |
|---|---|
| Language | Java 17 |
| HTTP client | REST Assured 5.5 (+ JSON Schema Validator) |
| Test framework | TestNG 7.10, tests run in parallel on 3 threads |
| JSON mapping | Jackson 2.17 (request/response bodies as Java objects) |
| Build and run | Maven, Surefire using `testng.xml` |

## Running the tests

```bash
mvn test
```

This runs 16 tests across three classes. Endpoints and the practice APIs' public demo logins are in `src/test/resources/config.properties`.

## What's tested

| Class | Covers |
|---|---|
| `AuthTest` | Valid and invalid login on Restful Booker (token returned or rejected) |
| `BookingTest` | Create, read, list, update and delete bookings; updating without a token is rejected; the response matches a JSON schema |
| `JwtAuthTest` | JWT login; decoding the token's claims; access with, without and with a tampered token; refreshing an expired access token |

## Design

```
src/main/java/com/api
├── client/ApiClient.java            Interface: the HTTP operations (get/post/put/delete) the services depend on
├── client/RestAssuredApiClient.java Implementation: base URL, logging and auth (cookie token or Bearer JWT) in ONE place
├── config/ConfigReader.java         Singleton: reads config.properties once
├── services/                        Service layer: AuthService, BookingService, JwtAuthService (one class per API area)
├── models/                          POJOs mapped by Jackson: Booking, BookingDates, AuthRequest, JwtLoginResponse...
└── utils/JwtUtils.java              Decodes JWT payloads to check claims such as expiry

src/test/java/com/api/tests          Tests call the services and assert on the results
src/test/resources/schemas/          JSON Schema used for contract validation
```

- **Layered design:** tests → services → API client, each with one responsibility.
- **Programming to an interface:** services depend on the `ApiClient` interface, with `RestAssuredApiClient` as its implementation.
- **POJO serialization:** request and response bodies are typed Java objects, not hand-built JSON strings.
- **Contract testing:** JSON Schema validation catches changed or missing fields.
- **Auth handled centrally:** both cookie-token and Bearer-JWT schemes are applied by the client, not by each test.
- **Parallel execution** via `testng.xml` (`parallel="methods"`, 3 threads).
