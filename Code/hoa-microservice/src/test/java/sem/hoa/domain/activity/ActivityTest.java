package sem.hoa.domain.activity;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class ActivityTest {

    @Test
    void getActivityId() {
        // Setup Activity
        Activity activity1;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);
        String testName1 = "Test1";
        String testDesc1 = "Test1 Desc";
        String username = "test123";
        int testHoaId1 = 1;
        int activityId = 1;
        Date testDate1 = calendar.getTime();
        activity1 = new Activity(activityId, testHoaId1, testName1, testDate1, testDesc1, username);

        assertThat(activity1.getActivityId()).isEqualTo(activityId);
    }

    @Test
    void getName() {
        // Setup Activity
        Activity activity1;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);
        String testName1 = "Test1";
        String testDesc1 = "Test1 Desc";
        String username = "test123";
        int testHoaId1 = 1;
        int activityId = 1;
        Date testDate1 = calendar.getTime();
        activity1 = new Activity(activityId, testHoaId1, testName1, testDate1, testDesc1, username);

        assertThat(activity1.getName()).isEqualTo(testName1);
    }

    @Test
    void getDate() {
        // Setup Activity
        Activity activity1;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);
        String testName1 = "Test1";
        String testDesc1 = "Test1 Desc";
        String username = "test123";
        int testHoaId1 = 1;
        int activityId = 1;
        Date testDate1 = calendar.getTime();
        activity1 = new Activity(activityId, testHoaId1, testName1, testDate1, testDesc1, username);

        assertThat(activity1.getDate()).isEqualTo(testDate1);
    }

    @Test
    void getDescription() {
        // Setup Activity
        Activity activity1;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);
        String testName1 = "Test1";
        String testDesc1 = "Test1 Desc";
        String username = "test123";
        int testHoaId1 = 1;
        int activityId = 1;
        Date testDate1 = calendar.getTime();
        activity1 = new Activity(activityId, testHoaId1, testName1, testDate1, testDesc1, username);

        assertThat(activity1.getDescription()).isEqualTo(testDesc1);
    }

    @Test
    void getHoaId() {
        // Setup Activity
        Activity activity1;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);
        String testName1 = "Test1";
        String testDesc1 = "Test1 Desc";
        String username = "test123";
        int testHoaId1 = 1;
        int activityId = 1;
        Date testDate1 = calendar.getTime();
        activity1 = new Activity(activityId, testHoaId1, testName1, testDate1, testDesc1, username);

        assertThat(activity1.getHoaId()).isEqualTo(testHoaId1);
    }

    @Test
    void getCreatedBy() {
        // Setup Activity
        Activity activity1;
        Calendar calendar = new GregorianCalendar();
        calendar.set(2022, 1, 1, 0, 0);
        String testName1 = "Test1";
        String testDesc1 = "Test1 Desc";
        String username = "test123";
        int testHoaId1 = 1;
        int activityId = 1;
        Date testDate1 = calendar.getTime();
        activity1 = new Activity(activityId, testHoaId1, testName1, testDate1, testDesc1, username);

        assertThat(activity1.getCreatedBy()).isEqualTo(username);
    }
}