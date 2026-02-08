package com.revplay.service;

import com.revplay.dao.PlaylistDAO;
import com.revplay.model.Playlist;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PlaylistServiceTest {
    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {
        // In a real H2 setup, we would point DBConnection to jdbc:h2:mem:testdb
        playlistService = new PlaylistService();
    }

    @Test
    void testCreatePlaylist_Success() {
        // Expect Playlist object, not boolean
        Playlist created = playlistService.create(1, "Gym Hits", "Workout music", true);
        assertNotNull(created);
        assertEquals("Gym Hits", created.getName());
    }

    @Test
    void testGetPublicPlaylists() {
        List<Playlist> publics = playlistService.getPublicPlaylists();
        assertNotNull(publics);
    }

    @Test
    void testDelete_OwnershipCheck() {
        // Test that user 2 cannot delete user 1's playlist
        boolean deleted = playlistService.delete(1, 2);
        assertFalse(deleted, "User should not be able to delete another's playlist");
    }
}
