# Quarkus REST API Demo

A simple REST API demo using Quarkus 3.8.3 with GET and POST endpoints for message management.

## 🎯 Demo Purpose

This application demonstrates:
- How to create a simple REST API with Quarkus
- Quarkus project configuration with Maven
- Automated testing with RestAssured and JUnit
- Packaging and running a Quarkus application
- Automatic documentation with OpenAPI/Swagger

## 🚀 Features

The API exposes two endpoints:
- `GET /messages`: Retrieves the list of all messages
- `POST /messages`: Adds a new message

Messages are stored in memory (static list) for demo simplicity.

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.8.1** or higher
- **Git** (to clone the project)

### Prerequisites verification
```bash
java -version    # Should display Java 17+
mvn -version     # Should display Maven 3.8.1+
```

## 🛠️ Installation and Setup

### 1. Clone the project
```bash
git clone <your-repo-url>
cd quarkus-demo
```

### 2. Compile the project
```bash
mvn clean compile
```

### 3. Run tests
```bash
mvn test
```

### 4. Package the application
```bash
mvn clean package
```

## 🏃‍♂️ Running the Application

### Development mode (recommended for development)
```bash
mvn quarkus:dev
```
The application starts on http://localhost:8080 with hot-reload enabled.

### Production mode (from jar)
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## 🧪 API Testing

### With curl

**Retrieve all messages:**
```bash
curl -X GET http://localhost:8080/messages
```

**Add a message:**
```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello Quarkus!"}'
```

**Complete test:**
```bash
# 1. View empty list
curl -X GET http://localhost:8080/messages

# 2. Add a message
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"My first message"}'

# 3. View updated list
curl -X GET http://localhost:8080/messages
```

### With Swagger UI
Access http://localhost:8080/q/swagger-ui/ for an interactive graphical interface.

## 📁 Project Structure

```
quarkus-demo/
├── src/
│   ├── main/
│   │   ├── java/org/acme/demo/
│   │   │   ├── Message.java          # Message POJO
│   │   │   └── MessageResource.java  # REST Resource
│   │   └── resources/
│   │       └── application.properties # Quarkus Configuration
│   └── test/
│       └── java/org/acme/demo/
│           └── MessageResourceTest.java # Integration Tests
├── target/
│   └── quarkus-app/
│       └── quarkus-run.jar           # Executable Application
├── pom.xml                           # Maven Configuration
├── .gitignore                        # Git ignored files
├── README.md                         # This file
├── README_FR.md                      # French version
└── README_EN.md                      # English version
```

## ⚙️ pom.xml Structure

### Important Properties
- **Quarkus 3.8.3**: Stable and modern version with Jakarta EE support
- **Java 17**: LTS version required by Quarkus 3.x
- **UTF-8**: Source and report encoding

### Key Dependencies

#### Production
- `quarkus-resteasy-reactive-jackson`: REST + JSON support
- `quarkus-smallrye-openapi`: Automatic OpenAPI/Swagger documentation
- `quarkus-arc`: Dependency injection (CDI)

#### Tests
- `quarkus-junit5`: Quarkus testing framework
- `rest-assured`: Simplified REST API testing
- `hamcrest`: Matchers for test assertions

### Essential Plugins

#### quarkus-maven-plugin
```xml
<plugin>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>build</goal>              <!-- Generates fast-jar -->
        <goal>generate-code</goal>      <!-- Code generation -->
        <goal>generate-code-tests</goal> <!-- Test generation -->
      </goals>
    </execution>
  </executions>
</plugin>
```

#### Standard Maven Plugins
- **maven-compiler-plugin**: Java 17 compilation with preserved parameters
- **maven-surefire-plugin**: Unit tests with Quarkus configuration
- **maven-failsafe-plugin**: Integration tests

### Critical Points for Proper Functioning

1. **Correct Quarkus BOM**:
   ```xml
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-bom</artifactId>
   ```

2. **Quarkus Plugin Goals**: Required to generate `target/quarkus-app/`

3. **Test Configuration**: System variables required for Quarkus

4. **Package Type**: `fast-jar` configured in `application.properties`

## 🔧 Configuration

### application.properties
```properties
# Package type (generates target/quarkus-app/)
quarkus.package.type=fast-jar

# OIDC configuration (example, not used in this demo)
quarkus.oidc.auth-server-url=https://oidc.example.com/auth/realm/client
```

## 🧪 Automated Tests

The project includes integration tests that:
- Verify the message list is initially empty
- Test message addition via POST
- Validate message retrieval via GET
- Use RestAssured to simulate HTTP requests

Running tests:
```bash
mvn test
```

## 📊 Useful Endpoints

- **API**: http://localhost:8080/messages
- **Documentation**: http://localhost:8080/q/swagger-ui/
- **Health check**: http://localhost:8080/q/health
- **Metrics**: http://localhost:8080/q/metrics

## 🐛 Troubleshooting

### quarkus-app folder is not generated
- Check that all Maven plugins are present in pom.xml
- Run `mvn clean package` and check error logs

### Error "no main manifest attribute"
- Don't use `target/quarkus-demo-1.0-SNAPSHOT.jar`
- Use `target/quarkus-app/quarkus-run.jar`

### Test failures
- Check that the application is not already running on port 8080
- Run `mvn clean test` for a clean environment

### Dependency issues
- Run `mvn clean install -U` to force dependency updates
- Verify you're using Java 17+

## 📚 Going Further

- [Quarkus Documentation](https://quarkus.io/guides/)
- [REST with Quarkus Guide](https://quarkus.io/guides/rest-json)
- [Testing with Quarkus](https://quarkus.io/guides/getting-started-testing)
- [RestAssured Documentation](https://rest-assured.io/)

## 👥 Contributing

This project serves as an educational demo. Feel free to use it as a base for your own Quarkus projects!
