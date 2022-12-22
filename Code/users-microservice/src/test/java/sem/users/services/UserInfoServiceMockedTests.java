package sem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.HashedPassword;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.Username;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserInfoServiceMockedTests {

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient UserInfoService userInfoService;

    @Autowired
    private transient PasswordHashingService passwordHashingService;


    @Test
    void changeFullName() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        assertDoesNotThrow(() -> userInfoService.changeFullName(
                user.getUsername(), password, new FullName("this is a different full name")));
        assertEquals(new FullName("this is a different full name"), assertDoesNotThrow(() -> userInfoService.getFullname(user.getUsername())));
    }

    @Test
    void changeSameFullName() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(InvalidParameterException.class, () -> userInfoService.changeFullName(
                user.getUsername(), password, user.getFullName()));
        assertEquals("New full name must be different from current full name!", e.getMessage());
    }

    @Test
    void changeFullNameBadCredentials() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(BadCredentialsException.class, () -> userInfoService.changeFullName(
                user.getUsername(), new Password("asd"), new FullName("firstname and also lastname")));
        assertEquals("Current password does not match!", e.getMessage());
    }

    @Test
    void changeFullNameUserDoesntExist() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        assertThrows(NoSuchElementException.class, () -> userInfoService.changeFullName(
                new Username("this username doesn't exist."), password, new FullName("firstname and also lastname")));
    }

    /**
     * boundary testing.
     */
    @Test
    void changeFullNameEmpty() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(InvalidParameterException.class, () -> userInfoService.changeFullName(
                user.getUsername(), password, new FullName("")));
        assertEquals("New full name cannot be empty!", e.getMessage());
    }

    @Test
    void changeFullNameTooLong() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(InvalidParameterException.class, () -> userInfoService.changeFullName(
                user.getUsername(), password, new FullName("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijx")));
        assertEquals("New full name cannot be longer than 100 characters!", e.getMessage());
    }
}
