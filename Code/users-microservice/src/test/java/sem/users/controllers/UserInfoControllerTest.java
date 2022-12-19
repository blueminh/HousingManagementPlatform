package sem.users.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sem.users.domain.user.AppUser;
import sem.users.domain.user.FullName;
import sem.users.domain.user.Password;
import sem.users.domain.user.PasswordHashingService;
import sem.users.domain.user.RegistrationService;
import sem.users.domain.user.UserNotFoundException;
import sem.users.domain.user.Username;
import sem.users.models.FullnameRequestModel;
import sem.users.models.FullnameResponseModel;
import sem.users.utils.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService passwordHashingService;


    @Test
    void getFullNameTest() throws Exception {
        AppUser user = new AppUser(new Username("testname"), passwordHashingService.hash(new Password("test password")), new FullName("Full Name of user"));
        assertEquals(user, assertDoesNotThrow(() -> registrationService.registerUser(new Username("testname"), new Password("test password"), new FullName("Full Name of user"))));
        assertEquals(user.getFullName(), assertDoesNotThrow(() -> registrationService.getFullname(new Username("testname"))));

        FullnameRequestModel requestModel = new FullnameRequestModel();
        requestModel.setUsername("testname");

        ResultActions resultActions = mockMvc.perform(post("/getfullname")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Username", "testname")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk());
        FullnameResponseModel responseModel = JsonUtil.deserialize(
                resultActions.andReturn()
                        .getResponse()
                        .getContentAsString(),
                        FullnameResponseModel.class);

        assertEquals(user.getFullName().toString(), responseModel.getFullname());
    }

    @Test
    void getFullNameFailTest() throws Exception {
        assertThrows(UserNotFoundException.class, () -> registrationService.getFullname(new Username("testname")));

        FullnameRequestModel requestModel = new FullnameRequestModel();
        requestModel.setUsername("testname");

        ResultActions resultActions = mockMvc.perform(post("/getfullname")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Username", "testname")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isBadRequest());
        assertEquals(400, resultActions.andReturn().getResponse().getStatus());
        assertEquals("user: testname was not found!", resultActions.andReturn().getResponse().getErrorMessage());
    }
}