package com.revplay.util;

public class UIUtils {

    public static void printLogo() {
        System.out.println(ConsoleColors.CYAN_BOLD +
                "  _____             _____  _               \n" +
                " |  __ \\           |  __ \\| |              \n" +
                " | |__) |_____   __| |__) | | __ _ _   _   \n" +
                " |  _  // _ \\ \\ / /|  ___/| |/ _` | | | |  \n" +
                " | | \\ \\  __/\\ V / | |    | | (_| | |_| |  \n" +
                " |_|  \\_\\___| \\_/  |_|    |_|\\__,_|\\__, |  \n" +
                "                                    __/ |  \n" +
                "                                   |___/   " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "   >> Premium Music Streaming Experience <<" + ConsoleColors.RESET);
        System.out.println();
    }

    public static void printHeader(String title) {
        String border = "=".repeat(Math.max(30, title.length() + 10));
        System.out.println("\n" + ConsoleColors.BLUE_BOLD + border);
        System.out.println("     " + title.toUpperCase());
        System.out.println(border + ConsoleColors.RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(ConsoleColors.GREEN_BOLD + "✅ " + message + ConsoleColors.RESET);
    }

    public static void printError(String message) {
        System.out.println(ConsoleColors.RED_BOLD + "❌ " + message + ConsoleColors.RESET);
    }

    public static void printInfo(String message) {
        System.out.println(ConsoleColors.YELLOW + "ℹ️  " + message + ConsoleColors.RESET);
    }

    public static void clearConsole() {
        // Simple ANSI clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
