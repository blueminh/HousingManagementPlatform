package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserIsMemberForAtLeast3YearsValidator extends BaseValidator {
    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        try {
            long joiningDate = HoaCommunication.getJoiningDate(username, proposal.getHoaId());
            long duration = Instant.now().toEpochMilli() - joiningDate;

            final int daysInYear = 365;
            final int minMembershipYears = 3;
            if (TimeUnit.MILLISECONDS.toDays(duration) / daysInYear < minMembershipYears) {
                throw new InvalidRequestException("User has not been a member for at least 3 years");
            }
            return super.checkNext(username, option, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
