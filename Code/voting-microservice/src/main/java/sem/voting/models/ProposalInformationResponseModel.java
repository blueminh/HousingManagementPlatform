package sem.voting.models;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.ProposalType;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;

/**
 * Model representing a response to a request of information about a proposal.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposalInformationResponseModel {
    private int proposalId;
    private int hoaId;
    private String title;
    private String motion;
    private Date deadline;
    private ProposalStage status;
    private List<String> options;
    private ProposalType type;

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
        if (proposal.getOptionValidationService() instanceof BoardElectionOptionValidationService) {
            this.type = ProposalType.BoardElection;
        } else {
            this.type = ProposalType.HoaRuleChange;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProposalInformationResponseModel that = (ProposalInformationResponseModel) o;
        return proposalId == that.proposalId && hoaId == that.hoaId && title.equals(that.title)
                && motion.equals(that.motion) && deadline.equals(that.deadline) && status == that.status && Objects.equals(options, that.options) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(proposalId, hoaId, title, motion, deadline, status, options, type);
    }
}
