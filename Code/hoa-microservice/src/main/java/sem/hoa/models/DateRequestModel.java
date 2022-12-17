package sem.hoa.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class DateRequestModel {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;

    public DateRequestModel(Date date) {
        this.date = date;
    }
}
