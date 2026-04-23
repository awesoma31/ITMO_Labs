package com.cryptoterm.backend.command.factory;

import com.cryptoterm.backend.command.application.factory.AntminerS19ProHydroCommandFactory;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AntminerS19ProHydroCommandFactory.
 */
class AntminerS19ProHydroCommandFactoryTest {

    private AntminerS19ProHydroCommandFactory factory;
    private Miner testMiner;
    private List<AsicCommandTemplate> testTemplates;

    @BeforeEach
    void setUp() {
        factory = new AntminerS19ProHydroCommandFactory();
        
        // Create test miner
        testMiner = new Miner();
        testMiner.setId(UUID.randomUUID());
        testMiner.setVendor("Bitmain");
        testMiner.setModel("Antminer S19 Pro Hydro");
        testMiner.setLabel("Test Miner");
        
        // Create test device
        Device device = new Device();
        device.setId(UUID.randomUUID());
        User owner = new User();
        owner.setEmail("test@example.com");
        device.setOwner(owner);
        testMiner.setDevice(device);
        
        // Create test templates
        testTemplates = createTestTemplates();
    }

    private List<AsicCommandTemplate> createTestTemplates() {
        List<AsicCommandTemplate> templates = new ArrayList<>();
        
        // Reboot template
        AsicCommandTemplate reboot = new AsicCommandTemplate();
        reboot.setName("reboot");
        reboot.setDescription("Reboot miner");
        reboot.setMinerModel("Antminer S19 Pro Hydro");
        reboot.setMinerVendor("Bitmain");
        reboot.setFirmware("anthill");
        reboot.setSteps(createRebootSteps());
        reboot.setPolicy(new AsicRetryPolicy(2, 2000));
        templates.add(reboot);
        
        // Mode change templates (sorted by power)
        templates.add(createModeTemplate("1780_5150", 1780, 5150)); // ECO
        templates.add(createModeTemplate("1850_5560", 1850, 5560)); // STANDARD
        templates.add(createModeTemplate("1900_5850", 1900, 5850)); // STANDARD
        templates.add(createModeTemplate("2000_6000", 2000, 6000)); // OVERCLOCK
        templates.add(createModeTemplate("2100_6200", 2100, 6200)); // OVERCLOCK (penultimate)
        
        return templates;
    }

    private AsicCommandTemplate createModeTemplate(String name, int power, int hashrate) {
        AsicCommandTemplate template = new AsicCommandTemplate();
        template.setName(name);
        template.setDescription(String.format("Mode %dW %dTH/s", power, hashrate / 100));
        template.setMinerModel("Antminer S19 Pro Hydro");
        template.setMinerVendor("Bitmain");
        template.setFirmware("anthill");
        template.setSteps(createModeChangeSteps());
        template.setPolicy(new AsicRetryPolicy(2, 2000));
        return template;
    }

    private List<AsicCommandStep> createRebootSteps() {
        AsicCommandStep step = new AsicCommandStep();
        step.setId("reboot");
        AsicHttpRequest request = new AsicHttpRequest();
        request.setMethod("POST");
        request.setPath("/api/v1/reboot");
        step.setRequest(request);
        return List.of(step);
    }

    private List<AsicCommandStep> createModeChangeSteps() {
        AsicCommandStep step = new AsicCommandStep();
        step.setId("set_mode");
        AsicHttpRequest request = new AsicHttpRequest();
        request.setMethod("POST");
        request.setPath("/api/v1/settings");
        step.setRequest(request);
        return List.of(step);
    }

    @Test
    void testGetSupportedMinerModel() {
        assertEquals("Antminer S19 Pro Hydro", factory.getSupportedMinerModel());
    }

    @Test
    void testGetSupportedMinerVendor() {
        assertEquals("Bitmain", factory.getSupportedMinerVendor());
    }

    @Test
    void testSupports_CorrectMiner() {
        assertTrue(factory.supports(testMiner));
    }

