# Démo API REST Quarkus

Une démo simple d'API REST utilisant Quarkus 3.8.3 avec des endpoints GET et POST pour gérer des messages.

## 🎯 Objectif de la démo

Cette application démontre :
- Comment créer une API REST simple avec Quarkus
- Configuration d'un projet Quarkus avec Maven
- Tests automatisés avec RestAssured et JUnit
- Packaging et exécution d'une application Quarkus
- Documentation automatique avec OpenAPI/Swagger

## 🚀 Fonctionnalités

L'API expose deux endpoints :
- `GET /messages` : Récupère la liste de tous les messages
- `POST /messages` : Ajoute un nouveau message

Les messages sont stockés en mémoire (liste statique) pour la simplicité de la démo.

## 📋 Prérequis

- **Java 17** ou supérieur
- **Maven 3.8.1** ou supérieur
- **Git** (pour cloner le projet)

### Vérification des prérequis
```bash
java -version    # Doit afficher Java 17+
mvn -version     # Doit afficher Maven 3.8.1+
```

## 🛠️ Installation et configuration

### 1. Cloner le projet
```bash
git clone <votre-repo-url>
cd quarkus-demo
```

### 2. Compiler le projet
```bash
mvn clean compile
```

### 3. Exécuter les tests
```bash
mvn test
```

### 4. Packager l'application
```bash
mvn clean package
```

## 🏃‍♂️ Lancement de l'application

### Mode développement (recommandé pour le dev)
```bash
mvn quarkus:dev
```
L'application démarre sur http://localhost:8080 avec hot-reload activé.

### Mode production (à partir du jar)
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## 🧪 Test de l'API

### Avec curl

**Récupérer tous les messages :**
```bash
curl -X GET http://localhost:8080/messages
```

**Ajouter un message :**
```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello Quarkus!"}'
```

**Test complet :**
```bash
# 1. Voir la liste vide
curl -X GET http://localhost:8080/messages

# 2. Ajouter un message
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Mon premier message"}'

# 3. Voir la liste mise à jour
curl -X GET http://localhost:8080/messages
```

### Avec l'interface Swagger
Accédez à http://localhost:8080/q/swagger-ui/ pour une interface graphique interactive.

## 📁 Structure du projet

```
quarkus-demo/
├── src/
│   ├── main/
│   │   ├── java/org/acme/demo/
│   │   │   ├── Message.java          # POJO Message
│   │   │   └── MessageResource.java  # Ressource REST
│   │   └── resources/
│   │       └── application.properties # Configuration Quarkus
│   └── test/
│       └── java/org/acme/demo/
│           └── MessageResourceTest.java # Tests d'intégration
├── target/
│   └── quarkus-app/
│       └── quarkus-run.jar           # Application exécutable
├── pom.xml                           # Configuration Maven
├── .gitignore                        # Fichiers ignorés par Git
├── README.md                         # Ce fichier
├── README_FR.md                      # Version française
└── README_EN.md                      # Version anglaise
```

## ⚙️ Structure du pom.xml

### Propriétés importantes
- **Quarkus 3.8.3** : Version stable et moderne avec support Jakarta EE
- **Java 17** : Version LTS requise par Quarkus 3.x
- **UTF-8** : Encodage des sources et rapports

### Dépendances clés

#### Production
- `quarkus-resteasy-reactive-jackson` : Support REST + JSON
- `quarkus-smallrye-openapi` : Documentation OpenAPI/Swagger automatique
- `quarkus-arc` : Injection de dépendances (CDI)

#### Tests
- `quarkus-junit5` : Framework de tests Quarkus
- `rest-assured` : Tests d'API REST simplifiés
- `hamcrest` : Matchers pour assertions de tests

### Plugins essentiels

#### quarkus-maven-plugin
```xml
<plugin>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>build</goal>              <!-- Génère le fast-jar -->
        <goal>generate-code</goal>      <!-- Génération de code -->
        <goal>generate-code-tests</goal> <!-- Génération tests -->
      </goals>
    </execution>
  </executions>
</plugin>
```

#### Plugins Maven standards
- **maven-compiler-plugin** : Compilation Java 17 avec paramètres préservés
- **maven-surefire-plugin** : Tests unitaires avec configuration Quarkus
- **maven-failsafe-plugin** : Tests d'intégration

### Points critiques pour le bon fonctionnement

1. **BOM Quarkus correct** :
   ```xml
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-bom</artifactId>
   ```

2. **Goals du plugin Quarkus** : Nécessaires pour générer `target/quarkus-app/`

3. **Configuration des tests** : Variables système requises pour Quarkus

4. **Type de package** : `fast-jar` configuré dans `application.properties`

## 🔧 Configuration

### application.properties
```properties
# Type de packaging (génère target/quarkus-app/)
quarkus.package.type=fast-jar

# Configuration OIDC (exemple, non utilisée dans cette démo)
quarkus.oidc.auth-server-url=https://oidc.example.com/auth/realm/client
```

## 🧪 Tests automatisés

Le projet inclut des tests d'intégration qui :
- Vérifient que la liste des messages est initialement vide
- Testent l'ajout d'un message via POST
- Valident la récupération des messages via GET
- Utilisent RestAssured pour simuler les requêtes HTTP

Exécution des tests :
```bash
mvn test
```

## 📊 Endpoints utiles

- **API** : http://localhost:8080/messages
- **Documentation** : http://localhost:8080/q/swagger-ui/
- **Health check** : http://localhost:8080/q/health
- **Métriques** : http://localhost:8080/q/metrics

## 🐛 Résolution de problèmes

### Le dossier quarkus-app n'est pas généré
- Vérifiez que tous les plugins Maven sont présents dans le pom.xml
- Exécutez `mvn clean package` et vérifiez les logs d'erreur

### Erreur "no main manifest attribute"
- N'utilisez pas `target/quarkus-demo-1.0-SNAPSHOT.jar`
- Utilisez `target/quarkus-app/quarkus-run.jar`

### Tests en échec
- Vérifiez que l'application n'est pas déjà en cours d'exécution sur le port 8080
- Exécutez `mvn clean test` pour un environnement propre

### Problèmes de dépendances
- Exécutez `mvn clean install -U` pour forcer la mise à jour des dépendances
- Vérifiez que vous utilisez Java 17+

## 📚 Pour aller plus loin

- [Documentation Quarkus](https://quarkus.io/guides/)
- [Guide REST avec Quarkus](https://quarkus.io/guides/rest-json)
- [Tests avec Quarkus](https://quarkus.io/guides/getting-started-testing)
- [RestAssured Documentation](https://rest-assured.io/)

## 👥 Contribution

Ce projet sert de démo éducative. N'hésitez pas à l'utiliser comme base pour vos propres projets Quarkus !
