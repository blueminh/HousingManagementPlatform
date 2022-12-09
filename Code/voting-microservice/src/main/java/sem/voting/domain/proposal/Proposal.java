package sem.voting.domain.proposal;

import java.util.HashSet;
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

    @ElementCollection
    @Convert(converter = OptionAttributeConverter.class)
    private Set<Option> availableOptions = new HashSet<>();

    @ElementCollection
    private Set<Vote> votes = new HashSet<>();

    private VotingRightsService votingRightsService;
    private VoteValidationService voteValidationService;

    /* It should contain
    - Date to end voting
    - People that have voting rights on it (or a service that validates that)
    - List of possible votes (e.g. candidates)
    - Counter of current votes
    - Rules of what a certain person can vote on (e.g. cannot vote for themselves) (service)
    - Callback to what to do once voting is done
     */

    public int getId() {
        return id;
    }
}
