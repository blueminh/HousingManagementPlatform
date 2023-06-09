package sem.voting.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;

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
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalType;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.integration.utils.JsonUtil;
import sem.voting.models.AddOptionRequestModel;
import sem.voting.models.AddOptionResponseModel;
import sem.voting.models.ProposalCreationRequestModel;
import sem.voting.models.ProposalCreationResponseModel;
import sem.voting.models.ProposalGenericRequestModel;
import sem.voting.models.ProposalStartVotingResponseModel;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockProposalHandling"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class VotingManagerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private ProposalHandlingService proposalHandlingService;

    @Test
    void addProposalBoardMember() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(model.getDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(model.getMotion());
        returned.setTitle(model.getTitle());
        final int testProposalId = 3;
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isOk());

            ProposalCreationResponseModel response =
                    JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                            ProposalCreationResponseModel.class);

            assertThat(response.getProposalId()).isEqualTo(testProposalId);
        }
    }

    @Test
    void addProposalNotBoardMember() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(model.getDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(model.getMotion());
        returned.setTitle(model.getTitle());
        final int testProposalId = 3;
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addElectionProposalNoBoardMemberNewHOA() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(model.getDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(model.getMotion());
        returned.setTitle(model.getTitle());
        final int testProposalId = 3;
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(false);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isOk());

            ProposalCreationResponseModel response =
                    JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                            ProposalCreationResponseModel.class);

            assertThat(response.getProposalId()).isEqualTo(testProposalId);
        }
    }

    @Test
    void addRuleProposalNoBoardMemberNewHOA() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.HoaRuleChange;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        Proposal returned = new Proposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(model.getDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(model.getMotion());
        returned.setTitle(model.getTitle());
        final int testProposalId = 3;
        returned.setProposalId(testProposalId);
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(false);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addProposalInvalidDeadline() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().minusSeconds(1)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addProposalInvalidType() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        String requestBody = JsonUtil.serialize(model)
                .replaceFirst("BoardElection", "WrongType");

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(requestBody));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addProposalDuringElections() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);

        Proposal boardElections = new Proposal();
        boardElections.setVoteValidationService(new BoardElectionsVoteValidationService());
        boardElections.setOptionValidationService(new BoardElectionOptionValidationService());
        boardElections.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        boardElections.setHoaId(testHoaId);
        when(proposalHandlingService.getActiveProposals(testHoaId)).thenReturn(List.of(boardElections));

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addProposalNullRequest() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/propose")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(null)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void addProposalInvalidOption() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final ProposalType testType = ProposalType.BoardElection;

        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        model.setTitle(testTitle);
        model.setMotion(testMotion);
        model.setDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        model.setHoaId(testHoaId);
        model.setType(testType);
        model.setOptions(List.of("anotherUser"));

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkHoaHasBoard(userName, testHoaId))
                    .thenReturn(true);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/propose")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }
}
