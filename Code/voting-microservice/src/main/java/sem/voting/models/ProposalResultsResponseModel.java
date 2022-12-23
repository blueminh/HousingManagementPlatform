package sem.voting.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.Data;
import sem.voting.domain.proposal.Result;

/**
 * Model to represent the response to a request of results for an ended proposal.
 */
@Data
public class ProposalResultsResponseModel {
    private int proposalId;
    private int hoaId;
    private List<String> options;
    private List<Integer> results;

    /**
     * Set all results of the proposal.
     *
     * @param values Set or tuple (option - number of votes).
     */
    public void setAllResults(Set<Result> values) {
        options = new ArrayList<>();
        results = new ArrayList<>();
        for (Result r : values) {
            options.add(r.getOption().toString());
            results.add(r.getVotes());
        }
    }
}
