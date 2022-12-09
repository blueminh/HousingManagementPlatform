package sem.voting.domain.proposal;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.NoArgsConstructor;

/**
 * An entity representing a vote for a proposal.
 */
@Entity
@IdClass(VoteId.class)
@NoArgsConstructor
public class Vote {

    @Id
    private int user;

    @Id
    private int proposal;

    @Convert(converter = OptionAttributeConverter.class)
    private Option choice;

    /**
     * Constructor for Vote.
     *
     * @param voter Id of voter.
     * @param proposal Id of proposal.
     * @param choice Selected choice.
     */
    public Vote(int voter, int proposal, Option choice) {
        this.user = voter;
        this.proposal = proposal;
        this.choice = choice;
    }

    public Option getChoice() {
        return choice;
    }

    public int getVoter() {
        return user;
    }

    public int getProposal() {
        return proposal;
    }
}
