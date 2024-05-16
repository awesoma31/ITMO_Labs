package org.awesoma.client.bundles;

import java.util.ListResourceBundle;

public class Language_nl extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"login", "Log ind"},
                {"password", "Adgangskode"},
                {"loginButton", "Log ind"},
                {"cancelButton", "Annuller"},
                {"registerButton", "Registrer"},
                {"language", "Sprog"},
                {"register", "Registrer"},
                {"error", "Fejl"},
                {"info", "Info"},
                {"pleaseEnterLoginAndPassword", "Vul uw login en wachtwoord in:"},
                {"pleaseEnterLogin", "Vul uw login in:"},
                {"pleaseEnterPassword", "Vul uw wachtwoord in:"},
                {"registerSuggestion", "Als u nog geen account hebt, kunt u hier registreren:"},

                {"executeScript", "Script uitvoeren"},
                {"clear", "Wissen"},
                {"help", "Help"},
                {"add", "Toevoegen"},
                {"addIfMax", "Toevoegen (als max)"},
                {"info", "Info"},
                {"removeById", "Verwijderen (op ID)"},
                {"removeAt", "Verwijderen (op positie)"},
                {"update", "Bijwerken"},
                {"logOut", "Uitloggen"},
                {"exit", "Afsluiten"},

                {"id", "ID"},
                {"name", "Naam"},
                {"owner", "Eigenaar"},
                {"creationDate", "Aanmaakdatum"},
                {"operator", "Bestuurder"},
                {"genre", "Genre"},
                {"oscarsCount", "Oscars-aantal"},
                {"totalBoxOffice", "Totale box office"},
                {"language", "Taal"},
        };
    }
}
