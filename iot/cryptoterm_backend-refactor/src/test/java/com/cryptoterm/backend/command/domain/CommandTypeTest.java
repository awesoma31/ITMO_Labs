package com.cryptoterm.backend.command.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandType enum.
 */
class CommandTypeTest {

    @Test
    void testFromTemplateName_Reboot() {
        assertEquals(CommandType.RESTART, CommandType.fromTemplateName("reboot"));
        assertEquals(CommandType.RESTART, CommandType.fromTemplateName("REBOOT"));
        assertEquals(CommandType.RESTART, CommandType.fromTemplateName("miner_reboot"));
        assertEquals(CommandType.RESTART, CommandType.fromTemplateName("restart_miner"));
    }

    @Test
    void testFromTemplateName_ModeChange() {
        assertEquals(CommandType.MODE_CHANGE, CommandType.fromTemplateName("1780_5150"));
        assertEquals(CommandType.MODE_CHANGE, CommandType.fromTemplateName("2000_6000"));
        assertEquals(CommandType.MODE_CHANGE, CommandType.fromTemplateName("eco_1500_4800"));
        assertEquals(CommandType.MODE_CHANGE, CommandType.fromTemplateName("overclock-2200-6500"));
    }

    @Test
    void testFromTemplateName_Other() {
        assertEquals(CommandType.OTHER, CommandType.fromTemplateName("firmware_update"));
        assertEquals(CommandType.OTHER, CommandType.fromTemplateName("diagnostic"));
        assertEquals(CommandType.OTHER, CommandType.fromTemplateName(null));
        assertEquals(CommandType.OTHER, CommandType.fromTemplateName(""));
    }

    @Test
    void testExtractPowerWatts() {
        assertEquals(1780, CommandType.extractPowerWatts("1780_5150"));
        assertEquals(2000, CommandType.extractPowerWatts("2000_6000"));
        assertEquals(1500, CommandType.extractPowerWatts("eco_1500_4800"));
        assertNull(CommandType.extractPowerWatts("reboot"));
        assertNull(CommandType.extractPowerWatts(null));
    }

    @Test
    void testExtractHashrate() {
        assertEquals(5150, CommandType.extractHashrate("1780_5150"));
        assertEquals(6000, CommandType.extractHashrate("2000_6000"));
        assertEquals(4800, CommandType.extractHashrate("eco_1500_4800"));
        assertNull(CommandType.extractHashrate("reboot"));
        assertNull(CommandType.extractHashrate(null));
    }

    @Test
    void testExtractPowerWatts_WithDash() {
        assertEquals(1850, CommandType.extractPowerWatts("1850-5560"));
        assertEquals(5560, CommandType.extractHashrate("1850-5560"));
    }
}
