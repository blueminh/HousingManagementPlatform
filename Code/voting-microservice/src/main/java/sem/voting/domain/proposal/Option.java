package sem.voting.domain.proposal;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing an option in a proposal.
 */
@EqualsAndHashCode
public class Option {
    private final transient String optionValue;

    public Option(String option) {
        this.optionValue = option;
    }

    @Override
    public String toString() {
        return optionValue;
    }
}
