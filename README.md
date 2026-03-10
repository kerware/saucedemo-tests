# SauceDemo – Tests Automatisés Selenium + JUnit 5

Projet Maven de tests d'intégration pour la page d'authentification de
[https://www.saucedemo.com](https://www.saucedemo.com).

## 🏗️ Stack Technique

| Composant          | Version  |
|--------------------|----------|
| Java               | 17+      |
| Selenium Java      | 4.41.0   |
| JUnit Jupiter      | 5.11.0   |
| WebDriverManager   | 5.9.2    |
| Maven Surefire     | 3.3.1    |
| Logback            | 1.5.6    |

## 📁 Structure du Projet

```
saucedemo-selenium-tests/
├── pom.xml
└── src/test/java/com/saucedemo/
    ├── config/
    │   ├── WebDriverConfig.java       # Factory WebDriver + configuration
    │   └── TestCredentials.java       # Enum de tous les credentials de test
    ├── pages/
    │   ├── LoginPage.java             # POM : page de connexion
    │   └── InventoryPage.java         # POM : page d'inventaire (post-login)
    └── tests/
        ├── BaseTest.java              # Classe abstraite : cycle de vie du driver
        ├── LoginParameterizedTest.java  # Tests paramétrés @CsvSource
        ├── LoginEnumParameterizedTest.java  # Tests paramétrés @EnumSource
        └── LoginFunctionalTest.java   # Tests fonctionnels unitaires
```

## 🧪 Scénarios de Test

### `LoginParameterizedTest` – @CsvSource (test exigé)

| # | Username                  | Password       | Résultat attendu |
|---|---------------------------|----------------|-----------------|
| 1 | `standard_user`           | `secret_sauce` | ✅ Connexion OK  |
| 2 | `locked_out_user`         | `secret_sauce` | ❌ Bloqué        |
| 3 | `problem_user`            | `secret_sauce` | ✅ Connexion OK  |
| 4 | `performance_glitch_user` | `secret_sauce` | ✅ Connexion OK  |
| 5 | `error_user`              | `secret_sauce` | ✅ Connexion OK  |
| 6 | `visual_user`             | `secret_sauce` | ✅ Connexion OK  |
| 7 | `standard_user`           | `wrong_password`| ❌ Mauvais mdp  |
| 8 | `unknown_user`            | `secret_sauce` | ❌ Inconnu       |
| 9 | *(vide)*                  | `secret_sauce` | ❌ Username requis |
|10 | `standard_user`           | *(vide)*       | ❌ Password requis |
|11 | *(vide)*                  | *(vide)*       | ❌ Username requis |

### `LoginFunctionalTest` – Tests unitaires fonctionnels (18 tests)

- Chargement de la page et logo
- Connexion réussie pour chaque compte valide
- Vérification des messages d'erreur exacts
- Style CSS des champs en erreur
- Déconnexion et retour à la page de login
- Injection SQL (sécurité)
- Username très long (robustesse)

### `LoginEnumParameterizedTest` – @EnumSource

- Itération sur l'enum `TestCredentials` (11 valeurs)
- Sous-ensembles : comptes valides uniquement / comptes invalides uniquement

## ▶️ Exécution

### Prérequis

- JDK 17+
- Maven 3.8+
- Chrome ou Firefox installé (WebDriverManager télécharge le driver automatiquement)

### Commandes

```bash
# Tous les tests (mode headless Chrome par défaut)
mvn test

# Mode headless explicite (CI/CD)
mvn test -Pheadless

# Mode graphique
mvn test -Dheadless=false

# Firefox
mvn test -Pbrowser=firefox

# Sélectionner une classe de test
mvn test -Dtest=LoginParameterizedTest

# Sélectionner par tag
mvn test -Dgroups=parametrized

# URL personnalisée
mvn test -Dbase.url=https://www.saucedemo.com
```

## 📊 Rapports

Les rapports sont générés dans :

```
target/
├── surefire-reports/   # Rapports XML JUnit (compatible CI/CD)
├── screenshots/        # Captures d'écran (en cas d'échec)
└── logs/
    └── saucedemo-tests.log
```

## 🎯 Patterns Utilisés

- **Page Object Model (POM)** : `LoginPage`, `InventoryPage`
- **PageFactory** : initialisation via `@FindBy`
- **Fluent API** : chaînage des actions sur la page
- **WebDriverManager** : gestion automatique des drivers
- **@ParameterizedTest + @CsvSource** : tests tabulaires lisibles
- **@EnumSource** : alternative orientée objet aux CsvSource
- **BaseTest** : factorisation du cycle de vie (setup/teardown)
- **Profils Maven** : chrome / firefox / headless
