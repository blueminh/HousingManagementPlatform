package sem.hoa.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import sem.hoa.authentication.AuthManager;
import sem.hoa.authentication.JwtTokenVerifier;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.services.HoaRepository;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.RuleRepository;
import sem.hoa.dtos.rulemodels.RulesResponseModel;
import sem.hoa.dtos.rulemodels.AddRuleRequestModel;
import sem.hoa.dtos.rulemodels.AddRuleResponseModel;
import sem.hoa.dtos.rulemodels.DeleteRuleModel;
import sem.hoa.dtos.rulemodels.EditRuleModel;
import sem.hoa.dtos.rulemodels.RulesRequestModel;

import sem.hoa.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient HoaService hoaServiceMock;

    @Autowired
    private transient HoaRepository hoaRepoMock;

    @Autowired
    private transient RuleRepository ruleRepoMock;

    @Test
    public void emptyDisplayRules() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");

        hoaRepoMock.save(hoa);
        assertThat(hoa.getId()).isEqualTo(1);

        RulesRequestModel requestModel = new RulesRequestModel();
        requestModel.setHoaId(1);

        ResultActions resultActions = mockMvc.perform(get("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk());
        RulesResponseModel actual = JsonUtil
                .deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                        RulesResponseModel.class);
        RulesResponseModel expected = new RulesResponseModel();
        expected.setRules(new ArrayList<>());
        expected.setHoaId(1);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void displayRulesHoaNotFound() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");

        hoaRepoMock.save(hoa);

        RulesRequestModel requestModel = new RulesRequestModel();
        requestModel.setHoaId(2);

        ResultActions resultActions = mockMvc.perform(get("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void displayRulesHoaBadRequest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");

        hoaRepoMock.save(hoa);

        ResultActions resultActions = mockMvc.perform(get("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(null)));

        resultActions.andExpect(status().isBadRequest());
    }

    //added an assert statement that checks whether the hoa in the response is set properly
    @Test
    public void addTestAfterMutation() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");

        hoaRepoMock.save(hoa);

        AddRuleRequestModel requestModel =  new AddRuleRequestModel();
        requestModel.setHoaId(1);
        requestModel.setDescription("newRule");

        ResultActions resultActions = mockMvc.perform(post("/add-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk());

        Optional<Rule> rule = this.ruleRepoMock.findById(1);
        AddRuleResponseModel responseModel = JsonUtil
                .deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                        AddRuleResponseModel.class);
        assertThat(responseModel.getHoaId()).isEqualTo(requestModel.getHoaId());
        assertThat(responseModel.getRules().get(0).getDescription()).isEqualTo(rule.get().getDescription());
    }

    @Test
    public void notFoundAddRule() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");

        hoaRepoMock.save(hoa);

        AddRuleRequestModel requestModel =  new AddRuleRequestModel();
        requestModel.setHoaId(2);
        requestModel.setDescription("newRule");

        ResultActions resultActions = mockMvc.perform(post("/add-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void addRuleBadRequest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");

        hoaRepoMock.save(hoa);

        ResultActions resultActions = mockMvc.perform(post("/add-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(null)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void editRule() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        EditRuleModel requestModel = new EditRuleModel();
        requestModel.setRuleId(1);
        requestModel.setChange("newNewRule");

        ResultActions resultActions = mockMvc.perform(post("/edit-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk());

        Optional<Rule> rule1 = ruleRepoMock.findById(1);
        assertThat(rule1.get().getDescription()).isEqualTo("newNewRule");
        assertThat(rule1.get().getDescription()).isNotEqualTo("newRule");
    }

    @Test
    public void editRuleNotFound() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        EditRuleModel requestModel = new EditRuleModel();
        requestModel.setRuleId(2);
        requestModel.setChange("newNewRule");

        ResultActions resultActions = mockMvc.perform(post("/edit-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void editRuleBadRequest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        ResultActions resultActions = mockMvc.perform(post("/edit-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(null)));

        resultActions.andExpect(status().isBadRequest());
    }

    //tests that after a rule is added the size is 1, and after the delete-rule endpoint is called the size is 0
    @Test
    public void deleteRule() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        RulesRequestModel rulesRequestModelTest = new RulesRequestModel();
        rulesRequestModelTest.setHoaId(1);

        ResultActions resultActionsTest = mockMvc.perform(get("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(rulesRequestModelTest)));

        RulesResponseModel responseTest =
                JsonUtil.deserialize(resultActionsTest.andReturn().getResponse().getContentAsString(),
                        RulesResponseModel.class);

        assertThat(responseTest.getRules().size() == 1).isTrue();

        assertThat(this.ruleRepoMock.findById(1).isEmpty()).isFalse();

        DeleteRuleModel requestModel = new DeleteRuleModel();
        requestModel.setRuleId(1);
        requestModel.setHoaId(1);

        ResultActions resultActions = mockMvc.perform(delete("/delete-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isOk());
        assertThat(this.ruleRepoMock.findById(1).isEmpty()).isTrue();

        RulesRequestModel rulesRequestModel = new RulesRequestModel();
        rulesRequestModel.setHoaId(1);

        ResultActions resultActions2 = mockMvc.perform(get("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        RulesResponseModel response =
                JsonUtil.deserialize(resultActions2.andReturn().getResponse().getContentAsString(),
                        RulesResponseModel.class);

        assertThat(response.getRules().isEmpty()).isTrue();

    }

    @Test
    public void deleteRuleNotFoundRuleId() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        DeleteRuleModel requestModel = new DeleteRuleModel();
        requestModel.setRuleId(2);
        requestModel.setHoaId(1);

        ResultActions resultActions = mockMvc.perform(delete("/delete-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void deleteRuleNotFoundHoaId() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        DeleteRuleModel requestModel = new DeleteRuleModel();
        requestModel.setRuleId(1);
        requestModel.setHoaId(3);

        ResultActions resultActions = mockMvc.perform(delete("/delete-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(requestModel)));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void deleteRuleBadRequest() throws Exception {
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        Hoa hoa = new Hoa("ExampleName", "ExampleCountry", "ExampleCity");
        hoaRepoMock.save(hoa);

        Rule rule = new Rule(1, "newRule");
        ruleRepoMock.save(rule);

        DeleteRuleModel request = null;

        ResultActions resultActions = mockMvc.perform(delete("/delete-rule")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest());
    }
}
