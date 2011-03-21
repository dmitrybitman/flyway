/**
 * Copyright (C) 2010-2011 the original author or authors.
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
package com.googlecode.flyway.core.dbsupport.db2;

import com.googlecode.flyway.core.dbsupport.DbSupport;
import com.googlecode.flyway.core.migration.MigrationTestCase;
import org.junit.Ignore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test to demonstrate the migration functionality using DB2.
 */
@ContextConfiguration(locations = {"classpath:migration/db2/db2-context.xml"})
public class DB2MigrationMediumTest extends MigrationTestCase {
    @Override
    protected String getBaseDir() {
        return "migration/sql";
    }

    @Override
    protected String getQuoteBaseDir() {
        return "migration/quote";
    }

    @Override
    protected DbSupport getDbSupport(JdbcTemplate jdbcTemplate) {
        return new DB2DbSupport(jdbcTemplate);
    }

    @Ignore
    public void semicolonWithinStringLiteral() {
        // TODO implement for DB2
    }
}