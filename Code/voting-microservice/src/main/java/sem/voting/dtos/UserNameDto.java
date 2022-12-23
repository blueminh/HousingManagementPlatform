package sem.voting.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class UserNameDto {
    @Getter
    @Setter
    String username;

    public UserNameDto(String username) {
        this.username = username;
    }
}
