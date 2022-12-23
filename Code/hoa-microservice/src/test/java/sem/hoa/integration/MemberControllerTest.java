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
import org.springframework.test.web.servlet.ResultActions;
import sem.hoa.authentication.AuthManager;
import sem.hoa.authentication.JwtTokenVerifier;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.domain.entities.Membership;
import sem.hoa.domain.entities.MembershipId;
import sem.hoa.domain.services.HoaRepository;
import sem.hoa.domain.services.MemberManagementRepository;
import sem.hoa.domain.services.MemberManagementService;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
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
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient MemberManagementService memberManagementService;

    @Autowired
    private transient MemberManagementRepository memberManagementRepository;

    @Autowired
    private transient HoaRepository hoaRepository;

    private Hoa hoa;
    private String userName;

    @BeforeEach
    void setUp() {
        userName = "user1";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        hoa = hoaRepository.save(new Hoa("hoa1", "country1", "city1"));
    }

    @Test
    void find_user_role_hoa_name_test_ok() {
        // Arrange
        final Membership membership = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaName")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaName", hoa.getHoaName())
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("normalMember");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void find_user_role_hoa_name_test_board_ok() {
        // Arrange
        final Membership membership = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            true, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));

        // Act
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaName")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaName", hoa.getHoaName())
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());

            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("boardMember");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void find_user_role_hoa_name_test_fail() {
        // Arrange
        final Membership membership = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaName")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaName", "randomName")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("No HOA with the name: randomName");
        } catch (Exception e) {
            fail("Exception when making request");
        }

        String userName2 = "user2";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName2);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName2);
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaName")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaName", hoa.getHoaName())
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("User is not registered in this Hoa");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void find_user_role_hoa_id_test_ok() {
        // Arrange
        final Membership membership = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("normalMember");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void find_user_role_hoa_id_test_board_ok() {
        // Arrange
        final Membership membership = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            true, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));

        // Act
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());

            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("boardMember");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void find_user_role_hoa_id_test_fail() {
        // Arrange
        final Membership membership = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", "123")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("No HOA with the id: 123");
        } catch (Exception e) {
            fail("Exception when making request");
        }

        userName = "user2";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/findUserRoleByHoaID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("User is not registered in this Hoa");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void board_member_of_any_true() {
        // Arrange
        String userName = "user1";
        when(mockAuthenticationManager.getUsername()).thenReturn(userName);
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getUsernameFromToken(anyString())).thenReturn(userName);

        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));

        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));
        final Membership membership2 = memberManagementRepository.save(new Membership("user1", hoa2.getId(),
            true, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/isaBoardMemberOfAny")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo(hoa2.getId() + "");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    void board_member_of_any_false() {
        // Arrange
        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));

        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));
        final Membership membership2 = memberManagementRepository.save(new Membership("user1", hoa2.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/isaBoardMemberOfAny")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo(-1 + "");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void is_member_of_ok() {
        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", new Date().getTime(), -1L));

        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));

        try {
            ResultActions resultActions = mockMvc.perform(get("/member/isMemberOf")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo(Boolean.toString(true));

            resultActions = mockMvc.perform(get("/member/isMemberOf")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa2.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo(Boolean.toString(false));
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void is_member_of_fail() {
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/isMemberOf")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", "123")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("No HOA with the id: 123");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void joining_date_ok() {
        Long currentTime = new Date().getTime();
        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", currentTime, -1L));

        try {
            ResultActions resultActions = mockMvc.perform(get("/member/joiningDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo(currentTime.toString());
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void joining_date_fail() {
        Long currentTime = new Date().getTime();
        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", currentTime, -1L));

        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));

        try {
            ResultActions resultActions = mockMvc.perform(get("/member/joiningDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", "123")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("No HOA with the id: 123");

            resultActions = mockMvc.perform(get("/member/joiningDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa2.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("User is not registered in this Hoa");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void joining_board_date_ok() {
        long currentTime = new Date().getTime();
        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            true, "country1", "city1", "street1", 1, "postal1", currentTime, currentTime + 100));

        try {
            ResultActions resultActions = mockMvc.perform(get("/member/joiningBoardDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo(currentTime + 100 + "");
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void joining_board_date_fail() {
        long currentTime = new Date().getTime();
        final Membership membership1 = memberManagementRepository.save(new Membership("user1", hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", currentTime, -1L));

        final Hoa hoa2 = hoaRepository.save(new Hoa("hoa2", "country1", "city1"));
        final Membership membership2 = memberManagementRepository.save(new Membership("user1", hoa2.getId(),
            true, "country1", "city1", "street1", 1, "postal1", currentTime, currentTime + 100));

        final Hoa hoa3 = hoaRepository.save(new Hoa("hoa3", "country1", "city1"));
        try {
            ResultActions resultActions = mockMvc.perform(get("/member/joiningBoardDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", "123")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("No HOA with the id: 123");

            resultActions = mockMvc.perform(get("/member/joiningBoardDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa3.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("User is not registered in this Hoa");

            resultActions = mockMvc.perform(get("/member/joiningBoardDate")
                .contentType(MediaType.APPLICATION_JSON)
                .param("hoaId", hoa.getId() + "")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            response = resultActions.andReturn().getResponse().getContentAsString();
            String beginningOfTime = Date.from(Instant.ofEpochMilli(0)).getTime() + "";
            assertThat(response).isEqualTo(beginningOfTime);
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void update_user_role_ok() {
        long currentTime = new Date().getTime();
        final Membership membership1 = memberManagementRepository.save(new Membership(userName, hoa.getId(),
            true, "country1", "city1", "street1", 1, "postal1", currentTime, currentTime));

        final Membership membership2 = memberManagementRepository.save(new Membership("user2", hoa.getId(),
            false, "country1", "city1", "street1", 2, "postal1", currentTime, -1L));

        final Membership membership3 = memberManagementRepository.save(new Membership("user3", hoa.getId(),
            false, "country1", "city1", "street1", 3, "postal1", currentTime, -1L));

        try {
            ResultActions resultActions = mockMvc.perform(post("/member/updateRole")
                .contentType(MediaType.APPLICATION_JSON)
                .param("toBeUpdated", "user2")
                .param("hoaId", hoa.getId() + "")
                .param("isBoardMember", "true")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isOk());
            Membership membership = memberManagementRepository.findById(new MembershipId("user2", 1)).get();
            assertThat(membership.isBoardMember()).isTrue();
            assertThat(membership.getJoiningBoardDate()).isNotEqualTo(-1L);
        } catch (Exception e) {
            fail("Exception when making request");
        }
    }

    @Test
    public void update_user_role_fail() {
        long currentTime = new Date().getTime();
        final Membership membership1 = memberManagementRepository.save(new Membership(userName, hoa.getId(),
            false, "country1", "city1", "street1", 1, "postal1", currentTime, -1L));

        final Membership membership2 = memberManagementRepository.save(new Membership("user2", hoa.getId(),
            true, "country1", "city1", "street1", 2, "postal1", currentTime, currentTime));

        final Membership membership3 = memberManagementRepository.save(new Membership("user3", 2,
            false, "country1", "city1", "street1", 3, "postal1", currentTime, -1L));

        try {
            ResultActions resultActions = mockMvc.perform(post("/member/updateRole")
                .contentType(MediaType.APPLICATION_JSON)
                .param("toBeUpdated", "user2")
                .param("hoaId", hoa.getId() + "")
                .param("isBoardMember", "true")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            String response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("This user does not have the rights to update role of another user");


            when(mockAuthenticationManager.getUsername()).thenReturn("user2");
            resultActions = mockMvc.perform(post("/member/updateRole")
                .contentType(MediaType.APPLICATION_JSON)
                .param("toBeUpdated", "user3")
                .param("hoaId", hoa.getId() + "")
                .param("isBoardMember", "true")
                .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isBadRequest());
            response = resultActions.andReturn().getResponse().getContentAsString();
            assertThat(response).isEqualTo("User to be updated is not in this HOA");

        } catch (Exception e) {
            fail("Exception when making request");
        }
    }
}
