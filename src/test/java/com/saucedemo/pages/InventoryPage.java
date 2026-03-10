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
import java.util.List;

/**
 * Page Object Model pour la page d'inventaire (post-connexion).
 * Utilisée pour vérifier l'état de la session après authentification.
 */
public class InventoryPage {

    private static final Logger log = LoggerFactory.getLogger(InventoryPage.class);
    private static final int WAIT_SECONDS = 10;

    private final WebDriver     driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Éléments de la page
    // -------------------------------------------------------------------------

    @FindBy(id = "react-burger-menu-btn")
    private WebElement burgerMenuButton;

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(id = "shopping_cart_container")
    private WebElement shoppingCart;

    // -------------------------------------------------------------------------
    // Constructeur
    // -------------------------------------------------------------------------

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    // -------------------------------------------------------------------------
    // Vérifications
    // -------------------------------------------------------------------------

    /**
     * Vérifie que la page d'inventaire est bien chargée.
     */
    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/inventory.html"));
            wait.until(ExpectedConditions.visibilityOf(burgerMenuButton));
            log.info("Page inventaire chargée – URL : {}", driver.getCurrentUrl());
            return true;
        } catch (Exception e) {
            log.warn("Page inventaire non chargée.");
            return false;
        }
    }

    /**
     * Retourne le titre de la page d'inventaire.
     */
    public String getPageTitle() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Retourne le nombre de produits affichés.
     */
    public int getInventoryItemCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(inventoryItems));
            return inventoryItems.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Retourne l'URL courante.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Vérifie la présence du panier (shopping cart).
     */
    public boolean isShoppingCartVisible() {
        try {
            return shoppingCart.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Déconnecte l'utilisateur via le menu burger.
     */
    public void logout() {
        try {
            burgerMenuButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link")));
            driver.findElement(By.id("logout_sidebar_link")).click();
            log.info("Déconnexion effectuée.");
        } catch (Exception e) {
            log.warn("Impossible de se déconnecter : {}", e.getMessage());
        }
    }
}
