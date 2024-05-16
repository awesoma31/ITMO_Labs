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
        };
    }
}
