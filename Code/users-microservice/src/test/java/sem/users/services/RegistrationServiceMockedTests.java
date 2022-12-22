package sem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.HashedPassword;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserRepository;
import sem.users.domain.user.Username;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationServiceMockedTests {

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient RegistrationService registrationService;


    @Autowired
    private transient PasswordHashingService passwordHashingService;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    void changeSamePassword() {
        Password password = new Password("testpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.changePassword(
                user.getUsername(), password, password));
        assertEquals("New password must be different from current password!", e.getMessage());
    }

    @Test
    void changePasswordPass() {
        Password password = new Password("testpassword");
        Password newPassword = new Password("newpassword");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        when(mockPasswordEncoder.hash(newPassword)).thenReturn(new HashedPassword("hashed password2"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        assertDoesNotThrow(() -> registrationService.changePassword(
                user.getUsername(), password, newPassword));
        assertEquals(userRepository.findByUsername(new Username("username")).orElseThrow().getPassword(), new HashedPassword("hashed password2"));
    }

    /**
     * Boundary testing.
     */
    @Test
    void changePasswordTooLong() {
        Password password = new Password("testpassword");
        Password newPassword = new Password("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijx");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        when(mockPasswordEncoder.hash(newPassword)).thenReturn(new HashedPassword("hashed password2"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.changePassword(
                user.getUsername(), password, newPassword));
        assertEquals("The new password is too long! maximum 100 characters", e.getMessage());
    }

    @Test
    void changePasswordEmpty() {
        Password password = new Password("testpassword");
        Password newPassword = new Password("");
        when(mockPasswordEncoder.hash(password)).thenReturn(new HashedPassword("hashed password"));
        when(mockPasswordEncoder.hash(newPassword)).thenReturn(new HashedPassword("hashed password2"));
        AppUser user = new AppUser(new Username("username"), passwordHashingService.hash(
                password), new FullName("firstname lastname"));
        assertDoesNotThrow(() -> registrationService.registerUser(user.getUsername(),
                password, user.getFullName()));
        Exception e = assertThrows(InvalidParameterException.class, () -> registrationService.changePassword(
                user.getUsername(), password, newPassword));
        assertEquals("The new password cannot be empty!", e.getMessage());
    }
}
