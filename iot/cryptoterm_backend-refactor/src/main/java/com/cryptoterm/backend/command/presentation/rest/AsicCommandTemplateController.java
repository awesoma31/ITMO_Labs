package com.cryptoterm.backend.web;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.dto.AsicCommandTemplateResponse;
import com.cryptoterm.backend.dto.CreateAsicCommandTemplateRequest;
import com.cryptoterm.backend.service.AsicCommandTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST контроллер для управления шаблонами команд ASIC.
 * 
 * Пользователи могут создавать, просматривать и использовать шаблоны команд.
 * Удаление и обновление доступно только администраторам.
 * Шаблоны - это переиспользуемые последовательности команд для конкретных моделей майнеров.
 */
@RestController
@RequestMapping("/api/asic-command-templates")
@Tag(name = "Шаблоны команд ASIC", description = "API для управления шаблонами команд ASIC")
@SecurityRequirement(name = "bearerAuth")
public class AsicCommandTemplateController {
    private static final Logger log = LoggerFactory.getLogger(AsicCommandTemplateController.class);
    
    private final AsicCommandTemplateService templateService;
    
    public AsicCommandTemplateController(AsicCommandTemplateService templateService) {
        this.templateService = templateService;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Создать шаблон команды ASIC", 
               description = "Создать переиспользуемый шаблон команды для конкретной модели майнера")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Шаблон успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "409", description = "Шаблон с таким именем уже существует")
    })
    public ResponseEntity<AsicCommandTemplateResponse> createTemplate(
            @Valid @RequestBody CreateAsicCommandTemplateRequest request,
            Authentication authentication) {
        
        log.info("Администратор '{}' создает шаблон '{}'", authentication.getName(), request.getName());
        
        // Проверить, существует ли шаблон
        if (templateService.getTemplate(request.getName()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Шаблон с именем '" + request.getName() + "' уже существует"
            );
        }
        
        // Создать шаблон
        AsicCommandTemplate template = request.toEntity();
        AsicCommandTemplate created = templateService.createTemplate(template, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AsicCommandTemplateResponse.fromEntity(created));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Список всех шаблонов команд", 
               description = "Получить все доступные шаблоны команд")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Шаблоны успешно получены"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<List<AsicCommandTemplateResponse>> getAllTemplates() {
        List<AsicCommandTemplate> templates = templateService.getAllTemplates();
        List<AsicCommandTemplateResponse> responses = templates.stream()
            .map(AsicCommandTemplateResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{name}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Получить шаблон по имени", 
               description = "Получить подробную информацию о конкретном шаблоне")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Шаблон найден"),
        @ApiResponse(responseCode = "404", description = "Шаблон не найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<AsicCommandTemplate> getTemplate(
            @Parameter(description = "Имя шаблона") @PathVariable String name) {
        
        return templateService.getTemplate(name)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить шаблон (Только для администратора)", 
               description = "Удалить шаблон команды")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Шаблон успешно удален"),
        @ApiResponse(responseCode = "404", description = "Шаблон не найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль администратора")
    })
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "Имя шаблона") @PathVariable String name) {
        
        if (templateService.deleteTemplate(name)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/normalize-all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Нормализовать все шаблоны (Только для администратора)", 
               description = "Обновить miner_model и miner_vendor в нижний регистр для всех существующих шаблонов")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Шаблоны успешно обновлены"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль администратора")
    })
    public ResponseEntity<?> normalizeAllTemplates(Authentication authentication) {
        log.info("Администратор '{}' запустил нормализацию всех шаблонов", authentication.getName());
        
        int updated = templateService.normalizeAllTemplates();
        
        return ResponseEntity.ok(Map.of(
            "message", "Нормализация завершена",
            "updatedCount", updated
        ));
    }
}

