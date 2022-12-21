package sem.voting.domain.services.validators;

import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.Vote;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BoardMemberForLess10YearsValidator extends Validator {
    @Override
    public boolean handle(Vote vote, Proposal proposal) throws InvalidRequestException {
        try {
            long joinedBoardDate = HoaCommunication.getJoiningBoardDate(vote.getVoter(), proposal.getHoaId());
            long duration = new Date().getTime() - joinedBoardDate;

            final int daysInYear = 365;
            final int maxBoard = 10;
            if (TimeUnit.MILLISECONDS.toDays(duration) / daysInYear >= maxBoard) {
                throw new InvalidRequestException("User has been a board member for more than 10 years");
            }
            return super.checkNext(vote, proposal);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }
}
