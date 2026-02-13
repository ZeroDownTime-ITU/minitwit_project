package zerodowntime.service;

import org.mindrot.jbcrypt.BCrypt;

import zerodowntime.model.User;
import zerodowntime.repository.UserRepository;

public class AuthService {
    UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username"));

        if (!BCrypt.checkpw(password, user.getPwHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    public User register(String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        userRepository.createUser(username, email, hash);

        return userRepository.findByUsername(username).orElse(null);
    }

    private boolean validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("You have to enter a username");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("The username is already taken");
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("You have to enter a valid email address");
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("You have to enter a password");
        }
        return true;
    }
}
