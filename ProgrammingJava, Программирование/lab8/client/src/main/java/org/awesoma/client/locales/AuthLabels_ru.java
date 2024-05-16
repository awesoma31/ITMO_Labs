package org.awesoma.client.locales;

import java.util.ListResourceBundle;

public class AuthLabels_ru extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"login", "Логин"},
                {"password", "Пароль"},
                {"loginButton", "Войти"},
                {"cancelButton", "Отмена"}
        };
    }
}
