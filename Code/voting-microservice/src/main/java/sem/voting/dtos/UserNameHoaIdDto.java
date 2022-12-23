package sem.voting.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class UserNameHoaIdDto {
    @Getter
    @Setter
    public String username;

    @Getter
    @Setter
    public int hoaId;

    public UserNameHoaIdDto(String username, int hoaId) {
        this.username = username;
        this.hoaId = hoaId;
    }
}
