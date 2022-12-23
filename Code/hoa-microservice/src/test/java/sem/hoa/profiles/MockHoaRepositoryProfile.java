package sem.hoa.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sem.hoa.domain.services.HOARepository;

@Profile("hoaRepo")
@Configuration
public class MockHoaRepositoryProfile {
    /**
     * Mocks the hoa repo.
     *
     * @return mocked hoa repo
     */
    @Bean
    @Primary
    public HOARepository getHoaRepository() {
        return Mockito.mock(HOARepository.class);
    }
}
