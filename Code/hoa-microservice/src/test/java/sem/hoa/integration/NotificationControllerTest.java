package sem.hoa.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sem.hoa.authentication.AuthManager;
import sem.hoa.authentication.JwtTokenVerifier;
import sem.hoa.domain.notifications.Notification;
import sem.hoa.models.NotificationRequestModel;
import sem.hoa.models.NotificationResponseModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Test
    void newNotificationTest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Notification notification = new Notification("you've got mail", "username12");
        NotificationRequestModel request = new NotificationRequestModel();
        request.setMessage(notification.getMessage());
        request.setUsername(notification.getUsername());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/notifications/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions
                .andExpect(status().isOk());

    }

    @Test
    void fetchNotificationTest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Notification notification = new Notification("you've got mail", "username12");
        NotificationRequestModel request = new NotificationRequestModel();
        request.setMessage(notification.getMessage());
        request.setUsername(notification.getUsername());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/notifications/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions
                .andExpect(status().isOk());

        Notification notification2 = new Notification("you've got mail, again!", "username12");
        request.setMessage(notification2.getMessage());
        request.setUsername(notification2.getUsername());

        // Act
        ResultActions resultActions2 = mockMvc.perform(post("/notifications/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions2
                .andExpect(status().isOk());

        NotificationRequestModel getrequest = new NotificationRequestModel();
        getrequest.setUsername(notification.getUsername());

        // Act
        ResultActions resultActions3 = mockMvc.perform(post("/notifications/get")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions3
                .andExpect(status().isOk());

        NotificationResponseModel responseModel = JsonUtil.deserialize(
                resultActions3.andReturn().getResponse().getContentAsString(),
                NotificationResponseModel.class);

        assertEquals(2, responseModel.getMessages().size());
        assertTrue(responseModel.getMessages().contains(notification.getMessage()));
        assertTrue(responseModel.getMessages().contains(notification2.getMessage()));
    }

    @Test
    void duplicateNotificationTest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Notification notification = new Notification("you've got mail", "username12");
        NotificationRequestModel request = new NotificationRequestModel();
        request.setMessage(notification.getMessage());
        request.setUsername(notification.getUsername());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/notifications/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions
                .andExpect(status().isOk());


        // Act
        ResultActions resultActions2 = mockMvc.perform(post("/notifications/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions2
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyValuesTest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        NotificationRequestModel request = new NotificationRequestModel();

        // Act
        ResultActions resultActions = mockMvc.perform(post("/notifications/new")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest());
    }

}
