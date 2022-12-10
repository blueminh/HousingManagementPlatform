package sem.voting.domain.proposal;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing the amount of votes a given option got.
 */
@EqualsAndHashCode
public class Result {
    private final transient Option option;
    private final transient int votes;

    public Result(Option option, int votes) {
        this.option = option;
        this.votes = votes;
    }
}
