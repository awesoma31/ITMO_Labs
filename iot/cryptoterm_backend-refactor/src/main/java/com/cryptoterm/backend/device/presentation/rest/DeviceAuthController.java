package com.cryptoterm.backend.web;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.device.domain.Condition;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.TemperatureSensor;
import com.cryptoterm.backend.device.domain.SensorGroup;
import com.cryptoterm.backend.device.application.service.ConditionService;
import com.cryptoterm.backend.device.application.service.TemperatureSensorService;
import com.cryptoterm.backend.device.application.service.SensorGroupService;
import com.cryptoterm.backend.service.DeviceAuthService;
import com.cryptoterm.backend.service.MinerService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device-auth")
@Tag(name = "Аутентификация устройств", description = "API для регистрации устройств (Raspberry Pi с майнерами)")
public class DeviceAuthController {
    private final DeviceAuthService deviceAuthService;
    private final MinerService minerService;
    private final TemperatureSensorService temperatureSensorService;
    private final ConditionService conditionService;
    private final SensorGroupService sensorGroupService;
    
    @Value("${device.registration.api-key:default-device-key-change-in-production}")
    private String registrationApiKey;
    
    public DeviceAuthController(
            DeviceAuthService deviceAuthService, 
            MinerService minerService,
            TemperatureSensorService temperatureSensorService,
            ConditionService conditionService,
            SensorGroupService sensorGroupService) {
        this.deviceAuthService = deviceAuthService;
        this.minerService = minerService;
        this.temperatureSensorService = temperatureSensorService;
        this.conditionService = conditionService;
        this.sensorGroupService = sensorGroupService;
    }

    public record AsicRegisterDto(
        @NotBlank(message = "Имя ASIC обязательно")
        @Schema(description = "Имя ASIC", example = "ASIC1")
        String name,
        
        @Schema(description = "Модель ASIC", example = "default")
        String model,

        @Schema(description = "Производитель ASIC", example = "default")
        String vendor
    ) {}

    public record TemperatureSensorRegisterDto(
        @NotBlank(message = "Имя датчика температуры обязательно")
        @Schema(description = "Имя датчика температуры", example = "TempPin1")
        String name
    ) {}

    public record ConditionRegisterDto(
        @NotBlank(message = "Имя условия обязательно")
        @Schema(description = "Имя условия", example = "TempPinCondition")
        String name,
        
        @NotBlank(message = "Тип сравнения обязателен")
        @Schema(description = "Тип сравнения", example = ">=", allowableValues = {"==", ">", "<", ">=", "<="})
        String type,
        
        @Schema(description = "Пороговое значение", example = "70")
        float value
    ) {}

    public record SensorGroupRegisterDto(
        @NotBlank(message = "Имя группы датчиков обязательно")
        @Schema(description = "Имя группы датчиков", example = "TempGroup1")
        String name,
        
        @NotBlank(message = "Метод агрегации обязателен")
        @Schema(description = "Метод агрегации", example = "avg", allowableValues = {"min", "max", "avg"})
        String aggregationMethod
    ) {}

    // Legacy DTO для обратной совместимости
    public record MinerRegisterDto(
        @NotBlank(message = "Имя майнера обязательно")
        @Schema(description = "Имя майнера", example = "Miner-1")
        String name, 
        
        @NotBlank(message = "Производитель обязателен")
        @Schema(description = "Производитель", example = "Antminer")
        String vendor, 
        
        @NotBlank(message = "Модель обязательна")
        @Schema(description = "Модель", example = "S19")
        String model
    ) {}

    public record SignupRequest(
        @Email(message = "Email должен быть валидным")
        @Schema(description = "Email владельца", example = "user@example.com")
        String email, 
        
        @NotBlank(message = "Telegram обязателен")
        @Schema(description = "Telegram владельца", example = "@username")
        String telegram, 
        
        @NotBlank(message = "IP адрес обязателен")
        @Schema(description = "IP адрес устройства", example = "192.168.1.100")
        @JsonProperty("ipAdress")
        String ipAdress,
        
        // Legacy поле для обратной совместимости
        @Schema(description = "Список майнеров (legacy)")
        List<MinerRegisterDto> Miners,
        
        // Новые поля
        @Schema(description = "Список ASIC")
        List<AsicRegisterDto> asic,
        
        @Schema(description = "Список датчиков температуры")
        @JsonProperty("temperatureSensor")
        List<TemperatureSensorRegisterDto> temperatureSensor,
        
        @Schema(description = "Список условий")
        List<ConditionRegisterDto> condition,
        
        @Schema(description = "Список групп датчиков")
        @JsonProperty("sensorGroup")
        List<SensorGroupRegisterDto> sensorGroup
    ) {}

