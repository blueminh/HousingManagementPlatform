package sem.users.domain.user;

import org.springframework.stereotype.Service;

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
