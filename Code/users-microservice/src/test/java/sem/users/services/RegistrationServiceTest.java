package sem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.users.domain.user.*;

import static org.junit.jupiter.api.Assertions.*;


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
    void FullNameTest() throws Exception {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(new Username("testname"), new Password("test password"), new FullName("Full Name of user"))));
        assertEquals(user.getFullName(), assertDoesNotThrow(() -> registrationService.getFullname(new Username("testname"))));
    }

    @Test
    void FullNameFailTest() throws Exception {
        assertThrows(UserNotFoundException.class, () -> registrationService.getFullname(new Username("testname")));

    }

    /**
     * Boundary testing with empty full name
     *
    @Test
    void fullNameBoundaryTest(){
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName(""));

    }*/

}
