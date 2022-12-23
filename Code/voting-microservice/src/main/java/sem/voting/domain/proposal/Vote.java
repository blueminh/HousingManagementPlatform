package sem.voting.domain.proposal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A DDD value object representing a vote for a proposal.
 */
@EqualsAndHashCode
@AllArgsConstructor
public class Vote {
    @Getter
    @EqualsAndHashCode.Include
    private final transient String voter;

    @Getter
    @EqualsAndHashCode.Include
    private final transient Option choice;
}
