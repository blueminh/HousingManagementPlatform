package sem.voting.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import sem.voting.domain.proposal.ProposalType;

/**
 * Model to represent a request to create a new proposal.
 */
@Data
public class ProposalCreationRequestModel {
    private int hoaId;
    private String title;
    private String motion;
    private List<String> options = new ArrayList<>();
    private ProposalType type;
    private Date deadline;
}
