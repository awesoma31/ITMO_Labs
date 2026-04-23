package com.cryptoterm.backend.web;

import com.cryptoterm.backend.monitoring.domain.MetricType;
import com.cryptoterm.backend.service.MetricTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/metric-types")
@Tag(name = "Типы метрик", description = "CRUD для управления типами численных метрик")
public class MetricTypeController {

    private final MetricTypeService metricTypeService;

    public MetricTypeController(MetricTypeService metricTypeService) {
        this.metricTypeService = metricTypeService;
    }

    public record CreateMetricTypeRequest(String name, String unit, String description, String displayName) {}
    public record UpdateMetricTypeRequest(String unit, String description, String displayName) {}

    @PostMapping
    @Operation(summary = "Создать тип метрики",
            description = "name должен быть в формате [a-z0-9_]+, unit обязателен")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тип метрики создан"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public ResponseEntity<?> create(@RequestBody CreateMetricTypeRequest req) {
        try {
            MetricType mt = metricTypeService.create(req.name(), req.unit(), req.description(), req.displayName());
            return ResponseEntity.ok(mt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Получить все активные типы метрик")
    public ResponseEntity<?> getAllActive() {
        return ResponseEntity.ok(metricTypeService.findAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тип метрики по ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тип метрики найден"),
        @ApiResponse(responseCode = "404", description = "Тип метрики не найден")
    })
    public ResponseEntity<?> getById(@Parameter(description = "ID типа метрики") @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(metricTypeService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить тип метрики",
            description = "Можно обновить unit, description, displayName. name изменить нельзя.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тип метрики обновлён"),
        @ApiResponse(responseCode = "404", description = "Тип метрики не найден")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "ID типа метрики") @PathVariable Integer id,
            @RequestBody UpdateMetricTypeRequest req) {
        try {
            MetricType mt = metricTypeService.update(id, req.unit(), req.description(), req.displayName());
            return ResponseEntity.ok(mt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать тип метрики",
            description = "Soft-delete: тип помечается как неактивный, данные сохраняются")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тип метрики деактивирован"),
        @ApiResponse(responseCode = "404", description = "Тип метрики не найден")
    })
    public ResponseEntity<?> deactivate(@Parameter(description = "ID типа метрики") @PathVariable Integer id) {
        try {
            metricTypeService.deactivate(id);
            return ResponseEntity.ok(Map.of("status", "deactivated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Статистика по типу метрики",
            description = "Количество записей, дата первой и последней записи")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Статистика получена"),
        @ApiResponse(responseCode = "404", description = "Тип метрики не найден")
    })
    public ResponseEntity<?> getStats(@Parameter(description = "ID типа метрики") @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(metricTypeService.getStats(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
