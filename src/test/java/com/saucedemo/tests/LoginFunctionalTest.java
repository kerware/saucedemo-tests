package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fonctionnels unitaires de la page de connexion.
 * <p>
 * Complète {@link LoginParameterizedTest} en testant des comportements
 * spécifiques de l'UI : style des champs en erreur, état de la page,
 * persistance des valeurs, etc.
 * </p>
 */
@Tag("functional")
@Tag("login")
@DisplayName("Tests fonctionnels de la page de connexion")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginFunctionalTest extends BaseTest {

    // =========================================================================
    // 1. Tests de chargement de la page
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("La page de login est chargée avec le logo 'Swag Labs'")
    void testLoginPageIsLoaded() {
        assertTrue(loginPage.isPageLoaded(), "La page de login doit être accessible");
        assertEquals("Swag Labs", loginPage.getLogoText(), "Le logo doit afficher 'Swag Labs'");
    }

    @Test
    @Order(2)
    @DisplayName("Aucun message d'erreur n'est affiché initialement")
    void testNoErrorOnInitialLoad() {
        assertFalse(loginPage.isErrorDisplayed(),
            "Aucun message d'erreur ne doit être affiché au chargement");
    }

    // =========================================================================
    // 2. Tests des utilisateurs valides
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("standard_user : connexion réussie + page inventaire + 6 produits")
    void testStandardUserLoginSuccess() {
        loginPage.login("standard_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);

        assertTrue(inventoryPage.isLoaded(),
            "La page d'inventaire doit être chargée");
        assertEquals("Products", inventoryPage.getPageTitle(),
            "Le titre doit être 'Products'");
        assertEquals(6, inventoryPage.getInventoryItemCount(),
            "6 produits doivent être affichés");
        assertTrue(inventoryPage.getCurrentUrl().endsWith("/inventory.html"),
            "L'URL doit se terminer par /inventory.html");
    }

    @Test
    @Order(4)
    @DisplayName("performance_glitch_user : connexion lente mais réussie")
    void testPerformanceGlitchUserLogin() {
        long start = System.currentTimeMillis();
        loginPage.login("performance_glitch_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(inventoryPage.isLoaded(),
            "La page d'inventaire doit être chargée malgré la lenteur");

        long duration = System.currentTimeMillis() - start;
        log.info("Durée de connexion performance_glitch_user : {} ms", duration);
        // Pas d'assertion sur la durée (trop variable en CI), on logue seulement
    }

    @Test
    @Order(5)
    @DisplayName("problem_user : connexion OK malgré les bugs UI du catalogue")
    void testProblemUserLogin() {
        loginPage.login("problem_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(inventoryPage.isLoaded(),
            "La page d'inventaire doit être chargée pour problem_user");
    }

    @Test
    @Order(6)
    @DisplayName("error_user : connexion OK")
    void testErrorUserLogin() {
        loginPage.login("error_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(inventoryPage.isLoaded(), "Connexion réussie pour error_user");
    }

    @Test
    @Order(7)
    @DisplayName("visual_user : connexion OK")
    void testVisualUserLogin() {
        loginPage.login("visual_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(inventoryPage.isLoaded(), "Connexion réussie pour visual_user");
    }

    // =========================================================================
    // 3. Tests des utilisateurs invalides / bloqués
    // =========================================================================

    @Test
    @Order(8)
    @DisplayName("locked_out_user : connexion refusée avec message d'erreur spécifique")
    void testLockedOutUserLoginFails() {
        loginPage.login("locked_out_user", "secret_sauce");

        assertTrue(loginPage.isErrorDisplayed(),
            "Un message d'erreur doit être affiché");
        assertTrue(
            loginPage.getErrorMessage()
                     .contains("Sorry, this user has been locked out."),
            "Le message doit indiquer que l'utilisateur est bloqué"
        );
        assertFalse(loginPage.isLoginSuccessful(),
            "La connexion ne doit pas aboutir");
    }

    @Test
    @Order(9)
    @DisplayName("Mauvais mot de passe : message 'do not match'")
    void testWrongPasswordShowsError() {
        loginPage.login("standard_user", "wrong_password");

        assertTrue(loginPage.isErrorDisplayed());
        assertTrue(
            loginPage.getErrorMessage()
                     .contains("Username and password do not match any user in this service")
        );
    }

    @Test
    @Order(10)
    @DisplayName("Utilisateur inconnu : message 'do not match'")
    void testUnknownUserShowsError() {
        loginPage.login("nobody", "secret_sauce");

        assertTrue(loginPage.isErrorDisplayed());
        assertTrue(
            loginPage.getErrorMessage()
                     .contains("Username and password do not match any user in this service")
        );
    }

    // =========================================================================
    // 4. Tests de validation des champs vides
    // =========================================================================

    @Test
    @Order(11)
    @DisplayName("Username vide : message 'Username is required'")
    void testEmptyUsernameShowsRequiredError() {
        loginPage.login("", "secret_sauce");

        assertTrue(loginPage.isErrorDisplayed());
        String error = loginPage.getErrorMessage();
        assertTrue(error.contains("Username is required"),
            () -> "Attendu : 'Username is required' | Obtenu : '" + error + "'");
    }

    @Test
    @Order(12)
    @DisplayName("Password vide : message 'Password is required'")
    void testEmptyPasswordShowsRequiredError() {
        loginPage.login("standard_user", "");

        assertTrue(loginPage.isErrorDisplayed());
        String error = loginPage.getErrorMessage();
        assertTrue(error.contains("Password is required"),
            () -> "Attendu : 'Password is required' | Obtenu : '" + error + "'");
    }

    @Test
    @Order(13)
    @DisplayName("Username ET Password vides : message 'Username is required' en priorité")
    void testBothFieldsEmptyShowsUsernameError() {
        loginPage.login("", "");

        assertTrue(loginPage.isErrorDisplayed());
        assertTrue(loginPage.getErrorMessage().contains("Username is required"));
    }

    // =========================================================================
    // 5. Tests du style des champs en erreur
    // =========================================================================

    @Test
    @Order(14)
    @DisplayName("Les champs affichent la classe CSS d'erreur après un échec")
    void testErrorFieldsHaveErrorClass() {
        loginPage.login("wrong_user", "wrong_pass");

        assertTrue(loginPage.isUsernameFieldInError(),
            "Le champ username doit avoir la classe CSS d'erreur");
        assertTrue(loginPage.isPasswordFieldInError(),
            "Le champ password doit avoir la classe CSS d'erreur");
    }

    // =========================================================================
    // 6. Tests de déconnexion
    // =========================================================================

    @Test
    @Order(15)
    @DisplayName("Déconnexion : retour à la page de login après logout")
    void testLogoutReturnsToLoginPage() {
        loginPage.login("standard_user", "secret_sauce");

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(inventoryPage.isLoaded(), "Doit être sur la page inventaire");

        inventoryPage.logout();

        // Après déconnexion, on doit revenir sur la page de login
        assertTrue(loginPage.isPageLoaded(),
            "Doit retourner sur la page de login après déconnexion");
        assertEquals("Swag Labs", loginPage.getLogoText(),
            "La page de login doit afficher le logo Swag Labs");
    }

    // =========================================================================
    // 7. Tests de sécurité / robustesse
    // =========================================================================

    @Test
    @Order(16)
    @DisplayName("Injection SQL basique ne doit pas authentifier")
    void testSqlInjectionDoesNotAuthenticate() {
        loginPage.login("' OR '1'='1", "' OR '1'='1");

        assertFalse(loginPage.isLoginSuccessful(),
            "Une injection SQL ne doit pas permettre la connexion");
        assertTrue(loginPage.isErrorDisplayed(),
            "Un message d'erreur doit être affiché pour l'injection SQL");
    }

    @Test
    @Order(17)
    @DisplayName("Username avec espaces seuls : considéré vide ou invalide")
    void testWhitespaceOnlyUsername() {
        loginPage.login("   ", "secret_sauce");

        // SauceDemo traite les espaces comme un utilisateur invalide
        assertTrue(loginPage.isErrorDisplayed(),
            "Des espaces seuls en username doivent provoquer une erreur");
    }

    @Test
    @Order(18)
    @DisplayName("Username très long : ne doit pas provoquer de crash")
    void testVeryLongUsername() {
        String longUsername = "a".repeat(500);
        loginPage.login(longUsername, "secret_sauce");

        // On attend une erreur, pas un crash
        assertTrue(loginPage.isErrorDisplayed() || !loginPage.isLoginSuccessful(),
            "Un username trop long doit être rejeté sans crash");
    }
}
