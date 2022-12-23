package sem.hoa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * This is used to config the timezone to system default and not UTC.
 */
@Configuration
public class JacksonConfiguration {

    @Autowired
    public JacksonConfiguration(ObjectMapper objectMapper) {
        objectMapper.setTimeZone(TimeZone.getDefault());
    }
}