# 🔧 Guide PowerShell : Gestion des versions et opérateurs de commandes

## 🎯 Problème résolu : Support de `&&` dans PowerShell

### **Situation actuelle sur votre système**
- ✅ **Windows PowerShell 5.1** : Version classique (sans `&&`)
- ✅ **PowerShell 7.5.3** : Version moderne installée via Scoop (avec `&&`)

## 🚀 Comment utiliser PowerShell 7 avec `&&`

### **Lancement de PowerShell 7**
```powershell
# Dans votre terminal actuel, lancez PowerShell 7
pwsh

# Ou exécutez directement une commande avec &&
pwsh -c "commande1 && commande2"
```

### **Test de fonctionnement** ✅
```powershell
# Ceci fonctionne maintenant !
pwsh -c "cd 'C:/Users/bazydlo/codes/java/demo/quarkus-demo/scrapbook/articles' && pandoc scoop-gestionnaire-windows.md -o test.docx"
```

## 📋 Tableau comparatif des opérateurs

| Opération | Windows PS 5.1 | PowerShell 7+ | Bash/Linux |
|-----------|-----------------|---------------|------------|
| **Exécution séquentielle** | `;` | `;` ou `&&` | `&&` |
| **Si succès alors** | `if ($?) { }` | `&&` | `&&` |
| **Si échec alors** | `if (!$?) { }` | `\|\|` | `\|\|` |
| **Pipeline** | `\|` | `\|` | `\|` |
| **Parallèle** | `Start-Job` | `&` | `&` |

## 🔄 Solutions d'enchaînement selon votre version

### **Dans Windows PowerShell 5.1** (version actuelle de votre terminal)
```powershell
# Méthode 1 : Point-virgule (toujours exécute)
cd "C:/chemin/vers/repertoire" ; pandoc fichier.md -o fichier.docx

# Méthode 2 : Bloc conditionnel
cd "C:/chemin/vers/repertoire"
if ($?) { pandoc fichier.md -o fichier.docx }

# Méthode 3 : Bloc de script
& {
    cd "C:/chemin/vers/repertoire"
    pandoc fichier.md -o fichier.docx
}

# Méthode 4 : Try-Catch (plus robuste)
try {
    cd "C:/chemin/vers/repertoire"
    pandoc fichier.md -o fichier.docx
} catch {
    Write-Error "Erreur: $_"
}
```

### **Dans PowerShell 7** (nouvelle version avec Scoop)
```powershell
# Maintenant vous pouvez utiliser && et ||
cd "C:/chemin/vers/repertoire" && pandoc fichier.md -o fichier.docx

# Ou avec gestion d'erreur
cd "C:/chemin/vers/repertoire" && pandoc fichier.md -o fichier.docx || Write-Host "Échec de la conversion"
```

## ⚡ Recommandations pratiques

### **Pour vos futurs scripts**

1. **Scripts simples** : Utilisez PowerShell 7 avec `&&`
2. **Scripts d'entreprise** : Restez sur PS 5.1 pour compatibilité
3. **Développement** : PowerShell 7 pour plus de fonctionnalités

### **Configuration recommandée**

```powershell
# Créer un profil PowerShell 7 personnalisé
pwsh -c "New-Item -Type File -Path `$PROFILE -Force"

# Ajouter des alias utiles
pwsh -c "Add-Content `$PROFILE 'Set-Alias -Name ll -Value Get-ChildItem'"
```

## 🔧 Commandes corrigées pour votre projet

### **Conversion Markdown avec PowerShell 7**
```powershell
# Version avec && (PowerShell 7)
pwsh -c "cd 'C:/Users/bazydlo/codes/java/demo/quarkus-demo/scrapbook/articles' && pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows-v2.docx --toc --number-sections"
```

### **Version compatible PowerShell 5.1**
```powershell
# Version actuelle de votre terminal
cd "C:/Users/bazydlo/codes/java/demo/quarkus-demo/scrapbook/articles"
if ($?) { pandoc scoop-gestionnaire-windows.md -o scoop-gestionnaire-windows-v2.docx --toc --number-sections }
```

## 🎯 Changement de terminal par défaut (Optionnel)

### **Méthode 1 : Via Windows Terminal**
1. Ouvrir Windows Terminal
2. Paramètres → Profil par défaut → PowerShell 7
3. Redémarrer Windows Terminal

### **Méthode 2 : Alias dans le profil actuel**
```powershell
# Ajouter à votre profil PowerShell 5.1
Add-Content $PROFILE 'function ps7 { pwsh }'
```

## 💡 Bonnes pratiques

### **Quand utiliser PowerShell 7**
- ✅ Scripts personnels de développement
- ✅ Automatisations locales
- ✅ Compatibilité avec syntaxes Linux/macOS
- ✅ Fonctionnalités modernes (.NET Core)

### **Quand rester sur PowerShell 5.1**
- ✅ Scripts d'entreprise partagés
- ✅ Intégration avec systèmes anciens
- ✅ Modules Windows spécifiques
- ✅ Compatibilité garantie Windows

## 🚀 Test immédiat

Testez maintenant avec cette commande PowerShell 7 :
```powershell
pwsh -c "Write-Host '🎉 PowerShell 7 fonctionne' && Write-Host '✅ Support && activé'"
```

---

*Désormais, vous avez le choix entre les deux versions selon vos besoins. PowerShell 7 pour la modernité, PowerShell 5.1 pour la compatibilité !*
