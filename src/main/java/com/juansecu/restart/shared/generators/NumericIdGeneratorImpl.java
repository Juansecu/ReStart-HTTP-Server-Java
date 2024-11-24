package com.juansecu.restart.shared.generators;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class NumericIdGeneratorImpl implements IdentifierGenerator {
    @Override
    public Serializable generate(final SharedSessionContractImplementor session, Object object) {
        final String id = "" + UUID.randomUUID().toString().hashCode();
        return Integer.parseInt(id.replace("-", ""));
    }
}
