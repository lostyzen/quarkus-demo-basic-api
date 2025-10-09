# Quarkus Demo - Architecture Hexagonale

> **🌐 Documentation multilingue**: [🇫🇷 Version Française](README_FR.md) | [🇺🇸 English Version](README_EN.md)  
> **📚 Documentation technique**: [🏗️ Architecture Hexagonale](README_ARCHITECTURE_HEXAGONALE_FR.md) | [🏗️ Hexagonal Architecture (EN)](README_ARCHITECTURE_HEXAGONALE_EN.md)

---

## 🎯 Présentation du Projet

Ce projet démontre **la transformation d'une API REST simple vers une architecture hexagonale complète** avec Quarkus 3.8.3, illustrant concrètement les bénéfices de cette approche architecturale.

## 🏗️ Architecture Hexagonale

L'architecture hexagonale (Ports & Adapters) sépare clairement :
- **🎯 Domaine** : Logique métier pure et testable
- **🔌 Adaptateurs** : Infrastructure technique (REST, JPA, etc.)
- **📡 Ports** : Contrats d'interface entre domaine et infrastructure

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.9+

### Installation et Lancement
```bash
# Cloner le projet
git clone <repository-url>
cd quarkus-demo

# Lancer l'application en mode développement
./mvnw quarkus:dev
```

### Accès aux Interfaces
- 🌐 **API REST** : http://localhost:8080/api/messages
- 📊 **Swagger UI** : http://localhost:8080/q/swagger-ui
- 🗄️ **Base H2** : Accessible via DBeaver (port 9092)
- ❤️ **Health Check** : http://localhost:8080/q/health

## 📋 Fonctionnalités

### Endpoints API
- `POST /api/messages` - Création d'un message
- `GET /api/messages` - Liste des messages actifs
- `POST /api/messages/{id}/publish` - Publication d'un message
- `PUT /api/messages/{id}` - Mise à jour du contenu
- `DELETE /api/messages/{id}` - Suppression logique

### Base de Données H2
- **Mode serveur TCP** pour accès simultané
- **Persistance sur fichier** (./data/)
- **Connexion DBeaver** en temps réel

## 🧪 Tests

```bash
# Tests unitaires (domaine)
./mvnw test -Dtest="*Test"

# Tests d'intégration
./mvnw test -Dtest="*IntegrationTest"

# Tous les tests
./mvnw test
```

## 📚 Documentation Technique Détaillée

### Français 🇫🇷
- [📋 Guide Utilisateur Complet](README_FR.md)
- [🏗️ Architecture Hexagonale Détaillée](README_ARCHITECTURE_HEXAGONALE_FR.md)

### English 🇺🇸
- [📋 Complete User Guide](README_EN.md)
- [🏗️ Detailed Hexagonal Architecture](README_ARCHITECTURE_HEXAGONALE_EN.md)

## 🎯 Objectifs Pédagogiques

Ce projet illustre :
- ✅ **Séparation des responsabilités** (domaine vs infrastructure)
- ✅ **Testabilité** (tests unitaires ultra-rapides sans I/O)
- ✅ **Flexibilité** (changement d'infrastructure sans impact domaine)
- ✅ **Maintenabilité** (logique métier centralisée et claire)

## 🏆 Technologies Utilisées

- **Quarkus 3.8.3** - Framework Java cloud-native
- **Architecture Hexagonale** - Ports & Adapters pattern
- **H2 Database** - Base en mémoire avec serveur TCP
- **JPA/Hibernate** - ORM avec Panache
- **JAX-RS** - API REST réactive
- **OpenAPI/Swagger** - Documentation API
- **JUnit 5** - Framework de tests

---

**🎓 Projet éducatif** démontrant les bienfaits de l'architecture hexagonale dans un contexte Quarkus moderne.