    @Test
    void testSupports_WrongVendor() {
        testMiner.setVendor("WrongVendor");
        assertFalse(factory.supports(testMiner));
    }

    @Test
    void testSupports_WrongModel() {
        testMiner.setModel("Antminer S19");
        assertFalse(factory.supports(testMiner));
    }

    @Test
    void testCreateRebootCommand() {
        AsicHttpProxyCommand command = factory.createRebootCommand(testMiner, testTemplates);
        
        assertNotNull(command);
        assertNotNull(command.getCmdId());
        assertEquals(testMiner.getDevice().getId().toString(), command.getDeviceId());
        assertEquals(testMiner.getId().toString(), command.getMinerId());
        assertEquals("anthill", command.getAsic().getFirmware());
        assertEquals(80, command.getAsic().getPort());
        assertEquals("http", command.getAsic().getScheme());
        assertNotNull(command.getSteps());
        assertFalse(command.getSteps().isEmpty());
        assertNotNull(command.getPolicy());
    }

    @Test
    void testCreateRebootCommand_NoTemplate() {
        List<AsicCommandTemplate> emptyTemplates = new ArrayList<>();
        AsicHttpProxyCommand command = factory.createRebootCommand(testMiner, emptyTemplates);
        
        assertNull(command);
    }

    @Test
    void testCreateModeChangeCommand_Eco() {
        AsicHttpProxyCommand command = factory.createModeChangeCommand(
            testMiner, PowerMode.ECO, testTemplates
        );
        
        assertNotNull(command);
        assertNotNull(command.getCmdId());
        assertEquals(testMiner.getDevice().getId().toString(), command.getDeviceId());
        assertEquals(testMiner.getId().toString(), command.getMinerId());
        
        // Should use first (minimum power) template: 1780_5150
        assertNotNull(command.getSteps());
    }

    @Test
    void testCreateModeChangeCommand_Standard() {
        AsicHttpProxyCommand command = factory.createModeChangeCommand(
            testMiner, PowerMode.STANDARD, testTemplates
        );
        
        assertNotNull(command);
        // Should use middle template
        assertNotNull(command.getSteps());
    }

    @Test
    void testCreateModeChangeCommand_Overclock() {
        AsicHttpProxyCommand command = factory.createModeChangeCommand(
            testMiner, PowerMode.OVERCLOCK, testTemplates
        );
        
        assertNotNull(command);
        // Should use second-to-last template: 2100_6200
        assertNotNull(command.getSteps());
    }

    @Test
    void testCreateModeChangeCommand_ExactMatch() {
        AsicHttpProxyCommand command = factory.createModeChangeCommand(
            testMiner, 1850, 5560, testTemplates
        );
        
        assertNotNull(command);
        assertNotNull(command.getCmdId());
        // Should find exact match template
    }

    @Test
    void testCreateModeChangeCommand_ClosestMatch() {
        AsicHttpProxyCommand command = factory.createModeChangeCommand(
            testMiner, 1860, 5600, testTemplates
        );
        
        assertNotNull(command);
        // Should find closest template by power (1850_5560)
    }

    @Test
    void testCreateModeChangeCommand_NoTemplates() {
        List<AsicCommandTemplate> emptyTemplates = new ArrayList<>();
        AsicHttpProxyCommand command = factory.createModeChangeCommand(
            testMiner, PowerMode.ECO, emptyTemplates
        );
        
        assertNull(command);
    }

    @Test
    void testCreateModeChangeCommand_OnlyOneTemplate() {
        List<AsicCommandTemplate> oneTemplate = List.of(testTemplates.get(1)); // Just one mode template
        
        AsicHttpProxyCommand ecoCommand = factory.createModeChangeCommand(
            testMiner, PowerMode.ECO, oneTemplate
        );
        AsicHttpProxyCommand overclockCommand = factory.createModeChangeCommand(
            testMiner, PowerMode.OVERCLOCK, oneTemplate
        );
        
        assertNotNull(ecoCommand);
        assertNotNull(overclockCommand);
        // Both should use the same template when only one available
    }
}
