package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BoardMemberForLess10YearsValidator extends Validator {
    @Override
    public boolean handle(String username, Option option, Proposal proposal) throws InvalidRequestException {
        try {
            long joinedBoardDate = HoaCommunication.getJoiningBoardDate(username, proposal.getHoaId());
            if (joinedBoardDate == -1) {
                // Member was never in the board
                return true;
            }
            long duration = new Date().getTime() - joinedBoardDate;

            final int daysInYear = 365;
            final int maxBoard = 10;
            if (TimeUnit.MILLISECONDS.toDays(duration) / daysInYear >= maxBoard) {
                throw new InvalidRequestException("User has been a board member for more than 10 years");
            }
            return super.checkNext(username, option, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
