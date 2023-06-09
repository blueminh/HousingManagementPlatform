package sem.voting.domain.proposal;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sem.voting.domain.services.OptionValidationService;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.implementations.AddOptionException;
import sem.voting.domain.services.implementations.VotingException;

/**
 * Entity representing a proposal people can vote on.
 */
@Entity
@NoArgsConstructor
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private int proposalId;

    @Getter
    @Setter
    private int hoaId;

    /**
     * Title of the proposal.
     */
    @Getter
    @Setter
    private String title;

    /**
     * Content of the proposal.
     */
    @Getter
    @Setter
    private String motion;

    /**
     * Date at which the proposal will not accept new votes.
     */
    @Getter
    @Setter
    private Date votingDeadline;

    @Getter
    @Setter
    private ProposalStage status = ProposalStage.UnderConstruction;

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class)
    @Getter
    protected Set<Option> availableOptions = new HashSet<>();

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class, attributeName = "value")
    private Map<String, Option> votes = new HashMap<>();

    @Getter
    @Setter
    private VoteValidationService voteValidationService;

    @Getter
    @Setter
    private OptionValidationService optionValidationService;

    /**
     * Returns the number of votes each option got.
     *
     * @return Set of Result (option-number tuple).
     */
    public Set<Result> getResults() {
        return ProposalHelper.getResults(this, votes);
    }

    /**
     * Check if the deadline has been reached and update the proposal status accordingly.
     */
    public void checkDeadline() {
        ProposalHelper.checkDeadline(this);
    }

    /**
     * Go to voting stage.
     */
    public void startVoting() {
        this.checkDeadline();
        if (this.status == ProposalStage.UnderConstruction) {
            this.status = ProposalStage.Voting;
        }
    }

    /**
     * Add an option to vote on. This can be done only in the UnderConstruction stage of the proposal.
     *
     * @param newOption Option to add.
     * @param userId Id of the user adding the option.
     * @return true if the option was added, false otherwise.
     */
    public boolean addOption(Option newOption, String userId) throws AddOptionException {
        checkDeadline();
        if (this.status != ProposalStage.UnderConstruction) {
            throw new AddOptionException("Proposal is not accepting new options");
        }

        if (!this.optionValidationService.isOptionValid(userId, newOption, this)) {
            throw new AddOptionException("Option is not valid");
        }

        return this.availableOptions.add(newOption);
    }

    /**
     * Add a vote to one of the options or updates it. This can be done only in the Voting stage of the proposal.
     *
     * @param newVote Vote to add.
     * @return true if vote was edited successfully, false otherwise.
     */
    public boolean addVote(Vote newVote) throws VotingException {
        checkDeadline();
        if (this.status != ProposalStage.Voting) {
            throw new VotingException("Proposal is not in Voting phase");
        }

        if (!this.voteValidationService.isVoteValid(newVote, this)) {
            throw new VotingException("Vote is not valid");
        }

        if (newVote.getChoice() == null) {
            return this.votes.remove(newVote.getVoter()) != null;
        }

        if (this.availableOptions.contains(newVote.getChoice())) {
            this.votes.put(newVote.getVoter(), newVote.getChoice());
            return true;
        }
        return false;
    }
}
