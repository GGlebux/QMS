package most.qms.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoolWithStringDto {
    private Boolean bool;
    private String message;
}
