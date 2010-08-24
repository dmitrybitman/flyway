/**
 * Copyright (C) 2009-2010 the original author or authors.
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

package com.googlecode.flyway.core.metadatatable;

import com.googlecode.flyway.core.migration.Migration;
import com.googlecode.flyway.core.migration.MigrationState;
import com.googlecode.flyway.core.migration.MigrationType;
import com.googlecode.flyway.core.migration.SchemaVersion;

import java.util.Date;

/**
 * A row in the schema metadata table containing information about a migration that has already been applied to a db.
 */
public class MetaDataTableRow implements Comparable<MetaDataTableRow> {
    /**
     * The version of this migration.
     */
    private SchemaVersion schemaVersion;

    /**
     * The type of the migration (INIT, SQL or JAVA).
     */
    private MigrationType migrationType;

    /**
     * The script name for the migration history.
     */
    private String script;

    /**
     * The checksum of the migration.
     */
    private Integer checksum;

    /**
     * The timestamp when this migration was applied to the database. (Automatically set by the database)
     */
    private Date installedOn;

    /**
     * The time (in ms) it took to execute.
     */
    private Integer executionTime;

    /**
     * The state of this migration.
     */
    private MigrationState state;

    /**
     * Creates a new MetaDataTableRow. This constructor is here to support the rowmapper.
     *
     * @param schemaVersion The version of this migration.
     * @param migrationType The type of the migration (INIT, SQL or JAVA).
     * @param script        The script name for the migration history.
     * @param checksum      The checksum of the migration.
     * @param installedOn   The timestamp when this migration was applied to the database. (Automatically set by the database)
     * @param executionTime The time (in ms) it took to execute.
     * @param state         The state of this migration.
     */
    public MetaDataTableRow(SchemaVersion schemaVersion, MigrationType migrationType, String script, Integer checksum, Date installedOn, Integer executionTime, MigrationState state) {
        this.schemaVersion = schemaVersion;
        this.migrationType = migrationType;
        this.script = script;
        this.checksum = checksum;
        this.installedOn = installedOn;
        this.executionTime = executionTime;
        this.state = state;
    }

    /**
     * Initializes a new metadatatable row with this migration.
     *
     * @param migration The migration that was or is being applied.
     */
    public MetaDataTableRow(Migration migration) {
        schemaVersion = migration.getVersion();
        migrationType = migration.getMigrationType();
        script = migration.getScript();
        checksum = migration.getChecksum();
    }

    /**
     * Updates this MetaDataTableRow with this execution time and this migration state.
     *
     * @param executionTime The time (in ms) it took to execute.
     * @param state         The state of this migration.
     */
    public void update(Integer executionTime, MigrationState state) {
        this.executionTime = executionTime;
        this.state = state;
    }

    /**
     * @return The type of the migration (INIT, SQL or JAVA).
     */
    public MigrationType getMigrationType() {
        return migrationType;
    }

    /**
     * @return The checksum of the migration.
     */
    public Integer getChecksum() {
        return checksum;
    }

    /**
     * @return The schema version after the migration is complete.
     */
    public SchemaVersion getVersion() {
        return schemaVersion;
    }

    /**
     * @return The state of this migration.
     */
    public MigrationState getState() {
        return state;
    }

    /**
     * @return The timestamp when this migration was applied to the database. (Automatically set by the database)
     */
    public Date getInstalledOn() {
        return installedOn;
    }

    /**
     * @return The time (in ms) it took to execute.
     */
    public Integer getExecutionTime() {
        return executionTime;
    }

    /**
     * @return The script name for the migration history.
     */
    public String getScript() {
        return script;
    }

    /**
     * Asserts that this migration has not failed.
     *
     * @throws IllegalStateException Thrown when this migration has failed.
     */
    public void assertNotFailed() {
        if (MigrationState.FAILED == state) {
            throw new IllegalStateException("Migration to version " + schemaVersion
                    + " failed! Please restore backups and roll back database and code!");
        }
    }

    @Override
    public int compareTo(MetaDataTableRow o) {
        return getVersion().compareTo(o.getVersion());
    }
}