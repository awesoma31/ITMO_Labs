package com.cryptoterm.backend.command.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PowerMode enum.
 */
class PowerModeTest {

    @Test
    void testFromPower_AntminerS19ProHydro_Eco() {
        assertEquals(PowerMode.ECO, PowerMode.fromPower(1500, "Antminer S19 Pro Hydro"));
        assertEquals(PowerMode.ECO, PowerMode.fromPower(1700, "Antminer S19 Pro Hydro"));
        assertEquals(PowerMode.ECO, PowerMode.fromPower(1800, "Antminer S19 Pro Hydro"));
    }

    @Test
    void testFromPower_AntminerS19ProHydro_Standard() {
        assertEquals(PowerMode.STANDARD, PowerMode.fromPower(1850, "Antminer S19 Pro Hydro"));
        assertEquals(PowerMode.STANDARD, PowerMode.fromPower(1900, "Antminer S19 Pro Hydro"));
    }

    @Test
    void testFromPower_AntminerS19ProHydro_Overclock() {
        assertEquals(PowerMode.OVERCLOCK, PowerMode.fromPower(1950, "Antminer S19 Pro Hydro"));
        assertEquals(PowerMode.OVERCLOCK, PowerMode.fromPower(2000, "Antminer S19 Pro Hydro"));
        assertEquals(PowerMode.OVERCLOCK, PowerMode.fromPower(2200, "Antminer S19 Pro Hydro"));
    }

    @Test
    void testFromPower_DefaultThresholds() {
        assertEquals(PowerMode.ECO, PowerMode.fromPower(1000, "Other Model"));
        assertEquals(PowerMode.ECO, PowerMode.fromPower(1500, "Other Model"));
        assertEquals(PowerMode.STANDARD, PowerMode.fromPower(1600, "Other Model"));
        assertEquals(PowerMode.STANDARD, PowerMode.fromPower(2000, "Other Model"));
        assertEquals(PowerMode.OVERCLOCK, PowerMode.fromPower(2100, "Other Model"));
    }

    @Test
    void testFromPower_NullModel() {
        assertEquals(PowerMode.ECO, PowerMode.fromPower(1000, null));
        assertEquals(PowerMode.STANDARD, PowerMode.fromPower(1700, null));
        assertEquals(PowerMode.OVERCLOCK, PowerMode.fromPower(2500, null));
    }
}
