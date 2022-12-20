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
import sem.hoa.domain.activity.NoSuchParticipationException;
import sem.hoa.domain.activity.Participation;
import sem.hoa.domain.activity.ParticipationRepository;
import sem.hoa.domain.activity.UserAlreadyParticipatesException;
import sem.hoa.integeration.utils.JsonUtil;
import sem.hoa.models.ActivityCreationRequestModel;
import sem.hoa.models.ActivityResponseModel;
import sem.hoa.models.DateRequestModel;
import sem.hoa.models.UserParticipateModel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void testAddActivitySuccess() throws Exception {

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
    public void testGetActivitySuccess() throws Exception {
        // Setup mocking for authentication
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(userName);
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 0, 1, 0, 0);
        // This is required because in the repository we don't store the seconds and ms so, it won't match
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        final String testName = "Test";
        final String testDesc = "Test Desc";
        final int testHoaId = 1;
        final Date testDate = calendar.getTime();

        // Setup repository
        final Activity activity = new Activity(testHoaId, testName, testDate, testDesc);
        activityRepository.save(activity);
        int id = activity.getActivityId();

        // Make request
        ResultActions resultActions = mockMvc.perform(get("/activity/get?id=" + id)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());
        ActivityResponseModel responseModel = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), ActivityResponseModel.class);

        assertThat(responseModel.getActivityId()).isEqualTo(id);
        assertThat(responseModel.getName()).isEqualTo(testName);
        assertThat(responseModel.getDate()).isEqualTo(testDate);
        assertThat(responseModel.getDesc()).isEqualTo(testDesc);
        assertThat(responseModel.getHoaId()).isEqualTo(testHoaId);
    }

    @Test
    public void testGetActivityFailure() throws Exception {
        // Setup mocking for authentication
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(userName);

        int activityId = 2;

        // Make action
        ThrowableAssert.ThrowingCallable action = () -> activityService.getActivity(activityId);

        // Assert the response
        assertThatExceptionOfType(NoSuchActivityException.class)
                .isThrownBy(action);

        boolean exists = activityRepository.existsActivityByActivityId(activityId);
        assertThat(exists).isFalse();
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
    public void testRemoveActivityFailure() {

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
    public void testParticipationButUserAlreadyParticipatesInTheSameActivity() {

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
    public void testParticipationButActivityDoesNotExist() {

        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

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
    public void testRemoveParticipationSuccess() throws Exception {

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

        // Setup Participation Repository
        participationRepository.save(new Participation(activityId, username));

        // Setup request model
        final UserParticipateModel request = new UserParticipateModel();
        request.setActivityId(activityId);
        request.setUsername(username);

        // Make request
        ResultActions resultActions = mockMvc.perform(delete("/activity/removeParticipate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        // Assert the response
        resultActions.andExpect(status().isOk());

        // Check if added to repo
        Boolean addedToRep = participationRepository.existsByActivityIdAndUsername(activityId, username);
        assertThat(addedToRep).isFalse();
    }

    @Test
    public void testRemoveParticipationButActivityDoesNotExist() {

        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        final int activityId = 1;

        // Setup request model
        final UserParticipateModel request = new UserParticipateModel();
        request.setActivityId(activityId);
        request.setUsername(username);

        // Make action
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeParticipate(username, activityId);

        // Assert the response
        assertThatExceptionOfType(NoSuchActivityException.class)
                .isThrownBy(action);

        // Check if exists in repo
        Boolean activityStillExists = participationRepository.existsByActivityIdAndUsername(activityId, username);
        assertThat(activityStillExists).isFalse();
    }


    @Test
    public void testRemoveParticipationButNoSuchParticipation() {

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

        int activityId = 1;

        final Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc);
        activityRepository.save(activity);

        // Make action
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeParticipate(username, activityId);

        // Assert the response
        assertThatExceptionOfType(NoSuchParticipationException.class)
                .isThrownBy(action);

        // Check if exists in repo
        Boolean activityStillExists = participationRepository.existsByActivityIdAndUsername(activityId, username);
        assertThat(activityStillExists).isFalse();
    }

    @Test
    public void testGetAllActivitiesBeforeDateSuccess() throws Exception {
        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        setupRepository(activityRepository);

        // Set up request model
        Calendar calendar = new GregorianCalendar();
        calendar.set(2023, 1, 1, 0, 0);
        DateRequestModel dateRequestModel = new DateRequestModel(calendar.getTime());

        ResultActions resultActions = mockMvc.perform(get("/activity/getAllBeforeDate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(dateRequestModel)));

        resultActions.andExpect(status().isOk());
        ActivityResponseModel[] responseModel = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), ActivityResponseModel[].class);

        // Out of the three activities added, 2 are after 2021.
        assertThat(responseModel.length).isEqualTo(2);
        assertThat(responseModel[0].getDate()).isBefore(dateRequestModel.getDate());
        assertThat(responseModel[1].getDate()).isBefore(dateRequestModel.getDate());
    }

    @Test
    public void testGetAllActivitiesBeforeDateButNoSuchActivity() throws Exception {
        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        setupRepository(activityRepository);

        // Set up request model
        Calendar calendar = new GregorianCalendar();
        calendar.set(2000, 1, 1, 0, 0);
        DateRequestModel dateRequestModel = new DateRequestModel(calendar.getTime());

        ResultActions resultActions = mockMvc.perform(get("/activity/getAllBeforeDate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(dateRequestModel)));

        resultActions.andExpect(status().isBadRequest());

        // Out of the three activities added, 2 are after 2021.
        assertThat(activityRepository.existsActivityByDateBefore(dateRequestModel.getDate())).isFalse();
    }

    @Test
    public void testGetAllActivitiesAfterDateSuccess() throws Exception {
        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        setupRepository(activityRepository);

        // Set up request model
        Calendar calendar = new GregorianCalendar();
        calendar.set(2021, 1, 1, 0, 0);
        DateRequestModel dateRequestModel = new DateRequestModel(calendar.getTime());

        ResultActions resultActions = mockMvc.perform(get("/activity/getAllAfterDate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(dateRequestModel)));

        resultActions.andExpect(status().isOk());
        ActivityResponseModel[] responseModel = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), ActivityResponseModel[].class);

        // Out of the three activities added, 2 are after 2021.
        assertThat(responseModel.length).isEqualTo(2);
        assertThat(responseModel[0].getDate()).isAfter(dateRequestModel.getDate());
        assertThat(responseModel[1].getDate()).isAfter(dateRequestModel.getDate());
    }

    @Test
    public void testGetAllActivitiesAfterDateButNoSuchActivity() throws Exception {
        // Setup mocking for authentication
        final String username = "ExampleUser";
        when(mockAuthenticationManager.getNetId()).thenReturn(username);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(username);

        setupRepository(activityRepository);

        // Set up request model
        Calendar calendar = new GregorianCalendar();
        calendar.set(2026, 1, 1, 0, 0);
        DateRequestModel dateRequestModel = new DateRequestModel(calendar.getTime());

        ResultActions resultActions = mockMvc.perform(get("/activity/getAllAfterDate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(dateRequestModel)));

        resultActions.andExpect(status().isBadRequest());

        // Out of the three activities added, 2 are after 2021.
        assertThat(activityRepository.existsActivityByDateAfter(dateRequestModel.getDate())).isFalse();
    }

    /**
     * Private utility method to set up the repo.
     *
     * @param activityRepository the repository to be setup
     */
    private void setupRepository(ActivityRepository activityRepository) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        // Setup Activity Repository
        final String testName1 = "Test1";
        final String testDesc1 = "Test1 Desc";
        final int testHoaId1 = 1;
        final Date testDate1 = calendar.getTime();
        int activityId1 = 1;

        final Activity activity1 = new Activity(activityId1, testHoaId1, testName1, testDate1, testDesc1);

        calendar.set(2019, 1, 1, 0, 0);
        final String testName2 = "Test2";
        final String testDesc2 = "Test2 Desc";
        final int testHoaId2 = 1;
        final Date testDate2 = calendar.getTime();
        int activityId2 = 2;

        final Activity activity2 = new Activity(activityId2, testHoaId2, testName2, testDate2, testDesc2);

        calendar.set(2024, 1, 1, 0, 0);
        final String testName3 = "Test3";
        final String testDesc3 = "Test3 Desc";
        final int testHoaId3 = 2;
        final Date testDate3 = calendar.getTime();
        int activityId3 = 3;

        final Activity activity3 = new Activity(activityId3, testHoaId3, testName3, testDate3, testDesc3);

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
    }
}