    @PostMapping("/signup")
    @Operation(summary = "Зарегистрировать новое устройство", 
               description = "Регистрирует новое устройство (Raspberry Pi) вместе с подключенными ASIC, датчиками и условиями. Требуется X-API-Key заголовок.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Устройство успешно зарегистрировано"),
        @ApiResponse(responseCode = "400", description = "Неверные данные"),
        @ApiResponse(responseCode = "401", description = "Неверный или отсутствующий API ключ")
    })
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest req,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        if (apiKey == null || !apiKey.equals(registrationApiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                    "Неверный или отсутствующий API ключ");
        }
        
        Device device = deviceAuthService.RegisterDevice(req.email(), req.telegram(), req.ipAdress());
        User user = device.getOwner();
        
        Map<String, Object> response = new HashMap<>();
        response.put("device_id", device.getId().toString());
        
        // Обработка legacy поля Miners для обратной совместимости
        if (req.Miners != null && !req.Miners.isEmpty()) {
            Map<String, String> minerIds = new HashMap<>();
            for (MinerRegisterDto miner : req.Miners) {
                String minerId = minerService.registerMiner(device, miner.name(), miner.vendor(), miner.model());
                minerIds.put(miner.name(), minerId);
            }
            response.put("miners", minerIds);
        }
        
        // Обработка нового поля ASIC
        if (req.asic != null && !req.asic.isEmpty()) {
            Map<String, String> asicIds = new HashMap<>();
            for (AsicRegisterDto asic : req.asic) {
                // Использовать "default" как производителя, если не указан
                String vendor = asic.vendor != null ? asic.vendor : "default";;
                String model = asic.model != null ? asic.model : "default";
                String asicId = minerService.registerMiner(device, asic.name(), vendor, model);
                asicIds.put(asic.name(), asicId);
            }
            response.put("asic", asicIds);
        }
        
        // Обработка датчиков температуры
        if (req.temperatureSensor != null && !req.temperatureSensor.isEmpty()) {
            Map<String, String> sensorIds = new HashMap<>();
            for (TemperatureSensorRegisterDto sensor : req.temperatureSensor) {
                TemperatureSensor tempSensor = temperatureSensorService.registerTemperatureSensor(
                        user, device, sensor.name());
                sensorIds.put(sensor.name(), tempSensor.getId().toString());
            }
            response.put("temperature_sensor", sensorIds);
        }
        
        // Обработка условий
        if (req.condition != null && !req.condition.isEmpty()) {
            Map<String, String> conditionIds = new HashMap<>();
            for (ConditionRegisterDto condDto : req.condition) {
                // Преобразование строкового типа в enum
                Condition.ComparisonOperator operator = mapComparisonOperator(condDto.type());
                BigDecimal thresholdValue = BigDecimal.valueOf(condDto.value());
                
                Condition condition = conditionService.registerCondition(
                        user, device, condDto.name(), operator, thresholdValue);
                conditionIds.put(condDto.name(), condition.getId().toString());
            }
            response.put("condition", conditionIds);
        }
        
        // Обработка групп датчиков
        if (req.sensorGroup != null && !req.sensorGroup.isEmpty()) {
            Map<String, String> sensorGroupIds = new HashMap<>();
            for (SensorGroupRegisterDto groupDto : req.sensorGroup) {
                SensorGroup.AggregationMethod aggregation = SensorGroup.AggregationMethod.valueOf(groupDto.aggregationMethod());
                
                SensorGroup sensorGroup = sensorGroupService.registerSensorGroup(
                        user, device, groupDto.name(), aggregation);
                sensorGroupIds.put(groupDto.name(), sensorGroup.getId().toString());
            }
            response.put("sensor_group", sensorGroupIds);
        }

        return ResponseEntity.ok(response);
    }
    
    private Condition.ComparisonOperator mapComparisonOperator(String type) {
        // Поддерживаем как символы, так и текстовые варианты для обратной совместимости
        return switch (type) {
            case "=", "==", "eq" -> Condition.ComparisonOperator.eq;
            case ">", "gt" -> Condition.ComparisonOperator.gt;
            case "<", "lt" -> Condition.ComparisonOperator.lt;
            case ">=", "gte" -> Condition.ComparisonOperator.gte;
            case "<=", "lte" -> Condition.ComparisonOperator.lte;
            default -> Condition.ComparisonOperator.fromSymbol(type); // Попытка парсинга как символ
        };
    }
}