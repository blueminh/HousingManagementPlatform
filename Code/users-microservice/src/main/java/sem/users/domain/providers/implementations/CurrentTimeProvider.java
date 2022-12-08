package sem.users.domain.providers.implementations;

import org.springframework.stereotype.Component;
import sem.users.domain.providers.TimeProvider;

import java.time.Instant;

/**
 * An abstract time provider to make services testable.
 * The TimeProvider interface can be mocked in order to provide a predetermined current time and
 * make tests independent of the actual current time.
 */
@Component
public class CurrentTimeProvider implements TimeProvider {
    /**
     * Gets current time.
     *
     * @return The current time
     */
    public Instant getCurrentTime() {
        return Instant.now();
    }
}
