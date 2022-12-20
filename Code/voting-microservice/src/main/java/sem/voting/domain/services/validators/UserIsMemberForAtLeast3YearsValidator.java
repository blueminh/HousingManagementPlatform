package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserIsMemberForAtLeast3YearsValidator extends Validator {
    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        try {
            long joiningDate = HoaCommunication.getJoiningDate(vote.getVoter(), proposal.getHoaId());
            long duration = new Date().getTime() - joiningDate;
            // 1 year is 365 days
            if (!(TimeUnit.MILLISECONDS.toDays(duration) / 365 >= 3)) {
                throw new InvalidRequestException("User has not been a member for at least 3 years");
            }
            return super.checkNext(vote, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
