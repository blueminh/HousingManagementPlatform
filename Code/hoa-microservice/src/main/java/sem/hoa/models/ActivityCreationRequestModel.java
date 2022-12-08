package sem.hoa.models;

import lombok.Data;

import java.util.Date;

@Data
public class ActivityCreationRequestModel {
    private Integer hoaId;
    private String desc;
    private Date date;
}
