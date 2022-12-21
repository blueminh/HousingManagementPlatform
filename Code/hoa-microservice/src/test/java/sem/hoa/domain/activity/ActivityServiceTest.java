package sem.hoa.domain.activity;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.hoa.domain.services.HOARepository;
import sem.hoa.domain.services.MemberManagementRepository;
import sem.hoa.domain.utils.Clock;
import sem.hoa.models.ActivityResponseModel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "clock", "membershipRepo", "hoaRepo"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class ActivityServiceTest {
    @Autowired
    private transient Clock clock;

    @Autowired
    private transient ActivityRepository activityRepository;

    @Autowired
    private transient ParticipationRepository participationRepository;

    @Autowired
    private  transient MemberManagementRepository memberManagementRepository;
    @Autowired
    private transient HOARepository hoaRepository;

    @Autowired
    private transient ActivityService activityService;

    @Test
    void testActivityAddSuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);
        when(hoaRepository.existsById(testHoaId)).thenReturn(true);
        // act
        int res = activityService.addActivity(testHoaId, testName, testDate, testDesc, userName);

        // assert
        boolean addedToRepo = activityRepository.existsActivityByActivityId(res);
        assertThat(addedToRepo).isTrue();
    }

    @Test
    void testActivityAddButNotMemberOfRepo() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(false);
        when(hoaRepository.existsById(testHoaId)).thenReturn(true);
        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.addActivity(testHoaId, testName, testDate, testDesc, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testActivityAddButNoSuchHOA() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);
        when(hoaRepository.existsById(testHoaId)).thenReturn(false);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.addActivity(testHoaId, testName, testDate, testDesc, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchHOAException.class);
    }

    @Test
    void testActivityAddButAlreadyAdded() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();

        // Setup repo
        Activity activity = new Activity(testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);
        when(hoaRepository.existsById(testHoaId)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.addActivity(testHoaId, testName, testDate, testDesc, userName);

        // assert
        boolean addedToRepo = activityRepository.existsActivityByName(testName);
        assertThat(addedToRepo).isTrue();
        assertThatThrownBy(action).isInstanceOf(ActivityAlreadyExistsException.class);
    }

    @Test
    void testActivityRemoveSuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        activityService.removeActivity(activityId, userName);

        // assert
        boolean addedToRepo = activityRepository.existsActivityByActivityId(activityId);
        assertThat(addedToRepo).isFalse();
    }

    @Test
    void testActivityRemoveButActivityNotFound() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        // Nothing because it should not be found

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeActivity(activityId, userName);

        // assert
        boolean addedToRepo = activityRepository.existsActivityByActivityId(activityId);

        assertThat(addedToRepo).isFalse();
        assertThatThrownBy(action).isInstanceOf(NoSuchActivityException.class);
    }

    @Test
    void testActivityRemoveButNotMemberOfHoa() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(false);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeActivity(activityId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testActivityRemoveButNotTheCreator() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        // Note that the creator and the username of the caller is different
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, "some other name");
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeActivity(activityId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testGetActivitySuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        Activity res =  activityService.getActivity(activityId, userName);

        // assert
        assertThat(res.getCreatedBy()).isEqualTo(userName);
        assertThat(res.getActivityId()).isEqualTo(activityId);
        assertThat(res.getDate()).hasSameTimeAs(testDate);
        assertThat(res.getName()).isEqualTo(testName);
        assertThat(res.getDescription()).isEqualTo(testDesc);
        assertThat(res.getHoaId()).isEqualTo(testHoaId);
    }

    @Test
    void testGetActivityButNotMember() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(false);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.getActivity(activityId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testGetActivityButNoActivity() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        // Nothing

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.getActivity(activityId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchActivityException.class);
    }

    @Test
    void testParticipateSuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        activityService.participate(userName, activityId);

        // assert
        assertThat(participationRepository.existsByActivityIdAndUsername(activityId, userName)).isTrue();
    }

    @Test
    void testParticipationButNoActivity() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        // Nothing

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.participate(userName, activityId);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchActivityException.class);
    }

    @Test
    void testParticipationButAlreadyParticipates() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        participationRepository.save(new Participation(activityId, userName));
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.participate(userName, activityId);

        // assert
        assertThatThrownBy(action).isInstanceOf(UserAlreadyParticipatesException.class);
    }

    @Test
    void testParticipationButNotAMember() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(false);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.participate(userName, activityId);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testRemoveParticipateSuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        participationRepository.save(new Participation(activityId, userName));
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        activityService.removeParticipate(userName, activityId);

        // assert
        assertThat(participationRepository.existsByActivityIdAndUsername(activityId, userName)).isFalse();
    }

    @Test
    void testRemoveParticipationButNoActivity() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        // Nothing

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeParticipate(userName, activityId);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchActivityException.class);
    }

    @Test
    void testParticipationButNoSuchParticipation() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;
        final String testName = "Test";
        final String testDesc = "Test Desc";
        final Date testDate = calendar.getTime();
        final int activityId = 1;

        // Setup repo
        Activity activity = new Activity(activityId, testHoaId, testName, testDate, testDesc, userName);
        activityRepository.save(activity);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        ThrowableAssert.ThrowingCallable action = () -> activityService.removeParticipate(userName, activityId);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchParticipationException.class);
    }

    @Test
    void testGetAllActivitiesAfterDateSuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;


        // setup repo
        setupRepository(activityRepository, userName);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        calendar.set(2021, 1, 1, 0, 0);
        ActivityResponseModel[] res =  activityService.getAllActivitiesAfterDate(calendar.getTime(), testHoaId, userName);

        // assert
        assertThat(res.length).isEqualTo(2);
    }

    @Test
    void testGetAllActivitiesAfterDateButNotAMember() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;

        // Setup repo
        setupRepository(activityRepository, userName);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(false);

        // act
        calendar.set(2021, 1, 1, 0, 0);
        ThrowableAssert.ThrowingCallable action = () -> activityService.getAllActivitiesAfterDate(calendar.getTime(), testHoaId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testGetAllActivitiesAfterDateButNoActivity() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;

        // Setup repo
        setupRepository(activityRepository, userName);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        calendar.set(2030, 1, 1, 0, 0);
        ThrowableAssert.ThrowingCallable action = () -> activityService.getAllActivitiesAfterDate(calendar.getTime(), testHoaId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchActivityException.class);
    }

    @Test
    void testGetAllActivitiesBeforeDateSuccess() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;


        // setup repo
        setupRepository(activityRepository, userName);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        calendar.set(2021, 1, 1, 0, 0);
        ActivityResponseModel[] res =  activityService.getAllActivitiesBeforeDate(calendar.getTime(), testHoaId, userName);

        // assert
        assertThat(res.length).isEqualTo(1);
    }

    @Test
    void testGetAllActivitiesBeforeDateButNotAMember() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;

        // Setup repo
        setupRepository(activityRepository, userName);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(false);

        // act
        calendar.set(2021, 1, 1, 0, 0);
        ThrowableAssert.ThrowingCallable action = () -> activityService.getAllActivitiesBeforeDate(calendar.getTime(), testHoaId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoAccessToHoaException.class);
    }

    @Test
    void testGetAllActivitiesBeforeDateButNoActivity() throws Exception {

        // setup required variable
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);

        final String userName = "ExampleUser";
        final int testHoaId = 1;

        // Setup repo
        setupRepository(activityRepository, userName);

        // mock
        when(memberManagementRepository.existsMembershipByHoaIDAndUsername(testHoaId, userName)).thenReturn(true);

        // act
        calendar.set(2010, 1, 1, 0, 0);
        ThrowableAssert.ThrowingCallable action = () -> activityService.getAllActivitiesBeforeDate(calendar.getTime(), testHoaId, userName);

        // assert
        assertThatThrownBy(action).isInstanceOf(NoSuchActivityException.class);
    }




    /**
     * Private utility method to set up the repo.
     *
     * @param activityRepository the repository to be setup
     */
    private void setupRepository(ActivityRepository activityRepository, String username) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2015, 1, 1, 0, 0);

        // Setup Activity Repository
        final String testName0 = "Test0";
        final String testDesc0 = "Test0 Desc";
        final int testHoaId0 = 0;
        final Date testDate0 = calendar.getTime();
        int activityId0 = 0;

        final Activity activity0 = new Activity(activityId0, testHoaId0, testName0, testDate0, testDesc0, username);

        calendar.set(2022, 1, 1, 0, 0);

        // Setup Activity Repository
        final String testName1 = "Test1";
        final String testDesc1 = "Test1 Desc";
        final int testHoaId1 = 1;
        final Date testDate1 = calendar.getTime();
        int activityId1 = 1;

        final Activity activity1 = new Activity(activityId1, testHoaId1, testName1, testDate1, testDesc1, username);

        calendar.set(2019, 1, 1, 0, 0);
        final String testName2 = "Test2";
        final String testDesc2 = "Test2 Desc";
        final int testHoaId2 = 1;
        final Date testDate2 = calendar.getTime();
        int activityId2 = 2;

        final Activity activity2 = new Activity(activityId2, testHoaId2, testName2, testDate2, testDesc2, username);

        calendar.set(2024, 1, 1, 0, 0);
        final String testName3 = "Test3";
        final String testDesc3 = "Test3 Desc";
        final int testHoaId3 = 1;
        final Date testDate3 = calendar.getTime();
        int activityId3 = 3;

        final Activity activity3 = new Activity(activityId3, testHoaId3, testName3, testDate3, testDesc3, username);

        calendar.set(2025, 1, 1, 0, 0);
        final String testName4 = "Test4";
        final String testDesc4 = "Test4 Desc";
        final int testHoaId4 = 2;
        final Date testDate4 = calendar.getTime();
        int activityId4 = 4;

        final Activity activity4 = new Activity(activityId4, testHoaId4, testName4, testDate4, testDesc4, username);

        activityRepository.save(activity0);
        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);
        activityRepository.save(activity4);
    }

}