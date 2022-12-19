package sem.voting.models;

import java.util.List;

import lombok.Data;

/**
 * Model to represent a response to adding an option to a proposal.
 */
@Data
public class AddOptionResponseModel {
    private List<String> options;
    private int proposalId;
    private int hoaId;
}
