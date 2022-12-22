package sem.hoa.dtos;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * Model representing a proposal.
 */
@Data
public class ProposalDTO {
    private int proposalId;
    private int hoaId;
    private String title;
    private String motion;
    private Date deadline;
    private String status;
    private List<String> options;
    private String type;
}
