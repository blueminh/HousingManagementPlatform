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
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.VotingRightsService;

/**
 * Entity representing a proposal people can vote on.
 */
@Entity
@NoArgsConstructor
public class Proposal {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @Getter
    private int id;

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
    private ProposalStage status = ProposalStage.UnderConstruction;

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class)
    @Getter
    private Set<Option> availableOptions = new HashSet<>();

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class, attributeName = "value")
    private Map<Integer, Option> votes = new HashMap<>();

    @Getter
    @Setter
    private VotingRightsService votingRightsService;

    @Getter
    @Setter
    private VoteValidationService voteValidationService;

    /**
     * Returns the number of votes each option got.
     *
     * @return Set of Result (option-number tuple).
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Set<Result> getResults() {
        updateStatus();
        if (this.status != ProposalStage.Ended) {
            return null;
        }
        Set<Result> results = new HashSet<>();
        if (votes.isEmpty() || availableOptions.isEmpty()) {
            return results;
        }
        Map<Option, Integer> myMap;
        myMap = new HashMap<>();
        for (Integer user : votes.keySet()) {
            Option choice = votes.get(user);
            int newVal = myMap.getOrDefault(choice, -1) + 1;
            myMap.put(choice, newVal);
        }
        for (Option o : availableOptions) {
            int val = myMap.getOrDefault(o, 0);
            results.add(new Result(o, val));
        }
        return results;
    }

    /**
     * Check if the deadline has been reached and update the proposal status accordingly.
     */
    public void updateStatus() {
        Date now = Date.from(Instant.now());
        if (!now.before(this.votingDeadline)) {
            // Voting has ended
            this.status = ProposalStage.Ended;
        }
    }

    /**
     * Add an option to vote on. This can be done only in the UnderConstruction stage of the proposal.
     *
     * @param newOption Option to add.
     */
    public void addOption(Option newOption) {
        if (this.status == ProposalStage.UnderConstruction) {
            this.availableOptions.add(newOption);
        }
    }

    /**
     * Add a vote to one of the options or updates it. This can be done only in the Voting stage of the proposal.
     *
     * @param newVote Vote to add.
     * @return true if vote was edited successfully, false otherwise.
     */
    public boolean addVote(Vote newVote) {
        updateStatus();
        if (this.status == ProposalStage.Voting
                && this.availableOptions.contains(newVote.getChoice())
                && this.votingRightsService.canVote(newVote.getVoter(), this)
                && this.voteValidationService.isVoteValid(newVote, this)) {
            if (newVote.getChoice() == null) {
                this.votes.remove(newVote.getVoter());
            } else {
                this.votes.put(newVote.getVoter(), newVote.getChoice());
            }
            return true;
        }
        return false;
    }
}
