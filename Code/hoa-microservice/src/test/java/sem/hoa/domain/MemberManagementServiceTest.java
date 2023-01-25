package sem.hoa.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipId;
import sem.hoa.domain.services.HoaRepository;
import sem.hoa.domain.services.MemberManagementRepository;
import sem.hoa.domain.services.MemberManagementService;
import sem.hoa.exceptions.HoaJoiningException;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberManagementServiceTest {
    @Autowired
    private transient MemberManagementService memberManagementService;

    @Autowired
    private transient MemberManagementRepository memberManagementRepository;

    @Autowired
    private transient HoaRepository hoaRepository;

    @Test
    public void mutationPossibleCandidate() {
        // Arrange
        // This HOA has no candidates to join the board
        final Hoa hoa = hoaRepository.save(new Hoa("hoa1", "country1", "city1"));
        // join for 3 years - 1 day
        // expect this guy to be false, but got a yes instead
        final long yearInSeconds = 365 * 24 * 60 * 60;
        final long joiningDate = Instant.now().minusSeconds(yearInSeconds * 3 - 24 * 60 * 60).toEpochMilli();
        final Membership membership = new Membership("user1", hoa.getId(), false,
            "country1", "city1", "street1", 1, "postal1",
            joiningDate, -1L);

        // Act
        try {
            memberManagementService.addMembership(membership);
        } catch (HoaJoiningException e) {
            fail("joining error");
        }

        // Assert
        assertThat(memberManagementService.hasPossibleBoardCandidates(hoa.getId())).isFalse();
    }

    @Test
    public void addMembership_ok() {
        // Arrange
        final Hoa hoa = hoaRepository.save(new Hoa("hoa1", "country1", "city1"));
        final Membership membership = new Membership("user1", hoa.getId(), false,
            "country1", "city1", "street1", 1, "postal1",
            new Date().getTime(), -1L);

        // Act
        try {
            memberManagementService.addMembership(membership);
        } catch (HoaJoiningException e) {
            fail("joining error");
        }

        // Assert
        Optional<Membership> savedMembership = memberManagementRepository.findById(new MembershipId("user1", hoa.getId()));
        assertThat(savedMembership.isPresent()).isTrue();
        assertThat(savedMembership.get().equals(membership));
    }

    @Test
    public void is_board_member_of_test() {
        // Arrange
        final Hoa hoa = hoaRepository.save(new Hoa("hoa1", "country1", "city1"));
        final Membership membership1 = new Membership("user1", hoa.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership2 = new Membership("user2", hoa.getId(), true, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);

        // Act
        try {
            memberManagementService.addMembership(membership1);
            memberManagementService.addMembership(membership2);
        } catch (HoaJoiningException e) {
            fail("joining error");
        }

        // Assert
        assertThat(memberManagementService.isBoardMemberOf("user1")).isEqualTo(-1);
        assertThat(memberManagementService.isBoardMemberOf("user2")).isEqualTo(hoa.getId());
    }

    @Test
    public void address_check_test() {
        final Hoa hoa = new Hoa("hoa1", "country1", "city1");
        final Membership membership1 = new Membership("user1", hoa.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership2 = new Membership("user2", hoa.getId(), true, "country2", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership3 = new Membership("user2", hoa.getId(), true, "country1", "city2", "street1", 1, "postal1", new Date().getTime(), -1L);

        // Assert
        assertThat(memberManagementService.addressCheck(hoa, membership1)).isTrue();
        assertThat(memberManagementService.addressCheck(hoa, membership2)).isFalse();
        assertThat(memberManagementService.addressCheck(hoa, membership3)).isFalse();
    }

    @Test
    public void find_by_username_and_hoaId_test() {
        final Hoa hoa1 = hoaRepository.save(new Hoa("hoa1", "country1", "city1"));
        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));
        final Membership membership1 = new Membership("user1", hoa1.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership2 = new Membership("user1", hoa2.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership3 = new Membership("user2", hoa1.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);

        // Act
        try {
            memberManagementService.addMembership(membership1);
            memberManagementService.addMembership(membership2);
            memberManagementService.addMembership(membership3);
        } catch (HoaJoiningException e) {
            fail("joining error");
        }


        // Assert
        assertThat(memberManagementService.findByUsernameAndHoaId("user1", hoa1.getId()).isPresent()).isTrue();
        assertThat(memberManagementService.findByUsernameAndHoaId("user2", hoa1.getId()).isPresent()).isTrue();
        assertThat(memberManagementService.findByUsernameAndHoaId("user2", hoa2.getId()).isPresent()).isFalse();
    }

    @Test
    public void remove_membership_test() {
        final Hoa hoa1 = hoaRepository.save(new Hoa("hoa1", "country1", "city1"));
        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));
        final Membership membership1 = new Membership("user1", hoa1.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership2 = new Membership("user1", hoa2.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);
        final Membership membership3 = new Membership("user2", hoa1.getId(), false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L);

        // Act
        try {
            memberManagementService.addMembership(membership1);
            memberManagementService.addMembership(membership2);
            memberManagementService.addMembership(membership3);
        } catch (HoaJoiningException e) {
            fail("joining error");
        }

        // Assert
        // no username
        Assertions.assertThrows(Exception.class, () -> memberManagementService.removeMembership(new MembershipId("user3", hoa1.getId())));
        // not registered
        Assertions.assertThrows(Exception.class, () -> memberManagementService.removeMembership(new MembershipId("user2", hoa2.getId())));

        // ok
        Assertions.assertDoesNotThrow(() -> memberManagementService.removeMembership(new MembershipId("user1", hoa1.getId())));
        assertThat(memberManagementRepository.findById(new MembershipId("user1", hoa1.getId())).isEmpty()).isTrue();
    }
}
