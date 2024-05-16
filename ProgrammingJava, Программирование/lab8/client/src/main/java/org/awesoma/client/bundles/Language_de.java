package org.awesoma.client.bundles;

import java.util.ListResourceBundle;

public class Language_de extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"login", "Anmelden"},
                {"password", "Passwort"},
                {"loginButton", "Anmelden"},
                {"cancelButton", "Abbrechen"},
                {"registerButton", "Registrieren"},
                {"language", "Sprache"},
                {"register", "Registrieren"},
                {"error", "Fehler"},
                {"info", "Info"},
                {"pleaseEnterLoginAndPassword", "Bitte geben Sie Ihren Login und Ihr Passwort ein:"},
                {"pleaseEnterLogin", "Bitte geben Sie Ihren Login ein:"},
                {"pleaseEnterPassword", "Bitte geben Sie Ihr Passwort ein:"},
                {"registerSuggestion", "Wenn Sie noch keinen Account haben, können Sie sich hier registrieren:"},

                {"executeScript", "Skript ausführen"},
                {"clear", "Löschen"},
                {"help", "Hilfe"},
                {"add", "Hinzufügen"},
                {"addIfMax", "Hinzufügen (wenn maximal)"},
                {"info", "Info"},
                {"removeById", "Entfernen (nach ID)"},
                {"removeAt", "Entfernen (nach Position)"},
                {"update", "Aktualisieren"},
                {"logOut", "Abmelden"},
                {"exit", "Beenden"},

                {"id", "ID"},
                {"name", "Name"},
                {"owner", "Besitzer"},
                {"creationDate", "Erstellungsdatum"},
                {"operator", "Betreiber"},
                {"genre", "Genre"},
                {"oscarsCount", "Oscars-Anzahl"},
                {"totalBoxOffice", "Total Box Office"},
                {"language", "Sprache"},
        };
    }
}
