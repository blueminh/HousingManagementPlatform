package sem.voting.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sem.voting.domain.proposal.ProposalRepository;

/**
 * Profile to mock proposal repo.
 */
@Profile("mockProposalRepository")
@Configuration
public class MockProposalRepository {
    /**
     * It does what it says it does.
     *
     * @return a mock of the repo
     */
    @Bean
    @Primary
    public ProposalRepository getMockProposalRepository() {
        return Mockito.mock(ProposalRepository.class);
    }
}
