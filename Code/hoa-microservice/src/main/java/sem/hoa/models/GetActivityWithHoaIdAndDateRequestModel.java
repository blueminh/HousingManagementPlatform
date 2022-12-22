package sem.hoa.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class GetActivityWithHoaIdAndDateRequestModel {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;
    private Integer hoaId;

    public GetActivityWithHoaIdAndDateRequestModel(Date date, int hoaId) {
        this.date = date;
        this.hoaId = hoaId;
    }
}
