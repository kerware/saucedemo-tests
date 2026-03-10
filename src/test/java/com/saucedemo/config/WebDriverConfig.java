package com.saucedemo.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Factory et configuration du WebDriver.
 * Gère l'instanciation selon le navigateur cible et le mode headless.
 */
public class WebDriverConfig {

    private static final Logger log = LoggerFactory.getLogger(WebDriverConfig.class);

    // Constantes de configuration
    public static final String BASE_URL      = System.getProperty("base.url", "https://www.saucedemo.com");
    public static final String BROWSER       = System.getProperty("browser", "chrome");
    public static final boolean HEADLESS     = Boolean.parseBoolean(System.getProperty("headless", "true"));
    public static final int IMPLICIT_WAIT    = 5;   // secondes
    public static final int PAGE_LOAD_TIMEOUT = 30; // secondes

    private WebDriverConfig() {
        // Classe utilitaire – pas d'instanciation
    }

    /**
     * Crée et configure un WebDriver selon les propriétés système.
     *
     * @return un WebDriver prêt à l'emploi
     */
    public static WebDriver createDriver() {
        log.info("Création du WebDriver → browser={}, headless={}", BROWSER, HEADLESS);

        WebDriver driver = switch (BROWSER.toLowerCase()) {
            case "firefox" -> createFirefoxDriver();
            default        -> createChromeDriver();
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
        driver.manage().window().maximize();

        log.info("WebDriver créé avec succès.");
        return driver;
    }

    // -------------------------------------------------------------------------
    // Implémentations privées
    // -------------------------------------------------------------------------

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (HEADLESS) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1920,1080",
            "--remote-allow-origins=*"
        );
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        if (HEADLESS) {
            options.addArguments("--headless");
        }
        return new FirefoxDriver(options);
    }
}
