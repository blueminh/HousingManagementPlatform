package sem.hoa.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ActivityCreationRequestModel {
    private Integer hoaId;
    private String name;
    private String desc;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date date;
}
