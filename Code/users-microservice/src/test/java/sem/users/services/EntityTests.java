package sem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.FullNameWasChangedEvent;
import sem.users.domain.user.HashedPassword;
import sem.users.domain.user.PasswordWasChangedEvent;
import sem.users.domain.user.UserWasCreatedEvent;
import sem.users.domain.user.Username;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class EntityTests {

    @Test
    void userEqualstest() {
        AppUser user1 = new AppUser(new Username("username"), new HashedPassword("hashedpassword"), new FullName("full name"));
        AppUser user2 = new AppUser(new Username("username"), new HashedPassword("hashedpassword"), new FullName("full name"));
        assertEquals(user1, user2);
        assertEquals(user1, user1);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void userNotEqualstest() {
        AppUser user1 = new AppUser(new Username("username"), new HashedPassword("hashedpassword"), new FullName("full name"));
        AppUser user2 = new AppUser(new Username("different username"), new HashedPassword("hashedpassword"), new FullName("full name"));
        assertNotEquals(user1, user2);
        assertNotEquals(user1, new FullName("name"));
        assertNotEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, null);
    }

    @Test
    void fullNameEqualsTest() {
        FullName name1 = new FullName("firstname lastname");
        FullName name2 = new FullName("firstname lastname");
        assertEquals(name1, name2);
        assertEquals(name1, name1);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void fullNameNotEqualTest() {
        FullName name1 = new FullName("firstname lastname");
        FullName name2 = new FullName("lastname firstname");
        assertNotEquals(name1, name2);
        assertNotEquals(name1, "firstname lastname");
        assertNotEquals(name1.hashCode(), name2.hashCode());
        assertNotEquals(name1, null);
    }

    @Test
    void userCreatedEventTest() {
        UserWasCreatedEvent event = new UserWasCreatedEvent(new Username("name"));
        assertEquals(new Username("name"), event.getUsername());
    }

    @Test
    void passwordWasChangedEventTest() {
        AppUser user1 = new AppUser(new Username("username"), new HashedPassword("hashedpassword"), new FullName("full name"));
        PasswordWasChangedEvent event = new PasswordWasChangedEvent(user1);
        assertEquals(event.getUser(), user1);
    }

    @Test
    void fullNameWasChangedEventTest() {
        AppUser user1 = new AppUser(new Username("username"), new HashedPassword("hashedpassword"), new FullName("full name"));
        FullNameWasChangedEvent event = new FullNameWasChangedEvent(user1);
        assertEquals(event.getUser(), user1);
    }

}
