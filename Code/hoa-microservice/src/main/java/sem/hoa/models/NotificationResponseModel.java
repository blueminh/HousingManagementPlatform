package sem.hoa.models;

import lombok.Data;
import java.util.List;

@Data
public class NotificationResponseModel {
    private List<String> messages;

    public NotificationResponseModel(List<String> messages) {
        this.messages = messages;
    }
}
