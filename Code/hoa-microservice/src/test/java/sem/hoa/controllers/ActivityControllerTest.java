package sem.hoa.controllers;

import org.assertj.core.api.ThrowableAssert;
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
import sem.hoa.domain.activity.Activity;
import sem.hoa.domain.activity.ActivityRepository;
import sem.hoa.domain.activity.ActivityService;
import sem.hoa.domain.activity.NoSuchActivityException;
import sem.hoa.domain.activity.Participation;
import sem.hoa.domain.activity.ParticipationRepository;
import sem.hoa.domain.activity.UserAlreadyParticipatesException;
import sem.hoa.integeration.utils.JsonUtil;
import sem.hoa.models.ActivityCreationRequestModel;
import sem.hoa.models.UserParticipateModel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient ActivityService activityService;

    @Autowired
    private transient ActivityRepository activityRepository;

    @Autowired
    private transient ParticipationRepository participationRepository;

    @Test
    public void testAddActivity_Success() throws Exception {

        // Setup mocking for authentication
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(userName);
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String testName = "Test";
        final String testDesc = "Test Desc";
        final int testHoaId = 1;
        final Date testDate = calendar.getTime();

        // Setup request model
        final ActivityCreationRequestModel request = new ActivityCreationRequestModel();
        request.setName(testName);
        request.setDesc(testDesc);
        request.setHoaId(testHoaId);
        request.setDate(testDate);

        // Make request
        ResultActions resultActions = mockMvc.perform(post("/activity/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert the response
        resultActions.andExpect(status().isOk());
        Integer res = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), Integer.class);

        // Check if added to repo
        Boolean addedToRep = activityRepository.existsActivityByActivityId(res);
        assertThat(addedToRep).isTrue();
    }

    @Test
    public void testRemoveActivitySuccess() throws Exception {

        // Setup mocking for authentication
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(userName);
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String testName = "Test";
        final String testDesc = "Test Desc";
        final int testHoaId = 1;
        final Date testDate = calendar.getTime();

        // Setup repository
        final Activity activity = new Activity(testHoaId, testName, testDate, testDesc);
        activityRepository.save(activity);
        int id = activity.getActivityId();

        // Make request
        ResultActions resultActions = mockMvc.perform(delete("/activity/remove?id=" + id)
                .header("Authorization", "Bearer MockedToken"));

        // Assert the response
        resultActions.andExpect(status().isOk());

        // Check if added to repo
        Boolean activityStillExists = activityRepository.existsActivityByActivityId(id);
        assertThat(activityStillExists).isFalse();
    }

    @Test
    public void testRemoveActivityFailure() throws Exception {

        // Setup mocking for authentication
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(userName);

        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String testName = "Test";
        final String testDesc = "Test Desc";
        final int testHoaId = 1;
        final Date testDate = calendar.getTime();

        // Setup repository
        final Activity activity = new Activity(testHoaId, testName, testDate, testDesc);
        int id = activity.getActivityId();

        // Make action
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeActivity(id);

        // Assert the response
        assertThatExceptionOfType(NoSuchActivityException.class)
                .isThrownBy(action);

        // Check if exists in repo
        Boolean activityStillExists = activityRepository.existsActivityByActivityId(id);
        assertThat(activityStillExists).isFalse();
    }

    @Test
    public void testParticipationSuccess() throws Exception {

        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final int activityId = 1;

        // Setup Activity Repository
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final int testHoaId = 1;
        final Date testDate = calendar.getTime();

        final Activity activity = new Activity(1, testHoaId, testName, testDate, testDesc);
        activityRepository.save(activity);

        // Setup request model
        final UserParticipateModel request = new UserParticipateModel();
        request.setActivityId(activityId);
        request.setUsername(username);

        // Make request
        ResultActions resultActions = mockMvc.perform(post("/activity/participate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert the response
        resultActions.andExpect(status().isOk());

        // Check if added to repo
        Boolean addedToRep = participationRepository.existsByActivityIdAndUsername(activityId, username);
        assertThat(addedToRep).isTrue();
    }

    @Test
    public void testParticipationButActivityDoesNotExist() throws Exception {

        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final int activityId = 1;

        // Setup request model
        final UserParticipateModel request = new UserParticipateModel();
        request.setActivityId(activityId);
        request.setUsername(username);

        // Make action
        ThrowableAssert.ThrowingCallable action = () -> activityService.participate(username, activityId);

        // Assert the response
        assertThatExceptionOfType(NoSuchActivityException.class)
                .isThrownBy(action);

        // Check if exists in repo
        Boolean activityStillExists = participationRepository.existsByActivityIdAndUsername(activityId, username);
        assertThat(activityStillExists).isFalse();
    }

    @Test
    public void testParticipationButUserAlreadyParticipatesInTheSameActivity() throws Exception {

        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        // Setup Activity Repository
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final int testHoaId = 1;
        final Date testDate = calendar.getTime();

        final Activity activity = new Activity(1, testHoaId, testName, testDate, testDesc);
        activityRepository.save(activity);

        // Setup participation repo
        final int activityId = 1;
        participationRepository.save(new Participation(activityId, username));

        // Make action
        ThrowableAssert.ThrowingCallable action = () -> activityService.participate(username, activityId);

        // Assert the response
        assertThatExceptionOfType(UserAlreadyParticipatesException.class)
                .isThrownBy(action);

        // Check if exists in repo
        Boolean activityStillExists = participationRepository.existsByActivityIdAndUsername(activityId, username);
        assertThat(activityStillExists).isTrue();
    }
}