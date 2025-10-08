# 📄 Guide de conversion Markdown vers MS Word

## 🚀 Méthodes de conversion disponibles

### 1. **Pandoc** (Recommandé - Solution professionnelle)

Pandoc est déjà installé via Scoop et offre la meilleure qualité de conversion.

#### Conversion basique
```powershell
# Conversion simple
pandoc fichier.md -o fichier.docx

# Exemple avec votre fichier Scoop
pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows.docx
```

#### Conversion avancée avec options
```powershell
# Avec table des matières
pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows.docx --toc

# Avec numérotation des sections
pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows.docx --toc --number-sections

# Avec un template Word personnalisé
pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows.docx --reference-doc=template.docx

# Conversion avec métadonnées
pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows.docx --metadata title="Scoop Guide" --metadata author="Votre Nom"
```

#### Optimisation pour Yammer/Teams
```powershell
# Format optimisé pour partage d'entreprise
pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows.docx --toc --number-sections --highlight-style=github
```

### 2. **Via Word Online (Microsoft 365)**

1. Ouvrir Word Online
2. Créer un nouveau document
3. Coller le contenu Markdown
4. Word convertit automatiquement certains éléments (titres, listes)
5. Ajuster manuellement le formatage

### 3. **Extensions d'éditeurs**

#### Visual Studio Code
- Extension : "Markdown PDF"
- Permet d'exporter vers Word via PDF

#### Typora (éditeur Markdown WYSIWYG)
```powershell
# Installation via Scoop
scoop install typora
```

### 4. **Solutions en ligne**

- **Pandoc Try** : https://pandoc.org/try/
- **Markdown to Word** : Divers convertisseurs web
- **Dillinger.io** : Éditeur Markdown en ligne avec export

## 🎯 Commandes Pandoc utiles pour vos articles

### Conversion de tous vos articles
```powershell
# Se placer dans le répertoire articles
cd "C:/Users/bazydlo/codes/java/demo/quarkus-demo/scrapbook/articles"

# Convertir tous les fichiers .md en .docx
Get-ChildItem *.md | ForEach-Object { pandoc $_.Name -o ($_.BaseName + ".docx") --toc --number-sections }
```

### Script PowerShell de conversion en lot
```powershell
# Script de conversion automatisée
$sourceDir = "C:/Users/bazydlo/codes/java/demo/quarkus-demo/scrapbook/articles"
$outputDir = "C:/Users/bazydlo/codes/java/demo/quarkus-demo/scrapbook/articles/word-exports"

# Créer le répertoire de sortie s'il n'existe pas
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir
}

# Convertir tous les fichiers Markdown
Get-ChildItem "$sourceDir/*.md" | ForEach-Object {
    $outputFile = Join-Path $outputDir ($_.BaseName + ".docx")
    Write-Host "Conversion de $($_.Name)..." -ForegroundColor Green
    
    pandoc $_.FullName -o $outputFile `
        --toc `
        --number-sections `
        --highlight-style=github `
        --metadata title="$($_.BaseName)" `
        --metadata author="lostyzen"
    
    Write-Host "✅ Converti : $outputFile" -ForegroundColor Cyan
}

Write-Host "🎉 Conversion terminée !" -ForegroundColor Green
```

## 📝 Conseils pour optimiser la conversion

### Préparation du Markdown
- Utilisez des titres hiérarchiques corrects (`#`, `##`, `###`)
- Évitez les emojis si la compatibilité est importante
- Vérifiez que les liens sont bien formatés

### Post-traitement dans Word
- Ajustez les styles de titre
- Vérifiez la table des matières
- Adaptez les couleurs d'entreprise
- Ajoutez des en-têtes/pieds de page

### Pour Yammer/Teams
- Format recommandé : `.docx` avec table des matières
- Évitez les images trop lourdes
- Testez la lisibilité sur mobile

## 🔧 Installation d'outils complémentaires

```powershell
# Éditeurs Markdown avancés
scoop install typora
scoop install marktext

# Outils de traitement de documents
scoop install libreoffice  # Alternative gratuite à MS Office

# Outils de validation Markdown
scoop install markdownlint-cli
```

## 💡 Bonnes pratiques

1. **Sauvegardez vos originaux** : Gardez toujours les fichiers .md
2. **Versioning** : Utilisez Git pour tracer les modifications
3. **Templates** : Créez des templates Word pour harmoniser vos documents
4. **Validation** : Testez la conversion avant publication
5. **Métadonnées** : Ajoutez toujours auteur, titre, date

---

*Ce guide vous permet de transformer efficacement vos articles Markdown en documents Word professionnels pour publication sur Yammer ou toute autre plateforme d'entreprise.*
