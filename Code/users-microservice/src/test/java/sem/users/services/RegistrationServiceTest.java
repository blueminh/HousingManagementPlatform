package sem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.Username;

import javax.naming.directory.InvalidAttributesException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RegistrationServiceTest {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService passwordHashingService;


    @Test
    void fullNameTest() throws Exception {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(new Username("testname"), new Password("test password"), new FullName("Full Name of user"))));
        assertEquals(user.getFullName(), assertDoesNotThrow(() -> registrationService.getFullname(new Username("testname"))));
    }

    @Test
    void fullNameFailTest() throws Exception {
        assertThrows(UserNotFoundException.class, () -> registrationService.getFullname(new Username("testname")));

    }

    @Test
    void userExists() throws Exception {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(new Username("testname"), new Password("test password"), new FullName("Full Name of user"))));
        assertTrue(registrationService.userExists(user.getUsername()));
    }

    @Test
    void userDoesNotExists() throws Exception {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(new Username("testname"), new Password("test password"), new FullName("Full Name of user"))));
        assertFalse(registrationService.userExists(new Username("wrongusername")));
    }

    /**
     * Boundary testing.
     */
    @Test
    void fullNameEmpty() {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName(""));
        Exception e = assertThrows(InvalidAttributesException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("One of the attributes is empty!", e.getMessage());
    }

    @Test
    void fullNameTooLong() {
        AppUser user = new AppUser(new Username("testname"),
                                    passwordHashingService.hash(new Password("test password")),
                                    new FullName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        Exception e = assertThrows(InvalidAttributesException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("Full Name is too long! maximum 100 characters", e.getMessage());
    }

    @Test
    void usernameEmpty() {
        AppUser user = new AppUser(new Username(""), passwordHashingService.hash(new Password("test password")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidAttributesException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("One of the attributes is empty!", e.getMessage());
    }

    @Test
    void usernameTooLong() {
        AppUser user = new AppUser(new Username("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
                passwordHashingService.hash(new Password("test password")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidAttributesException.class, () -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName()));
        assertEquals("Username is too long! maximum 100 characters", e.getMessage());
    }

    @Test
    void passwordEmpty() {
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(new Password("")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidAttributesException.class, () -> registrationService.registerUser(user.getUsername(), new Password(""), user.getFullName()));
        assertEquals("One of the attributes is empty!", e.getMessage());
    }

    @Test
    void passwordTooLong() {
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                new Password("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")), new FullName("firstname lastname"));
        Exception e = assertThrows(InvalidAttributesException.class, () -> registrationService.registerUser(user.getUsername(),
                new Password("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), user.getFullName()));
        assertEquals("Password is too long! maximum 100 characters", e.getMessage());
    }
}
