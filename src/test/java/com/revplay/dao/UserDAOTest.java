package com.revplay.dao;
import com.revplay.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setEmail("test" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash("hashedpass");
        user.setUsername("testuser" + System.currentTimeMillis());
        user.setUserType("LISTENER");

        User saved = userDAO.register(user);
        assertNotNull(saved);
        assertTrue(saved.getUserId() > 0);
    }

    @Test
    void testFindByEmail() {
        User found = userDAO.findByEmail("nonexistent@email.com");
        assertNull(found);
    }
}