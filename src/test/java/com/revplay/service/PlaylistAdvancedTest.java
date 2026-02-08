package com.revplay.service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlaylistAdvancedTest {
    private PlaylistService playlistService = new PlaylistService();

    @Test
    void testAddDuplicateSongToPlaylist() {
        // Adding song ID 1 to playlist ID 1 twice
        playlistService.addSong(1, 1);
        boolean addedAgain = playlistService.addSong(1, 1);

        // Depending on business choice, this could be false (prevent dups) or true
        // If your schema allows it, it will be true. If you have unique constraint, it will be false.
        assertFalse(addedAgain, "Should not allow duplicate songs in the same playlist");
    }

    @Test
    void testRemoveNonExistentSong() {
        boolean removed = playlistService.removeSong(1, 9999);
        assertFalse(removed, "Should return false when removing a song not in playlist");
    }

    @Test
    void testCreateEmptyNamePlaylist() {
        // Should fail validation in Service layer
        assertThrows(IllegalArgumentException.class, () -> {
            playlistService.create(1, "", "Desc", true);
        });
    }
}
