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
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.VotingRightsService;

/**
 * Entity representing a proposal people can vote on.
 */
@Entity
@NoArgsConstructor
@Getter
public class Proposal {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    private int hoaId;

    /**
     * Title of the proposal.
     */
    private String title;

    /**
     * Content of the proposal.
     */
    private String motion;

    /**
     * Date at which the proposal will not accept new votes.
     */
    private Date votingDeadline;

    private ProposalStage status = ProposalStage.UnderConstruction;

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class)
    private Set<Option> availableOptions = new HashSet<>();

    @ElementCollection
    private Set<Vote> votes = new HashSet<>();

    private VotingRightsService votingRightsService;
    private VoteValidationService voteValidationService;

    /**
     * Returns the number of votes each option got.
     *
     * @return Set of Result (option-number tuple).
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Set<Result> getResults() {
        Set<Result> results = new HashSet<>();
        if (votes.isEmpty() || availableOptions.isEmpty()) {
            return results;
        }
        updateStatus();
        if (this.status != ProposalStage.Ended) {
            return results;
        }
        Map<Option, Integer> myMap;
        myMap = new HashMap<>();
        for (Vote v : votes) {
            int newVal = myMap.getOrDefault(v.getChoice(), -1) + 1;
            myMap.put(v.getChoice(), newVal);
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
}
