package com.cryptoterm.backend.device.domain;

import com.cryptoterm.backend.auth.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "condition")
public class Condition {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "comparison_operator", nullable = false, columnDefinition = "comparison_operator")
    private ComparisonOperator comparisonOperator;

    @Column(name = "threshold_value", nullable = false)
    private BigDecimal thresholdValue;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public enum ComparisonOperator {
        eq("=="),   // равно
        gt(">"),    // больше
        lt("<"),    // меньше
        gte(">="),  // больше или равно
        lte("<=");  // меньше или равно
        
        private final String symbol;
        
        ComparisonOperator(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public static ComparisonOperator fromSymbol(String symbol) {
            for (ComparisonOperator op : values()) {
                if (op.symbol.equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Unknown comparison operator: " + symbol);
        }
    }

    public UUID getId() { 
        return id; 
    }
    
    public void setId(UUID id) { 
        this.id = id; 
    }
    
    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) { 
        this.user = user; 
    }
    
    public Device getDevice() { 
        return device; 
    }
    
    public void setDevice(Device device) { 
        this.device = device; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public ComparisonOperator getComparisonOperator() { 
        return comparisonOperator; 
    }
    
    public void setComparisonOperator(ComparisonOperator comparisonOperator) { 
        this.comparisonOperator = comparisonOperator; 
    }
    
    public BigDecimal getThresholdValue() { 
        return thresholdValue; 
    }
    
    public void setThresholdValue(BigDecimal thresholdValue) { 
        this.thresholdValue = thresholdValue; 
    }
    
    public OffsetDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}
