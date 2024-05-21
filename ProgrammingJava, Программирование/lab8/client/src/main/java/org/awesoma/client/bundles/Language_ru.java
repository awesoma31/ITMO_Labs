package org.awesoma.client.bundles;

import org.awesoma.common.commands.AddCommand;
import org.awesoma.common.commands.AddIfMaxCommand;
import org.awesoma.common.commands.RemoveByIdCommand;

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

                {"opBirthdayLabel", "День рождения оператора"},
                {"opNameLabel", "Имя оператора"},
                {"opNameTextField", "Введите имя:"},
                {"opBirthdayTextField", "Введите день рождения:"},
                {"ocLabel", "Бакс-офис"},
                {"ocTextField", "Введите бакс-офис:"},
                {"genreLabel", "Жанр"},
                {"genreTextField", "Введите жанр:"},
                {"coordinatesLabel", "Координаты"},
                {"coordXLabel", "X координата"},
                {"coordXTextField", "Введите X координату:"},
                {"coordYLabel", "Y координата"},
                {"coordYTextField", "Введите Y координату:"},
                {"operatorLabel", "Оператор"},
                {"operatorNameLabel", "Имя оператора"},
                {"operatorNameTextField", "Введите имя:"},
                {"creationDateLabel", "Дата создания"},
                {"creationDateTextField", "Введите дату создания:"},
                {"colorMenuBar", "Меню цвета"},
                {"redColorMenuItem", "Красный"},
                {"blackColorMenuItem", "Чёрный"},
                {"yellowColorMenuItem", "Жёлтый"},
                {"orangeColorMenuItem", "Оранжевый"},
                {"whiteColorMenuItem", "Белый"},
                {"ukCountryMenuItem", "Великобритания"},
                {"germanyCountryMenuItem", "Германия"},
                {"franceCountryMenuItem", "Франция"},
                {"noneCountryMenuItem1", "Нет"},
                {"cancelButton", "Отмена"},
                {"okButton", "OK"},
                {"countryMenuBar", "Меню страны"},
                {"noneCountryMenuItem1", "Нет"},

                {"User:", "Пользователь:"},
                {"Update", "Обновить"},
                {"WrongOwnerException", "Не владелец фильма"},
                {"NumberFormatException", "Неверное число"},
                {"NotFoundException", "Фильм не найден"},
                {"IOException", "Ошибка чтения файла"},
                {"Can't read file", "Не удается прочитать файл"},
                {"ERROR", "ОШИБКА"},
                {"OK", "OK"},
                {"Cancel", "Отмена"},
                {"Yes", "Да"},
                {"WARNING", "ПРЕДУПРЕЖДЕНИЕ"},

                {"Movie", "Фильм"},
                {"Operator", "Оператор"},
                {"Genre", "Жанр"},
                {"Box office", "Бакс-офис"},
                {"Oscars count", "Количество oscars"},
                {"Creation date", "Дата создания"},
                {"Birthday", "День рождения"},
                {"Weight", "Вес"},

                {"Eye color", "Цвет глаз"},
                {"Nationality", "Национальность"},

                {"Update", "Обновить"},
                {"Remove", "Удалить"},
                {"Add", "Добавить"},
                {"Add (if max)", "Добавить (если макс)"},
                {"Update by ID", "Обновить по ID"},
                {"Remove by ID", "Удалить по ID"},
                {"Remove at", "Удалить по позиции"},

                {"FileChooserTitle", "Выбрать файл"},

                {new AddCommand().getName(), "Добавить"},
                {new AddIfMaxCommand().getName(), "Добавить (если макс)"},
                {new AddIfMaxCommand().getDescription(), "Добавить элемент, если его бакс-офис макс"},
                {new RemoveByIdCommand().getName(), "Удалить по ID"},
                {new RemoveByIdCommand().getDescription(), "Удалить элемент по ID"},

                {"delete", "Удалить"},
        };
    }
}
