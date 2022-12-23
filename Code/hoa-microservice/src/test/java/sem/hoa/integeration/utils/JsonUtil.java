package sem.hoa.integeration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.TimeZone;

/**
 * The Json util for tests.
 */
public class JsonUtil {
    /**
     * Serialize object into a string.
     *
     * @param object The object to be serialized.
     * @return A serialized string.
     * @throws JsonProcessingException if an error occurs during serialization.
     */
    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Deserializes a json string into an object.
     *
     * @param json The string to be deserialized.
     * @param type The type of the desired object.
     * @return The deserialized object.
     * @throws JsonProcessingException if an error occurs during deserialization.
     */
    public static <T> T deserialize(String json, Class<T> type) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        return objectMapper.readValue(json, type);
    }
}