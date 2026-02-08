package com.revplay.service;

import com.revplay.dao.UserDAO;
import com.revplay.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityServiceTest {
    private UserService userService = new UserService();
    private UserDAO userDAO = new UserDAO();

    @Test
    void testForgotPassword_WrongAnswer() {
        String unique = "sec_wrong_" + System.currentTimeMillis();
        userService.register(unique + "@test.com", "pass", unique, "LISTENER", "Question?", "CorrectAnswer");

        // Test reset with wrong answer - should return false
        boolean result = userService.resetPassword(unique + "@test.com", "WrongAnswer", "newPass");
        assertFalse(result, "Reset should fail with an incorrect security answer");
    }

    @Test
    void testChangePassword_ValidFlow() {
        String unique = "sec_change_" + System.currentTimeMillis();
        User registered = userService.register(unique + "@test.com", "old_pass", unique, "LISTENER", "Q", "A");
        assertNotNull(registered);

        // Change password and verify login works with new
        boolean changed = userService.changePassword(registered.getUserId(), "old_pass", "new_strong_pass123");
        assertTrue(changed);

        User u = userService.login(unique + "@test.com", "new_strong_pass123");
        assertNotNull(u, "Login should work after password rotation");
    }
}
