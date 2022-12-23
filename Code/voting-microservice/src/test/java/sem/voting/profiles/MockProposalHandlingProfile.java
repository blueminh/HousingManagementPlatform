package sem.voting.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sem.voting.domain.proposal.ProposalHandlingService;

/**
 * Profile to mock proposal handling.
 */
@Profile("mockProposalHandling")
@Configuration
public class MockProposalHandlingProfile {
    /**
     * It does what it says it does.
     *
     * @return a mock of the repo
     */
    @Bean
    @Primary
    public ProposalHandlingService getMockProposalHandlingService() {
        return Mockito.mock(ProposalHandlingService.class);
    }
}
