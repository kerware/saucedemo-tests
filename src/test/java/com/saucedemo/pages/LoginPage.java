package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Page Object Model (POM) pour la page de connexion SauceDemo.
 * <p>
 * Encapsule tous les sélecteurs et les interactions avec la page de login,
 * conformément au pattern Page Object recommandé avec Selenium 4.
 * </p>
 */
public class LoginPage {

    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    private static final int EXPLICIT_WAIT_SECONDS = 10;

    private final WebDriver      driver;
    private final WebDriverWait  wait;

    // -------------------------------------------------------------------------
    // Éléments de la page via @FindBy (PageFactory)
    // -------------------------------------------------------------------------

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = ".error-message-container h3")
    private WebElement errorMessage;

    // -------------------------------------------------------------------------
    // Constructeur
    // -------------------------------------------------------------------------

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    /**
     * Saisit le nom d'utilisateur dans le champ Username.
     *
     * @param username valeur à saisir
     * @return l'instance courante (fluent API)
     */
    public LoginPage enterUsername(String username) {
        log.debug("Saisie du username : '{}'", username);
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        return this;
    }

    /**
     * Saisit le mot de passe dans le champ Password.
     *
     * @param password valeur à saisir
     * @return l'instance courante (fluent API)
     */
    public LoginPage enterPassword(String password) {
        log.debug("Saisie du password");
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    /**
     * Clique sur le bouton Login.
     *
     * @return l'instance courante (fluent API)
     */
    public LoginPage clickLoginButton() {
        log.debug("Clic sur le bouton Login");
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
        return this;
    }

    /**
     * Enchaîne : saisie des credentials + clic Login.
     *
     * @param username nom d'utilisateur
     * @param password mot de passe
     */
    public void login(String username, String password) {
        log.info("Tentative de connexion → username='{}', password='[PROTECTED]'", username);
        enterUsername(username)
            .enterPassword(password)
            .clickLoginButton();
    }

    // -------------------------------------------------------------------------
    // Assertions / Vérifications
    // -------------------------------------------------------------------------

    /**
     * Vérifie si la connexion a abouti en testant la présence du bouton burger
     * (présent uniquement sur la page d'inventaire post-connexion).
     *
     * @return true si l'utilisateur est redirigé vers /inventory.html
     */
    public boolean isLoginSuccessful() {
        try {
            wait.until(ExpectedConditions.urlContains("/inventory.html"));
            boolean success = driver.getCurrentUrl().contains("/inventory.html");
            log.info("Connexion réussie : {}", success);
            return success;
        } catch (Exception e) {
            log.info("Connexion échouée – toujours sur la page de login.");
            return false;
        }
    }

    /**
     * Vérifie si un message d'erreur est affiché.
     *
     * @return true si le conteneur d'erreur est visible et non vide
     */
    public boolean isErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed() && !errorMessage.getText().isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retourne le texte du message d'erreur affiché.
     *
     * @return texte de l'erreur, ou chaîne vide si aucun message
     */
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            String text = errorMessage.getText();
            log.debug("Message d'erreur lu : '{}'", text);
            return text;
        } catch (Exception e) {
            log.debug("Aucun message d'erreur trouvé.");
            return "";
        }
    }

    /**
     * Vérifie que le champ username possède la classe CSS d'erreur.
     */
    public boolean isUsernameFieldInError() {
        return hasErrorClass(usernameInput);
    }

    /**
     * Vérifie que le champ password possède la classe CSS d'erreur.
     */
    public boolean isPasswordFieldInError() {
        return hasErrorClass(passwordInput);
    }

    /**
     * Retourne la valeur actuelle du champ username.
     */
    public String getUsernameFieldValue() {
        return usernameInput.getAttribute("value");
    }

    /**
     * Retourne la valeur actuelle du champ password.
     */
    public String getPasswordFieldValue() {
        return passwordInput.getAttribute("value");
    }

    /**
     * Vérifie que la page de login est chargée (présence du logo).
     */
    public boolean isPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".login_logo")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retourne le titre du logo affiché sur la page.
     */
    public String getLogoText() {
        return driver.findElement(By.cssSelector(".login_logo")).getText();
    }

    // -------------------------------------------------------------------------
    // Helpers privés
    // -------------------------------------------------------------------------

    private boolean hasErrorClass(WebElement element) {
        String classes = element.getAttribute("class");
        return classes != null && classes.contains("input_error");
    }
}
