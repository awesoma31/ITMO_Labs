package org.awesoma.points.repository.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PointDTO {
    @NotBlank
    private Double x;
    @NotBlank
    private Double y;
    @NotBlank
    private Double r;
}
