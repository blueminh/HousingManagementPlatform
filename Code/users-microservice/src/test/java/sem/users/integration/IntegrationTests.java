package sem.users.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import sem.users.authentication.JwtTokenGenerator;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.HashedPassword;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.UserRepository;
import sem.users.domain.user.Username;
import sem.users.models.AuthenticationRequestModel;
import sem.users.models.AuthenticationResponseModel;
import sem.users.models.ChangeUserInfoRequestModel;
import sem.users.models.FullnameRequestModel;
import sem.users.models.FullnameResponseModel;
import sem.users.models.RegistrationRequestModel;
import sem.users.utils.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder", "mockTokenGenerator", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class IntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient JwtTokenGenerator mockJwtTokenGenerator;

    @Autowired
    private transient AuthenticationManager mockAuthenticationManager;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void fullNameFetchFail() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");

        FullnameRequestModel model = new FullnameRequestModel();
        model.setUsername(testUsername.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/getfullname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        MvcResult result = resultActions
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("user: SomeUser was not found!", result.getResponse().getErrorMessage());

    }

    @Test
    public void fullNameFetchPass() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final FullName testFullName = new FullName("firstname lastname");

        final AppUser testUser = new AppUser(testUsername, testHashedPassword, testFullName);
        userRepository.save(testUser);

        FullnameRequestModel model = new FullnameRequestModel();
        model.setUsername(testUsername.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/getfullname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        FullnameResponseModel responseModel = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                FullnameResponseModel.class);
        assertEquals(testFullName.toString(), responseModel.getFullname());

    }

    @Test
    public void userExistsCheckFail() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final FullName testFullName = new FullName("firstname lastname");

        final AppUser testUser = new AppUser(testUsername, testHashedPassword, testFullName);
        userRepository.save(testUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUsername("this username shouldn't exist");

        // Act
        ResultActions resultActions = mockMvc.perform(post("/userexists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

    }

    @Test
    public void userExistsCheckPass() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final FullName testFullName = new FullName("firstname lastname");

        final AppUser testUser = new AppUser(testUsername, testHashedPassword, testFullName);
        userRepository.save(testUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUsername(testUsername.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/userexists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

    }

    @Test
    public void registerValidCredentials() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final FullName testFullName = new FullName("firstname lastname");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(testPassword.toString());
        model.setFullname(testFullName.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(testHashedPassword, savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void registerExistingUser() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password newTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(newTestPassword.toString());
        model.setFullname(testFullName.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(existingTestPassword, savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void loginValiduser() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                !testUsername.toString().equals(authentication.getPrincipal())
                    || !testPassword.toString().equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        final String testToken = "testJWTToken";
        when(mockJwtTokenGenerator.generateToken(
            argThat(userDetails -> userDetails.getUsername().equals(testUsername.toString())))
        ).thenReturn(testToken);

        AppUser appUser = new AppUser(testUsername, testHashedPassword, testFullName);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(testPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));


        // Assert
        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponseModel responseModel = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                AuthenticationResponseModel.class);

        assertEquals(testToken, responseModel.getToken());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUsername.toString().equals(authentication.getPrincipal())
                    && testPassword.toString().equals(authentication.getCredentials())));
    }

    @Test
    public void loginUserDoesntExist() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUsername(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && testPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void loginInvalidPassword() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String wrongPassword = "password1234";
        final String testPassword = "password123";
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(new Password(testPassword))).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && wrongPassword.equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        AppUser appUser = new AppUser(new Username(testUser), testHashedPassword, testFullName);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setUsername(testUser);
        model.setPassword(wrongPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && wrongPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void changePassword() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final Password newTestPassword = new Password("password4567");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(new HashedPassword("hash"));
        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(currentTestPassword.toString());
        model.setNewAttribute(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(new HashedPassword("hash"), savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void changePasswordUserDoesntExist() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final Password newTestPassword = new Password("password4567");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(new HashedPassword("hash"));
        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername("nonexistentuser");
        model.setPassword(currentTestPassword.toString());
        model.setNewAttribute(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(new HashedPassword("password123"), savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void changePasswordBadCredentials() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final Password newTestPassword = new Password("password4567");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(new HashedPassword("hash"));
        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(newTestPassword.toString());
        model.setNewAttribute(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(new HashedPassword("password123"), savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void changeSamePassword() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final Password newTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(newTestPassword)).thenReturn(new HashedPassword("hash"));
        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(currentTestPassword.toString());
        model.setNewAttribute(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changepassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(existingTestPassword, savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void changeFullName() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");
        final FullName newtestFullName = new FullName("lastname firstname");

        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(currentTestPassword.toString());
        model.setNewAttribute(newtestFullName.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changefullname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(existingTestPassword, savedUser.getPassword());
        assertEquals(newtestFullName, savedUser.getFullName());
    }

    @Test
    public void changeSameFullName() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword(currentTestPassword.toString());
        model.setNewAttribute(testFullName.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changefullname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(existingTestPassword, savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void changeFullNameUserNotFound() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");

        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername("this user does not exist");
        model.setPassword(currentTestPassword.toString());
        model.setNewAttribute("new full name");

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changefullname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(existingTestPassword, savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }

    @Test
    public void changeFullNameBadCredentials() throws Exception {
        // Arrange
        final Username testUsername = new Username("SomeUser");
        final Password currentTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final FullName testFullName = new FullName("firstname lastname");
        final FullName newtestFullName = new FullName("lastname firstname");

        when(mockPasswordEncoder.hash(currentTestPassword)).thenReturn(new HashedPassword("password123"));
        AppUser existingAppUser = new AppUser(testUsername, existingTestPassword, testFullName);
        userRepository.save(existingAppUser);

        ChangeUserInfoRequestModel model = new ChangeUserInfoRequestModel();
        model.setUsername(testUsername.toString());
        model.setPassword("invalidpass");
        model.setNewAttribute(newtestFullName.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/changefullname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        AppUser savedUser = userRepository.findByUsername(testUsername).orElseThrow();

        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(existingTestPassword, savedUser.getPassword());
        assertEquals(testFullName, savedUser.getFullName());
    }
}
