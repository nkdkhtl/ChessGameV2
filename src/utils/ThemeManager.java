package utils;

public class ThemeManager {
    private static String selectedTheme = "classic"; // Default theme

    public static void setTheme(String theme) {
        selectedTheme = theme;
    }

    public static String getTheme() {
        return selectedTheme;
    }
}