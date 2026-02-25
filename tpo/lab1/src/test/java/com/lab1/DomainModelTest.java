package com.lab1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class DomainModelTest {
    
    @Test
    void testEntityCreation() {
        DomainModel.Entity caps = new DomainModel.Entity(DomainModel.EntityType.BIRTHDAY_CAP, Integer.MAX_VALUE);
        assertEquals(DomainModel.EntityType.BIRTHDAY_CAP, caps.getType());
        assertEquals(Integer.MAX_VALUE, caps.getCount());
        assertEquals(DomainModel.State.FALLEN_OUT, caps.getState());
        assertNotNull(caps.getLocation());
    }
    
    @ParameterizedTest
    @EnumSource(DomainModel.EntityType.class)
    void testAllEntityTypes(DomainModel.EntityType type) {
        DomainModel.Entity entity = new DomainModel.Entity(type, 1);
        assertNotNull(entity);
        assertEquals(type, entity.getType());
        assertEquals(1, entity.getCount());
    }
    
    @Test
    void testCosmicEventProcessing() {
        DomainModel.CosmicEvent event = new DomainModel.CosmicEvent();
        
        event.addEntity(new DomainModel.Entity(DomainModel.EntityType.BIRTHDAY_CAP, Integer.MAX_VALUE));
        event.addEntity(new DomainModel.Entity(DomainModel.EntityType.INFLATABLE_BALL, Integer.MAX_VALUE));
        event.addEntity(new DomainModel.Entity(DomainModel.EntityType.MARKET_ANALYST, 7));
        event.addEntity(new DomainModel.Entity(DomainModel.EntityType.FRIED_EGG, 239000));
        
        assertEquals(4, event.getEntities().size());
        
        event.processEvent();
        
        long floatingCount = event.getEntities().stream()
            .filter(e -> e.getState() == DomainModel.State.FLOATING_AWAY)
            .count();
        assertEquals(2, floatingCount, "Should have 2 floating entities");
        
        long deadCount = event.getEntities().stream()
            .filter(e -> e.getState() == DomainModel.State.DEAD)
            .count();
        assertEquals(1, deadCount, "Should have 1 dead entity");
        
        long materializedCount = event.getEntities().stream()
            .filter(e -> e.getState() == DomainModel.State.MATERIALIZED)
            .count();
        assertEquals(1, materializedCount, "Should have 1 materialized entity");
    }
    
    @ParameterizedTest
    @CsvSource({
        "BIRTHDAY_CAP, 1, FLOATING_AWAY, Universe",
        "INFLATABLE_BALL, 1, FLOATING_AWAY, Universe",
        "MARKET_ANALYST, 7, DEAD, source",
        "FRIED_EGG, 239000, MATERIALIZED, Poghril"
    })
    void testEntityTransitions(DomainModel.EntityType type, int count, 
                              DomainModel.State expectedState, String expectedLocation) {
        DomainModel.Entity entity = new DomainModel.Entity(type, count);
        DomainModel.CosmicEvent event = new DomainModel.CosmicEvent();
        event.addEntity(entity);
        
        event.processEvent();
        
        assertEquals(expectedState, entity.getState(), 
            String.format("State mismatch for %s", type));
        
        String location = entity.getLocation().getDescription();
        assertTrue(location.contains(expectedLocation) || expectedLocation.contains(location),
            String.format("Location '%s' should contain '%s'", location, expectedLocation));
    }
    
    @Test
    void testAnalystDeath() {
        DomainModel.Entity analyst = new DomainModel.Entity(DomainModel.EntityType.MARKET_ANALYST, 7);
        DomainModel.CosmicEvent event = new DomainModel.CosmicEvent();
        event.addEntity(analyst);
        
        event.processEvent();
        
        assertEquals(DomainModel.State.DEAD, analyst.getState());
        assertNotNull(analyst.getDeathCause(), "Death cause should be set");
    }
    
    @Test
    void testLargeNumbers() {
        DomainModel.Entity eggs = new DomainModel.Entity(DomainModel.EntityType.FRIED_EGG, 239000);
        assertEquals(239000, eggs.getCount());
        
        DomainModel.Entity caps = new DomainModel.Entity(DomainModel.EntityType.BIRTHDAY_CAP, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, caps.getCount());
    }
    
    @Test
    void testLocationCreation() {
        DomainModel.Location loc1 = new DomainModel.Location("test location");
        assertEquals("test location", loc1.getDescription());
        
        DomainModel.Location loc2 = new DomainModel.Location("Universe", "Poghril", "Pansel system");
        assertTrue(loc2.getDescription().contains("Poghril"));
        assertTrue(loc2.getDescription().contains("Pansel"));
    }
}