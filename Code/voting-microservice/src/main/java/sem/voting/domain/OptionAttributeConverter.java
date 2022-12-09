package sem.voting.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the Option value object.
 */
@Converter
public class OptionAttributeConverter implements AttributeConverter<Option, String> {
    @Override
    public String convertToDatabaseColumn(Option attribute) {
        return attribute.toString();
    }

    @Override
    public Option convertToEntityAttribute(String dbData) {
        return new Option(dbData);
    }
}
