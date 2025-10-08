# Quarkus Demo - Hexagonal Architecture 🏗️

> **Navigation**: [🏠 Home](README.md) | [🇫🇷 Français](README_FR.md) | [🏗️ Technical Guide](README_ARCHITECTURE_HEXAGONALE.md)

A comprehensive demonstration of **transforming a simple REST API into a complete hexagonal architecture** with Quarkus 3.8.3.

## 🎯 Demo Purpose

This application concretely illustrates:
- **Before**: Fat controller with mixed business logic
- **After**: Hexagonal architecture with rich domain model
- **Measurable benefits** of this transformation
- How to **structure a project** for long-term maintainability

## 🏗️ Implemented Hexagonal Architecture

### 🎯 **Domain Layer** (Pure business core)
```java
// Rich entity with business logic
public class Message {
    public void publish() { /* business rules */ }
    public void updateContent(String content) { /* validation */ }
    // State transitions: DRAFT → PUBLISHED → ARCHIVED
}

// Use Cases (application logic)
@ApplicationScoped
public class CreateMessageUseCase {
    public Message execute(String content, String author) { /* ... */ }
}
```

### 🔌 **Infrastructure Layer** (Adapters)
```java
// REST Adapter (input)
@Path("/api/messages")
public class MessageController {
    // Delegates everything to Use Cases
}

// JPA Adapter (output)  
@ApplicationScoped
public class JpaMessageRepository implements MessageRepository {
    // Implements domain interfaces
}
```

## 🚀 Quick Start

### 1. **Setup**
```bash
git clone <your-repo-url>
cd quarkus-demo
mvn clean compile
```

### 2. **Launch** (two options)
```bash
# Option 1: System Maven (recommended)
mvn quarkus:dev

# Option 2: Maven Wrapper
.\mvnw quarkus:dev
```

### 3. **Immediate Test**
```bash
# Create a message
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"First hexagonal architecture test","author":"Java Developer"}'

# Publish the message (get ID from previous response)
curl -X POST http://localhost:8080/api/messages/{ID}/publish

# List all messages
curl http://localhost:8080/api/messages
```

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.8.2** or higher (for Quarkus 3.8.3)
- **Git** (to clone the project)

### Prerequisites Verification
```bash
java -version    # Should display Java 17+
mvn -version     # Should display Maven 3.8.2+
```

## 🌐 Complete REST API

### **Message Management**
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/messages` | List all active messages |
| `POST` | `/api/messages` | Create new message (status: DRAFT) |
| `PUT` | `/api/messages/{id}` | Update message content |
| `DELETE` | `/api/messages/{id}` | Logical deletion (status: DELETED) |

### **Business Actions**
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/messages/{id}/publish` | Publish message (DRAFT → PUBLISHED) |

### **Advanced Filtering**
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/messages/status/{status}` | Filter by status (DRAFT, PUBLISHED, ARCHIVED, DELETED) |
| `GET` | `/api/messages/author/{author}` | Filter by author |

### **Complete Flow Example**
```bash
# 1. Create a message
POST /api/messages
{
  "content": "Hexagonal architecture with Quarkus",
  "author": "Java Expert"
}
# Response: {"id": "abc-123", "status": "DRAFT", ...}

# 2. Publish the message  
POST /api/messages/abc-123/publish
# Response: {"id": "abc-123", "status": "PUBLISHED", ...}

# 3. Filter published messages
GET /api/messages/status/PUBLISHED
```

## 🧪 Testing and Validation

### **Running Tests**
```bash
# Domain unit tests (ultra-fast)
mvn test -Dtest="*Test"

# Integration tests (end-to-end)
mvn test -Dtest="*IntegrationTest"

# All tests
mvn test
```

### **Expected Results**
- ✅ **21 tests** all passing
- ⚡ **Domain tests**: < 0.1s (pure logic)
- 🚀 **Use Case tests**: < 0.5s (with mocks)
- 🏗️ **Integration tests**: < 5s (database)

## 🔧 Development Tools

### **Available Web Interfaces**
- **Swagger UI**: http://localhost:8080/q/swagger-ui/
- **Quarkus Dev UI**: http://localhost:8080/q/dev/
- **Health Check**: http://localhost:8080/q/health
- **OpenAPI Spec**: http://localhost:8080/q/openapi

### **Live Reload**
In `quarkus:dev` mode, modify source code and refresh your browser - changes are automatically applied!

## 🏆 Demonstrated Benefits

| Aspect | Before (Fat Controller) | After (Hexagonal Architecture) |
|--------|------------------------|----------------------------------|
| **Testing** | Slow (infrastructure required) | Ultra-fast (isolated logic) |
| **Evolution** | Difficult (tight coupling) | Easy (interchangeable adapters) |
| **Validation** | Scattered | Centralized in domain |
| **Maintenance** | Complex | Clear and modular structure |

## 📚 Advanced Documentation

- **[🏗️ Technical Architecture Guide](README_ARCHITECTURE_HEXAGONALE.md)** - Detailed transformation analysis
- **[🇫🇷 Documentation Française](README_FR.md)** - Guide complet en français
- **[🏠 Main README](README.md)** - Project overview

## 🎓 Illustrated Technical Concepts

### **Architectural Patterns**
- ✅ **Ports & Adapters** (Hexagonal Architecture)
- ✅ **Dependency Inversion** (DIP)
- ✅ **Use Cases** (Clean Architecture)
- ✅ **Domain-Driven Design** (DDD)

### **Quarkus Best Practices**
- ✅ **Dependency Injection** with CDI
- ✅ **Externalized Configuration**
- ✅ **Testing** with dedicated profiles
- ✅ **Hot Reload** in development
- ✅ **Automatic OpenAPI** documentation

### **Code Quality**
- ✅ **Separation** of concerns
- ✅ **Test Pyramid** (unit → integration)
- ✅ **Centralized Business** validation
- ✅ **Robust Error** handling

## 🚨 Troubleshooting

### **Maven Version Issue**
```bash
# Error: "Detected Maven Version (3.8.1) is not supported"
# Solution: Use system Maven instead of wrapper
mvn quarkus:dev  # Instead of ./mvnw quarkus:dev
```

### **Port Already in Use**
```bash
# If port 8080 is occupied
mvn quarkus:dev -Dquarkus.http.port=8081
```

### **Failing Tests**
```bash
# Make sure you're using the correct endpoints (/api/messages)
# Old tests used /messages (legacy)
```

## 📄 License and Attribution

**MIT License** - Free to use for education and training.

**Attribution required** for any reuse:
- Author: [@lostyzen](https://github.com/lostyzen)
- Project: Quarkus Demo - Hexagonal Architecture

---

*💡 This project is an educational resource to master hexagonal architecture with Quarkus and Java 17.*
