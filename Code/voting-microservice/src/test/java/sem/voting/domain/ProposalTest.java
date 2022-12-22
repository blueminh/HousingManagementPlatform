package sem.voting.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.Result;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.OptionValidationService;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.implementations.AddOptionException;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.domain.services.implementations.RuleChangesOptionValidationService;
import sem.voting.domain.services.implementations.RuleChangesVoteValidationService;
import sem.voting.domain.services.implementations.VotingException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ProposalTest {

    private static Proposal makeProposal(int hoaId, long votingDeadline,
                                         VoteValidationService voteValidationService,
                                         OptionValidationService optionValidationService) {
        Proposal proposal = new Proposal();
        proposal.setHoaId(hoaId);
        proposal.setTitle("titile");
        proposal.setMotion("motion");
        proposal.setVotingDeadline(new Date(votingDeadline));
        proposal.setVoteValidationService(voteValidationService);
        proposal.setOptionValidationService(optionValidationService);
        return proposal;
    }

    private VoteValidationService boardVoteService = mock(BoardElectionsVoteValidationService.class);
    private VoteValidationService ruleVoteService = mock(RuleChangesVoteValidationService.class);
    private OptionValidationService boardOptionService = mock(BoardElectionOptionValidationService.class);
    private OptionValidationService ruleOptionService = mock(RuleChangesOptionValidationService.class);

    @Test
    public void check_deadline_test() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);

        proposal.checkDeadline();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStage.UnderConstruction);

        proposal.setVotingDeadline(new Date(deadline - 1000 * 100));
        proposal.checkDeadline();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStage.Ended);
    }

    @Test
    public void stat_voting_test() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);

        proposal.startVoting();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStage.Voting);

        proposal.setVotingDeadline(new Date(deadline - 1000 * 100));
        proposal.startVoting();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStage.Ended);
    }

    @Test
    public void add_option_test_ok() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);
        Option option = new Option("option");
        String username = "user";
        when(boardOptionService.isOptionValid(username, option, proposal)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> proposal.addOption(new Option("option"), "user"));
    }

    @Test
    void add_option_fail() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);
        Option option = new Option("option");
        String username = "user";
        when(boardOptionService.isOptionValid(username, option, proposal)).thenReturn(true);

        proposal.startVoting();
        Assertions.assertThrows(AddOptionException.class, () -> proposal.addOption(new Option("option"), "user"));

        when(boardOptionService.isOptionValid(username, option, proposal)).thenReturn(false);
        Assertions.assertThrows(AddOptionException.class, () -> proposal.addOption(new Option("option"), "user"));

        proposal.setVotingDeadline(new Date(deadline - 1000 * 100));
        Assertions.assertThrows(AddOptionException.class, () -> proposal.addOption(new Option("option"), "user"));
    }

    @Test
    public void add_vote_ok() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);

        when(boardVoteService.isVoteValid(any(Vote.class), any(Proposal.class))).thenReturn(true);
        when(boardOptionService.isOptionValid(any(String.class), any(Option.class), any(Proposal.class))).thenReturn(true);

        try {
            proposal.addOption(new Option("user2"), "user2");
        } catch (Exception e) {
            fail("cannot add option");
        }

        proposal.startVoting();
        try {
            assertThat(proposal.addVote(new Vote("user", new Option("user2")))).isTrue();
        } catch (Exception e) {
            fail("error removing vote");
        }

        try {
            assertThat(proposal.addVote(new Vote("user", new Option("user3")))).isFalse();
        } catch (Exception e) {
            fail("error removing vote");
        }

        try {
            assertThat(proposal.addVote(new Vote("user", null))).isTrue();
        } catch (Exception e) {
            fail("error removing vote");
        }

        try {
            assertThat(proposal.addVote(new Vote("user", null))).isFalse();
        } catch (Exception e) {
            fail("error removing vote");
        }


    }

    @Test
    public void add_vote_fail() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);

        when(boardVoteService.isVoteValid(any(Vote.class), any(Proposal.class))).thenReturn(true);
        Assertions.assertThrows(VotingException.class, () -> proposal.addVote(new Vote("user", new Option("user2"))));

        proposal.startVoting();
        when(boardVoteService.isVoteValid(any(Vote.class), any(Proposal.class))).thenReturn(false);
        Assertions.assertThrows(VotingException.class, () -> proposal.addVote(new Vote("user", new Option("user2"))));
    }

    @Test
    public void get_results_empty() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);

        Assertions.assertNull(proposal.getResults());

        when(boardVoteService.isVoteValid(any(Vote.class), any(Proposal.class))).thenReturn(true);
        when(boardOptionService.isOptionValid(any(String.class), any(Option.class), any(Proposal.class))).thenReturn(true);

        proposal.setVotingDeadline(new Date(deadline - 1000 * 100));
        Set<Result> expected = new HashSet<>();
        assertThat(proposal.getResults()).isEqualTo(expected);
    }

    @Test
    public void get_results_non_empty() {
        long deadline = new Date().getTime() + 1000 * 60;
        Proposal proposal = makeProposal(1, deadline, boardVoteService, boardOptionService);

        Assertions.assertNull(proposal.getResults());

        when(boardVoteService.isVoteValid(any(Vote.class), any(Proposal.class))).thenReturn(true);
        when(boardOptionService.isOptionValid(any(String.class), any(Option.class), any(Proposal.class))).thenReturn(true);

        try {
            proposal.addOption(new Option("user2"), "user2");
            proposal.addOption(new Option("user3"), "user3");
            proposal.startVoting();
            proposal.addVote(new Vote("user1", new Option("user2")));
            proposal.addVote(new Vote("user4", new Option("user2")));
            proposal.addVote(new Vote("user5", new Option("user3")));
        } catch (Exception e) {
            fail("Cannot add options and votes");
        }

        proposal.setVotingDeadline(new Date(deadline - 1000 * 100));
        Set<Result> expected = new HashSet<>();
        expected.add(new Result(new Option("user2"), 2));
        expected.add(new Result(new Option("user3"), 1));

        assertThat(proposal.getResults()).isEqualTo(expected);
    }
}
