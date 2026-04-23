package com.cryptoterm.backend.device.infrastructure.persistence;

import com.cryptoterm.backend.device.domain.Condition;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ComparisonOperatorConverter implements UserType<Condition.ComparisonOperator> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Condition.ComparisonOperator> returnedClass() {
        return Condition.ComparisonOperator.class;
    }

    @Override
    public boolean equals(Condition.ComparisonOperator x, Condition.ComparisonOperator y) {
        return x == y;
    }

    @Override
    public int hashCode(Condition.ComparisonOperator x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Condition.ComparisonOperator nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return Condition.ComparisonOperator.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Condition.ComparisonOperator value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public Condition.ComparisonOperator deepCopy(Condition.ComparisonOperator value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Condition.ComparisonOperator value) {
        return value;
    }

    @Override
    public Condition.ComparisonOperator assemble(Serializable cached, Object owner) {
        return (Condition.ComparisonOperator) cached;
    }

    @Override
    public Condition.ComparisonOperator replace(Condition.ComparisonOperator detached, Condition.ComparisonOperator managed, Object owner) {
        return detached;
    }
}
