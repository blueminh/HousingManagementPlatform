package sem.users.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.UserRepository;
import sem.users.domain.user.Username;

import java.security.InvalidParameterException;
import java.util.Optional;

/**
 * A DDD service for managing user information.
 */
@Service
public class UserInfoService {
    private final transient UserRepository userRepository;

    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserInfoService.
     *
     * @param userRepository         the user repository
     * @param passwordHashingService the password hashing service
     */
    public UserInfoService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Fetch the full name of a user in the database.
     *
     * @param username user to fetch the full name of.
     *
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
     * Change a user's full name.
     *
     * @param username user to change the full name for
     * @param password user's current password
     * @param fullName user's new full name
     */
    public void changeFullName(Username username, Password password, FullName fullName) {
        if (fullName.toString().length() > RegistrationService.getMaxlength()) {
            throw new InvalidParameterException("New full name cannot be longer than 100 characters!");
        }
        if (fullName.toString().length() < RegistrationService.getMinlength()) {
            throw new InvalidParameterException("New full name cannot be empty!");
        }
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        if (!user.getPassword().equals(passwordHashingService.hash(password))) {
            throw new BadCredentialsException("Current password does not match!");
        }
        if (user.getFullName().equals(fullName)) {
            throw new InvalidParameterException("New full name must be different from current full name!");
        }
        user.changeFullName(fullName);
        userRepository.save(user);
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
