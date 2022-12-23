package sem.hoa.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class JoiningRequestModelTest {
    private static JoiningRequestModel joiningRequestModelA;
    private static JoiningRequestModel joiningRequestModelB;
    private static JoiningRequestModel joiningRequestModelC;

    @BeforeEach
    void setup() {
        String hoaNameA = "hoaNameA";
        String usernameA = "usernameA";
        String countryA = "countryA";
        String cityA = "cityA";
        joiningRequestModelA = new JoiningRequestModel(hoaNameA, usernameA, countryA, cityA);

        String hoaNameB = "hoaNameB";
        String usernameB = "usernameB";
        String countryB = "countryB";
        String cityB = "cityB";
        joiningRequestModelB = new JoiningRequestModel(hoaNameB, usernameB, countryB, cityB);

        String hoaNameC = "hoaNameA";
        String usernameC = "usernameA";
        String countryC = "countryA";
        String cityC = "cityA";
        joiningRequestModelC = new JoiningRequestModel(hoaNameC, usernameC, countryC, cityC);
    }

    @Test
    void getHoaName() {
        assertThat(joiningRequestModelA.getHoaName()).isEqualTo("hoaNameA");
    }

    @Test
    void getUserName() {
        assertThat(joiningRequestModelA.getUserName()).isEqualTo("usernameA");
    }

    @Test
    void getCountry() {
        assertThat(joiningRequestModelA.getCountry()).isEqualTo("countryA");
    }

    @Test
    void getCity() {
        assertThat(joiningRequestModelA.getCity()).isEqualTo("cityA");
    }

    @Test
    void testEquals() {
        assertThat(joiningRequestModelA.equals(joiningRequestModelC)).isTrue();
    }

    @Test
    void testNotEquals() {
        assertThat(joiningRequestModelA.equals(joiningRequestModelB)).isFalse();
    }

    @Test
    void testHashCodeSame() {
        assertThat(joiningRequestModelA.hashCode() == joiningRequestModelC.hashCode()).isTrue();
    }

    @Test
    void testHashCodeNotSame() {
        assertThat(joiningRequestModelA.hashCode() == joiningRequestModelB.hashCode()).isFalse();
    }
}