package org.awesoma.points.repository.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointDTO {
    @NotBlank
    private Double x;
    @NotBlank
    private Double y;
    @NotBlank
    private Double r;
}
