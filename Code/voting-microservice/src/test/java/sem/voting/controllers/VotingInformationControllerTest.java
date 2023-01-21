package sem.voting.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import sem.voting.domain.proposal.ProposalType;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.integration.utils.JsonUtil;
import sem.voting.models.ProposalCreationRequestModel;
import sem.voting.models.ProposalGenericRequestModel;
import sem.voting.models.ProposalStartVotingResponseModel;
import java.sql.Date;
import java.time.Instant;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockProposalHandling"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class VotingInformationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private ProposalHandlingService proposalHandlingService;

    @Test
    void startProposalNotFound() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("username");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("username");

        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(0);
        model.setType(testType);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/start")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void startProposalPass() throws Exception {
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        final int testProposalId = 3;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;

        ProposalGenericRequestModel model = new ProposalGenericRequestModel();
        model.setProposalId(testProposalId);
        model.setHoaId(testHoaId);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(testMotion);
        returned.setTitle(testTitle);
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(returned);
        when(proposalHandlingService.save(returned)).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isOk());

            ProposalStartVotingResponseModel response =
                    JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                            ProposalStartVotingResponseModel.class);

            assertThat(response.getProposalId()).isEqualTo(testProposalId);
            assertThat(response.getHoaId()).isEqualTo(testHoaId);
        }
    }

    @Test
    void startProposalCheckHOAFail() throws Exception {
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        final int testProposalId = 3;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;

        ProposalGenericRequestModel model = new ProposalGenericRequestModel();
        model.setProposalId(testProposalId);
        model.setHoaId(testHoaId);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(testMotion);
        returned.setTitle(testTitle);
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(returned);
        when(proposalHandlingService.save(returned)).thenReturn(returned);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/start")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void startProposalUnauthorized() throws Exception {
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        final int testProposalId = 3;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;

        ProposalGenericRequestModel model = new ProposalGenericRequestModel();
        model.setProposalId(testProposalId);
        model.setHoaId(testHoaId);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(testMotion);
        returned.setTitle(testTitle);
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(returned);
        when(proposalHandlingService.save(returned)).thenReturn(returned);


        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isUnauthorized());
        }
    }

    @Test
    void startProposalNull() throws Exception {
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        ProposalGenericRequestModel model = null;

        // Act
        ResultActions resultActions = mockMvc.perform(post("/start")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
}