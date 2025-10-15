# Quarkus Demo - Complete User Guide 🇺🇸

> **🌐 Other languages**: [🇫🇷 Version Française](README_FR.md) | [🏠 Back to home](README.md)  
> **📚 Technical documentation**: [🏗️ Hexagonal Architecture](README_ARCHITECTURE_HEXAGONALE_EN.md)

---

## 📖 Table of Contents

1. [🎯 Overview](#-overview)
2. [🚀 Installation and Setup](#-installation-and-setup)
3. [🗄️ H2 Database - Server Mode](#-h2-database---server-mode)
4. [📡 REST API - Endpoints](#-rest-api---endpoints)
5. [🧪 Testing and Quality](#-testing-and-quality)
6. [🔧 Advanced Configuration](#-advanced-configuration)
7. [🐛 Troubleshooting](#-troubleshooting)

---

## 🎯 Overview

This project demonstrates the implementation of **hexagonal architecture** with Quarkus, transforming a simple REST API into a maintainable and testable application.

### Key Features
- ✅ **Complete REST API** for message management
- ✅ **Hexagonal architecture** (Ports & Adapters)
- ✅ **H2 database** in TCP server mode
- ✅ **Unit and integration tests**
- ✅ **OpenAPI documentation** (Swagger)
- ✅ **Monitoring** and health checks

---

## 🚀 Installation and Setup

### System Requirements
```bash
# Check Java
java -version  # Required: Java 17+

# Check Maven (optional, wrapper included)
mvn -version   # Recommended: Maven 3.9+
```

### Project Installation
```bash
# 1. Clone the repository
git clone <repository-url>
cd quarkus-demo

# 2. Install dependencies
./mvnw clean compile

# 3. Start the application
./mvnw quarkus:dev
```

### Startup Verification
Once the application is running, check these endpoints:
- 🌐 **Application**: http://localhost:8080
- 📊 **Swagger UI**: http://localhost:8080/q/swagger-ui
- ❤️ **Health Check**: http://localhost:8080/q/health
- 📈 **Metrics**: http://localhost:8080/q/metrics

---

## 🗄️ H2 Database - Server Mode

### Advanced H2 Configuration

Our application uses **H2 in TCP server mode** to allow simultaneous access from the application and external tools like DBeaver.

#### H2 Database Architecture
```
Quarkus Application ──────┐
                         │
                         ├─► H2 TCP Server (Port 9092)
                         │       │
DBeaver/External Tools ──┘       │
                                 ▼
                         H2 Database File
                         (./data/quarkus-demo.mv.db)
```

#### Automatic Configuration

The `H2TcpServerManager` class automatically starts an H2 TCP server:

**Features**:
- ✅ **Automatic startup** when Quarkus launches
- ✅ **Clean shutdown** when application stops  
- ✅ **Port conflict management**
- ✅ **File persistence** (survives restarts)
- ✅ **Concurrent access** Quarkus + external tools

### Connecting with DBeaver

#### DBeaver Installation
```bash
# With Scoop (Windows)
scoop install dbeaver

# Or manual download from https://dbeaver.io
```

#### DBeaver Connection Configuration

1. **Create a new connection**:
   - Type: **H2 Server**
   - Host: `localhost`
   - Port: `9092`
   - Database: `quarkus-demo`
   - Username: `sa`
   - Password: *(empty)*

2. **Complete JDBC URL**:
   ```
   jdbc:h2:tcp://localhost:9092/quarkus-demo
   ```

3. **Connection test**:
   - Make sure Quarkus is running
   - Click "Test Connection" in DBeaver
   - You should see: ✅ "Connected"

#### Useful SQL Queries

```sql
-- View all messages
SELECT * FROM message;

-- Messages by status
SELECT * FROM message WHERE status = 'PUBLISHED';

-- Recent messages
SELECT * FROM message 
ORDER BY created_at DESC 
LIMIT 10;

-- Delete a specific message
DELETE FROM message WHERE id = 'message-uuid';

-- Statistics by status
SELECT status, COUNT(*) as total 
FROM message 
GROUP BY status;
```

### Data Management

#### File Locations
```bash
# H2 files created automatically
./data/quarkus-demo.mv.db      # Main database
./data/quarkus-demo.trace.db   # Trace file (debugging)
```

#### Backup and Restore
```bash
# Backup (copy files)
cp -r ./data ./backup-$(date +%Y%m%d)

# Restore (replace files)
rm -rf ./data
cp -r ./backup-20240101 ./data
```

#### Complete Database Reset
```bash
# Stop Quarkus (Ctrl+C)
rm -rf ./data
# Restart Quarkus - new database created automatically
./mvnw quarkus:dev
```

---

## 📡 REST API - Endpoints

### Messages - Complete Management

#### Create a Message
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "My first message with hexagonal architecture!",
    "author": "Java Developer"
  }'
```

**Response**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "content": "My first message...",
  "author": "Java Developer",
  "status": "DRAFT",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

#### Publish a Message
```bash
curl -X POST http://localhost:8080/api/messages/{id}/publish
```

#### List Messages
```bash
# All active messages
curl http://localhost:8080/api/messages

# By status
curl http://localhost:8080/api/messages/status/PUBLISHED

# By author
curl http://localhost:8080/api/messages/author/JohnDoe
```

#### Update a Message
```bash
curl -X PUT http://localhost:8080/api/messages/{id} \
  -H "Content-Type: application/json" \
  -d '{"content": "Modified content"}'
```

#### Delete a Message (logical)
```bash
curl -X DELETE http://localhost:8080/api/messages/{id}
```

### HTTP Response Codes

| Code | Meaning | Use Case |
|------|---------|----------|
| `200` | OK | Successful retrieval, update |
| `201` | Created | Message created successfully |
| `204` | No Content | Successful deletion |
| `400` | Bad Request | Invalid data |
| `404` | Not Found | Message not found |
| `500` | Server Error | Server error |

---

## 🧪 Testing and Quality

### Implemented Test Types

#### Unit Tests (Ultra-fast)
```bash
# Domain tests only
./mvnw test -Dtest="*Test"

# Specific test
./mvnw test -Dtest="MessageTest"
```

**Characteristics**:
- ⚡ **< 10ms per test** (no I/O)
- 🎯 **Pure business logic**
- 🧪 **Mocks for dependencies**

#### Integration Tests
```bash
# Tests with database
./mvnw test -Dtest="*IntegrationTest"

# Complete controller test
./mvnw test -Dtest="MessageControllerIntegrationTest"
```

**Characteristics**:
- 🔄 **In-memory H2 test database**
- 📡 **Complete end-to-end tests**
- 🌐 **HTTP and JSON validation**

#### Code Coverage
```bash
# Generate coverage report
./mvnw jacoco:report

# View report
open target/site/jacoco/index.html
```

### Testing Strategy

| Type | Layer | Purpose | Speed |
|------|-------|---------|-------|
| **Unit** | Domain | Business logic | ⚡⚡⚡ |
| **Integration** | Application | E2E behavior | ⚡⚡ |
| **Acceptance** | API | User contract | ⚡ |

---

## 🛠️ Technologies and Tools

### Lombok - Boilerplate Code Reduction

The project uses **Lombok 1.18.30** to significantly reduce Java boilerplate code and improve code readability.

#### Maven Configuration
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

#### Annotations Used in the Project

##### `@Data` - DTO Classes and Entities
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private String content;
    private String author;
}
```

**Automatically generates**:
- ✅ Getters for all fields
- ✅ Setters for all fields  
- ✅ Informative `toString()`
- ✅ `equals()` and `hashCode()`

##### `@Getter` - Domain Models
```java
@Getter
public class Message {
    private final String id;
    private final String content;
    private final String author;
    // Constructor and business methods...
}
```

**Benefits**:
- 🔒 **Preserved immutability** (no setters)
- 🎯 **Controlled access** to properties
- 📖 **More readable** and concise code

##### `@NoArgsConstructor` / `@AllArgsConstructor`
```java
@NoArgsConstructor  // Parameter-less constructor (JPA)
@AllArgsConstructor // Constructor with all parameters
public class MessageEntity {
    // fields...
}
```

#### Lombok Benefits in Hexagonal Architecture

| Layer | Lombok Usage | Benefit |
|-------|-------------|---------|
| **Domain** | `@Getter` only | Preserves immutability |
| **Application** | `@Data` for DTOs | Simplifies transfers |
| **Infrastructure** | `@Data` + `@NoArgsConstructor` | JPA/JSON compatible |

#### IDE Configuration

##### IntelliJ IDEA
1. **Install the plugin**:
   - File → Settings → Plugins
   - Search for "Lombok"
   - Install and restart

2. **Enable annotation processing**:
   - File → Settings → Build → Compiler → Annotation Processors
   - ✅ Check "Enable annotation processing"

##### Eclipse
```bash
# Download lombok.jar and run
java -jar lombok.jar
# Follow the installation wizard
```

#### Installation Validation

```bash
# Compile the project (should succeed)
./mvnw clean compile

# Verify method generation
javap -cp target/classes io.lostyzen.demo.infrastructure.adapter.in.rest.dto.MessageDto
```

**Expected output**:
```java
public class MessageDto {
    // Methods generated by Lombok
    public java.lang.String getId();
    public java.lang.String getContent();
    public void setId(java.lang.String);
    public boolean equals(java.lang.Object);
    public java.lang.String toString();
    // ...
}
```

#### Impact on Testing

Lombok also simplifies test writing:

```java
// Before Lombok
CreateMessageRequest request = new CreateMessageRequest();
request.setContent("Test message");
request.setAuthor("Test author");

// With Lombok @AllArgsConstructor
CreateMessageRequest request = new CreateMessageRequest("Test message", "Test author");
```

---

## 🔧 Advanced Configuration

### Environment Variables

```bash
# Development mode
export QUARKUS_PROFILE=dev

# Database configuration
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:file:./data/quarkus-demo
export QUARKUS_DATASOURCE_USERNAME=sa
export QUARKUS_DATASOURCE_PASSWORD=

# Log level
export QUARKUS_LOG_CONSOLE_LEVEL=DEBUG
```

### Configuration Profiles

#### Development (default)
```properties
# application.properties
%dev.quarkus.log.console.level=DEBUG
%dev.quarkus.hibernate-orm.log.sql=true
```

#### Test
```properties
%test.quarkus.datasource.jdbc.url=jdbc:h2:mem:test
%test.quarkus.hibernate-orm.database.generation=drop-and-create
```

#### Production
```properties
%prod.quarkus.log.console.level=INFO
%prod.quarkus.hibernate-orm.database.generation=validate
```

### Customizable H2 Parameters

```properties
# H2 TCP server port
h2.tcp.port=9092

# Database directory
h2.database.path=./data/quarkus-demo

# Connection parameters
quarkus.datasource.jdbc.url=jdbc:h2:file:${h2.database.path};DB_CLOSE_DELAY=-1
```

---

## 🐛 Troubleshooting

### Common Issues

#### Port 8080 Already in Use
```bash
# Identify the process
netstat -ano | findstr :8080
taskkill /PID <process-id> /F

# Or change Quarkus port
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

#### H2 Database Locked
```bash
# Error: Database may be already in use
# Solution: Stop all Java processes
taskkill /F /IM java.exe

# Or remove lock file
rm ./data/*.lock.db
```

#### DBeaver Connection Failed
```bash
# Check that Quarkus is running
curl http://localhost:8080/q/health

# Check H2 port
netstat -ano | findstr :9092

# Test H2 connection
telnet localhost 9092
```

### Debug Logging

#### Enable SQL Logs with Values and Formatting

The project uses **P6Spy** to display SQL queries with actual parameter values (instead of `?`) and professional formatting.

**Configuration**:

1. **Maven Dependencies** (already configured):
```xml
<dependency>
    <groupId>p6spy</groupId>
    <artifactId>p6spy</artifactId>
    <version>3.9.1</version>
</dependency>
```

2. **Configuration in `application.properties`**:
```properties
# P6Spy driver that intercepts JDBC queries
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.driver=com.p6spy.engine.spy.P6SpyDriver
quarkus.datasource.jdbc.url=jdbc:p6spy:h2:file:./data/quarkus-demo;DB_CLOSE_DELAY=-1

# Hibernate dialect (required with P6Spy)
quarkus.hibernate-orm.dialect=org.hibernate.dialect.H2Dialect
```

3. **File `spy.properties`** (in `src/main/resources`):
```properties
# Real JDBC driver
realdatasource=org.h2.Driver

# Use custom formatter with Hibernate indentation
logMessageFormat=org.acme.demo.infrastructure.config.P6SpySqlFormatter

# Log via SLF4J
appender=com.p6spy.engine.spy.appender.Slf4JLogger

# Filter useless categories
excludecategories=info,debug,result,resultset,batch
excludebinary=true
autoflush=true
```

**Result in logs**:
```
Hibernate: 
    select
        me1_0.id,
        me1_0.author,
        me1_0.content,
        me1_0.created_at,
        me1_0.status 
    from
        messages me1_0 
    where
        me1_0.status='PUBLISHED'
```

**Advantages**:
- ✅ **Real values** displayed directly (no `?`)
- ✅ **Professional indentation** (native Hibernate formatter)
- ✅ **All query types**: SELECT, INSERT, UPDATE, DELETE
- ✅ **Ideal for development** and debugging

**⚠️ Important**: P6Spy adds a slight overhead. In production, disable it by reverting to standard H2 configuration:
```properties
%prod.quarkus.datasource.jdbc.driver=org.h2.Driver
%prod.quarkus.datasource.jdbc.url=jdbc:h2:file:./data/quarkus-demo;DB_CLOSE_DELAY=-1
```

#### Standard Hibernate Logs (without values)
```properties
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.log.format-sql=true
quarkus.log.category."org.hibernate.SQL".level=DEBUG
```

#### Detailed H2 Logs
```properties
quarkus.log.category."org.h2".level=DEBUG
quarkus.log.category."io.lostyzen.demo.infrastructure.config.H2TcpServerManager".level=DEBUG
```

### Frequent Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| `Port 8080 in use` | Application already running | Stop existing process |
| `Database locked` | Active H2 connection | Close DBeaver or restart |
| `Connection refused :9092` | H2 TCP server not started | Check startup logs |
| `Tests failing` | Polluted test database | `./mvnw clean test` |

---

## 📞 Support and Resources

### Official Documentation
- 📚 **Quarkus**: https://quarkus.io/guides/
- 🗄️ **H2 Database**: http://h2database.com/html/main.html
- 🏗️ **Hexagonal Architecture**: [Detailed Guide](README_ARCHITECTURE_HEXAGONALE_EN.md)

### Diagnostic Commands
```bash
# Java version
java -version

# Quarkus information
./mvnw quarkus:info

# Port status
netstat -ano | findstr "8080\|9092"

# Active Java processes  
jps -v
```

---

**📝 Documentation maintained by the development team**  
**🔄 Last updated**: Octobre 2025
