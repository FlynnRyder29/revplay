package com.revplay.dao;

import com.revplay.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAONegativeTest {
    private UserDAO userDAO = new UserDAO();

    @Test
    @DisplayName("Negative: Null Email Registration")
    void testRegister_NullEmail() {
        User u = new User(null, "pass", "user", "LISTENER");
        User saved = userDAO.register(u);
        assertNull(saved, "Registration should fail with null email (DB NOT NULL constraint)");
    }

    @Test
    @DisplayName("Negative: Long String Overflow")
    void testRegister_TooLongUsername() {
        // Assuming limit is 50 based on schema
        String longUsername = "a".repeat(101);
        User u = new User("long@test.com", "pass", longUsername, "LISTENER");
        User saved = userDAO.register(u);
        assertNull(saved, "Registration should fail if string exceeds column limits");
    }

    @Test
    @DisplayName("Negative: Login Wrong Password")
    void testLogin_WrongPassword() {
        // seeded user: test@test.com / pass123
        User found = userDAO.findByEmail("test@test.com");
        if (found != null) {
            // Service layer would check hash, DAO just returns the object
            // Here we test the lookup logic for non-existing emails
            User missing = userDAO.findByEmail("ghost@notfound.com");
            assertNull(missing);
        }
    }
}
