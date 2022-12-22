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
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.Username;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserInfoServiceTest {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient UserInfoService userInfoService;

    @Autowired
    private transient PasswordHashingService passwordHashingService;

    @Test
    void fullNameTest() {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName())));
        assertEquals(user.getFullName(), assertDoesNotThrow(() -> userInfoService.getFullname(user.getUsername())));
    }

    @Test
    void fullNameFailTest() {
        assertThrows(UserNotFoundException.class, () -> userInfoService.getFullname(new Username("testname")));

    }

    @Test
    void userExists() {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName())));
        assertTrue(userInfoService.userExists(user.getUsername()));
    }

    @Test
    void userDoesNotExists() {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(), new Password("test password"), user.getFullName())));
        assertFalse(userInfoService.userExists(new Username("wrongusername")));
    }



}
