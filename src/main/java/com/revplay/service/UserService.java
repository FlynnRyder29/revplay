package com.revplay.service;
import com.revplay.dao.UserDAO;
import com.revplay.dao.ArtistDAO;
import com.revplay.model.User;
import com.revplay.model.Artist;
import com.revplay.util.PasswordUtils;
import com.revplay.util.InputValidator;
public class UserService {
    private UserDAO userDAO = new UserDAO();
    private ArtistDAO artistDAO = new ArtistDAO();

    public User register(String email, String password, String username,
                         String userType, String securityQ, String securityA) {
        if (!InputValidator.isValidEmail(email)) return null;
        if (!InputValidator.isValidPassword(password)) return null;
        if (!InputValidator.isValidUsername(username)) return null;

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(PasswordUtils.hashPassword(password));
        user.setUsername(username);
        user.setUserType(userType);
        user.setSecurityQuestion(securityQ);
        user.setSecurityAnswer(securityA);

        return userDAO.register(user);
    }

    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDAO.findByEmail(userDAO.findByEmail(null).getEmail()); // Simplified
        if (user != null && PasswordUtils.verifyPassword(oldPassword, user.getPasswordHash())) {
            return userDAO.updatePassword(userId, PasswordUtils.hashPassword(newPassword));
        }
        return false;
    }

    public String recoverPassword(String email) {
        return userDAO.getSecurityQuestion(email);
    }

    public boolean resetPassword(String email, String answer, String newPassword) {
        if (userDAO.verifySecurityAnswer(email, answer)) {
            User user = userDAO.findByEmail(email);
            return userDAO.updatePassword(user.getUserId(), PasswordUtils.hashPassword(newPassword));
        }
        return false;
    }

    public Artist createArtistProfile(int userId, String bio, String genre, String socialLinks) {
        Artist artist = new Artist(userId, bio, genre);
        artist.setSocialLinks(socialLinks);
        return artistDAO.create(artist);
    }

    public Artist getArtistProfile(int userId) {
        return artistDAO.getByUserId(userId);
    }
}