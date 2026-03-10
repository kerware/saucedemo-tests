package com.saucedemo.config;

/**
 * Enumération de tous les comptes SauceDemo avec leur comportement attendu.
 */
public enum TestCredentials {

    // -----------------------------------------------------------------------
    // Comptes valides (authentification réussie)
    // -----------------------------------------------------------------------

    STANDARD_USER(
        "standard_user",
        "secret_sauce",
        true,
        null,
        "Utilisateur standard – accès complet"
    ),

    PERFORMANCE_GLITCH_USER(
        "performance_glitch_user",
        "secret_sauce",
        true,
        null,
        "Utilisateur dégradé en performance – connexion lente mais réussie"
    ),

    PROBLEM_USER(
        "problem_user",
        "secret_sauce",
        true,
        null,
        "Utilisateur avec des bugs UI sur le catalogue"
    ),

    ERROR_USER(
        "error_user",
        "secret_sauce",
        true,
        null,
        "Utilisateur qui génère des erreurs sur certaines actions"
    ),

    VISUAL_USER(
        "visual_user",
        "secret_sauce",
        true,
        null,
        "Utilisateur avec des anomalies visuelles"
    ),

    // -----------------------------------------------------------------------
    // Compte bloqué (authentification échouée)
    // -----------------------------------------------------------------------

    LOCKED_OUT_USER(
        "locked_out_user",
        "secret_sauce",
        false,
        "Epic sadface: Sorry, this user has been locked out.",
        "Utilisateur bloqué – connexion refusée"
    ),

    // -----------------------------------------------------------------------
    // Cas invalides supplémentaires (mauvais mot de passe / login vide)
    // -----------------------------------------------------------------------

    INVALID_PASSWORD(
        "standard_user",
        "wrong_password",
        false,
        "Epic sadface: Username and password do not match any user in this service",
        "Mot de passe incorrect"
    ),

    EMPTY_USERNAME(
        "",
        "secret_sauce",
        false,
        "Epic sadface: Username is required",
        "Username vide"
    ),

    EMPTY_PASSWORD(
        "standard_user",
        "",
        false,
        "Epic sadface: Password is required",
        "Password vide"
    ),

    EMPTY_CREDENTIALS(
        "",
        "",
        false,
        "Epic sadface: Username is required",
        "Username et Password vides"
    ),

    UNKNOWN_USER(
        "unknown_user",
        "secret_sauce",
        false,
        "Epic sadface: Username and password do not match any user in this service",
        "Utilisateur inexistant"
    );

    // -----------------------------------------------------------------------
    // Champs
    // -----------------------------------------------------------------------

    private final String  username;
    private final String  password;
    private final boolean expectSuccess;
    private final String  expectedErrorMessage;
    private final String  description;

    // -----------------------------------------------------------------------
    // Constructeur
    // -----------------------------------------------------------------------

    TestCredentials(
        String username,
        String password,
        boolean expectSuccess,
        String expectedErrorMessage,
        String description
    ) {
        this.username             = username;
        this.password             = password;
        this.expectSuccess        = expectSuccess;
        this.expectedErrorMessage = expectedErrorMessage;
        this.description          = description;
    }

    // -----------------------------------------------------------------------
    // Accesseurs
    // -----------------------------------------------------------------------

    public String  getUsername()             { return username; }
    public String  getPassword()             { return password; }
    public boolean isExpectSuccess()         { return expectSuccess; }
    public String  getExpectedErrorMessage() { return expectedErrorMessage; }
    public String  getDescription()          { return description; }

    @Override
    public String toString() {
        return String.format("[%s] %s", name(), description);
    }
}
