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

package com.googlecode.flyway.maven;

import com.googlecode.flyway.core.Flyway;

/**
 * Base class for mojos that rely on loading migrations from the classpath.
 *
 * @execute phase="compile"
 */
abstract class AbstractMigrationLoadingMojo extends AbstractFlywayMojo {
    /**
     * The base package where the Java migrations are located. (default: db.migration) <br>
     * Also configurable with Maven or System Property: ${flyway.basePackage}
     *
     * @parameter default-value="${flyway.basePackage}"
     */
    private String basePackage;

    /**
     * The base directory on the classpath where the Sql migrations are located. (default: sql/location)<br>
     * Also configurable with Maven or System Property: ${flyway.baseDir}
     *
     * @parameter default-value="${flyway.baseDir}"
     */
    private String baseDir;

    /**
     * The encoding of Sql migrations. (default: UTF-8)<br>
     * Also configurable with Maven or System Property: ${flyway.encoding}
     *
     * @parameter default-value="${flyway.encoding}"
     */
    private String encoding;

    /**
     * The file name prefix for sql migrations (default: V)
     * Also configurable with Maven or System Property: ${flyway.sqlMigrationPrefix}
     *
     * @parameter default-value="${flyway.sqlMigrationPrefix}"
     */
    private String sqlMigrationPrefix;

    /**
     * The file name suffix for sql migrations (default: .sql)
     * Also configurable with Maven or System Property: ${flyway.sqlMigrationSuffix}
     *
     * @parameter default-value="${flyway.sqlMigrationSuffix}"
     */
    private String sqlMigrationSuffix;

    @Override
    protected void doExecute(Flyway flyway) throws Exception {
        if (basePackage != null) {
            flyway.setBasePackage(basePackage);
        }
        if (baseDir != null) {
            flyway.setBaseDir(baseDir);
        }
        if (encoding != null) {
            flyway.setEncoding(encoding);
        }
        if (sqlMigrationPrefix != null) {
            flyway.setSqlMigrationPrefix(sqlMigrationPrefix);
        }
        if (sqlMigrationSuffix != null) {
            flyway.setSqlMigrationSuffix(sqlMigrationSuffix);
        }
    }
}