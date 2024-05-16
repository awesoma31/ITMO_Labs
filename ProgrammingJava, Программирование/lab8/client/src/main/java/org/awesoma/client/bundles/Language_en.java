package org.awesoma.client.bundles;

import java.util.ListResourceBundle;

public class Language_en extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"login", "Login"},
                {"password", "Password"},
                {"loginButton", "Login"},
                {"cancelButton", "Cancel"},
                {"registerButton", "Register"},
                {"language", "Language"},
                {"register", "Register"},
                {"error", "Error"},
                {"info", "Info"},
                {"pleaseEnterLoginAndPassword", "Please enter your login and password:"},
                {"pleaseEnterLogin", "Please enter your login:"},
                {"pleaseEnterPassword", "Please enter your password:"},
                {"registerSuggestion", "If you don't have an account you might want to register here:"},

                {"executeScript", "Execute Script"},
                {"clear", "Clear"},
                {"help", "Help"},
                {"add", "Add"},
                {"addIfMax", "Add (if max)"},
                {"info", "Info"},
                {"removeById", "Remove (by ID)"},
                {"removeAt", "Remove (by position)"},
                {"update", "Update"},
                {"logOut", "Log out"},
                {"exit", "Exit"},

                {"id", "ID"},
                {"name", "Name"},
                {"owner", "Owner"},
                {"creationDate", "Creation date"},
                {"operator", "Operator"},
                {"genre", "Genre"},
                {"oscarsCount", "Oscars count"},
                {"totalBoxOffice", "Total box office"},
                {"language", "Language"},
        };
    }
}
