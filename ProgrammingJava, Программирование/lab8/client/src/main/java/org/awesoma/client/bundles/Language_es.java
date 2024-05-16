package org.awesoma.client.bundles;

import java.util.ListResourceBundle;

public class Language_es extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"login", "Iniciar sesión"},
                {"password", "Contraseña"},
                {"loginButton", "Iniciar sesión"},
                {"cancelButton", "Cancelar"},
                {"registerButton", "Registrarse"},
                {"language", "Idioma"},
                {"register", "Registrarse"},
                {"error", "Error"},
                {"info", "Info"},
                {"pleaseEnterLoginAndPassword", "Por favor ingrese su login y contraseña:"},
                {"pleaseEnterLogin", "Por favor ingrese su login:"},
                {"pleaseEnterPassword", "Por favor ingrese su contraseña:"},
                {"registerSuggestion", "Si no tienes cuenta, puedes registrarte aquí:"},

                {"executeScript", "Ejecutar script"},
                {"clear", "Borrar"},
                {"help", "Ayuda"},
                {"add", "Añadir"},
                {"addIfMax", "Añadir (si máximo)"},
                {"info", "Info"},
                {"removeById", "Eliminar (por ID)"},
                {"removeAt", "Eliminar (por posición)"},
                {"update", "Actualizar"},
                {"logOut", "Cerrar sesión"},
                {"exit", "Salir"},

                {"id", "ID"},
                {"name", "Nombre"},
                {"owner", "Propietario"},
                {"creationDate", "Fecha de creación"},
                {"operator", "Operador"},
                {"genre", "Género"},
                {"oscarsCount", "Cantidad de oscar"},
                {"totalBoxOffice", "Total box office"},
                {"language", "Idioma"},
        };
    }
}
