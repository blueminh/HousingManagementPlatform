package sem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserRepository;
import sem.users.domain.user.Username;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationServiceTest {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService passwordHashingService;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    void registrationTest() {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(new Username("testname"), new Password("test password"), new FullName("Full Name of user"))));
        assertEquals(user.getFullName(), userRepository.findByUsername(user.getUsername()).orElseThrow().getFullName());
    }

    /**
     * Boundary testing.
     */
    @Test
    void fullNameEmpty() {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName(""));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("At least one of the parameters is empty!", e.getMessage());
    }

    @Test
    void fullNameTooLong() {
        AppUser user = new AppUser(new Username("testname"),
                                    passwordHashingService.hash(new Password("test password")),
                                    new FullName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("At least one parameter is too long! maximum 100 characters", e.getMessage());
    }

    @Test
    void usernameEmpty() {
        AppUser user = new AppUser(new Username(""), passwordHashingService.hash(new Password("test password")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("At least one of the parameters is empty!", e.getMessage());
    }

    @Test
    void usernameTooLong() {
        AppUser user = new AppUser(new Username("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
                passwordHashingService.hash(new Password("test password")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("At least one parameter is too long! maximum 100 characters", e.getMessage());
    }

    @Test
    void passwordEmpty() {
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(new Password("")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.registerUser(user.getUsername(), new Password(""), user.getFullName()));
        assertEquals("At least one of the parameters is empty!", e.getMessage());
    }

    @Test
    void passwordTooLong() {
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                new Password("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.registerUser(user.getUsername(),
                new Password("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), user.getFullName()));
        assertEquals("At least one parameter is too long! maximum 100 characters", e.getMessage());
    }
}
