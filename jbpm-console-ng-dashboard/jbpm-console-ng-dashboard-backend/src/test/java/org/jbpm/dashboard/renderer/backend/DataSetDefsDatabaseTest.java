/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.dashboard.renderer.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class DataSetDefsDatabaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetDefsDatabaseTest.class);

    EntityManagerFactory emf;
    Connection conn;
    Database database;

    @Mock
    DataSetDefRegistry defRegistry;

    @InjectMocks
    DataSetDefsBootstrap dataSetDefs;

    public DataSetDefsDatabaseTest(Database database) {
        this.database = database;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Database.H2},
                {Database.H2_DB2},
                {Database.H2_MSSQLServer},
                {Database.H2_MySQL},
                {Database.H2_Oracle},
                {Database.H2_PostgreSQL},
                {Database.HSQLDB}});
    }

    @Before
    public void setup() throws SQLException {
        LOGGER.info("Testing DataSet SQL with DB: {}", database);
        MockitoAnnotations.initMocks(this);

        emf = Persistence.createEntityManagerFactory("org.jbpm.domain", database.properties());

        conn = DriverManager.getConnection(database.url);
    }

    @After
    public void clean() throws SQLException {
        if (conn != null) {
            conn.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testDataSetSQL() {
        dataSetDefs.registerDataSetDefinitions();

        final ArgumentCaptor<SQLDataSetDef> dataSetDef = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(defRegistry, times(2)).registerDataSetDef(dataSetDef.capture());


        for (SQLDataSetDef dataSet : dataSetDef.getAllValues()) {
            if (dataSet.getDbSQL() == null) {
                continue;
            }

            try {
                LOGGER.info("Testing SQL DataSet: {}", dataSet.getName());
                LOGGER.info("SQL: {}", dataSet.getDbSQL());

                PreparedStatement ps = conn.prepareStatement(dataSet.getDbSQL());
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData resultSetMetaData = rs.getMetaData();
                for (int i=0; i < dataSet.getColumns().size(); i++) {
                    assertEquals("select column name don't match", dataSet.getColumns().get(i).getId(), resultSetMetaData.getColumnLabel(i+1));
                }
                ps.close();

                final String countSQL = "SELECT COUNT(*) FROM (SELECT * FROM (" + dataSet.getDbSQL() + ") )";

                conn.prepareStatement(countSQL).executeQuery();
            } catch (Exception ex) {
                LOGGER.error("Failed to execute query for DataSet {} on DB {}", dataSet.getName(), database.name(), ex);
                fail(ex.getMessage());
            }
        }

    }

    public enum Database {

        HSQLDB("org.hibernate.dialect.HSQLDialect", "jdbc:hsqldb:mem:datasetdb;sql.enforce_refs=true;shutdown=true", "org.hsqldb.jdbc.JDBCDriver"),
        H2("org.hibernate.dialect.H2Dialect", "jdbc:h2:mem:datasetdb", "org.h2.Driver"),
        H2_DB2("org.hibernate.dialect.DB2Dialect", "jdbc:h2:mem:datasetdbdb2;MODE=DB2", "org.h2.Driver"),
        H2_Oracle("org.hibernate.dialect.Oracle10gDialect", "jdbc:h2:mem:datasetdboracle;MODE=Oracle", "org.h2.Driver"),
        H2_PostgreSQL("org.hibernate.dialect.PostgreSQL82Dialect", "jdbc:h2:mem:datasetdbpostgresql;MODE=PostgreSQL", "org.h2.Driver"),
        H2_MySQL("org.hibernate.dialect.MySQLDialect", "jdbc:h2:mem:datasetdbmysql;MODE=MySQL", "org.h2.Driver"),
        H2_MSSQLServer("org.hibernate.dialect.SQLServerDialect", "jdbc:h2:mem:datasetdbmssql;MODE=MSSQLServer", "org.h2.Driver");

        private String dialect;

        private String url;

        private String driver;

        Database(final String dialect, final String url, final String driver) {
            this.dialect = dialect;
            this.url = url;
            this.driver = driver;
        }

        public Properties properties() {
            final Properties p = new Properties();
            p.put("hibernate.dialect", dialect);
            p.put("javax.persistence.jdbc.driver", driver);
            p.put("javax.persistence.jdbc.url", url);
            return p;
        }
    }
}
