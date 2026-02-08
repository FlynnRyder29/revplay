package com.revplay.service;

//import com.revplay.dao.FavoriteDAO;
//import com.revplay.dao.HistoryDAO;
import org.junit.jupiter.api.*;
//import com.revplay.dao.UserDAO;
//import com.revplay.model.User;
import static org.junit.jupiter.api.Assertions.*;

public class SocialFeatureTest {
    private SongService songService = new SongService();

    @Test
    void testAddFavoriteTwice() {
        // User 1 favorites song 1
        songService.addToFavorites(1, 1);
        boolean secondAdd = songService.addToFavorites(1, 1);
        assertFalse(secondAdd, "Should not be able to favorite the same song twice");
    }

//    @Test
//    void testRecentlyPlayedOrder() {
//        // To avoid ORA-02291, we must seed valid parent data
//        String unique = "social_" + System.currentTimeMillis();
//
//        // 1. Create a User
//        UserDAO userDAO = new UserDAO();
//        User u = new User(unique + "@test.com", "pass", unique, "LISTENER");
//        User savedUser = userDAO.register(u);
//        assertNotNull(savedUser, "Failed to create test user for social test");
//        int userId = savedUser.getUserId();
//        // 2. We assume song IDs 1 and 2 exist, OR we should ideally create them.
//        // For a robust test, we use existing IDs if available, but here we try to be safe.
//        // Providing a descriptive failure if they don't exist:
//        HistoryDAO dao = new HistoryDAO();
//
//        try {
//            dao.recordPlay(userId, 1);
//            dao.recordPlay(userId, 2);
//
//            var history = dao.getRecentlyPlayed(userId, 10);
//            assertFalse(history.isEmpty(), "History should not be empty");
//            assertEquals(2, history.get(0).getSongId(), "Most recent song (ID 2) should be at index 0");
//        } catch (Exception e) {
//            fail("Social test failed due to missing parent data (check if Songs 1 & 2 exist): " + e.getMessage());
//        }
//    }
}
