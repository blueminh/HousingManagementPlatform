package sem.users.services;

import org.springframework.stereotype.Service;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.HashedPassword;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.UserRepository;
import sem.users.domain.user.Username;
import sem.users.domain.user.UsernameAlreadyInUseException;

import javax.naming.directory.InvalidAttributesException;
import java.util.Optional;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository         the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */

    public AppUser registerUser(Username username, Password password, FullName fullname) throws Exception {
        int minlength = 1;

        if (username.toString().length() < minlength || password.toString().length() < minlength || fullname.toString().length() < minlength) {
            throw new InvalidAttributesException("One of the attributes is empty!");
        }

        int maxlength = 100;
        // Setting a 100 character limit to all attributes
        if (username.toString().length() > maxlength) {
            throw new InvalidAttributesException("Username is too long! maximum 100 characters");
        }
        if (password.toString().length() > maxlength) {
            throw new InvalidAttributesException("Password is too long! maximum 100 characters");
        }
        if (fullname.toString().length() > maxlength) {
            throw new InvalidAttributesException("Full Name is too long! maximum 100 characters");
        }


        if (!userExists(username)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            if (username == null || hashedPassword == null || fullname == null) {
                throw new InvalidAttributesException("At least one of the properties is NULL!");
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
     * Fetch the full name of a user in the database.
     *
     * @param username user to fetch the full name of.
     * @return full name of the user.
     * @throws UserNotFoundException Exception to throw if the user was not found.
     */
    public FullName getFullname(Username username) throws UserNotFoundException {
        Optional<AppUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getFullName();
        } else {
            throw new UserNotFoundException(username);
        }
    }

    /**
     * Method to check if a user is registered in the database.
     *
     * @param username user to check
     * @return true if exists, false otherwise
     */
    public boolean userExists(Username username) {
        if (userRepository.existsByUsername(username)) {
            return true;
        }
        return false;
    }
}
