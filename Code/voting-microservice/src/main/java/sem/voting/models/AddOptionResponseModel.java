package sem.voting.models;

import java.util.List;
import lombok.Data;
import sem.voting.domain.proposal.Option;

/**
 * Model to represent a response to adding an option to a proposal.
 */
@Data
public class AddOptionResponseModel {
    private List<Option> options;
    private int proposalId;
    private int hoaId;
}
