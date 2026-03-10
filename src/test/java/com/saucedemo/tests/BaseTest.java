package com.saucedemo.tests;

import com.saucedemo.config.WebDriverConfig;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe de base abstraite pour tous les tests Selenium.
 * <p>
 * Gère le cycle de vie du WebDriver (création avant chaque test,
 * fermeture et capture d'écran en cas d'échec après chaque test).
 * </p>
 */
public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    /** WebDriver partagé entre les méthodes de test d'une même instance. */
    protected WebDriver driver;

    /** Page Object de la page de login. */
    protected LoginPage loginPage;

    // -------------------------------------------------------------------------
    // Cycle de vie JUnit 5
    // -------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        log.info("═══════════════════════════════════════════════════");
        log.info("▶ Démarrage du test : {}", "LoginOK");
        log.info("═══════════════════════════════════════════════════");

        driver    = WebDriverConfig.createDriver();
        loginPage = new LoginPage(driver);

        // Navigation vers la page de login
        driver.get(WebDriverConfig.BASE_URL);
        log.info("Navigation vers : {}", WebDriverConfig.BASE_URL);
    }

    @AfterEach
    void tearDown() {
        log.info("───────────────────────────────────────────────────");
        log.info("■ Fin du test : {}", "Fin LoginOK");

        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver fermé.");
            } catch (Exception e) {
                log.warn("Erreur lors de la fermeture du driver : {}", e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Utilitaires de test
    // -------------------------------------------------------------------------

    /**
     * Prend une capture d'écran et la sauvegarde dans target/screenshots/.
     *
     * @param testName nom du test (utilisé dans le nom du fichier)
     */
    protected void takeScreenshot(String testName) {
        if (driver instanceof TakesScreenshot ts) {
            try {
                byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
                String timestamp  = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filename   = "screenshot_" + testName + "_" + timestamp + ".png";

                Path screenshotDir = Paths.get("target", "screenshots");
                Files.createDirectories(screenshotDir);

                Path filePath = screenshotDir.resolve(filename);
                Files.write(filePath, screenshot);
                log.info("Screenshot sauvegardé : {}", filePath.toAbsolutePath());
            } catch (IOException e) {
                log.warn("Impossible de sauvegarder le screenshot : {}", e.getMessage());
            }
        }
    }
}
