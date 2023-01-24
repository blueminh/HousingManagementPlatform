package sem.voting.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalHandlingService;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.Vote;
import sem.voting.domain.services.OptionValidationService;
import sem.voting.domain.services.VoteValidationService;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;
import sem.voting.domain.services.implementations.BoardElectionsVoteValidationService;
import sem.voting.integration.utils.JsonUtil;
import sem.voting.models.AddOptionRequestModel;
import sem.voting.models.AddOptionResponseModel;
import sem.voting.models.CastVoteRequestModel;
import sem.voting.models.ProposalInformationResponseModel;
import java.sql.Date;
import java.time.Instant;
import java.util.Set;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockProposalHandling"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class VotingInteractionControllerTest {
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

    private class TestOptionValidation implements OptionValidationService {
        private boolean returnVal;

        public TestOptionValidation(boolean returnVal) {
            this.returnVal = returnVal;
        }

        @Override
        public boolean isOptionValid(String userId, Option option, Proposal proposal) {
            return returnVal;
        }
    }

    private class TestVoteValidation implements VoteValidationService {
        private boolean returnVal;

        public TestVoteValidation(boolean returnVal) {
            this.returnVal = returnVal;
        }

        @Override
        public boolean isVoteValid(Vote vote, Proposal proposal) {
            return returnVal;
        }
    }

    @Test
    void voteOk() throws Exception {
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
        final String candidate = "ExampleUser2";

        CastVoteRequestModel model = new CastVoteRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(candidate);
        model.setUsername(userName);
        model.setProposalId(testProposalId);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        returned.setOptionValidationService(new TestOptionValidation(true));
        returned.setVoteValidationService(new TestVoteValidation(true));
        returned.setMotion(testMotion);
        returned.setTitle(testTitle);
        returned.setProposalId(testProposalId);
        returned.setStatus(ProposalStage.Voting);
        returned.setOptions(Set.of(new Option(candidate)));
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(returned);
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        ProposalInformationResponseModel response =
                JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                        ProposalInformationResponseModel.class);

        assertThat(response.getProposalId()).isEqualTo(testProposalId);
        assertThat(response.getHoaId()).isEqualTo(testHoaId);
    }

    @Test
    void voteInvalidVote() throws Exception {
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
        final String candidate = "ExampleUser2";

        CastVoteRequestModel model = new CastVoteRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(candidate);
        model.setUsername(userName);
        model.setProposalId(testProposalId);

        TestProposal returned = new TestProposal();
        returned.setHoaId(model.getHoaId());
        returned.setVotingDeadline(Date.from(Instant.now().plusSeconds(weekInSeconds)));
        returned.setOptionValidationService(new TestOptionValidation(true));
        returned.setVoteValidationService(new TestVoteValidation(false));
        returned.setMotion(testMotion);
        returned.setTitle(testTitle);
        returned.setProposalId(testProposalId);
        returned.setStatus(ProposalStage.Voting);
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(returned);
        when(proposalHandlingService.save(any(Proposal.class))).thenReturn(returned);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void voteNullProposal() throws Exception {
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
        final String candidate = "ExampleUser2";

        CastVoteRequestModel model = new CastVoteRequestModel();
        model.setHoaId(testHoaId);
        model.setOption(candidate);
        model.setUsername(userName);
        model.setProposalId(testProposalId);

        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(null);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void voteNullRequest() throws Exception {
        // Arrange
        final String userName = "ExampleUser";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(null)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
        when(proposalHandlingService.checkHoa(testProposalId, testHoaId)).thenReturn(current);

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
}