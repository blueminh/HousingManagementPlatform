package sem.voting.models;

import java.util.List;
import lombok.Data;
import sem.voting.domain.proposal.Result;

@Data
public class ProposalResultsResponseModel {
    private int proposalId;
    private int hoaId;
    private List<Result> results;
}
