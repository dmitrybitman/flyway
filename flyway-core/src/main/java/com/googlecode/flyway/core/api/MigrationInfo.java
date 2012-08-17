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


import java.util.Date;

/**
 * Info about a migration.
 */
public class MigrationInfo implements Comparable<MigrationInfo> {
    /**
     * The target version of this migration.
     */
    private final MigrationVersion version;

    /**
     * The description of the migration.
     */
    private final String description;

    /**
     * The name of the script to execute for this migration, relative to its classpath location.
     */
    private final String script;

    /**
     * The checksum of the migration.
     */
    private final Integer checksum;

    /**
     * The type of migration (INIT, SQL, ...)
     */
    private final MigrationType migrationType;

    /**
     * The state of the migration (PENDING, SUCCESS, ...)
     */
    private MigrationState migrationState = MigrationState.PENDING;

    /**
     * The timestamp when this migration was installed. (Only for applied migrations)
     */
    private Date installedOn;

    /**
     * The execution time (in millis) of this migration. (Only for applied migrations)
     */
    private Integer executionTime;

    /**
     * Creates a new MigrationInfo. It will be initialized in state PENDING.
     *
     * @param version        The target version of this migration.
     * @param description    The description of the migration.
     * @param script         The name of the script to execute for this migration, relative to its classpath location.
     * @param checksum       The checksum of the migration.
     * @param migrationType  The type of migration (INIT, SQL, ...)
     */
    public MigrationInfo(MigrationVersion version, String description, String script, Integer checksum, MigrationType migrationType) {
        this.version = version;
        this.description = description;
        this.script = script;
        this.checksum = checksum;
        this.migrationType = migrationType;
    }

    /**
     * Adds details about the execution of this migration.
     *
     * @param installedOn The timestamp when this migration was installed.
     * @param executionTime The execution time (in millis) of this migration.
     * @param migrationState The state of the migration (FAILED, SUCCESS, ...)
     */
    public void addExecutionDetails(Date installedOn, Integer executionTime, MigrationState migrationState) {
        this.installedOn = installedOn;
        this.executionTime = executionTime;
        this.migrationState = migrationState;
    }

    /**
     * @return The type of migration (INIT, SQL or JAVA)
     */
    public MigrationType getType() {
        return migrationType;
    }

    /**
     * @return The target version of this migration.
     */
    public Integer getChecksum() {
        return checksum;
    }

    /**
     * @return The schema version after the migration is complete.
     */
    public MigrationVersion getVersion() {
        return version;
    }

    /**
     * @return The description of the migration.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The name of the script to execute for this migration, relative to its classpath location.
     */
    public String getScript() {
        return script;
    }

    /**
     * @return The state of the migration (PENDING, SUCCESS, ...)
     */
    public MigrationState getState() {
        return migrationState;
    }

    /**
     * @return The timestamp when this migration was installed. (Only for applied migrations)
     */
    public Date getInstalledOn() {
        return installedOn;
    }

    /**
     * @return The execution time (in millis) of this migration. (Only for applied migrations)
     */
    public Integer getExecutionTime() {
        return executionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MigrationInfo that = (MigrationInfo) o;

        if (checksum != null ? !checksum.equals(that.checksum) : that.checksum != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (executionTime != null ? !executionTime.equals(that.executionTime) : that.executionTime != null)
            return false;
        if (installedOn != null ? !installedOn.equals(that.installedOn) : that.installedOn != null) return false;
        if (migrationState != that.migrationState) return false;
        if (migrationType != that.migrationType) return false;
        if (script != null ? !script.equals(that.script) : that.script != null) return false;
        return version.equals(that.version);
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        result = 31 * result + migrationType.hashCode();
        result = 31 * result + migrationState.hashCode();
        result = 31 * result + (installedOn != null ? installedOn.hashCode() : 0);
        result = 31 * result + (executionTime != null ? executionTime.hashCode() : 0);
        return result;
    }

    public int compareTo(MigrationInfo o) {
        return version.compareTo(o.version);
    }
}