package sem.hoa.dtos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserNameHoaNameDtoTest {

    @Test
    void testNotEquals() {
        String usernameA = "a";
        String usernameB = "b";
        String hoaNameA = "aa";
        String hoaNameB = "bb";
        UserNameHoaNameDto userNameHoaNameDtoA = new UserNameHoaNameDto(usernameA, hoaNameA);
        UserNameHoaNameDto userNameHoaNameDtoB = new UserNameHoaNameDto(usernameB, hoaNameB);
        assertThat(userNameHoaNameDtoA.equals(userNameHoaNameDtoB)).isFalse();
    }

    @Test
    void testEquals() {
        String usernameA = "a";
        String usernameB = "a";
        String hoaNameA = "aa";
        String hoaNameB = "aa";
        UserNameHoaNameDto userNameHoaNameDtoA = new UserNameHoaNameDto(usernameA, hoaNameA);
        UserNameHoaNameDto userNameHoaNameDtoB = new UserNameHoaNameDto(usernameB, hoaNameB);
        assertThat(userNameHoaNameDtoA.equals(userNameHoaNameDtoB)).isTrue();
    }

    @Test
    void testHashCodeSame() {
        String usernameA = "a";
        String usernameB = "a";
        String hoaNameA = "aa";
        String hoaNameB = "aa";
        UserNameHoaNameDto userNameHoaNameDtoA = new UserNameHoaNameDto(usernameA, hoaNameA);
        UserNameHoaNameDto userNameHoaNameDtoB = new UserNameHoaNameDto(usernameB, hoaNameB);
        assertThat(userNameHoaNameDtoA.hashCode() == userNameHoaNameDtoB.hashCode()).isTrue();
    }

    @Test
    void testHashCodeNotSame() {
        String usernameA = "a";
        String usernameB = "b";
        String hoaNameA = "aa";
        String hoaNameB = "ab";
        UserNameHoaNameDto userNameHoaNameDtoA = new UserNameHoaNameDto(usernameA, hoaNameA);
        UserNameHoaNameDto userNameHoaNameDtoB = new UserNameHoaNameDto(usernameB, hoaNameB);
        assertThat(userNameHoaNameDtoA.hashCode() != userNameHoaNameDtoB.hashCode()).isTrue();
    }
}