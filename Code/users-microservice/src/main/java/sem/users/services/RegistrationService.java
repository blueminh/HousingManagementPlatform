package sem.users.services;

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

        checkInputLength(username, password, fullname);

        if (!userRepository.existsByUsername(username)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(username, hashedPassword, fullname);

            user = userRepository.save(user);

            return user;
        } else {
            throw new UsernameAlreadyInUseException(username);
        }
    }

    /**
     * Checks the length of provided user details.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @throws Exception if the user details have an incorrect length
     */
    public void checkInputLength(Username username, Password password, FullName fullname) throws Exception{

        if (username == null || password == null || fullname == null) {
            throw new InvalidParameterException("At least one of the properties is null!");
        }

        if (username.toString().length() < minlength || password.toString().length() < minlength || fullname.toString().length() < minlength) {
            throw new InvalidParameterException("At least one of the parameters is empty!");
        }

        // Setting a 100 character limit to all attributes
        if (username.toString().length() > maxlength || password.toString().length() > maxlength || fullname.toString().length() > maxlength) {
            throw new InvalidParameterException("At least one parameter is too long! maximum 100 characters");
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
        if (user.getPassword().equals(passwordHashingService.hash(newpassword))) {
            throw new InvalidParameterException("New password must be different from current password!");
        }
        user.changePassword(passwordHashingService.hash(newpassword));
        userRepository.save(user);
    }

}
