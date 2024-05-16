package org.awesoma.client.bundles;

import java.util.ListResourceBundle;

public class Language_ru extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"login", "Логин"},
                {"password", "Пароль"},
                {"loginButton", "Войти"},
                {"cancelButton", "Отмена"},
                {"registerButton", "Регистрация"},
                {"language", "Язык"},
                {"register", "Регистрация"},
                {"error", "Ошибка"},
                {"info", "Информация"},
                {"pleaseEnterLoginAndPassword", "Пожалуйста, введите логин и пароль:"},
                {"pleaseEnterLogin", "Пожалуйста, введите логин:"},
                {"pleaseEnterPassword", "Пожалуйста, введите пароль:"},
                {"registerSuggestion", "Если у вас нет аккаунта, вы можете зарегистрироваться здесь:"},

                {"executeScript", "Выполнить скрипт"},
                {"clear", "Очистить"},
                {"help", "Помощь"},
                {"add", "Добавить"},
                {"addIfMax", "Добавить (если макс)"},
                {"info", "Информация"},
                {"removeById", "Удалить (по ID)"},
                {"removeAt", "Удалить (по позиции)"},
                {"update", "Обновить элемент"},
                {"logOut", "Выйти"},
                {"exit", "Закрыть"},

                {"id", "ID"},
                {"name", "Имя"},
                {"owner", "Владелец"},
                {"creationDate", "Дата создания"},
                {"operator", "Оператор"},
                {"genre", "Жанр"},
                {"oscarsCount", "Количество oscars"},
                {"totalBoxOffice", "Общий бакс-офис"},
                {"language", "Язык"},
        };
    }
}
