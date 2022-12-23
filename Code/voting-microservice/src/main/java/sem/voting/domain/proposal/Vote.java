package sem.voting.domain.proposal;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DDD value object representing a vote for a proposal.
 */
@EqualsAndHashCode
public class Vote {
    @Getter
    private final transient String voter;

    @Getter
    private final transient Option choice;

    /**
     * Constructor for Vote.
     *
     * @param voter Id of voter.
     * @param choice Selected choice.
     */
    public Vote(String voter, Option choice) {
        this.voter = voter;
        this.choice = choice;
    }
}
