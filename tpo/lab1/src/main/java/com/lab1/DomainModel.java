package com.lab1;

import java.util.*;

public class DomainModel {
    
    public enum EntityType {
        BIRTHDAY_CAP, INFLATABLE_BALL, MARKET_ANALYST, FRIED_EGG, UNKNOWN
    }
    
    public enum DeathCause {
        SUFFOCATION, SURPRISE, UNKNOWN
    }
    
    public enum State {
        FALLEN_OUT, FLOATING_AWAY, MATERIALIZED, DEAD, UNKNOWN
    }
    
    public static class Entity {
        private EntityType type;
        private int count;
        private State state;
        private DeathCause deathCause;
        private Location location;
        
        public Entity(EntityType type, int count) {
            this.type = type;
            this.count = count;
            this.state = State.FALLEN_OUT;
            this.location = new Location("near the source");
        }
        
        public EntityType getType() { return type; }
        public int getCount() { return count; }
        public State getState() { return state; }
        public void setState(State state) { this.state = state; }
        public DeathCause getDeathCause() { return deathCause; }
        public void setDeathCause(DeathCause deathCause) { this.deathCause = deathCause; }
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
    }
    
    public static class Location {
        private String description;
        private String universe;
        private String planet;
        private String starSystem;
        
        public Location(String description) {
            this.description = description;
        }
        
        public Location(String universe, String planet, String starSystem) {
            this.universe = universe;
            this.planet = planet;
            this.starSystem = starSystem;
            this.description = String.format("%s in %s system", planet, starSystem);
        }
        
        public String getDescription() { return description; }
    }
    
    public static class CosmicEvent {
        private List<Entity> entities;
        private Date timestamp;
        
        public CosmicEvent() {
            this.entities = new ArrayList<>();
            this.timestamp = new Date();
        }
        
        public void addEntity(Entity entity) {
            entities.add(entity);
        }
        
        public void processEvent() {
            for (Entity entity : entities) {
                processEntity(entity);
            }
        }
        
        private void processEntity(Entity entity) {
            switch (entity.getType()) {
                case BIRTHDAY_CAP:
                case INFLATABLE_BALL:
                    entity.setState(State.FLOATING_AWAY);
                    entity.setLocation(new Location("far reaches of the Universe"));
                    break;
                    
                case MARKET_ANALYST:
                    entity.setState(State.DEAD);
                    entity.setDeathCause(DeathCause.SUFFOCATION);
                    entity.setLocation(new Location("near the source"));
                    break;
                    
                case FRIED_EGG:
                    entity.setState(State.MATERIALIZED);
                    entity.setLocation(new Location("Universe", "Poghril", "Pansel system"));
                    break;
            }
        }
        
        public List<Entity> getEntities() { return entities; }
    }
}