/**
 * Copyright (C) 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.flyway.core.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Info about all migrations, including applied, current and pending with details and status.
 */
public class MigrationInfos {
    /**
     * The migration infos.
     */
    private final List<MigrationInfo> migrationInfos;

    /**
     * Creates a new migrationInfos based on these migration infos.
     *
     * @param migrationInfos The migration infos.
     */
    public MigrationInfos(List<MigrationInfo> migrationInfos) {
        this.migrationInfos = migrationInfos;
    }

    /**
     * Retrieves the full set of infos about applied, current and future migrations.
     *
     * @return The full set of infos. An empty array if none.
     */
    public MigrationInfo[] all() {
        return migrationInfos.toArray(new MigrationInfo[migrationInfos.size()]);
    }

    /**
     * Retrieves the information of the current migration.
     *
     * @return The info. {@code null} if no migrations have been applied yet.
     */
    public MigrationInfo current() {
        // Look for the first applied & available migration.
        for (int i = migrationInfos.size() - 1; i >= 0; i--) {
            MigrationInfo migrationInfo = migrationInfos.get(i);
            if (MigrationState.SUCCESS.equals(migrationInfo.getState())
                    || MigrationState.FAILED.equals(migrationInfo.getState())) {
                return migrationInfo;
            }
        }

        return null;
    }

    /**
     * Retrieves the full set of infos about pending migrations, available locally, but not yet applied to the DB.
     *
     * @return The pending migrations. An empty array if none.
     */
    public MigrationInfo[] pending() {
        List<MigrationInfo> pendingMigrations = new ArrayList<MigrationInfo>();
        for (MigrationInfo migrationInfo : migrationInfos) {
            if (MigrationState.PENDING == migrationInfo.getState()) {
                pendingMigrations.add(migrationInfo);
            }
        }

        return pendingMigrations.toArray(new MigrationInfo[pendingMigrations.size()]);
    }
}