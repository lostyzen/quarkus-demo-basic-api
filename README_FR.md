# Démo Quarkus - Architecture Hexagonale 🏗️

> **Navigation**: [🏠 Accueil](README.md) | [🇺🇸 English](README_EN.md) | [🏗️ Guide Technique](README_ARCHITECTURE_HEXAGONALE.md)

Une démonstration complète de **transformation d'une API REST simple vers une architecture hexagonale** avec Quarkus 3.8.3.

## 🎯 Objectif de la Démonstration

Cette application illustre concrètement :
- **Avant** : Controller obèse avec logique métier mélangée
- **Après** : Architecture hexagonale avec domaine métier riche
- Les **bienfaits mesurables** de cette transformation
- Comment **structurer un projet** pour la maintenabilité à long terme

## 🏗️ Architecture Hexagonale Implémentée

### 🎯 **Couche Domaine** (Cœur métier pur)
```java
// Entité riche avec logique métier
public class Message {
    public void publish() { /* règles métier */ }
    public void updateContent(String content) { /* validation */ }
    // Transitions d'état : DRAFT → PUBLISHED → ARCHIVED
}

// Use Cases (logique applicative)
@ApplicationScoped
public class CreateMessageUseCase {
    public Message execute(String content, String author) { /* ... */ }
}
```

### 🔌 **Couche Infrastructure** (Adaptateurs)
```java
// Adaptateur REST (entrée)
@Path("/api/messages")
public class MessageController {
    // Délègue tout aux Use Cases
}

// Adaptateur JPA (sortie)  
@ApplicationScoped
public class JpaMessageRepository implements MessageRepository {
    // Implémente les interfaces du domaine
}
```

## 🚀 Démarrage Rapide

### 1. **Installation**
```bash
git clone <votre-repo-url>
cd quarkus-demo
mvn clean compile
```

### 2. **Lancement** (deux options)
```bash
# Option 1: Maven système (recommandé)
mvn quarkus:dev

# Option 2: Wrapper Maven
.\mvnw quarkus:dev
```

### 3. **Test Immédiat**
```bash
# Créer un message
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Premier test architecture hexagonale","author":"Développeur Java"}'

# Publier le message (récupérer l'ID de la réponse précédente)
curl -X POST http://localhost:8080/api/messages/{ID}/publish

# Voir tous les messages
curl http://localhost:8080/api/messages
```

## 📋 Prérequis

- **Java 17** ou supérieur
- **Maven 3.8.2** ou supérieur (pour Quarkus 3.8.3)
- **Git** (pour cloner le projet)

### Vérification des Prérequis
```bash
java -version    # Doit afficher Java 17+
mvn -version     # Doit afficher Maven 3.8.2+
```

## 🌐 API REST Complète

### **Gestion des Messages**
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/messages` | Liste tous les messages actifs |
| `POST` | `/api/messages` | Créer un nouveau message (statut: DRAFT) |
| `PUT` | `/api/messages/{id}` | Modifier le contenu d'un message |
| `DELETE` | `/api/messages/{id}` | Suppression logique (statut: DELETED) |

### **Actions Métier**
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/messages/{id}/publish` | Publier un message (DRAFT → PUBLISHED) |

### **Filtrage Avancé**
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/messages/status/{status}` | Filtrer par statut (DRAFT, PUBLISHED, ARCHIVED, DELETED) |
| `GET` | `/api/messages/author/{author}` | Filtrer par auteur |

### **Exemple de Flux Complet**
```bash
# 1. Créer un message
POST /api/messages
{
  "content": "Architecture hexagonale avec Quarkus",
  "author": "Expert Java"
}
# Réponse: {"id": "abc-123", "status": "DRAFT", ...}

# 2. Publier le message  
POST /api/messages/abc-123/publish
# Réponse: {"id": "abc-123", "status": "PUBLISHED", ...}

