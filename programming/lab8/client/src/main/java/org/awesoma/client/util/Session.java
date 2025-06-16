package org.awesoma.client.util;

public class Session {
    public static String currentUser = null;
    public static String currentLanguage = "English";

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(String currentUser) {
        Session.currentUser = currentUser;
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(String currentLanguage) {
        Session.currentLanguage = currentLanguage;
    }
}
