package sem.hoa.controllers;

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
import sem.hoa.domain.services.HoaRepository;
import sem.hoa.domain.services.HoaService;
import sem.hoa.dtos.HoaModifyDTO;
import sem.hoa.utils.JsonUtil;

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


    /**
     * try to create a HOA normally using the endpoint for it.
     *
     */
    @Test
    public void createOne() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        HoaModifyDTO request = new HoaModifyDTO();
        request.setHoaName("exampleName");
        request.setUserCity("exUserCity");
        request.setUserCountry("exUserCountry");

        request.setUserStreet("Jump Street");
        request.setUserHouseNumber(21);
        request.setUserPostalCode("JUMP");


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
    }

    /**
     *  Try to pass a blank String or null or an int<0 as one of the variables
     *  should return bad request and nothing should be saved.
     *
     */
    @Test
    public void createBadRequest() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getUsername()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn("ExampleUser");

        HoaModifyDTO request = new HoaModifyDTO();
        request.setHoaName("exampleName");

        request.setUserCity("");

        request.setUserCountry("exUserCountry");

        request.setUserStreet("Jump Street");
        request.setUserHouseNumber(21);
        request.setUserPostalCode("JUMP");


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
     *  tests the normal behaviour of the service related
     *  to HOA creation.
     *
     */
    @Test
    public void createService() throws Exception {
        Hoa hoaT = new Hoa("name", "country", "city");
        hoaServiceMock.createNewHOA(hoaT);
        assertThat(hoaRepoMock.findByHoaName(hoaT.getHoaName())).isPresent();
    }

    /**
     *  tests the behaviour of the service related
     *  to HOA creation when trying to save a HOA with an existing name.
     *
     */
    @Test
    public void createServiceDup() throws Exception {
        Hoa hoaT = new Hoa("name", "country", "city");
        hoaServiceMock.createNewHOA(hoaT);
        Hoa hoaD = new Hoa("name", "diffCountry", "diffCity");

        assertThatThrownBy(() -> hoaServiceMock.createNewHOA(hoaD))
                .isInstanceOf(HoaCreationException.class);
        assertThatThrownBy(() -> hoaServiceMock.createNewHOA(hoaD))
                .hasMessage("HOA was not saved successfully: HOA already exists");
    }

}

