package sem.voting.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sem.voting.authentication.AuthManager;
import sem.voting.authentication.JwtTokenVerifier;
import sem.voting.communication.HoaCommunication;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalRepository;
import sem.voting.domain.proposal.ProposalType;
import sem.voting.domain.services.validators.MemberIsBoardMemberValidator;
import sem.voting.domain.services.validators.Validator;
import sem.voting.integration.utils.JsonUtil;
import sem.voting.models.ProposalCreationRequestModel;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class VotingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient ProposalHandlingService proposalHandlingService;

    @Autowired
    private transient ProposalRepository proposalRepository;

    @Test
    void testMockConstructor() throws Exception {
        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember("ciao", 1))
                    .thenReturn(true);

            assertThat(HoaCommunication.checkUserIsBoardMember("ciao", 1)).isTrue();

            Validator val = new MemberIsBoardMemberValidator();
            Proposal p = new Proposal();
            p.setHoaId(1);
            assertThat(val.handle("ciao", null, p)).isTrue();
        }
    }

    @Test
    void addProposalNonBoardMember() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;
        final String opt1 = "Sem Semminson";
        final String opt2 = "Mario Rossi";

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);
        model.setOptions(List.of(opt1, opt2));

        // Act
        ResultActions resultActions = mockMvc.perform(post("/propose")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
        /*
        ProposalCreationResponseModel response =
                JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                        ProposalCreationResponseModel.class);

        Proposal savedProposal = proposalRepository.findById(response.getProposalId()).orElseThrow();

        assertThat(savedProposal.getTitle()).isEqualTo(testTitle);
        assertThat(savedProposal.getMotion()).isEqualTo(testMotion);
        */
    }
}