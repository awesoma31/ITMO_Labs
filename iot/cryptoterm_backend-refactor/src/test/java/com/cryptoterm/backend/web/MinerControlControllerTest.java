package com.cryptoterm.backend.web;

import com.cryptoterm.backend.command.application.factory.AsicCommandFactory;
import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.CommandType;
import com.cryptoterm.backend.command.domain.PowerMode;
import com.cryptoterm.backend.command.domain.asic.AsicCommandStep;
import com.cryptoterm.backend.command.domain.asic.AsicHttpRequest;
import com.cryptoterm.backend.command.domain.asic.AsicRetryPolicy;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.mqtt.CommandPublisher;
import com.cryptoterm.backend.service.AsicCommandService;
import com.cryptoterm.backend.service.AsicCommandTemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MinerControlController, specifically the schedule-change-mode endpoint.
 */
@WebMvcTest(controllers = MinerControlController.class)
class MinerControlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MinerRepository minerRepository;

    @MockBean
    private AsicCommandTemplateService templateService;

    @MockBean
    private AsicCommandService commandService;

    @MockBean
    private CommandPublisher commandPublisher;

    @MockBean
    private List<AsicCommandFactory> commandFactories;

    @MockBean
    private AsicCommandFactory mockFactory;

    private UUID testUserId;
    private UUID testMinerId;
    private UUID testDeviceId;
    private Miner testMiner;
    private List<AsicCommandTemplate> testTemplates;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testMinerId = UUID.randomUUID();
        testDeviceId = UUID.randomUUID();

        // Create test user
        User testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");

        // Create test device
        Device testDevice = new Device();
        testDevice.setId(testDeviceId);
        testDevice.setName("Test Device");
        testDevice.setOwner(testUser);

        // Create test miner
        testMiner = new Miner();
        testMiner.setId(testMinerId);
        testMiner.setVendor("Bitmain");
        testMiner.setModel("Antminer S19 Pro Hydro");
        testMiner.setLabel("Test Miner");
        testMiner.setDevice(testDevice);

        // Create test templates
        testTemplates = createTestTemplates();
    }

    private List<AsicCommandTemplate> createTestTemplates() {
        List<AsicCommandTemplate> templates = new ArrayList<>();

        // Create mode change templates
        templates.add(createModeTemplate("1780_5150", 1780, 5150, CommandType.MODE_CHANGE));
        templates.add(createModeTemplate("1850_5560", 1850, 5560, CommandType.MODE_CHANGE));
        templates.add(createModeTemplate("2000_6000", 2000, 6000, CommandType.MODE_CHANGE));

        return templates;
    }

    private AsicCommandTemplate createModeTemplate(String name, int power, int hashrate, CommandType type) {
        AsicCommandTemplate template = new AsicCommandTemplate();
        template.setName(name);
        template.setDescription(String.format("Mode %dW %dTH/s", power, hashrate / 100));
        template.setMinerModel("antminer s19 pro hydro");
        template.setMinerVendor("bitmain");
        template.setFirmware("anthill");
        template.setCommandType(type);

        AsicCommandStep step = new AsicCommandStep();
        step.setId("set_mode");
        AsicHttpRequest request = new AsicHttpRequest();
        request.setMethod("POST");
        request.setPath("/api/v1/settings");
        step.setRequest(request);

        template.setSteps(List.of(step));
        template.setPolicy(new AsicRetryPolicy(2, 2000));
        return template;
    }

    private AsicHttpProxyCommand createMockCommand() {
        AsicHttpProxyCommand command = new AsicHttpProxyCommand();
        command.setCmdId(UUID.randomUUID().toString());
        command.setDeviceId(testDeviceId.toString());
        command.setMinerId(testMinerId.toString());
        command.setStatus(AsicHttpProxyCommand.CommandStatus.SCHEDULED);
        command.setCreatedAt(OffsetDateTime.now());
        return command;
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = {"USER"})
    void testScheduleChangeMinerMode_Success() throws Exception {
        // Arrange
        UUID mockUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);

        // Mock authentication - use the actual user ID
        testMiner.getDevice().getOwner().setId(mockUserId);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));
        when(commandFactories.stream()).thenReturn(List.of(mockFactory).stream());
        when(mockFactory.supports(any(Miner.class))).thenReturn(true);
        when(mockFactory.getSupportedMinerVendor()).thenReturn("Bitmain");
        when(mockFactory.getSupportedMinerModel()).thenReturn("Antminer S19 Pro Hydro");

        when(templateService.getTemplatesForMinerModelAndVendor(anyString(), anyString()))
            .thenReturn(testTemplates);

        AsicHttpProxyCommand mockCommand = createMockCommand();
        when(mockFactory.createModeChangeCommand(any(Miner.class), eq(PowerMode.STANDARD), anyList()))
            .thenReturn(mockCommand);

        when(commandService.createCommand(any(AsicHttpProxyCommand.class), any(OffsetDateTime.class)))
            .thenReturn(mockCommand);

        String requestBody = String.format("""
            {
              "mode": "STANDARD",
              "scheduledAt": "%s"
            }
            """, scheduledTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.cmd_id").value(mockCommand.getCmdId()))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = {"USER"})
    void testScheduleChangeMinerMode_PastTime_BadRequest() throws Exception {
        // Arrange
        UUID mockUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testMiner.getDevice().getOwner().setId(mockUserId);

        OffsetDateTime pastTime = OffsetDateTime.now().minusHours(1);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));

        String requestBody = String.format("""
            {
              "mode": "STANDARD",
              "scheduledAt": "%s"
            }
            """, pastTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test-user-id", roles = {"USER"})
    void testScheduleChangeMinerMode_MinerNotFound() throws Exception {
        // Arrange
        OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.empty());

        String requestBody = String.format("""
            {
              "mode": "STANDARD",
              "scheduledAt": "%s"
            }
            """, scheduledTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000002", roles = {"USER"})
    void testScheduleChangeMinerMode_NotOwner_Forbidden() throws Exception {
        // Arrange
        UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testMiner.getDevice().getOwner().setId(ownerId);

        OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));

        String requestBody = String.format("""
            {
              "mode": "STANDARD",
              "scheduledAt": "%s"
            }
            """, scheduledTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = {"USER"})
    void testScheduleChangeMinerMode_WithExactPowerAndHashrate() throws Exception {
        // Arrange
        UUID mockUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testMiner.getDevice().getOwner().setId(mockUserId);

        OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));
        when(commandFactories.stream()).thenReturn(List.of(mockFactory).stream());
        when(mockFactory.supports(any(Miner.class))).thenReturn(true);
        when(mockFactory.getSupportedMinerVendor()).thenReturn("Bitmain");
        when(mockFactory.getSupportedMinerModel()).thenReturn("Antminer S19 Pro Hydro");

        when(templateService.getTemplatesForMinerModelAndVendor(anyString(), anyString()))
            .thenReturn(testTemplates);

        AsicHttpProxyCommand mockCommand = createMockCommand();
        when(mockFactory.createModeChangeCommand(any(Miner.class), eq(1850), eq(5560), anyList()))
            .thenReturn(mockCommand);

        when(commandService.createCommand(any(AsicHttpProxyCommand.class), any(OffsetDateTime.class)))
            .thenReturn(mockCommand);

        String requestBody = String.format("""
            {
              "mode": "STANDARD",
              "scheduledAt": "%s",
              "powerWatts": 1850,
              "hashrate": 5560
            }
            """, scheduledTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.cmd_id").value(mockCommand.getCmdId()));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = {"USER"})
    void testScheduleChangeMinerMode_NoTemplatesFound() throws Exception {
        // Arrange
        UUID mockUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testMiner.getDevice().getOwner().setId(mockUserId);

        OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));
        when(commandFactories.stream()).thenReturn(List.of(mockFactory).stream());
        when(mockFactory.supports(any(Miner.class))).thenReturn(true);

        when(templateService.getTemplatesForMinerModelAndVendor(anyString(), anyString()))
            .thenReturn(Collections.emptyList());

        String requestBody = String.format("""
            {
              "mode": "STANDARD",
              "scheduledAt": "%s"
            }
            """, scheduledTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = {"USER"})
    void testScheduleChangeMinerMode_InvalidMode() throws Exception {
        // Arrange
        UUID mockUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testMiner.getDevice().getOwner().setId(mockUserId);

        OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);

        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));

        String requestBody = String.format("""
            {
              "mode": "INVALID_MODE",
              "scheduledAt": "%s"
            }
            """, scheduledTime.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test-user-id", roles = {"USER"})
    void testScheduleChangeMinerMode_MissingScheduledAt() throws Exception {
        // Arrange
        when(minerRepository.findById(testMinerId)).thenReturn(Optional.of(testMiner));

        String requestBody = """
            {
              "mode": "STANDARD"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/miners/{minerId}/schedule-change-mode", testMinerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }
}
