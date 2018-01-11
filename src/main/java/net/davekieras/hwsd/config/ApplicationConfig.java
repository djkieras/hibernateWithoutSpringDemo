/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.config;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import net.davekieras.hwsd.config.registry.FactoryRegistry;
import net.davekieras.hwsd.dataSource.DataSourceFactory;
import net.davekieras.hwsd.persistence.dao.Dao;
import net.davekieras.hwsd.persistence.dao.DaoFactory;
import net.davekieras.hwsd.persistence.dao.DogDao;
import net.davekieras.hwsd.persistence.dao.impl.DogDaoImpl;
import net.davekieras.hwsd.persistence.entity.impl.Dog;

public final class ApplicationConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);
	private volatile Boolean loaded = Boolean.FALSE;

	private ApplicationConfig() {
	}

	private static class LazyHolder {
		static final ApplicationConfig INSTANCE = new ApplicationConfig();
	}

	public static ApplicationConfig getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void load() {
		//double-checked locking ensures that multiple threads cannot load the config, and it can be loaded only once
		if (!loaded) {
			synchronized (this) {
				if (!loaded) {
					LOG.info("Loading configuration...");
					registerDaos();
					DataSource dataSource = newDataSource();
					newEntityManagerFactory(dataSource);
					loaded = Boolean.TRUE;
				}
			}
		}
	}

	private final DataSource newDataSource() {
		LOG.info("Loading dataSource...");
		SQLServerDataSource dataSource = new SQLServerDataSource();
		dataSource.setURL(ApplicationProperties.getDatasourceUrl());
		dataSource.setUser(ApplicationProperties.getDatasourceUser());
		dataSource.setPassword(ApplicationProperties.getDatasourcePassword());
		DataSourceFactory.getInstance().register(dataSource);
		return dataSource;
	}

	private final EntityManagerFactory newEntityManagerFactory(DataSource dataSource) {
		LOG.info("Loading entityManagerFactory...");
		PersistenceUnitInfo persistenceUnitInfo = newPersistenceUnitInfo("PersistenceName", dataSource);
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(AvailableSettings.INTERCEPTOR, interceptor());
		EntityManagerFactory factory = new EntityManagerFactoryBuilderImpl(
				new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration).build();
		FactoryRegistry.getInstance().register(EntityManagerFactory.class, factory);
		return factory;
	}
	
	private final PersistenceUnitInfo newPersistenceUnitInfo(String persistenceUnitName, DataSource dataSource) {
		Properties props = ApplicationProperties.getHibernateProperties();
		props.put("hibernate.connection.datasource", dataSource);
		return new PersistenceUnitInfo() {
			
		    private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;

		    @Override
		    public String getPersistenceUnitName() {
		        return persistenceUnitName;
		    }

		    @Override
		    public String getPersistenceProviderClassName() {
		        return HibernatePersistenceProvider.class.getName();
		    }

		    @Override
		    public PersistenceUnitTransactionType getTransactionType() {
		        return transactionType;
		    }

		    @Override
		    public DataSource getJtaDataSource() {
		        return null;
		    }

		    @Override
		    public DataSource getNonJtaDataSource() {
		        return null;
		    }

		    @Override
		    public List<String> getMappingFileNames() {
		        return null;
		    }

		    @Override
		    public List<URL> getJarFileUrls() {
		        return Collections.emptyList();
		    }

		    @Override
		    public URL getPersistenceUnitRootUrl() {
		        return null;
		    }

		    @Override
		    public List<String> getManagedClassNames() {
		        return getEntityClassNames();
		    }

		    @Override
		    public boolean excludeUnlistedClasses() {
		        return false;
		    }

		    @Override
		    public SharedCacheMode getSharedCacheMode() {
		        return SharedCacheMode.UNSPECIFIED;
		    }

		    @Override
		    public ValidationMode getValidationMode() {
		        return ValidationMode.AUTO;
		    }

		    public Properties getProperties() {
		        return props;
		    }

		    @Override
		    public String getPersistenceXMLSchemaVersion() {
		        return "2.1";
		    }

		    @Override
		    public ClassLoader getClassLoader() {
		        return Thread.currentThread().getContextClassLoader();
		    }

		    @Override
		    public void addTransformer(ClassTransformer transformer) {

		    }

		    @Override
		    public ClassLoader getNewTempClassLoader() {
		        return null;
		    }
		};
	}

	private final Interceptor interceptor() {
		return EmptyInterceptor.INSTANCE;
	}

	private void registerDaos() {
		Map<Class<? extends Dao<?>>, Dao<?>> daoMap = new HashMap<>();
		daoMap.put(DogDao.class, new DogDaoImpl());
		DaoFactory.getInstance().register(daoMap);
	}
	
	private List<String> getEntityClassNames() {
		return Arrays.asList(new String[]{Dog.class.getName()});
	}

}
