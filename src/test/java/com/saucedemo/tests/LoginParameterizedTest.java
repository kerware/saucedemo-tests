package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests paramétrés de la page de connexion SauceDemo.
 * <p>
 * Utilise {@link CsvSource} pour couvrir l'ensemble des cas d'authentification
 * possibles : comptes valides, compte bloqué, identifiants invalides.
 * </p>
 *
 * <p>Format CsvSource : username, password, expectSuccess, expectedError</p>
 */
@Tag("parametrized")
@Tag("login")
@DisplayName("Tests paramétrés de l'authentification SauceDemo")
class LoginParameterizedTest extends BaseTest {

    /**
     * Test paramétré principal couvrant TOUS les scénarios de connexion.
     *
     * <p>Colonnes CSV :</p>
     * <ol>
     *   <li>username       – identifiant saisi</li>
     *   <li>password       – mot de passe saisi</li>
     *   <li>expectSuccess  – true si la connexion doit aboutir</li>
     *   <li>expectedError  – message d'erreur attendu (vide si succès)</li>
     *   <li>description    – libellé affiché dans le rapport</li>
     * </ol>
     */
    @ParameterizedTest(name = "[{index}] {4} → user=''{0}''")
    @CsvSource(delimiter = '|', textBlock = """
        # username                  | password      | expectSuccess | expectedError                                                                   | description
        standard_user               | secret_sauce  | true          |                                                                                 | Utilisateur standard - connexion OK
        locked_out_user             | secret_sauce  | false         | Epic sadface: Sorry, this user has been locked out.                             | Utilisateur bloqué - connexion refusée
        problem_user                | secret_sauce  | true          |                                                                                 | Utilisateur avec bugs UI - connexion OK
        performance_glitch_user     | secret_sauce  | true          |                                                                                 | Utilisateur dégradé en perf - connexion OK
        error_user                  | secret_sauce  | true          |                                                                                 | Utilisateur error - connexion OK
        visual_user                 | secret_sauce  | true          |                                                                                 | Utilisateur visual - connexion OK
        standard_user               | wrong_password| false         | Epic sadface: Username and password do not match any user in this service       | Mauvais mot de passe
        unknown_user                | secret_sauce  | false         | Epic sadface: Username and password do not match any user in this service       | Utilisateur inexistant
                                    | secret_sauce  | false         | Epic sadface: Username is required                                              | Username vide
        standard_user               |               | false         | Epic sadface: Password is required                                              | Password vide
                                    |               | false         | Epic sadface: Username is required                                              | Username et Password vides
        """)
    @DisplayName("Authentification complète – tous les scénarios")
    void testAuthentication(
        String username,
        String password,
        boolean expectSuccess,
        String expectedError,
        String description
    ) {
        log.info("▶ Scénario : {}", description);

        // Normalisation des valeurs nulles (cellules vides en CsvSource)
        String safeUsername = (username == null) ? "" : username.trim();
        String safePassword = (password == null) ? "" : password.trim();

        // ── Action ─────────────────────────────────────────────────────────
        loginPage.login(safeUsername, safePassword);

        // ── Assertions ─────────────────────────────────────────────────────
        if (expectSuccess) {
            assertLoginSuccess(description);
        } else {
            assertLoginFailure(expectedError, description);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers d'assertion
    // -------------------------------------------------------------------------

    private void assertLoginSuccess(String description) {
        InventoryPage inventoryPage = new InventoryPage(driver);

        assertTrue(
            inventoryPage.isLoaded(),
            () -> String.format(
                "[%s] La page d'inventaire devrait être chargée après connexion réussie. URL actuelle : %s",
                description, driver.getCurrentUrl()
            )
        );

        assertTrue(
            inventoryPage.isShoppingCartVisible(),
            () -> String.format("[%s] Le panier devrait être visible après connexion.", description)
        );

        assertEquals(
            "Products", inventoryPage.getPageTitle(),
            () -> String.format("[%s] Le titre de la page devrait être 'Products'.", description)
        );

        assertTrue(
            inventoryPage.getInventoryItemCount() > 0,
            () -> String.format("[%s] Des produits devraient être affichés.", description)
        );

        log.info("✅ Connexion réussie confirmée – {}", description);
    }

    private void assertLoginFailure(String expectedError, String description) {
        assertTrue(
            loginPage.isErrorDisplayed(),
            () -> String.format("[%s] Un message d'erreur devrait être affiché.", description)
        );

        if (expectedError != null && !expectedError.isBlank()) {
            String actualError = loginPage.getErrorMessage();
            assertTrue(
                actualError.contains(expectedError),
                () -> String.format(
                    "[%s] Message d'erreur incorrect.%nAttendu : '%s'%nObtenu   : '%s'",
                    description, expectedError, actualError
                )
            );
        }

        assertFalse(
            loginPage.isLoginSuccessful(),
            () -> String.format("[%s] La connexion ne devrait PAS aboutir.", description)
        );

        log.info("✅ Échec de connexion confirmé – {} | Erreur : '{}'",
            description, loginPage.getErrorMessage());
    }
}