# 3. Filtrer les messages publiés
GET /api/messages/status/PUBLISHED
```

## 🧪 Tests et Validation

### **Exécution des Tests**
```bash
# Tests unitaires du domaine (ultra-rapides)
mvn test -Dtest="*Test"

# Tests d'intégration (end-to-end)
mvn test -Dtest="*IntegrationTest"

# Tous les tests
mvn test
```

### **Résultats Attendus**
- ✅ **21 tests** qui passent tous
- ⚡ **Tests domaine** : < 0.1s (logique pure)
- 🚀 **Tests Use Cases** : < 0.5s (avec mocks)
- 🏗️ **Tests intégration** : < 5s (base de données)

## 🔧 Outils de Développement

### **Interfaces Web Disponibles**
- **Swagger UI** : http://localhost:8080/q/swagger-ui/
- **Dev UI Quarkus** : http://localhost:8080/q/dev/
- **Health Check** : http://localhost:8080/q/health
- **OpenAPI Spec** : http://localhost:8080/q/openapi

### **Live Reload**
En mode `quarkus:dev`, modifiez le code source et rafraîchissez votre navigateur - les changements sont automatiquement appliqués !

## 🏆 Bienfaits Démontrés

| Aspect | Avant (Controller obèse) | Après (Architecture hexagonale) |
|--------|-------------------------|-----------------------------------|
| **Tests** | Lents (infrastructure requise) | Ultra-rapides (logique isolée) |
| **Évolution** | Difficile (couplage fort) | Facile (adaptateurs interchangeables) |
| **Validation** | Éparpillée | Centralisée dans le domaine |
| **Maintenance** | Complexe | Structure claire et modulaire |

## 📚 Documentation Avancée

- **[🏗️ Guide Architecture Technique](README_ARCHITECTURE_HEXAGONALE.md)** - Analyse détaillée de la transformation
- **[🇺🇸 English Documentation](README_EN.md)** - Complete guide in English
- **[🏠 README Principal](README.md)** - Vue d'ensemble du projet

## 🎓 Concepts Techniques Illustrés

### **Patterns Architecturaux**
- ✅ **Ports & Adaptateurs** (Architecture hexagonale)
- ✅ **Inversion de Dépendance** (DIP)
- ✅ **Use Cases** (Clean Architecture)
- ✅ **Domain-Driven Design** (DDD)

### **Bonnes Pratiques Quarkus**
- ✅ **Dependency Injection** avec CDI
- ✅ **Configuration** externalisée
- ✅ **Tests** avec profils dédiés
- ✅ **Hot Reload** en développement
- ✅ **Documentation** OpenAPI automatique

### **Qualité de Code**
- ✅ **Séparation** des responsabilités
- ✅ **Tests** pyramide (unitaire → intégration)
- ✅ **Validation** métier centralisée
- ✅ **Gestion d'erreurs** robuste

## 🚨 Résolution de Problèmes

### **Problème Maven Version**
```bash
# Erreur: "Detected Maven Version (3.8.1) is not supported"
# Solution: Utiliser Maven système au lieu du wrapper
mvn quarkus:dev  # Au lieu de ./mvnw quarkus:dev
```

### **Port déjà utilisé**
```bash
# Si le port 8080 est occupé
mvn quarkus:dev -Dquarkus.http.port=8081
```

### **Tests qui échouent**
```bash
# Vérifier que vous utilisez les bons endpoints (/api/messages)
# Les anciens tests utilisaient /messages (legacy)
```

## 📄 Licence et Attribution

**Licence MIT** - Libre d'utilisation pour l'éducation et la formation.

**Attribution requise** pour toute réutilisation :
- Auteur : [@lostyzen](https://github.com/lostyzen)
- Projet : Quarkus Demo - Architecture Hexagonale

---

*💡 Ce projet est une ressource éducative pour maîtriser l'architecture hexagonale avec Quarkus et Java 17.*
