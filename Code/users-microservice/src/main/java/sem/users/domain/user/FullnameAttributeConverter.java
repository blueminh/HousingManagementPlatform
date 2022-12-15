package sem.users.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class FullnameAttributeConverter implements AttributeConverter<Fullname, String> {

    @Override
    public String convertToDatabaseColumn(Fullname attribute) {
        return attribute.toString();
    }

    @Override
    public Fullname convertToEntityAttribute(String dbData) {
        return new Fullname(dbData);
    }
}
