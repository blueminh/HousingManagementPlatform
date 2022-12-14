package sem.voting.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Data;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.Result;

/**
 * Model to represent a response on past proposals.
 */
@Data
public class ProposalHistoryResponseModel {
    private int proposalId;
    private int hoaId;
    private String title;
    private String motion;
    private Date deadline;
    private ProposalStage status;
    private List<String> options;
    private List<Integer> results;

    /**
     * Constructor from a Proposal object.
     *
     * @param proposal the Proposal to get information on.
     */
    public ProposalHistoryResponseModel(Proposal proposal) {
        this.proposalId = proposal.getId();
        this.hoaId = proposal.getHoaId();
        this.title = proposal.getTitle();
        this.motion = proposal.getMotion();
        this.deadline = proposal.getVotingDeadline();
        this.status = proposal.getStatus();
        setAllResults(proposal.getResults());
    }

    /**
     * Set all results of the proposal.
     *
     * @param values Set or tuple (option - number of votes).
     */
    private void setAllResults(Set<Result> values) {
        options = new ArrayList<>();
        results = new ArrayList<>();
        for (Result r : values) {
            options.add(r.getOption().toString());
            results.add(r.getVotes());
        }
    }
}
