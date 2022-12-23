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
import java.util.Optional;
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
class VotingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private ProposalHandlingService proposalHandlingService;

    private class TestProposal extends Proposal {
        public void setOptions(Set<Option> newOptions) {
            this.availableOptions = newOptions;
        }
    }

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

    @Test
    void addOptionOk() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(current.getVotingDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(current.getMotion());
        returned.setTitle(current.getTitle());
        returned.setProposalId(testProposalId);
        returned.setOptions(Set.of(new Option(userName)));
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(-1L);
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(4 * yearInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isOk());

            AddOptionResponseModel response =
                    JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                            AddOptionResponseModel.class);

            assertThat(response.getProposalId()).isEqualTo(testProposalId);
            assertThat(response.getOptions()).containsExactly(userName);
        }
    }

    @Test
    void addOptionNotSelf() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption("anotherUser");
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(-1L);
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(4 * yearInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addOptionNotMember() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(current.getVotingDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(current.getMotion());
        returned.setTitle(current.getTitle());
        returned.setProposalId(testProposalId);
        returned.setOptions(Set.of(new Option(userName)));
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(-1L);
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(4 * yearInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addOptionOnDifferentBoard() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(current.getVotingDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(current.getMotion());
        returned.setTitle(current.getTitle());
        returned.setProposalId(testProposalId);
        returned.setOptions(Set.of(new Option(userName)));
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(-1L);
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(4 * yearInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addOptionJustJoined() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(current.getVotingDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(current.getMotion());
        returned.setTitle(current.getTitle());
        returned.setProposalId(testProposalId);
        returned.setOptions(Set.of(new Option(userName)));
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            final long dayInSeconds = 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now()
                            .minusSeconds(3 * yearInSeconds)
                            .plusSeconds(dayInSeconds).toEpochMilli());
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(-1L);

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addOptionOldBoardMember() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(11 * yearInSeconds).toEpochMilli());
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(4 * yearInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Test
    void addOptionAlmostOldBoardMember() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(current.getVotingDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(current.getMotion());
        returned.setTitle(current.getTitle());
        returned.setProposalId(testProposalId);
        returned.setOptions(Set.of(new Option(userName)));
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            final long dayInSeconds = 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(10 * yearInSeconds).plusSeconds(dayInSeconds).toEpochMilli());
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now().minusSeconds(4 * yearInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isOk());

            AddOptionResponseModel response =
                    JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                            AddOptionResponseModel.class);

            assertThat(response.getProposalId()).isEqualTo(testProposalId);
            assertThat(response.getOptions()).containsExactly(userName);
        }
    }

    @Test
    void addOptionYoungMemberNoBoard() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        final int testHoaId = 0;
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        final String testTitle = "New Amazing Board Members";
        final String testMotion = "Choose!";
        final long weekInSeconds = 7 * 24 * 60 * 60;
        final int testProposalId = 3;

        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(userName);
        model.setProposalId(testProposalId);

        Proposal current = new Proposal();
        current.setHoaId(model.getHoaId());
        current.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        current.setOptionValidationService(new BoardElectionOptionValidationService());
        current.setVoteValidationService(new BoardElectionsVoteValidationService());
        current.setMotion(testMotion);
        current.setTitle(testTitle);
        current.setProposalId(testProposalId);
        when(proposalHandlingService.getProposalById(testProposalId)).thenReturn(Optional.of(current));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(current.getVotingDeadline());
        returned.setOptionValidationService(new BoardElectionOptionValidationService());
        returned.setVoteValidationService(new BoardElectionsVoteValidationService());
        returned.setMotion(current.getMotion());
        returned.setTitle(current.getTitle());
        returned.setProposalId(testProposalId);
        returned.setOptions(Set.of(new Option(userName)));
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        try (MockedStatic<HoaCommunication> com = Mockito.mockStatic(HoaCommunication.class)) {
            com.when(() -> HoaCommunication.checkUserIsBoardMember(userName, testHoaId))
                    .thenReturn(false);
            com.when(() -> HoaCommunication.checkUserIsMemberOfThisHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.checkUserIsNotBoardMemberOfAnyHoa(userName, testHoaId))
                    .thenReturn(true);
            com.when(() -> HoaCommunication.getJoiningBoardDate(userName, testHoaId))
                    .thenReturn(-1L);
            com.when(() -> HoaCommunication.checkHoaHasPossibleCandidates(userName, testHoaId))
                    .thenReturn(false);
            final long yearInSeconds = 365 * 24 * 60 * 60;
            final long dayInSeconds = 24 * 60 * 60;
            com.when(() -> HoaCommunication.getJoiningDate(userName, testHoaId))
                    .thenReturn(Instant.now()
                            .minusSeconds(3 * yearInSeconds)
                            .plusSeconds(dayInSeconds).toEpochMilli());

            // Act
            ResultActions resultActions = mockMvc.perform(post("/add-option")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer MockedToken")
                    .content(JsonUtil.serialize(model)));

            // Assert
            resultActions.andExpect(status().isOk());

            AddOptionResponseModel response =
                    JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                            AddOptionResponseModel.class);

            assertThat(response.getProposalId()).isEqualTo(testProposalId);
            assertThat(response.getOptions()).containsExactly(userName);
        }
    }

    @Test
    void addOptionNullRequest() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/add-option")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(null)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void startproposalNotFound() throws Exception {
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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);
        when(proposalHandlingService.getProposalById((testProposalId))).thenReturn(Optional.of(returned));
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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(false);
        when(proposalHandlingService.getProposalById((testProposalId))).thenReturn(Optional.of(returned));
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
    void startProposalUnauthorizes() throws Exception {
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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(true);
        when(proposalHandlingService.getProposalById((testProposalId))).thenReturn(Optional.of(returned));
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
