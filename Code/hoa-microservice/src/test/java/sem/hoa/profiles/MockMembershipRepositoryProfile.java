package sem.hoa.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sem.hoa.domain.services.MemberManagementRepository;

@Profile("membershipRepo")
@Configuration
public class MockMembershipRepositoryProfile {
    /**
     * Mocks the membership repo.
     *
     * @return mocked membership repo
     */
    @Bean
    @Primary
    public MemberManagementRepository getMockMembershipManagementRepository() {
        return Mockito.mock(MemberManagementRepository.class);
    }
}
