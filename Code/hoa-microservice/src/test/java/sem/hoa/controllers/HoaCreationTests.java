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
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.entities.HasEvents;
import sem.hoa.domain.services.HOARepository;
import sem.hoa.domain.services.HOAService;
import sem.hoa.dtos.HoaModifyDTO;
import sem.hoa.utils.JsonUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class HoaCreationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient HOAService hoaServiceMock;

    @Autowired
    private transient HOARepository hoaRepoMock;


    @Test
    public void createOne() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");

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

        HOA given = new HOA(request.hoaName, request.userCountry, request.userCity);
        HOA responded = JsonUtil
                .deserialize(resultActions.andReturn().getResponse().getContentAsString(),
                HOA.class);
        HOA saved = hoaRepoMock.findById(responded.getId()).orElseThrow();

        assertThat(saved.getHoaName()).isEqualTo(given.getHoaName());

    }
}
