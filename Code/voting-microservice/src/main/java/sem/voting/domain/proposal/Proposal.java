package sem.voting.domain.proposal;

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
import lombok.NoArgsConstructor;
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
    private int id;

    private int hoaId;

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class)
    private Set<Option> availableOptions = new HashSet<>();

    @ElementCollection
    private Set<Vote> votes = new HashSet<>();

    private VotingRightsService votingRightsService;
    private VoteValidationService voteValidationService;

    /* It should contain
    - Date to end voting
    - HOA of reference
    - People that have voting rights on it (or a service that validates that)
    - List of possible votes (e.g. candidates)
    - Counter of current votes
    - Rules of what a certain person can vote on (e.g. cannot vote for themselves) (service)
    - Callback to what to do once voting is done
     */

    public int getId() {
        return id;
    }

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
}
