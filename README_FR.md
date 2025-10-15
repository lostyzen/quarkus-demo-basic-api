# Quarkus Demo - Guide Utilisateur Complet 🇫🇷

> **🌐 Autres langues**: [🇺🇸 English Version](README_EN.md) | [🏠 Retour accueil](README.md)  
> **📚 Documentation technique**: [🏗️ Architecture Hexagonale](README_ARCHITECTURE_HEXAGONALE_FR.md)

---

## 📖 Table des Matières

1. [🎯 Vue d'ensemble](#-vue-densemble)
2. [🚀 Installation et Configuration](#-installation-et-configuration)
3. [🗄️ Base de Données H2 - Mode Serveur](#-base-de-données-h2---mode-serveur)
4. [📡 API REST - Endpoints](#-api-rest---endpoints)
5. [🧪 Tests et Qualité](#-tests-et-qualité)
6. [🔧 Configuration Avancée](#-configuration-avancée)
7. [🐛 Dépannage](#-dépannage)

---

## 🎯 Vue d'ensemble

Ce projet démontre l'implémentation d'une **architecture hexagonale** avec Quarkus, transformant une API REST simple en une application maintenable et testable.

### Fonctionnalités Principales
- ✅ **API REST complète** pour gestion de messages
- ✅ **Architecture hexagonale** (Ports & Adapters)
- ✅ **Base de données H2** en mode serveur TCP
- ✅ **Tests unitaires** et d'intégration
- ✅ **Documentation OpenAPI** (Swagger)
- ✅ **Monitoring** et health checks

---

## 🚀 Installation et Configuration

### Prérequis Système
```bash
# Vérifier Java
java -version  # Requis: Java 17+

# Vérifier Maven (optionnel, wrapper inclus)
mvn -version   # Recommandé: Maven 3.9+
```

### Installation du Projet
```bash
# 1. Cloner le repository
git clone <repository-url>
cd quarkus-demo

# 2. Installer les dépendances
./mvnw clean compile

# 3. Lancer l'application
./mvnw quarkus:dev
```

### Vérification du Démarrage
Une fois l'application lancée, vérifiez ces endpoints :
- 🌐 **Application** : http://localhost:8080
- 📊 **Swagger UI** : http://localhost:8080/q/swagger-ui
- ❤️ **Health Check** : http://localhost:8080/q/health
- 📈 **Métriques** : http://localhost:8080/q/metrics

---

## 🗄️ Base de Données H2 - Mode Serveur

### Configuration Avancée H2

Notre application utilise **H2 en mode serveur TCP** pour permettre l'accès simultané depuis l'application et des outils externes comme DBeaver.

#### Architecture de la Base H2
```
Quarkus Application ──────┐
                         │
                         ├─► H2 TCP Server (Port 9092)
                         │       │
DBeaver/Outils externes ─┘       │
                                 ▼
                         H2 Database File
                         (./data/quarkus-demo.mv.db)
```

#### Configuration Automatique

La classe `H2TcpServerManager` démarre automatiquement un serveur H2 TCP :

**Fonctionnalités** :
- ✅ **Démarrage automatique** au lancement de Quarkus
- ✅ **Arrêt propre** à l'arrêt de l'application  
- ✅ **Gestion des conflits** de port
- ✅ **Persistance sur fichier** (survit aux redémarrages)
- ✅ **Accès simultané** Quarkus + outils externes

### Connexion avec DBeaver

#### Installation de DBeaver
```bash
# Avec Scoop (Windows)
scoop install dbeaver

# Ou téléchargement manuel depuis https://dbeaver.io
```

#### Configuration de la Connexion DBeaver

1. **Créer une nouvelle connexion** :
   - Type : **H2 Server**
   - Host : `localhost`
   - Port : `9092`
   - Database : `quarkus-demo`
   - Username : `sa`
   - Password : *(vide)*

2. **URL JDBC complète** :
   ```
   jdbc:h2:tcp://localhost:9092/quarkus-demo
   ```

3. **Test de connexion** :
   - Assurez-vous que Quarkus est en cours d'exécution
   - Cliquez sur "Test Connection" dans DBeaver
   - Vous devriez voir : ✅ "Connected"

#### Requêtes SQL Utiles

```sql
-- Voir tous les messages
SELECT * FROM message;

-- Messages par statut
SELECT * FROM message WHERE status = 'PUBLISHED';

-- Messages récents
SELECT * FROM message 
ORDER BY created_at DESC 
LIMIT 10;

-- Supprimer un message spécifique
DELETE FROM message WHERE id = 'uuid-du-message';

-- Statistiques par statut
SELECT status, COUNT(*) as total 
FROM message 
GROUP BY status;
```

### Gestion des Données

#### Localisation des Fichiers
```bash
# Fichiers H2 créés automatiquement
./data/quarkus-demo.mv.db      # Base principale
./data/quarkus-demo.trace.db   # Fichier de trace (debugging)
```

#### Sauvegarde et Restauration
```bash
# Sauvegarde (copier les fichiers)
cp -r ./data ./backup-$(date +%Y%m%d)

# Restauration (remplacer les fichiers)
rm -rf ./data
cp -r ./backup-20240101 ./data
```

#### Reset Complet de la Base
```bash
# Arrêter Quarkus (Ctrl+C)
rm -rf ./data
# Relancer Quarkus - nouvelle base créée automatiquement
./mvnw quarkus:dev
```

---

## 📡 API REST - Endpoints

### Messages - Gestion Complète

#### Créer un Message
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Mon premier message avec architecture hexagonale !",
    "author": "Développeur Java"
  }'
```

**Réponse** :
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "content": "Mon premier message...",
  "author": "Développeur Java",
  "status": "DRAFT",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

#### Publier un Message
```bash
curl -X POST http://localhost:8080/api/messages/{id}/publish
```

#### Lister les Messages
```bash
# Tous les messages actifs
curl http://localhost:8080/api/messages

# Par statut
curl http://localhost:8080/api/messages/status/PUBLISHED

# Par auteur
curl http://localhost:8080/api/messages/author/JohnDoe
```

#### Mettre à Jour un Message
```bash
curl -X PUT http://localhost:8080/api/messages/{id} \
  -H "Content-Type: application/json" \
  -d '{"content": "Contenu modifié"}'
```

#### Supprimer un Message (logique)
```bash
curl -X DELETE http://localhost:8080/api/messages/{id}
```

### Codes de Réponse HTTP

| Code | Signification | Cas d'usage |
|------|---------------|-------------|
| `200` | OK | Récupération, mise à jour réussie |
| `201` | Created | Message créé avec succès |
| `204` | No Content | Suppression réussie |
| `400` | Bad Request | Données invalides |
| `404` | Not Found | Message inexistant |
| `500` | Server Error | Erreur serveur |

---

## 🧪 Tests et Qualité

### Types de Tests Implémentés

#### Tests Unitaires (Ultra-rapides)
```bash
# Tests du domaine uniquement
./mvnw test -Dtest="*Test"

# Test spécifique
./mvnw test -Dtest="MessageTest"
```

**Caractéristiques** :
- ⚡ **< 10ms par test** (pas d'I/O)
- 🎯 **Logique métier pure**
- 🧪 **Mocks pour les dépendances**

#### Tests d'Intégration
```bash
# Tests avec base de données
./mvnw test -Dtest="*IntegrationTest"

# Test du controller complet
./mvnw test -Dtest="MessageControllerIntegrationTest"
```

**Caractéristiques** :
- 🔄 **Base H2 en mémoire de test**
- 📡 **Tests end-to-end complets**
- 🌐 **Validation HTTP et JSON**

#### Couverture de Code
```bash
# Génération du rapport de couverture
./mvnw jacoco:report

# Voir le rapport
open target/site/jacoco/index.html
```

### Stratégie de Tests

| Type | Couche | Objectif | Vitesse |
|------|--------|----------|---------|
| **Unitaire** | Domaine | Logique métier | ⚡⚡⚡ |
| **Intégration** | Application | Comportement E2E | ⚡⚡ |
| **Acceptance** | API | Contrat utilisateur | ⚡ |

---

## 🛠️ Technologies et Outils

### Lombok - Réduction du Code Boilerplate

Le projet utilise **Lombok 1.18.30** pour réduire considérablement le code boilerplate Java et améliorer la lisibilité du code.

#### Configuration Maven
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

#### Annotations Utilisées dans le Projet

##### `@Data` - Classes DTO et Entités
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private String content;
    private String author;
}
```

**Génère automatiquement** :
- ✅ Getters pour tous les champs
- ✅ Setters pour tous les champs  
- ✅ `toString()` informatif
- ✅ `equals()` et `hashCode()`

##### `@Getter` - Modèles du Domaine
```java
@Getter
public class Message {
    private final String id;
    private final String content;
    private final String author;
    // Constructeur et méthodes métier...
}
```

**Avantages** :
- 🔒 **Immutabilité préservée** (pas de setters)
- 🎯 **Accès contrôlé** aux propriétés
- 📖 **Code plus lisible** et concis

##### `@NoArgsConstructor` / `@AllArgsConstructor`
```java
@NoArgsConstructor  // Constructeur sans paramètre (JPA)
@AllArgsConstructor // Constructeur avec tous les paramètres
public class MessageEntity {
    // champs...
}
```

#### Bénéfices de Lombok dans l'Architecture Hexagonale

| Couche | Usage Lombok | Bénéfice |
|--------|-------------|----------|
| **Domaine** | `@Getter` uniquement | Préserve l'immutabilité |
| **Application** | `@Data` pour DTOs | Simplifie les transferts |
| **Infrastructure** | `@Data` + `@NoArgsConstructor` | Compatible JPA/JSON |

#### Configuration IDE

##### IntelliJ IDEA
1. **Installer le plugin** :
   - File → Settings → Plugins
   - Rechercher "Lombok"
   - Installer et redémarrer

2. **Activer l'annotation processing** :
   - File → Settings → Build → Compiler → Annotation Processors
   - ✅ Cocher "Enable annotation processing"

##### Eclipse
```bash
# Télécharger lombok.jar et exécuter
java -jar lombok.jar
# Suivre l'assistant d'installation
```

#### Validation de l'Installation

```bash
# Compiler le projet (doit réussir)
./mvnw clean compile

# Vérifier la génération des méthodes
javap -cp target/classes io.lostyzen.demo.infrastructure.adapter.in.rest.dto.MessageDto
```

**Sortie attendue** :
```java
public class MessageDto {
    // Méthodes générées par Lombok
    public java.lang.String getId();
    public java.lang.String getContent();
    public void setId(java.lang.String);
    public boolean equals(java.lang.Object);
    public java.lang.String toString();
    // ...
}
```

#### Impact sur les Tests

Lombok simplifie également l'écriture des tests :

```java
// Avant Lombok
CreateMessageRequest request = new CreateMessageRequest();
request.setContent("Test message");
request.setAuthor("Test author");

// Avec Lombok @AllArgsConstructor
CreateMessageRequest request = new CreateMessageRequest("Test message", "Test author");
```

---

## 🔧 Configuration Avancée

### Variables d'Environnement

```bash
# Mode de développement
export QUARKUS_PROFILE=dev

# Configuration base de données
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:file:./data/quarkus-demo
export QUARKUS_DATASOURCE_USERNAME=sa
export QUARKUS_DATASOURCE_PASSWORD=

# Niveau de logs
export QUARKUS_LOG_CONSOLE_LEVEL=DEBUG
```

### Profils de Configuration

#### Développement (par défaut)
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

### Paramètres H2 Personnalisables

```properties
# Port du serveur TCP H2
h2.tcp.port=9092

# Répertoire de la base
h2.database.path=./data/quarkus-demo

# Paramètres de connexion
quarkus.datasource.jdbc.url=jdbc:h2:file:${h2.database.path};DB_CLOSE_DELAY=-1
```

---

## 🐛 Dépannage

### Problèmes Courants

#### Port 8080 Déjà Utilisé
```bash
# Identifier le processus
netstat -ano | findstr :8080
taskkill /PID <process-id> /F

# Ou changer le port Quarkus
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

#### Base H2 Verrouillée
```bash
# Erreur: Database may be already in use
# Solution: Arrêter tous les processus Java
taskkill /F /IM java.exe

# Ou supprimer le fichier de lock
rm ./data/*.lock.db
```

#### Connexion DBeaver Échouée
```bash
# Vérifier que Quarkus tourne
curl http://localhost:8080/q/health

# Vérifier le port H2
netstat -ano | findstr :9092

# Tester la connexion H2
telnet localhost 9092
```

### Logs de Débogage

#### Activer les Logs SQL avec Valeurs et Formatage

Le projet utilise **P6Spy** pour afficher les requêtes SQL avec les valeurs réelles des paramètres (au lieu des `?`) et un formatage professionnel.

**Configuration** :

1. **Dépendances Maven** (déjà configurées) :
```xml
<dependency>
    <groupId>p6spy</groupId>
    <artifactId>p6spy</artifactId>
    <version>3.9.1</version>
</dependency>
```

2. **Configuration dans `application.properties`** :
```properties
# Driver P6Spy qui intercepte les requêtes JDBC
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.driver=com.p6spy.engine.spy.P6SpyDriver
quarkus.datasource.jdbc.url=jdbc:p6spy:h2:file:./data/quarkus-demo;DB_CLOSE_DELAY=-1

# Dialecte Hibernate (nécessaire avec P6Spy)
quarkus.hibernate-orm.dialect=org.hibernate.dialect.H2Dialect
```

3. **Fichier `spy.properties`** (dans `src/main/resources`) :
```properties
# Driver JDBC réel
realdatasource=org.h2.Driver

# Utiliser le formatteur personnalisé avec indentation Hibernate
logMessageFormat=org.acme.demo.infrastructure.config.P6SpySqlFormatter

# Logger via SLF4J
appender=com.p6spy.engine.spy.appender.Slf4JLogger

# Filtrer les catégories inutiles
excludecategories=info,debug,result,resultset,batch
excludebinary=true
autoflush=true
```

**Résultat dans les logs** :
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

**Avantages** :
- ✅ **Valeurs réelles** affichées directement (pas de `?`)
- ✅ **Indentation professionnelle** (formatteur Hibernate natif)
- ✅ **Tous les types de requêtes** : SELECT, INSERT, UPDATE, DELETE
- ✅ **Idéal pour le développement** et le débogage

**⚠️ Important** : P6Spy ajoute un léger overhead. En production, désactivez-le en revenant à la configuration H2 standard :
```properties
%prod.quarkus.datasource.jdbc.driver=org.h2.Driver
%prod.quarkus.datasource.jdbc.url=jdbc:h2:file:./data/quarkus-demo;DB_CLOSE_DELAY=-1
```

#### Logs Hibernate Standard (sans valeurs)
```properties
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.log.format-sql=true
quarkus.log.category."org.hibernate.SQL".level=DEBUG
```

#### Logs H2 Détaillés
```properties
quarkus.log.category."org.h2".level=DEBUG
quarkus.log.category."io.lostyzen.demo.infrastructure.config.H2TcpServerManager".level=DEBUG
```

### Messages d'Erreur Fréquents

| Erreur | Cause | Solution |
|--------|-------|---------|
| `Port 8080 in use` | Application déjà lancée | Arrêter le processus existant |
| `Database locked` | Connexion H2 active | Fermer DBeaver ou redémarrer |
| `Connection refused :9092` | Serveur H2 TCP non démarré | Vérifier les logs de démarrage |
| `Tests failing` | Base de test polluée | `./mvnw clean test` |

---

## 📞 Support et Ressources

### Documentation Officielle
- 📚 **Quarkus** : https://quarkus.io/guides/
- 🗄️ **H2 Database** : http://h2database.com/html/main.html
- 🏗️ **Architecture Hexagonale** : [Guide détaillé](README_ARCHITECTURE_HEXAGONALE_FR.md)

### Commandes de Diagnostic
```bash
# Version Java
java -version

# Informations Quarkus
./mvnw quarkus:info

# État des ports
netstat -ano | findstr "8080\|9092"

# Processus Java actifs  
jps -v
```

---

**📝 Documentation maintenue par l'équipe de développement**  
**🔄 Dernière mise à jour** : octobre 2025
