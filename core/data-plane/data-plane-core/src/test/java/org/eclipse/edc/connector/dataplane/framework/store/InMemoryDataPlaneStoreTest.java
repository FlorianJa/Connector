/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.edc.connector.dataplane.framework.store;

import org.eclipse.edc.connector.dataplane.spi.store.DataPlaneStore;
import org.eclipse.edc.connector.dataplane.spi.testfixtures.store.DataPlaneStoreTestBase;
import org.eclipse.edc.spi.persistence.Lease;
import org.junit.jupiter.api.BeforeEach;

import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

class InMemoryDataPlaneStoreTest extends DataPlaneStoreTestBase {

    private final Map<String, Lease> leases = new HashMap<>();
    private InMemoryDataPlaneStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryDataPlaneStore(CONNECTOR_NAME, leases);
    }

    @Override
    protected DataPlaneStore getStore() {
        return store;
    }

    @Override
    protected void leaseEntity(String entityId, String owner, Duration duration) {
        leases.put(entityId, new Lease(owner, Clock.systemUTC().millis(), duration.toMillis()));
    }

    @Override
    protected boolean isLeasedBy(String entityId, String owner) {
        return leases.entrySet().stream().anyMatch(e -> e.getKey().equals(entityId) &&
                e.getValue().getLeasedBy().equals(owner) &&
                !isExpired(e.getValue()));
    }

    private boolean isExpired(Lease e) {
        return e.getLeasedAt() + e.getLeaseDuration() < Clock.systemUTC().millis();
    }
}
