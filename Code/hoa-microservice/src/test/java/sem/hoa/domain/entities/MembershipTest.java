package sem.hoa.domain.entities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MembershipTest {

    @Test
    void testEqualsSameObject() {
        Membership membership = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        assertThat(membership.equals(membership)).isTrue();
    }

    @Test
    void testEqualsNull() {
        Membership membership = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        assertThat(membership.equals(null)).isFalse();
    }

    @Test
    void testEqualsDifferentClasses() {
        Membership membership = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        assertThat(membership.equals("another class")).isFalse();
    }

    @Test
    void testEqualsDifferentUsernames() {
        Membership membership1 = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        Membership membership2 = new Membership("user2", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);

        assertThat(membership1.equals(membership2)).isFalse();
    }

    @Test
    void testEqualsDifferentHOAs() {
        Membership membership1 = new Membership("user1", 2, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        Membership membership2 = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);

        assertThat(membership1.equals(membership2)).isFalse();
    }

    @Test
    void testEqualsIdentical() {
        Membership membership1 = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        Membership membership2 = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);

        assertThat(membership1.equals(membership2)).isTrue();
    }

    @Test
    void testHashCodeIdentical() {
        Membership membership1 = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        Membership membership2 = new Membership("user1", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        assertThat(membership1.hashCode()).isEqualTo(membership2.hashCode());
    }

    @Test
    void testHashCodeNotSame() {
        Membership membership1 = new Membership("user1231", 2, false,
                "country121", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);
        Membership membership2 = new Membership("user1sad", 1, false,
                "country1", "city1", "street1", 1, "postal1",
                new Date().getTime(), -1L);

        assertThat(membership1.hashCode()).isNotEqualTo(membership2.hashCode());
    }
}