package com.revplay.ui;
import com.revplay.model.User;
import com.revplay.service.UserService;
import com.revplay.util.InputValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Scanner;
import java.util.function.Predicate;
public class MenuHandler {
    private static final Logger logger = LogManager.getLogger(MenuHandler.class);
    private Scanner scanner = new Scanner(System.in);
    private UserService userService = new UserService();
    private User currentUser = null;

    public void start() {
        while (true) {
            try {
                if (currentUser == null) {
                    showMainMenu();
                } else if ("ARTIST".equals(currentUser.getUserType())) {
                    new ArtistMenu(scanner, currentUser, this).show();
                } else {
                    new UserMenu(scanner, currentUser, this).show();
                }
            } catch (Exception e) {
                logger.error("Error in menu", e);
                System.out.println("An error occurred. Please try again.");
            }
        }
    }

    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return -1;
        }
    }

    private void showMainMenu() {
        System.out.println("\n========== REVPLAY ==========");
        System.out.println("1. Register as Listener");
        System.out.println("2. Register as Artist");
        System.out.println("3. Login");
        System.out.println("4. Forgot Password");
        System.out.println("0. Exit");
        System.out.print("Choice: ");

        int choice = getChoice();
        switch (choice) {
            case 1 -> register("LISTENER");
            case 2 -> register("ARTIST");
            case 3 -> login();
            case 4 -> forgotPassword();
            case 0 -> { System.out.println("Goodbye!"); System.exit(0); }
            default -> System.out.println("Invalid choice. Try again.");
        }
    }

    private void register(String userType) {
        try {
            String email = promptForInput("Email: ",
                    InputValidator::isValidEmail,
                    "Invalid email format! Please try again.");

            String username = promptForInput("Username (3+ alphanumeric chars): ",
                    InputValidator::isValidUsername,
                    "Invalid username! Must be at least 3 chars and alphanumeric.");

            String password = promptForInput("Password (min 6 chars): ",
                    InputValidator::isValidPassword,
                    "Invalid password! Must be at least 6 characters.");

            System.out.print("Security Question: ");
            String secQ = scanner.nextLine().trim();
            while (secQ.isEmpty()) {
                System.out.println("Security question cannot be empty.");
                System.out.print("Security Question: ");
                secQ = scanner.nextLine().trim();
            }

            System.out.print("Security Answer: ");
            String secA = scanner.nextLine().trim();
            while (secA.isEmpty()) {
                System.out.println("Security answer cannot be empty.");
                System.out.print("Security Answer: ");
                secA = scanner.nextLine().trim();
            }

            User user = userService.register(email, password, username, userType, secQ, secA);
            if (user != null) {
                System.out.println("Registration successful! Please login.");
                if ("ARTIST".equals(userType)) {
                    System.out.print("Bio: ");
                    String bio = scanner.nextLine();
                    System.out.print("Genre: ");
                    String genre = scanner.nextLine();
                    userService.createArtistProfile(user.getUserId(), bio, genre, "");
                }
            } else {
                System.out.println("Registration failed! Email or username might already exist.");
            }
        } catch (Exception e) {
            logger.error("Registration error", e);
            System.out.println("Registration error. Please try again.");
        }
    }

    private String promptForInput(String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    private void login() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            currentUser = userService.login(email, password);
            if (currentUser != null) {
                System.out.println("Welcome, " + currentUser.getUsername() + "!");
            } else {
                System.out.println("Invalid credentials!");
            }
        } catch (Exception e) {
            logger.error("Login error", e);
            System.out.println("Login error. Please try again.");
        }
    }

    private void forgotPassword() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            String question = userService.recoverPassword(email);
            if (question != null) {
                System.out.println("Security Question: " + question);
                System.out.print("Answer: ");
                String answer = scanner.nextLine();
                System.out.print("New Password: ");
                String newPass = scanner.nextLine();
                if (userService.resetPassword(email, answer, newPass)) {
                    System.out.println("Password reset successful!");
                } else {
                    System.out.println("Incorrect answer!");
                }
            } else {
                System.out.println("Email not found!");
            }
        } catch (Exception e) {
            logger.error("Password recovery error", e);
            System.out.println("Error during password recovery.");
        }
    }

    public void logout() { currentUser = null; }
}