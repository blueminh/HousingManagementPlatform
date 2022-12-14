package sem.voting.domain.proposal;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing the amount of votes a given option got.
 */
@EqualsAndHashCode
@Data
public class Result {
    @EqualsAndHashCode.Include
    private final transient Option option;

    @EqualsAndHashCode.Include
    private final transient int votes;

    public Result(Option option, int votes) {
        this.option = option;
        this.votes = votes;
    }
}
