package sem.hoa.domain.activity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ParticipationKeyTest {

    @Test
    void getUsername() {
        ParticipationKey pk = new ParticipationKey(1, "username");
        assertThat(pk.getUsername()).isEqualTo("username");
    }

    @Test
    void getActivityId() {
        ParticipationKey pk = new ParticipationKey(1, "username");
        assertThat(pk.getActivityId()).isEqualTo(1);
    }

    @Test
    void testEquals() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        ParticipationKey pk2 = new ParticipationKey(1, "username");
        assertThat(pk1.equals(pk2)).isTrue();
    }

    @Test
    void testNotEquals() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        ParticipationKey pk2 = new ParticipationKey(2, "username");
        assertThat(pk1.equals(pk2)).isFalse();
    }

    @Test
    void testEqualsSame() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        assertThat(pk1.equals(pk1)).isTrue();
    }

    @Test
    void testEqualsNull() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        assertThat(pk1.equals(null)).isFalse();
    }

    @Test
    void testEqualsDifferentObject() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        String o = "abc";
        assertThat(pk1.equals(o)).isFalse();
    }

    @Test
    void testHashCodeSame() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        ParticipationKey pk2 = new ParticipationKey(1, "username");
        assertThat(pk1.hashCode() == pk2.hashCode()).isTrue();
    }

    @Test
    void testHashCodeNotSame() {
        ParticipationKey pk1 = new ParticipationKey(1, "username");
        ParticipationKey pk2 = new ParticipationKey(2, "username");
        assertThat(pk1.hashCode() == pk2.hashCode()).isFalse();
    }
}