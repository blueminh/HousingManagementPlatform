package sem.hoa.integration;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import sem.hoa.authentication.AuthManager;
import sem.hoa.authentication.JwtTokenVerifier;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipId;
import sem.hoa.domain.services.HoaRepository;
import sem.hoa.domain.services.HoaService;
import sem.hoa.domain.services.MemberManagementRepository;
import sem.hoa.dtos.HoaModifyDTO;
import sem.hoa.utils.JsonUtil;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class HoaControllerTests {

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
    private transient MemberManagementRepository memberRepoMock;

    private HoaModifyDTO request;

    @BeforeEach
    void setUp() {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        request = new HoaModifyDTO();
        request.setHoaName("exampleName");
        request.setUserCity("exUserCity");
        request.setUserCountry("exUserCountry");
        request.setUserStreet("Jump Street");
        request.setUserHouseNumber(21);
        request.setUserPostalCode("JUMP");
    }

    /**
     * try to create a HOA normally using the endpoint for it.
     *
     */
    @Test
    public void createOne() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/createHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());

        Hoa responded = JsonUtil
                .deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                        Hoa.class);

        Hoa saved = hoaRepoMock.findById(responded.getId()).orElseThrow();

        Hoa given = new Hoa(request.hoaName, request.userCountry, request.userCity);

        assertThat(saved.getHoaName()).isEqualTo(given.getHoaName());
        assertThat(saved.getCity()).isEqualTo(given.getCity());
        assertThat(saved.getCountry()).isEqualTo(given.getCountry());
        assertThat(saved.getId()).isEqualTo(1);

        Optional<Membership> savedOpt = memberRepoMock
                .findById(new MembershipId(mockAuthenticationManager.getUsername(), 1));

        assertThat(savedOpt.isPresent());
        assertThat(savedOpt.get().isBoardMember());
        assertThat(savedOpt.get().getCity()).isEqualTo(request.getUserCity());
        assertThat(savedOpt.get().getCountry()).isEqualTo(request.getUserCountry());
    }

    /**
     *  Try to pass a blank String or null or an int<0 as one of the variables
     *  should return bad request and nothing should be saved.
     *
     */
    @Test
    public void createBadRequest() throws Exception {

        request.setUserCity("");

        ResultActions resultActions = mockMvc.perform(post("/createHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("Attempted to create HOA, but Fields can not be Empty"));

        request.setUserCity(null);

        resultActions = mockMvc.perform(post("/createHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("Attempted to create HOA, but Fields can not be Invalid(null)"));

        request.setUserCity("null");

        resultActions = mockMvc.perform(post("/createHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());

        request.setUserHouseNumber(-2);

        resultActions = mockMvc.perform(post("/createHOA")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("Attempted to create HOA, but House Number must be a positive integer"));

    }

    /**
     * Test for normal joining of HOA.
     */
    @Test
    public void joinHoa() throws Exception {

        hoaServiceMock.createNewHOA(new Hoa(request.hoaName, request.userCountry,
                request.userCity));

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));


        resultActions.andExpect(status().isOk());

        Optional<Membership> savedOpt = memberRepoMock
                .findById(new MembershipId(mockAuthenticationManager.getUsername(), 1));

        assertThat(savedOpt.isPresent());
        assertThat(savedOpt.get().isBoardMember()).isFalse();
        assertThat(savedOpt.get().getCity()).isEqualTo(request.getUserCity());
        assertThat(savedOpt.get().getCountry()).isEqualTo(request.getUserCountry());

    }

    /**
     * Test when two different people join the HOA
     */
    @Test
    public void joinHoaTwo() throws Exception {

        hoaServiceMock.createNewHOA(new Hoa(request.hoaName, request.userCountry,
                request.userCity));

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));


        resultActions.andExpect(status().isOk());

        Optional<Membership> savedOpt = memberRepoMock
                .findById(new MembershipId(mockAuthenticationManager.getUsername(), 1));

        assertThat(savedOpt.isPresent());

        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser2");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser2");

        request.setUserHouseNumber(25);
        request.setUserPostalCode("HOP");

        resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());

        Optional<Membership> saved2Opt = memberRepoMock
                .findById(new MembershipId(mockAuthenticationManager.getUsername(), 1));

        assertThat(saved2Opt.isPresent());
        assertThat(saved2Opt.get().isBoardMember()).isFalse();
        assertThat(saved2Opt.get().getPostalCode()).isEqualTo(request.getUserPostalCode());
        assertThat(saved2Opt.get().getHouseNumber()).isEqualTo(request.getUserHouseNumber());

    }
    /**
     * Try to join a non-existing HOA
     */
    @Test
    public void joinHoaBadName() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("No such HOA with this name: " + request.hoaName));
    }

    /**
     * Trying to join a HOA that the user is already in.
     */
    @Test
    public void joinHoaButAlreadyJoined() throws Exception {

        hoaServiceMock.createNewHOA(new Hoa(request.hoaName, request.userCountry,
                request.userCity));

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isOk());

        resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("User is already in this HOA"));

    }

    /**
     *  Test if the city of the applicant match to the HOA
     */
    @Test
    public void joinHoaBadCity() throws Exception {
        hoaServiceMock.createNewHOA(new Hoa(request.hoaName, request.userCountry,
                request.userCity));

        request.setUserCity("wrongCity");

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("Address not compatible with HOA area"));
    }

    /**
     *  Test if the country of the applicant match to the HOA
     */
    @Test
    public void joinHoaBadCountry() throws Exception {
        hoaServiceMock.createNewHOA(new Hoa(request.hoaName, request.userCountry,
                request.userCity));

        request.setUserCountry("wrongCountry");

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("Address not compatible with HOA area"));
    }

    /**
     *  Test if the country and city of the applicant match to the HOA
     */
    @Test
    public void joinHoaBadAddress() throws Exception {
        hoaServiceMock.createNewHOA(new Hoa(request.hoaName, request.userCountry,
                request.userCity));

        request.setUserCountry("wrongCountry");
        request.setUserCity("wrongCity");

        ResultActions resultActions = mockMvc.perform(post("/joining")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken")
                .content(JsonUtil.serialize(request)));

        resultActions.andExpect(status().isBadRequest())
                .andExpect(status().reason("Address not compatible with HOA area"));
    }
}

