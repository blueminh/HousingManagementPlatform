package sem.users.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.HashedPassword;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserRepository;
import sem.users.domain.user.Username;
import sem.users.domain.user.UsernameAlreadyInUseException;

import java.security.InvalidParameterException;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {

    private static final int minlength = 1;

    private static final int maxlength = 100;
    private final transient UserRepository userRepository;

    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new RegistrationService.
     *
     * @param userRepository         the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    public static int getMinlength() {
        return minlength;
    }

    public static int getMaxlength() {
        return maxlength;
    }

    /**
     * Register a new user.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(Username username, Password password, FullName fullname) throws Exception {

        if (username.toString().length() < minlength || password.toString().length() < minlength || fullname.toString().length() < minlength) {
            throw new InvalidParameterException("One of the parameters is empty!");
        }

        // Setting a 100 character limit to all attributes
        if (username.toString().length() > maxlength) {
            throw new InvalidParameterException("Username is too long! maximum 100 characters");
        }
        if (password.toString().length() > maxlength) {
            throw new InvalidParameterException("Password is too long! maximum 100 characters");
        }
        if (fullname.toString().length() > maxlength) {
            throw new InvalidParameterException("Full Name is too long! maximum 100 characters");
        }


        if (!userRepository.existsByUsername(username)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            if (username == null || hashedPassword == null || fullname == null) {
                throw new InvalidParameterException("At least one of the properties is null!");
            }

            // Create new account
            AppUser user = new AppUser(username, hashedPassword, fullname);

            user = userRepository.save(user);

            return user;
        } else {
            throw new UsernameAlreadyInUseException(username);
        }
    }

    /**
     * Change a user's password.
     *
     * @param username user to change the password for
     * @param oldpassword user's current password
     * @param newpassword user's new password
     */
    public void changePassword(Username username, Password oldpassword, Password newpassword) {

        if (newpassword.toString().length() > maxlength) {
            throw new InvalidParameterException("The new password is too long! maximum 100 characters");
        }
        if (newpassword.toString().length() < minlength) {
            throw new InvalidParameterException("The new password cannot be empty!");
        }
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        if (!user.getPassword().equals(passwordHashingService.hash(oldpassword))) {
            throw new BadCredentialsException("Current password does not match!");
        }
        if (user.getPassword().equals(passwordHashingService.hash(newpassword))) {
            throw new InvalidParameterException("New password must be different from current password!");
        }
        user.changePassword(passwordHashingService.hash(newpassword));
        userRepository.save(user);
    }

}
