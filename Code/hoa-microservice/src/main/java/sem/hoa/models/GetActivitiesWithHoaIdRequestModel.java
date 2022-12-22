package sem.hoa.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetActivitiesWithHoaIdRequestModel {
    private Integer hoaId;

    public GetActivitiesWithHoaIdRequestModel(Integer hoaId) {
        this.hoaId = hoaId;
    }
}
