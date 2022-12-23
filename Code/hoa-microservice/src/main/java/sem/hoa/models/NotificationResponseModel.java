package sem.hoa.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NotificationResponseModel {
    private List<String> messages;

    public NotificationResponseModel(List<String> messages) {
        this.messages = messages;
    }
}
