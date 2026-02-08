package com.revplay.dao;

import com.revplay.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;
    private static final String UNIQUE_PREFIX = "test_" + System.currentTimeMillis();

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void testRegister_Success() {
        User u = new User();
        u.setEmail("reg_" + UNIQUE_PREFIX + "@revplay.com");
        u.setUsername("reg_" + UNIQUE_PREFIX);
        u.setPasswordHash("hashed_val");
        u.setUserType("LISTENER");
        u.setSecurityQuestion("Favorite Sport?");
        u.setSecurityAnswer("Cricket");

        User saved = userDAO.register(u);
        assertNotNull(saved, "Registration returned null - check your database connection or NOT NULL constraints.");
        assertTrue(saved.getUserId() > 0, "Saved user should have a valid generated ID.");
    }

    @Test
    void testRegister_DuplicateEmail() {
        User u1 = new User();
        String dupEmail = "dup_" + UNIQUE_PREFIX + "@test.com";
        u1.setEmail(dupEmail);
        u1.setUsername("u1_" + UNIQUE_PREFIX);
        u1.setPasswordHash("pass");
        u1.setUserType("LISTENER");
        u1.setSecurityQuestion("Q");
        u1.setSecurityAnswer("A");
        User first = userDAO.register(u1);
        assertNotNull(first, "First registration failed in duplicate test case");

        // Attempt duplicate
        User u2 = new User();
        u2.setEmail(dupEmail);
        u2.setUsername("u2_" + UNIQUE_PREFIX);
        u2.setPasswordHash("pass");
        u2.setUserType("LISTENER");
        u2.setSecurityQuestion("Q");
        u2.setSecurityAnswer("A");

        User saved = userDAO.register(u2);
        assertNull(saved, "Duplicate email should return null (failed insert), but it was saved successfully!");
    }

    @Test
    void testLogin_Success() {
        String email = "login_" + UNIQUE_PREFIX + "@revplay.com";
        User u = new User();
        u.setEmail(email);
        u.setUsername("login_" + UNIQUE_PREFIX);
        u.setPasswordHash("hashed_val");
        u.setUserType("LISTENER");
        u.setSecurityQuestion("Favorite Color?");
        u.setSecurityAnswer("Blue");

        User saved = userDAO.register(u);
        assertNotNull(saved, "Registration failed for login test - check logs for SQL errors");

        User found = userDAO.findByEmail(email);
        assertNotNull(found, "Could not find user after successful registration");
    }

    @Test
    void testUpdatePassword_Success() {
        String email = "update_" + UNIQUE_PREFIX + "@revplay.com";
        User u = new User();
        u.setEmail(email);
        u.setUsername("update_" + UNIQUE_PREFIX);
        u.setPasswordHash("old_hash");
        u.setUserType("LISTENER");
        u.setSecurityQuestion("Q");
        u.setSecurityAnswer("A");
        User saved = userDAO.register(u);
        assertNotNull(saved, "Setup failed for updatePassword test");

        boolean updated = userDAO.updatePassword(saved.getUserId(), "new_hash");
        assertTrue(updated, "Update password returned false for a valid existing user");
    }
}
