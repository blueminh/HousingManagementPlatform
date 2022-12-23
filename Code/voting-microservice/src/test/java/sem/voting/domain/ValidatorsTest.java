package sem.voting.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalRepository;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.OptionValidationService;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.domain.services.implementations.RuleChangesVoteValidationService;
import sem.voting.domain.services.validators.BoardMemberForLess10YearsValidator;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.IsAffirmativeOrNegativeValidator;
import sem.voting.domain.services.validators.MemberIsAddingThemselvesValidator;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;
import sem.voting.domain.services.validators.MemberIsNotBoardMemberOfAnyHoaValidator;
import sem.voting.domain.services.validators.NoBoardElectionValidator;
import sem.voting.domain.services.validators.NoSelfVoteValidator;
import sem.voting.domain.services.validators.UserIsMemberForAtLeast3YearsValidator;
import sem.voting.domain.services.validators.UserIsMemberOfThisHoaValidator;
import sem.voting.domain.services.validators.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockProposalHandlingService"})
public class ValidatorsTest {

    @Autowired
    private transient ProposalHandlingService proposalHandlingService;

    @Test
    public void board_member_less_10() throws Exception {
        try (MockedStatic<HoaCommunication> utilities = Mockito.mockStatic(HoaCommunication.class)) {
            String username = "user1";
            utilities.when(() -> HoaCommunication.getJoiningBoardDate(username, 1)).thenReturn(-1L);

            Validator validator = new BoardMemberForLess10YearsValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);

            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.getJoiningBoardDate(username, 1)).thenReturn(new Date().getTime());
            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.getJoiningBoardDate(username, 1)).thenReturn(new Date().getTime() - 10L * 365 * 24 * 60 * 60 * 1000);
            Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
        }
    }

    @Test
    public void is_affirmative_or_negative() throws Exception {
        Validator validator = new IsAffirmativeOrNegativeValidator();
        Proposal p = new Proposal();
        p.setHoaId(1);
        String username = "user1";
        assertThat(validator.handle(username, new Option("Yes"), p)).isTrue();
        assertThat(validator.handle(username, new Option("No"), p)).isTrue();
        Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
    }

    @Test
    public void member_adding_themselves() throws Exception {
        Validator validator = new MemberIsAddingThemselvesValidator();
        Proposal p = new Proposal();
        p.setHoaId(1);
        String username = "user1";
        assertThat(validator.handle(username, new Option(username), p)).isTrue();
        Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
    }

    @Test
    public void member_is_board_member() throws Exception {
        try (MockedStatic<HoaCommunication> utilities = Mockito.mockStatic(HoaCommunication.class)) {
            String username = "user1";
            utilities.when(() -> HoaCommunication.checkUserIsBoardMember(username, 1)).thenReturn(true);

            Validator validator = new MemberIsBoardMemberValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);

            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.checkUserIsBoardMember(username, 1)).thenReturn(false);
            Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
        }
    }

    @Test
    public void member_is_not_board_member_of_any() throws Exception {
        try (MockedStatic<HoaCommunication> utilities = Mockito.mockStatic(HoaCommunication.class)) {
            String username = "user1";
            utilities.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(username, 1)).thenReturn(true);

            Validator validator = new MemberIsNotBoardMemberOfAnyHoaValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);

            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(username, 1)).thenReturn(false);
            Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
        }
    }

    @Test
    public void member_at_least_3_years() throws Exception {
        try (MockedStatic<HoaCommunication> utilities = Mockito.mockStatic(HoaCommunication.class)) {
            String username = "user1";
            long joiningDate = new Date().getTime() - 4L * 365 * 24 * 60 * 60 * 1000;
            utilities.when(() -> HoaCommunication.getJoiningDate(username, 1)).thenReturn(joiningDate);

            Validator validator = new UserIsMemberForAtLeast3YearsValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);

            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.getJoiningDate(username, 1)).thenReturn(new Date().getTime() + 2L * 365 * 24 * 60 * 60 * 1000);
            Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
        }
    }

    @Test
    public void member_of_this_hoa() throws Exception {
        try (MockedStatic<HoaCommunication> utilities = Mockito.mockStatic(HoaCommunication.class)) {
            String username = "user1";
            utilities.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(username, 1)).thenReturn(true);

            Validator validator = new UserIsMemberOfThisHoaValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);

            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(username, 1)).thenReturn(false);
            Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
        }
    }


}
