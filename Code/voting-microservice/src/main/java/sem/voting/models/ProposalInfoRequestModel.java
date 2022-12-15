package sem.voting.models;

import lombok.Data;

/**
 * Model to request information about all proposals of an HOA.
 */
@Data
public class ProposalInfoRequestModel {
    private int hoaId;
}
