package sem.voting.models;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalStage;

/**
 * Model representing a response to a request of information about a proposal.
 */
@Data
public class ProposalInformationResponseModel {
    private int proposalId;
    private int hoaId;
    private String title;
    private String motion;
    private Date deadline;
    private ProposalStage status;
    private List<String> options;

    /**
     * Constructor from a Proposal object.
     *
     * @param proposal the Proposal to get information on.
     */
    public ProposalInformationResponseModel(Proposal proposal) {
        this.proposalId = proposal.getProposalId();
        this.hoaId = proposal.getHoaId();
        this.title = proposal.getTitle();
        this.motion = proposal.getMotion();
        this.deadline = proposal.getVotingDeadline();
        this.status = proposal.getStatus();
        this.options = proposal.getAvailableOptions().stream()
                .map(Option::toString).collect(Collectors.toList());
    }
}
