package sem.hoa.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import sem.hoa.authentication.AuthManager;
import sem.hoa.authentication.JwtTokenVerifier;
import sem.hoa.domain.entities.HOA;
import sem.hoa.domain.services.HOARepository;
import sem.hoa.domain.services.HOAService;
import sem.hoa.exceptions.HoaCreationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class HoaServiceTests {

    @Autowired
    private transient HOAService hoaServiceMock;

    @Autowired
    private transient HOARepository hoaRepoMock;

    /**
     *  tests the normal behaviour of the service related
     *  to HOA creation.
     *
     */
    @Test
    public void createService() throws Exception {
        HOA hoaT = new HOA("name", "country", "city");
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
        HOA hoaT = new HOA("name", "country", "city");
        hoaServiceMock.createNewHOA(hoaT);
        HOA hoaD = new HOA("name", "diffCountry", "diffCity");

        assertThatThrownBy(() -> hoaServiceMock.createNewHOA(hoaD))
                .isInstanceOf(HoaCreationException.class);
        assertThatThrownBy(() -> hoaServiceMock.createNewHOA(hoaD))
                .hasMessage("HOA was not saved successfully: HOA already exists");
    }

}