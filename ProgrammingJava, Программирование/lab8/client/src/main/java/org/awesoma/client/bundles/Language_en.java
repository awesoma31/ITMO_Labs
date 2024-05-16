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
        };
    }
}
