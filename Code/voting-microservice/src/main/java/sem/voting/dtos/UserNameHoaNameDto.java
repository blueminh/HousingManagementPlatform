package sem.voting.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class UserNameHoaNameDto {
    @Getter
    @Setter
    public String hoaName;

    @Getter
    @Setter
    public String username;

    public UserNameHoaNameDto(String hoaName, String username) {
        this.hoaName = hoaName;
        this.username = username;
    }
}
