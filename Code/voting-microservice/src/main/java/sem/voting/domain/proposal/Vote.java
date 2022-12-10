package sem.voting.domain.proposal;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a vote for a proposal.
 */
@EqualsAndHashCode
public class Vote {
    private final transient int voter;

    private final transient Option choice;

    /**
     * Constructor for Vote.
     *
     * @param voter Id of voter.
     * @param choice Selected choice.
     */
    public Vote(int voter, Option choice) {
        this.voter = voter;
        this.choice = choice;
    }

    public Option getChoice() {
        return choice;
    }

    public int getVoter() {
        return voter;
    }
}
