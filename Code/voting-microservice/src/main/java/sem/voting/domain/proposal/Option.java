package sem.voting.domain.proposal;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing an option in a proposal.
 */
@EqualsAndHashCode
public class Option {
    @EqualsAndHashCode.Include
    private final transient String optionValue;

    public Option(String optionValue) {
        this.optionValue = optionValue;
    }

    @Override
    public String toString() {
        return optionValue;
    }
}
