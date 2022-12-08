package sem.users.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

/**
 * Converter for the HashedPassword value object.
 */
@Converter
public class MembershipAttributeConverter implements AttributeConverter<Membership, List<HoaMembership>> {

    @Override
    public List<HoaMembership> convertToDatabaseColumn(Membership attribute) {
        return attribute.getMembershipList();
    }

    @Override
    public Membership convertToEntityAttribute(List<HoaMembership> dbData) {
        return new Membership(dbData);
    }
}

