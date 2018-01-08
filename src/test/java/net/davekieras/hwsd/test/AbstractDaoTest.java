/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.test;

import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.davekieras.hwsd.dataSource.DataSourceFactory;
import net.davekieras.hwsd.dataSource.EntityManagerFactory;
import net.davekieras.hwsd.test.config.ApplicationBootstrap;
import net.davekieras.hwsd.test.dbunit.DbUnitDatatypeFactoryFactory;

public class AbstractDaoTest {

	private Logger LOG = LoggerFactory.getLogger(AbstractDaoTest.class);

	private IDatabaseTester databaseTester;
	private EntityManager entityManager;
	private DataSource dataSource;
	private IDataTypeFactory datatypeFactory;
	
	private String dataSetFileName = "dataset.xml";

	//hook to wire all the JPA and Hibernate stuff
	static {
		ApplicationBootstrap.getInstance().initialize("applicationTestPooled.properties");
	}
	
	/**
	 * Override to use a different dataset than the default. Set before the setUp() method is executed.
	 * 
	 * @param dataSetFileName
	 */
	protected void setDataSetFileName(String dataSetFileName) {
		this.dataSetFileName = dataSetFileName;
	}
	
	protected String getDataSetFileName() {
		return dataSetFileName;
	}

	//Not enough contention on these lazy loads to justify double-checked locking and synchronization
	
	protected EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = EntityManagerFactory.createEntityManager();
		}
		return entityManager;
	}

	protected DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = DataSourceFactory.getDataSource();
		}
		return dataSource;
	}
	
	protected IDataTypeFactory getDbUnitDatatypeFactory() {
		if (datatypeFactory == null) {
			datatypeFactory = DbUnitDatatypeFactoryFactory.getDatatypeFactory();
		}
		return datatypeFactory;
	}
	
	@Before
	public void setUp() throws Exception {
		getEntityManager().getTransaction().begin();
		try {
			databaseTester = new DataSourceDatabaseTester(getDataSource()) {
				@Override
				public IDatabaseConnection getConnection() throws Exception {
					IDatabaseConnection dbC = super.getConnection();
					dbC.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
							getDbUnitDatatypeFactory());
					dbC.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
					dbC.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
					return dbC;
				}
			};
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(getDataSetFileName());
			IDataSet dataSet = new FlatXmlDataSetBuilder().build(inputStream);
			databaseTester.setDataSet(dataSet);
			databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
			databaseTester.setTearDownOperation(DatabaseOperation.NONE);
			databaseTester.onSetup();
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().rollback();
			}
		} finally {
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().commit();
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		if (databaseTester != null) {
			databaseTester.onTearDown();
		}
	}

}
