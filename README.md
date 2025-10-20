# Chakray Technical Test

Spring Boot (Java 17) in-memory REST API sample.

This project provides a simple REST API to manage users, with filtering, sorting, and authentication. It uses AES-256 encryption for passwords and stores dates in Madagascar timezone.

## Requirements
- Java 17+
- Maven 3+
- Docker (optional, for containerization)

## Run Locally

1. Build the project:

mvn clean package

2. Run the application:

mvn spring-boot:run

or using the jar:

java -jar target/technical-test-0.0.1-SNAPSHOT.jar

3. Open Swagger UI:

http://localhost:8080/swagger-ui/index.html

## API Endpoints

### Users

| Method | Endpoint | Description | Body / Params |
|--------|---------|-------------|---------------|
| GET | `/users` | List all users | Optional query parameters: `sortedBy` (`email|id|name|phone|tax_id|created_at`), `filter` (`attr+op+value`, op in `co` (contains), `eq` (equals), `sw` (starts with), `ew` (ends with`)) |
| POST | `/users` | Create a new user | JSON body: `{"name":"...","email":"...","phone":"...","taxId":"...","password":"..."}` |
| PATCH | `/users/{id}` | Update an existing user | JSON body: partial user fields |
| DELETE | `/users/{id}` | Delete a user by ID | N/A |

### Authentication

| Method | Endpoint | Description | Body |
|--------|---------|-------------|------|
| POST | `/login` | Authenticate a user | JSON body: `{"taxId":"...","password":"..."}` |

## Data Details

- **Passwords**: Stored encrypted with AES-256 (demo key in `application.properties`).
- **created_at**: Uses Madagascar timezone (`Indian/Antananarivo`) formatted as `dd-MM-yyyy HH:mm`.

## Running Tests

- Unit tests: `UserServiceTest` (service layer)
- Controller tests: `UserControllerTest` (uses `MockMvc` with in-memory data)

Run tests using:

mvn test

## Docker

### Build Image

mvn clean package
docker build -t technical-test-app .

### Run Container

docker run -p 8080:8080 technical-test-app

- Access API at: `http://localhost:8080`
- Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

## Notes

- This is an in-memory demo API. All data will be lost on restart.
- Use `@ExtendWith(MockitoExtension.class)` in service tests and `@WebMvcTest` for controller tests.
- Example filters:

/users?filter=name co alice   # name contains 'alice'

/users?filter=email eq user1@mail.com

- Sorting example:

/users?sortedBy=name

## Authors

- Chakray Technical Test Demo

## License

MIT License


