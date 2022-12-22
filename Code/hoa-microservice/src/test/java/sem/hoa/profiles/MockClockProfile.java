package sem.hoa.profiles;


import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sem.hoa.domain.utils.Clock;

@Profile("clock")
@Configuration
public class MockClockProfile {

    /**
     * Mocks the Clock.
     *
     * @return A mocked Clock
     */
    @Bean
    @Primary
    public Clock getClock() {
        return Mockito.mock(Clock.class);
    }
}
