package com.saucedemo.tests;

import com.saucedemo.config.TestCredentials;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests paramétrés utilisant l'enum {@link TestCredentials}.
 * <p>
 * Alternative à {@link LoginParameterizedTest} basée sur {@link EnumSource}.
 * Permet une meilleure maintenabilité : ajouter un scénario = ajouter une valeur à l'enum.
 * </p>
 */
@Tag("enum-parametrized")
@Tag("login")
@DisplayName("Tests paramétrés via EnumSource – tous les credentials")
class LoginEnumParameterizedTest extends BaseTest {

    /**
     * Teste TOUS les scénarios définis dans l'enum {@link TestCredentials}.
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @EnumSource(TestCredentials.class)
    @DisplayName("Scénario d'authentification")
    void testLoginWithAllCredentials(TestCredentials credentials) {
        log.info("▶ {}", credentials);

        // ── Action ──────────────────────────────────────────────────────────
        loginPage.login(credentials.getUsername(), credentials.getPassword());

        // ── Assertions ──────────────────────────────────────────────────────
        if (credentials.isExpectSuccess()) {
            assertSuccessfulLogin(credentials);
        } else {
            assertFailedLogin(credentials);
        }
    }

    /**
     * Teste uniquement les comptes qui doivent réussir la connexion.
     */
    @ParameterizedTest(name = "[{index}] ✅ Succès attendu : {0}")
    @EnumSource(
        value = TestCredentials.class,
        names = {
            "STANDARD_USER",
            "PROBLEM_USER",
            "PERFORMANCE_GLITCH_USER",
            "ERROR_USER",
            "VISUAL_USER"
        }
    )
    @DisplayName("Comptes valides : connexion réussie")
    void testValidAccountsLogin(TestCredentials credentials) {
        loginPage.login(credentials.getUsername(), credentials.getPassword());

        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(
            inventoryPage.isLoaded(),
            () -> String.format(
                "Le compte '%s' devrait permettre la connexion. URL : %s",
                credentials.getUsername(), driver.getCurrentUrl()
            )
        );
    }

    /**
     * Teste uniquement les comptes qui doivent échouer la connexion.
     */
    @ParameterizedTest(name = "[{index}] ❌ Échec attendu : {0}")
    @EnumSource(
        value = TestCredentials.class,
        names = {
            "LOCKED_OUT_USER",
            "INVALID_PASSWORD",
            "EMPTY_USERNAME",
            "EMPTY_PASSWORD",
            "EMPTY_CREDENTIALS",
            "UNKNOWN_USER"
        }
    )
    @DisplayName("Comptes invalides : connexion refusée")
    void testInvalidAccountsLoginFails(TestCredentials credentials) {
        loginPage.login(credentials.getUsername(), credentials.getPassword());

        assertFalse(
            loginPage.isLoginSuccessful(),
            () -> String.format(
                "Le compte '%s' ne devrait PAS permettre la connexion.",
                credentials.getUsername()
            )
        );

        assertTrue(
            loginPage.isErrorDisplayed(),
            () -> String.format(
                "Un message d'erreur doit être affiché pour '%s'.",
                credentials.getUsername()
            )
        );

        // Vérification du message d'erreur attendu
        String expected = credentials.getExpectedErrorMessage();
        if (expected != null && !expected.isBlank()) {
            String actual = loginPage.getErrorMessage();
            assertTrue(
                actual.contains(expected),
                () -> String.format(
                    "Message d'erreur incorrect pour '%s'.%nAttendu : '%s'%nObtenu   : '%s'",
                    credentials.getUsername(), expected, actual
                )
            );
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void assertSuccessfulLogin(TestCredentials credentials) {
        InventoryPage inventoryPage = new InventoryPage(driver);
        assertTrue(
            inventoryPage.isLoaded(),
            () -> String.format("[%s] Page inventaire non chargée. URL : %s",
                credentials, driver.getCurrentUrl())
        );
        log.info("✅ Connexion réussie : {}", credentials.getUsername());
    }

    private void assertFailedLogin(TestCredentials credentials) {
        assertFalse(
            loginPage.isLoginSuccessful(),
            () -> "[" + credentials + "] La connexion ne devrait pas aboutir."
        );

        assertTrue(
            loginPage.isErrorDisplayed(),
            () -> "[" + credentials + "] Un message d'erreur devrait être affiché."
        );

        String expectedError = credentials.getExpectedErrorMessage();
        if (expectedError != null && !expectedError.isBlank()) {
            String actualError = loginPage.getErrorMessage();
            assertTrue(
                actualError.contains(expectedError),
                () -> String.format(
                    "[%s] Message d'erreur incorrect.%nAttendu : '%s'%nObtenu   : '%s'",
                    credentials, expectedError, actualError
                )
            );
        }

        log.info("✅ Échec confirmé : {} | Erreur : '{}'",
            credentials.getUsername(), loginPage.getErrorMessage());
    }
}
