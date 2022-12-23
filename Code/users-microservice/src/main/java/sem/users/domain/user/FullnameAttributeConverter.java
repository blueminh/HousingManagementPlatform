package sem.users.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FullnameAttributeConverter implements AttributeConverter<FullName, String> {

    @Override
    public String convertToDatabaseColumn(FullName attribute) {
        return attribute.toString();
    }

    @Override
    public FullName convertToEntityAttribute(String dbData) {
        return new FullName(dbData);
    }
}
