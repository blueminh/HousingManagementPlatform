package sem.voting.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.services.validators.BoardMemberForLess10YearsValidator;
import sem.voting.domain.services.validators.InvalidRequestException;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;
import sem.voting.domain.services.validators.Validator;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ValidatorsTest {

    @Test
    public void board_member_less_10() throws Exception{
        try (MockedStatic<HoaCommunication> utilities = Mockito.mockStatic(HoaCommunication.class)) {
            String username = "user1";
            utilities.when(() -> HoaCommunication.getJoiningBoardDate(username, 1))
                .thenReturn(-1L);

            Validator validator = new BoardMemberForLess10YearsValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);

            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.getJoiningBoardDate(username, 1))
                .thenReturn(100L);
            assertThat(validator.handle(username, new Option("option"), p)).isTrue();

            utilities.when(() -> HoaCommunication.getJoiningBoardDate(username, 1))
                .thenReturn(new Date().getTime() - 10 * 365 * 24 * 60 * 60);
            Assertions.assertThrows(InvalidRequestException.class, () -> validator.handle(username, new Option("option"), p));
        }


    }
}
