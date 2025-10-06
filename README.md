# Quarkus REST API Demo

> **Documentation**: [🇫🇷 Version Française](README_FR.md) | [🇺🇸 English Version](README_EN.md)

---

## 🎯 À propos

Une démo simple d'API REST utilisant **Quarkus 3.8.3** avec des endpoints GET et POST pour gérer des messages.

Cette application démontre les bonnes pratiques pour :
- Créer une API REST avec Quarkus
- Configurer un projet Maven avec les plugins nécessaires
- Implémenter des tests automatisés
- Packager et déployer une application Quarkus

## 🚀 Démarrage rapide

```bash
# Cloner et compiler
git clone <votre-repo-url>
cd quarkus-demo
mvn clean package

# Lancer l'application
java -jar target/quarkus-app/quarkus-run.jar

# Tester l'API
curl -X GET http://localhost:8080/messages
curl -X POST http://localhost:8080/messages -H "Content-Type: application/json" -d '{"content":"Hello!"}'
```

## 📋 Prérequis

- Java 17+
- Maven 3.8.1+

## 📚 Documentation complète

Pour les instructions détaillées d'installation, configuration, tests et déploiement :

- **[📖 Documentation en Français](README_FR.md)** - Guide complet en français
- **[📖 English Documentation](README_EN.md)** - Complete guide in English

## 🌐 API Endpoints

- `GET /messages` - Récupérer tous les messages
- `POST /messages` - Ajouter un nouveau message
- Swagger UI disponible sur http://localhost:8080/q/swagger-ui/

## 📄 Licence

Ce projet est distribué sous **licence MIT** - voir le fichier [LICENSE](LICENSE) pour les détails.

**Open source** et libre d'utilisation pour des fins éducatives, de formation et de démonstration.

**Attribution requise** : Si vous réutilisez ce projet ou vous en inspirez, merci de mentionner :
- Auteur : [@lostyzen](https://github.com/lostyzen) sur GitHub
- Projet source : Quarkus REST API Demo

Vous êtes libre de :
- ✅ Utiliser ce code pour vos formations
- ✅ L'adapter pour vos propres démos
- ✅ Le distribuer en mentionnant l'attribution
- ✅ Le modifier selon vos besoins
- ✅ L'utiliser à des fins commerciales

---

*Ce projet sert de démo éducative pour apprendre Quarkus et les bonnes pratiques de développement d'API REST.*
