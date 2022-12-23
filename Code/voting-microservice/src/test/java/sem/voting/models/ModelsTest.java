package sem.voting.models;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sem.voting.domain.proposal.Option;
import sem.voting.domain.proposal.Proposal;
import sem.voting.domain.proposal.ProposalStage;
import sem.voting.domain.proposal.ProposalType;
import sem.voting.domain.proposal.Result;
import sem.voting.domain.services.implementations.BoardElectionOptionValidationService;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ModelsTest {

    @Test
    void addOptionRequestModelTest() {
        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(0);
        model.setProposalId(0);
        model.setOption("option");
        assertEquals(0, model.getHoaId());
        assertEquals(0, model.getProposalId());
        assertEquals("option", model.getOption());

    }

    @Test
    void addOptionRequestModelEqualsTest() {
        AddOptionRequestModel model = new AddOptionRequestModel();
        model.setHoaId(1);
        model.setProposalId(1);
        model.setOption("option");
        AddOptionRequestModel model2 = new AddOptionRequestModel();
        model2.setHoaId(1);
        model2.setProposalId(1);
        model2.setOption("option");
        assertEquals(model, model2);
        assertEquals(model.hashCode(), model2.hashCode());
        model.setOption("option2");
        assertNotEquals(model, model2);
        assertNotEquals(null, model);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void addOptionResponseModelTest() {
        AddOptionResponseModel model = new AddOptionResponseModel();
        model.setHoaId(1);
        model.setOptions(new ArrayList<>());
        model.setProposalId(1);
        assertEquals(1, model.getHoaId());
        assertEquals(1, model.getProposalId());
        assertEquals(new ArrayList<>(), model.getOptions());
    }

    @Test
    void addOptionResponseModelEqualsTest() {
        AddOptionResponseModel model = new AddOptionResponseModel();
        model.setHoaId(1);
        model.setOptions(new ArrayList<>());
        model.setProposalId(1);
        AddOptionResponseModel model2 = new AddOptionResponseModel();
        model2.setHoaId(1);
        model2.setOptions(new ArrayList<>());
        model2.setProposalId(1);
        assertEquals(model, model2);
        assertEquals(model.hashCode(), model2.hashCode());
        assertNotEquals(null, model);
        model2.setProposalId(5);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void castVoteRequestModelTest() {
        CastVoteRequestModel model = new CastVoteRequestModel();
        model.setHoaId(1);
        model.setOption("option");
        model.setProposalId(1);
        model.setUsername("username");
        assertEquals(1, model.getHoaId());
        assertEquals("option", model.getOption());
        assertEquals(1, model.getProposalId());
        assertEquals("username", model.getUsername());
    }

    @Test
    void castVoteRequestModelEqualsTest() {
        CastVoteRequestModel model = new CastVoteRequestModel();
        model.setHoaId(1);
        model.setOption("option");
        model.setProposalId(1);
        model.setUsername("username");
        CastVoteRequestModel model2 = new CastVoteRequestModel();
        model2.setHoaId(1);
        model2.setOption("option");
        model2.setProposalId(1);
        model2.setUsername("username");
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setUsername("another username");
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalCreationRequestModelTest() {
        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        java.util.Date date = Date.from(Instant.now());
        model.setDeadline(date);
        model.setType(ProposalType.HoaRuleChange);
        model.setTitle("title");
        model.setMotion("motion");
        model.setHoaId(1);
        model.setOptions(new ArrayList<>());
        assertEquals(date, model.getDeadline());
        assertEquals(ProposalType.HoaRuleChange, model.getType());
        assertEquals("title", model.getTitle());
        assertEquals("motion", model.getMotion());
        assertEquals(1, model.getHoaId());
        assertEquals(new ArrayList<>(), model.getOptions());
    }

    @Test
    void proposalCreationRequestModelEqualsTest() {
        ProposalCreationRequestModel model = new ProposalCreationRequestModel();
        java.util.Date date = Date.from(Instant.now());
        model.setDeadline(date);
        model.setType(ProposalType.HoaRuleChange);
        model.setTitle("title");
        model.setMotion("motion");
        model.setHoaId(1);
        model.setOptions(new ArrayList<>());
        ProposalCreationRequestModel model2 = new ProposalCreationRequestModel();
        model2.setDeadline(date);
        model2.setType(ProposalType.HoaRuleChange);
        model2.setTitle("title");
        model2.setMotion("motion");
        model2.setHoaId(1);
        model2.setOptions(new ArrayList<>());
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setType(ProposalType.BoardElection);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalCreationResposeModelTest() {
        ProposalCreationResponseModel model = new ProposalCreationResponseModel();
        model.setProposalId(1);
        assertEquals(1, model.getProposalId());
        ProposalCreationResponseModel model2 = new ProposalCreationResponseModel(1);
        assertEquals(1, model2.getProposalId());
    }

    @Test
    void proposalCreationResposeModelEqualsTest() {
        ProposalCreationResponseModel model = new ProposalCreationResponseModel();
        model.setProposalId(1);
        ProposalCreationResponseModel model2 = new ProposalCreationResponseModel();
        model2.setProposalId(1);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setProposalId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalGenericRequestModelTest() {
        ProposalGenericRequestModel model = new ProposalGenericRequestModel();
        model.setProposalId(1);
        model.setHoaId(1);
        assertEquals(1, model.getProposalId());
        assertEquals(1,  model.getHoaId());
    }

    @Test
    void proposalGenericRequestModelEqualsTest() {
        ProposalGenericRequestModel model = new ProposalGenericRequestModel();
        model.setProposalId(1);
        model.setHoaId(1);
        ProposalGenericRequestModel model2 = new ProposalGenericRequestModel();
        model2.setProposalId(1);
        model2.setHoaId(1);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setProposalId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalHistoryResponseModelTest() {
        java.util.Date date = Date.from(Instant.now());
        Proposal proposal = new Proposal();
        proposal.setHoaId(1);
        proposal.setProposalId(1);
        proposal.setTitle("title");
        proposal.setMotion("motion");
        proposal.setVotingDeadline(date);
        ProposalHistoryResponseModel model = new ProposalHistoryResponseModel(proposal);
        assertEquals(1, model.getProposalId());
        assertEquals(1, model.getHoaId());
        assertEquals("motion", model.getMotion());
        assertEquals("title", model.getTitle());
        assertEquals(date, model.getDeadline());
        model.setProposalId(2);
        model.setDeadline(date);
        model.setTitle("title");
        model.setMotion("motion");
        model.setHoaId(2);
        model.setResults(null);
        model.setOptions(null);
        model.setStatus(ProposalStage.Ended);
        assertEquals(2, model.getProposalId());
        assertEquals(2, model.getHoaId());
        assertEquals("motion", model.getMotion());
        assertEquals("title", model.getTitle());
        assertEquals(date, model.getDeadline());
        assertNull(model.getOptions());
        assertNull(model.getResults());
        assertEquals(ProposalStage.Ended, model.getStatus());
    }

    @Test
    void proposalHistoryResponseModelGetAllTest() {
        java.util.Date date = Date.from(Instant.now());
        Proposal proposal = Mockito.mock(Proposal.class);
        Set<Result> set = new HashSet<>();
        set.add(new Result(new Option("option"), 10));
        when(proposal.getResults()).thenReturn(set);
        ProposalHistoryResponseModel model = new ProposalHistoryResponseModel(proposal);
        assertTrue(model.getResults().contains(10));
        assertTrue(model.getOptions().contains("option"));
    }


    @Test
    void proposalHistoryResponseModelEqualsTest() {
        java.util.Date date = Date.from(Instant.now());
        Proposal proposal = new Proposal();
        proposal.setHoaId(1);
        proposal.setProposalId(1);
        proposal.setTitle("title");
        proposal.setMotion("motion");
        proposal.setVotingDeadline(date);
        ProposalHistoryResponseModel model = new ProposalHistoryResponseModel(proposal);
        ProposalHistoryResponseModel model2 = new ProposalHistoryResponseModel(proposal);
        model.setStatus(ProposalStage.Ended);
        model2.setStatus(ProposalStage.Ended);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setProposalId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalInfoRequestModelTest() {
        ProposalInfoRequestModel model = new ProposalInfoRequestModel();
        model.setHoaId(1);
        assertEquals(1, model.getHoaId());
    }

    @Test
    void proposalInfoRequestModelEqualsTest() {
        ProposalInfoRequestModel model = new ProposalInfoRequestModel();
        model.setHoaId(1);
        ProposalInfoRequestModel model2 = new ProposalInfoRequestModel();
        model2.setHoaId(1);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setHoaId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalInformationResponseModelTest() {
        java.util.Date date = Date.from(Instant.now());
        ProposalInformationResponseModel model = new ProposalInformationResponseModel(new Proposal());
        model.setDeadline(date);
        model.setProposalId(1);
        model.setStatus(ProposalStage.Ended);
        model.setType(ProposalType.BoardElection);
        model.setMotion("motion");
        model.setOptions(null);
        model.setTitle("title");
        model.setHoaId(1);
        assertEquals(date, model.getDeadline());
        assertEquals(1, model.getProposalId());
        assertEquals(ProposalStage.Ended, model.getStatus());
        assertEquals(ProposalType.BoardElection, model.getType());
        assertEquals("motion", model.getMotion());
        assertNull(model.getOptions());
        assertEquals("title", model.getTitle());
        assertEquals(1, model.getHoaId());
    }

    @Test
    void proposalInformationResponseModelBranchTest() {
        java.util.Date date = Date.from(Instant.now());
        Proposal proposal = new Proposal();
        proposal.setOptionValidationService(new BoardElectionOptionValidationService());
        ProposalInformationResponseModel model = new ProposalInformationResponseModel(proposal);
        assertEquals(ProposalType.BoardElection, model.getType());
    }

    @Test
    void proposalInformationResponseModelEqualsTest() {
        java.util.Date date = Date.from(Instant.now());
        ProposalInformationResponseModel model = new ProposalInformationResponseModel(new Proposal());
        model.setDeadline(date);
        model.setProposalId(1);
        model.setStatus(ProposalStage.Ended);
        model.setType(ProposalType.BoardElection);
        model.setMotion("motion");
        model.setOptions(null);
        model.setTitle("title");
        model.setHoaId(1);
        ProposalInformationResponseModel model2 = new ProposalInformationResponseModel(new Proposal());
        model2.setDeadline(date);
        model2.setProposalId(1);
        model2.setStatus(ProposalStage.Ended);
        model2.setType(ProposalType.BoardElection);
        model2.setMotion("motion");
        model2.setOptions(null);
        model2.setTitle("title");
        model2.setHoaId(1);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setHoaId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalResultsResponseModelTest() {
        ProposalResultsResponseModel model = new ProposalResultsResponseModel();
        model.setResults(new ArrayList<>());
        model.setProposalId(1);
        model.setOptions(new ArrayList<>());
        model.setHoaId(1);
        assertEquals(model.getResults(), new ArrayList<>());
        assertEquals(1, model.getProposalId());
        assertEquals(new ArrayList<>(), model.getOptions());
        assertEquals(1, model.getHoaId());
        assertEquals(new ArrayList<>(), model.getResults());
        Set<Result> set = new HashSet<>();
        set.add(new Result(new Option("option"), 10));
        model.setAllResults(set);
        assertTrue(model.getResults().contains(10));
        assertTrue(model.getOptions().contains("option"));
    }

    @Test
    void proposalResultsResponseModelEqualsTest() {
        ProposalResultsResponseModel model = new ProposalResultsResponseModel();
        model.setResults(new ArrayList<>());
        model.setProposalId(1);
        model.setOptions(new ArrayList<>());
        model.setHoaId(1);
        ProposalResultsResponseModel model2 = new ProposalResultsResponseModel();
        model2.setResults(new ArrayList<>());
        model2.setProposalId(1);
        model2.setOptions(new ArrayList<>());
        model2.setHoaId(1);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setHoaId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }

    @Test
    void proposalStartVotingResponseModelTest() {
        ProposalStartVotingResponseModel model = new ProposalStartVotingResponseModel();
        model.setHoaId(1);
        model.setProposalId(1);
        model.setStatus(ProposalStage.Ended);
        assertEquals(1, model.getHoaId());
        assertEquals(1, model.getProposalId());
        assertEquals(ProposalStage.Ended, model.getStatus());
    }

    @Test
    void proposalStartVotingResponseModelEqualsTest() {
        ProposalStartVotingResponseModel model = new ProposalStartVotingResponseModel();
        model.setHoaId(1);
        model.setProposalId(1);
        model.setStatus(ProposalStage.Ended);
        ProposalStartVotingResponseModel model2 = new ProposalStartVotingResponseModel();
        model2.setHoaId(1);
        model2.setProposalId(1);
        model2.setStatus(ProposalStage.Ended);
        assertEquals(model, model2);
        assertNotEquals(null, model);
        assertEquals(model.hashCode(), model2.hashCode());
        model2.setHoaId(2);
        assertNotEquals(model, model2);
        assertFalse(model.equals(new ArrayList<>()));
    }
}
