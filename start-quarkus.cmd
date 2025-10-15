@echo off
echo Configuration de l'encodage UTF-8 pour la console...
chcp 65001 > nul

echo Configuration des variables d'environnement Java pour UTF-8...
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8
set MAVEN_OPTS=-Dfile.encoding=UTF-8

echo Demarrage de Quarkus en mode developpement...
.\mvnw quarkus:dev
