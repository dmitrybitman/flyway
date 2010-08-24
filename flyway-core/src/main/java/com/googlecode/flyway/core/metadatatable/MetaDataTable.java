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

import com.googlecode.flyway.core.dbsupport.DbSupport;
import com.googlecode.flyway.core.migration.MigrationState;
import com.googlecode.flyway.core.migration.MigrationType;
import com.googlecode.flyway.core.migration.SchemaVersion;
import com.googlecode.flyway.core.migration.sql.PlaceholderReplacer;
import com.googlecode.flyway.core.migration.sql.SqlScript;
import com.googlecode.flyway.core.util.ResourceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supports reading and writing to the metadata table.
 */
public class MetaDataTable {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(MetaDataTable.class);

    /**
     * Database-specific functionality.
     */
    private final DbSupport dbSupport;

    /**
     * The name of the schema metadata table used by flyway.
     */
    private final String tableName;

    /**
     * JdbcTemplate with ddl manipulation access to the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * The transaction template to use.
     */
    private final TransactionTemplate transactionTemplate;

    /**
     * Creates a new instance of the metadata table support.
     *
     * @param transactionTemplate The transaction template to use.
     * @param jdbcTemplate        JdbcTemplate with ddl manipulation access to the
     *                            database.
     * @param dbSupport           Database-specific functionality.
     * @param tableName           The name of the schema metadata table used by flyway.
     */
    public MetaDataTable(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate, DbSupport dbSupport,
                         String tableName) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.tableName = tableName;
    }

    /**
     * Checks whether Flyway's metadata table is already present in the
     * database.
     *
     * @return {@code true} if the table exists, {@code false} if it doesn't.
     */
    public boolean exists() {
        return dbSupport.tableExists(jdbcTemplate, tableName);
    }

    /**
     * Creates Flyway's metadata table.
     */
    private void create() {
        String location = dbSupport.getScriptLocation() + "createMetaDataTable.sql";
        String createMetaDataTableScriptSource = ResourceUtils.loadResourceAsString(location);

        Map<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("tableName", tableName);
        PlaceholderReplacer placeholderReplacer = new PlaceholderReplacer(placeholders, "${", "}");

        SqlScript sqlScript = new SqlScript(createMetaDataTableScriptSource, placeholderReplacer);
        sqlScript.execute(transactionTemplate, jdbcTemplate);
        LOG.info("Metadata table created: " + tableName);
    }

    /**
     * Creates the metadata table if it doesn't already exist.
     */
    public void createIfNotExists() {
        if (!exists()) {
            create();
        }
    }

    /**
     * Acquires an exclusive read-write lock on the metadata table. This lock
     * will be released automatically on commit.
     */
    public void lock() {
        if (dbSupport.supportsLocking()) {
            jdbcTemplate.queryForList("SELECT script FROM " + tableName + " FOR UPDATE");
        }
    }

    /**
     * Adds this row to the metadata table and mark it as current.
     *
     * @param metaDataTableRow The metaDataTableRow to add.
     */
    public void insert(final MetaDataTableRow metaDataTableRow) {
        jdbcTemplate.update("UPDATE " + tableName + " SET current_version=0");
        final SchemaVersion schemaVersion = metaDataTableRow.getVersion();
        final String version = schemaVersion.getVersion();
        final String description = schemaVersion.getDescription();
        final String state = metaDataTableRow.getState().name();
        final String migrationType = metaDataTableRow.getMigrationType().name();
        final Integer checksum = metaDataTableRow.getChecksum();
        final String scriptName = metaDataTableRow.getScript();
        final Integer executionTime = metaDataTableRow.getExecutionTime();
        jdbcTemplate.update("INSERT INTO " + tableName
                + " (version, description, migration_type, script, checksum, installed_by, execution_time, state, current_version)"
                + " VALUES (?, ?, ?, ?, ?, " + dbSupport.getCurrentUserFunction() + ", ?, ?, 1)",
                new Object[]{version, description, migrationType, scriptName, checksum, executionTime, state});
    }

    /**
     * @return The latest migration applied on the schema. {@code null} if no migration has been applied so far.
     */
    public MetaDataTableRow latestAppliedMigration() {
        if (!exists()) {
            return null;
        }

        String query = getSelectStatement() + " where current_version=1";
        @SuppressWarnings({"unchecked"})
        final List<MetaDataTableRow> metaDataTableRows = jdbcTemplate.query(query, new MetaDataTableRowMapper());

        if (metaDataTableRows.isEmpty()) {
            return null;
        }

        return metaDataTableRows.get(0);
    }

    /**
     * @return The list of all migrations applied on the schema (oldest first). An empty list if no migration has been
     *         applied so far.
     */
    public List<MetaDataTableRow> allAppliedMigrations() {
        if (!exists()) {
            return new ArrayList<MetaDataTableRow>();
        }

        String query = getSelectStatement();

        @SuppressWarnings({"unchecked"})
        final List<MetaDataTableRow> metaDataTableRows = jdbcTemplate.query(query, new MetaDataTableRowMapper());

        Collections.sort(metaDataTableRows);

        return metaDataTableRows;
    }

    /**
     * @return The select statement for reading the metadata table.
     */
    private String getSelectStatement() {
        return "select VERSION, DESCRIPTION, SCRIPT, EXECUTION_TIME, STATE, INSTALLED_ON, CHECKSUM, MIGRATION_TYPE from " + tableName;
    }

    /**
     * Converts this number into an Integer.
     *
     * @param number The Number to convert.
     * @return The matching Integer.
     */
    private Integer toInteger(Number number) {
        if (number == null) {
            return null;
        }

        return number.intValue();
    }

    /**
     * Row mapper for Migrations.
     */
    private class MetaDataTableRowMapper implements RowMapper {
        @Override
        public MetaDataTableRow mapRow(final ResultSet rs, int rowNum) throws SQLException {
            SchemaVersion schemaVersion = new SchemaVersion(rs.getString("VERSION"), rs.getString("DESCRIPTION"));
            MigrationType migrationType = MigrationType.valueOf(rs.getString("MIGRATION_TYPE"));
            String script = rs.getString("SCRIPT");
            Integer checksum = toInteger((Number) rs.getObject("CHECKSUM"));
            Date installedOn = rs.getTimestamp("INSTALLED_ON");
            Integer executionTime = toInteger((Number) rs.getObject("EXECUTION_TIME"));
            MigrationState migrationState = MigrationState.valueOf(rs.getString("STATE"));

            return new MetaDataTableRow(schemaVersion, migrationType, script, checksum, installedOn, executionTime, migrationState);
        }
    }
}