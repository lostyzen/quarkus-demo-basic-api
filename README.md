# Quarkus Demo - Architecture Hexagonale

> **Documentation**: [🇫🇷 Version Française](README_FR.md) | [🇺🇸 English Version](README_EN.md) | [🏗️ Guide Architecture](README_ARCHITECTURE_HEXAGONALE.md)

---

## 🏗️ Transformation Architecturale

Ce projet démontre la **transformation d'une API REST simple vers une architecture hexagonale complète** avec Quarkus 3.8.3.

### ✨ **Évolution du Projet**
- **Phase 1** : API REST basique avec controller obèse
- **Phase 2** : **Architecture hexagonale** avec domaine métier riche

## 🎯 Architecture Hexagonale Implémentée

```
🎯 Domain (Cœur métier)
├── model/ (Entités et Value Objects)
│   ├── Message.java (Entité riche avec logique métier)
│   ├── MessageId.java (Value Object)
│   └── MessageStatus.java (Enum avec transitions)
├── service/ (Use Cases)
│   ├── CreateMessageUseCase.java
│   ├── PublishMessageUseCase.java
│   ├── UpdateMessageUseCase.java
│   └── GetMessagesUseCase.java
└── port/out/ (Interfaces de sortie)
    └── MessageRepository.java

🔌 Infrastructure (Adaptateurs)
├── adapter/in/rest/ (API REST)
│   ├── MessageController.java
│   └── dto/ (DTOs de transport)
└── adapter/out/persistence/ (Persistance JPA)
    ├── MessageEntity.java
    └── JpaMessageRepository.java
```

## 🚀 Démarrage rapide

```bash
# Cloner et compiler
git clone <votre-repo-url>
cd quarkus-demo
mvn clean compile

# Lancer l'application (deux options)
mvn quarkus:dev          # Maven système
# ou
.\mvnw quarkus:dev       # Wrapper Maven

# Tester l'API hexagonale
curl -X GET http://localhost:8080/api/messages
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Message avec architecture hexagonale","author":"Developer"}'
```

## 📋 Prérequis

- Java 17+
- Maven 3.8.2+ (compatible avec Quarkus 3.8.3)

## 🌐 API Endpoints (Architecture Hexagonale)

**Gestion des Messages :**
- `GET /api/messages` - Liste tous les messages actifs
- `POST /api/messages` - Créer un nouveau message
- `PUT /api/messages/{id}` - Modifier le contenu
- `POST /api/messages/{id}/publish` - Publier un message
- `DELETE /api/messages/{id}` - Suppression logique

**Filtrage Avancé :**
- `GET /api/messages/status/{status}` - Filtrer par statut (DRAFT, PUBLISHED, ARCHIVED)
- `GET /api/messages/author/{author}` - Filtrer par auteur

**Documentation et outils :**
- **Swagger UI** : http://localhost:8080/q/swagger-ui/ (interface graphique interactive)
- **OpenAPI spec** : http://localhost:8080/q/openapi (spécification OpenAPI en JSON)
- **Dev UI** : http://localhost:8080/q/dev/ (uniquement en mode développement)
- **Health check** : http://localhost:8080/q/health (état de l'application)

## 📚 Documentation Complète

- **[🏗️ Guide Architecture Hexagonale](README_ARCHITECTURE_HEXAGONALE.md)** - Transformation détaillée et bienfaits
- **[📖 Documentation en Français](README_FR.md)** - Guide complet d'utilisation
- **[📖 English Documentation](README_EN.md)** - Complete usage guide

## 🏆 Avantages Démontrés

✅ **Tests ultra-rapides** (logique métier isolée)  
✅ **Séparation claire** des responsabilités  
✅ **Évolutivité** facilitée (nouveaux adaptateurs)  
✅ **Robustesse** avec validation métier centralisée  
✅ **Maintenabilité** à long terme  

## 📄 Licence

Ce projet est distribué sous **licence MIT** - voir le fichier [LICENSE](LICENSE) pour les détails.

**Open source** et libre d'utilisation pour des fins éducatives, de formation et de démonstration.

**Attribution requise** : Si vous réutilisez ce projet ou vous en inspirez, merci de mentionner :
- Auteur : [@lostyzen](https://github.com/lostyzen) sur GitHub
- Projet source : Quarkus Demo - Architecture Hexagonale

---

*Ce projet sert de démo éducative pour apprendre l'architecture hexagonale avec Quarkus et les bonnes pratiques de développement.*
