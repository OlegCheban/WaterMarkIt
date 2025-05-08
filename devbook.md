# Journal de développement - WaterMarkIt

## Objectif
Implémenter la détection automatique du type d'image dans `ImageConverter.java` pour résoudre l'issue #37.

## Étapes du projet

### 1. Configuration initiale
- [x] Fork du dépôt
- [x] Clone local
- [x] Configuration de l'upstream
- [x] Configuration de l'environnement de développement

### 2. Analyse du code
- [x] Lecture du README.md
- [x] Vérification des dépendances dans pom.xml
- [x] Localisation de ImageConverter.java
- [x] Analyse des méthodes à modifier

### 3. Développement
- [x] Création de la branche de développement
- [x] Implémentation de la détection automatique
- [x] Tests unitaires
- [x] Vérification de la compilation

### 4. Soumission
- [x] Commit des modifications
- [x] Push vers le fork
- [x] Création de la Pull Request
- [ ] Réponse aux commentaires

## Notes et décisions techniques

### Dépendances importantes
- Java 11+
- Apache PDFBox 2.0.32
- JAI Image I/O Core 1.4.0
- JAI Image I/O JPEG2000 1.3.0
- JBIG2 Image I/O 3.0.3

### Structure du projet
- Package principal : `com.markit`
- Classe à modifier : `com.markit.image.ImageConverter`
- Type d'image : `com.markit.api.ImageType`

### Plan d'implémentation
1. Ajouter une méthode pour détecter le type d'image à partir d'un byte[]
2. Ajouter une méthode pour détecter le type d'image à partir d'un File
3. Modifier les méthodes existantes pour utiliser la détection automatique
4. Ajouter la validation avec ImageWriterSpi

## Problèmes rencontrés et solutions

### 1. Problème d'interopérabilité Java-Kotlin
- **Problème** : Les classes d'exceptions Kotlin n'étaient pas correctement importées dans le code Java
- **Solution** : Modification du `pom.xml` pour compiler Kotlin avant Java et ajustement des phases de compilation

### 2. Validation du type d'image
- **Problème** : Utilisation incorrecte de `ImageIO.getImageWriters()`
- **Solution** : 
  - Utilisation de `ImageIO.getImageReaders()` pour la détection du format
  - Utilisation de `ImageIO.getImageWritersByFormatName()` pour la validation
  - Séparation de la détection et de la validation en deux méthodes distinctes

### 3. Formats d'image supportés
- **Problème** : Confusion sur les formats supportés
- **Solution** : Vérification de l'énumération `ImageType` qui définit les formats supportés :
  - PNG
  - JPEG
  - TIFF
  - BMP

### 4. Tests
- **Problème** : Besoin de tests manuels pour valider la fonctionnalité
- **Solution** : 
  - Ajout de tests unitaires complets
  - Création d'un test manuel `ImageConverterManualTest` pour validation pratique
  - Tests couvrant tous les formats supportés et les cas d'erreur 