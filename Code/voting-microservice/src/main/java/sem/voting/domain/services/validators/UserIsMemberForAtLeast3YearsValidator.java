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

            final int daysInYear = 365;
            final int minMembership = 3;
            if (TimeUnit.MILLISECONDS.toDays(duration) / daysInYear < minMembership) {
                throw new InvalidRequestException("User has not been a member for at least 3 years");
            }
            return super.checkNext(vote, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
