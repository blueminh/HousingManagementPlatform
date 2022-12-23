package sem.hoa.dtos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class CastVoteRequestModelTest {

    @Test
    void testGetterProposalId() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.getProposalId()).isEqualTo(1);
    }

    @Test
    void testGetterHoaId() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.getHoaId()).isEqualTo(1);
    }

    @Test
    void testGetterUsername() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.getUsername()).isEqualTo(username);
    }

    @Test
    void testGetterOption() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.getOption()).isEqualTo(option);
    }

    @Test
    void testEquals() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1,1, username, option);
        CastVoteRequestModel b = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void testNotEqualsUsername() {
        String usernameA = "abc";
        String usernameB = "xyz";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, usernameA, option);
        CastVoteRequestModel b = new CastVoteRequestModel(1, 1, usernameB, option);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    void testNotEqualsOption() {
        String username = "abc";
        String optionA = "xyz";
        String optionB = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, optionA);
        CastVoteRequestModel b = new CastVoteRequestModel(1, 1, username, optionB);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    void testNotEqualsHoaId() {
        String username = "abc";
        String option = "xyz";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, option);
        CastVoteRequestModel b = new CastVoteRequestModel(1, 2, username, option);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    void testNotEqualsProposalId() {
        String username = "abc";
        String option = "xyz";
        CastVoteRequestModel a = new CastVoteRequestModel(1, 1, username, option);
        CastVoteRequestModel b = new CastVoteRequestModel(2, 1, username, option);
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    void testHashCodeSame() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1,1, username, option);
        CastVoteRequestModel b = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.hashCode() == b.hashCode()).isTrue();
    }

    @Test
    void testHashCodeNotSame() {
        String username = "abc";
        String option = "A";
        CastVoteRequestModel a = new CastVoteRequestModel(1,2, username, option);
        CastVoteRequestModel b = new CastVoteRequestModel(1, 1, username, option);
        assertThat(a.hashCode() == b.hashCode()).isFalse();
    }
}