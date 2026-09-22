package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginOK extends BaseTest {


    @Test
    public void testStandardUser() {
        LoginPage page = new LoginPage( driver );
        page.login("standard_user","secret_sauce");
        InventoryPage productsPage = new InventoryPage( driver );
        assertTrue(productsPage.isLoaded());
    }
}
