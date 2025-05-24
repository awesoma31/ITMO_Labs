package org.awesoma.back.services;

import org.awesoma.back.model.Point;
import org.awesoma.back.repository.dto.PointDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AreaCheckServiceTest {

    private final AreaCheckService svc = new AreaCheckService();

    @ParameterizedTest
    @CsvSource({
            "0, 0, 2, true",
            "-1, 1, 2, false",
            "-1, -1, 2, true",
            "1, 2, 2, false"
    })
    void checkWithPrimitives(double x, double y, double r, boolean expected) {
        Point p = svc.checkAndGetPoint(x, y, r);
        assertEquals(expected, p.isResult());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 2, true",
            "-1, -1, 2, true"
    })
    void checkWithDto(double x, double y, double r, boolean expected) {
        // Так как нет конструктора — устанавливаем значения через сеттеры
        PointDTO dto = new PointDTO();
        dto.setX(x);
        dto.setY(y);
        dto.setR(r);

        Point p = svc.checkAndGetPoint(dto);
        assertEquals(expected, p.isResult());
    }
}
