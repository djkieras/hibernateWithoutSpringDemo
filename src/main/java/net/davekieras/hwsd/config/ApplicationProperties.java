/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplicationProperties {

	private static Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);

	public static final String DEFAULT_PROPERTIES_FILE = "application.properties";
	
	public static final String DATASOURCE = "datasource";
	public static final String DATASOURCE_URL = "datasource.url";
	public static final String DATASOURCE_USER = "datasource.user";
	public static final String DATASOURCE_PASSWORD = "datasource.password";
	public static final String DATASOURCE_PROVIDER = "datasource.provider";

	public static final String HIBERNATE_DIALECT = "hibernate.dialect";
	public static final String HIBERNATE_HBM2DDL = "hibernate.hbm2ddl.auto";
	public static final String HIBERNATE_SHOWSQL = "hibernate.show_sql";
	public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";

	private static Map<Object, Object> propertyMap = new ConcurrentHashMap<>();
	private static Properties hibernateProperties = new Properties();

	private volatile Boolean loaded = Boolean.FALSE;

	private ApplicationProperties() {
	}

	private static class LazyHolder {
		static final ApplicationProperties INSTANCE = new ApplicationProperties();
	}

	public static ApplicationProperties getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void load() {
		load(DEFAULT_PROPERTIES_FILE);
	}

	public void load(String applicationPropertiesFileName) {
		InputStream is = ApplicationProperties.class.getResourceAsStream(applicationPropertiesFileName);
		if (is == null) {
			is = ApplicationProperties.class.getClassLoader().getResourceAsStream(applicationPropertiesFileName);
			if (is == null) {
				is = ClassLoader.getSystemClassLoader().getResourceAsStream(applicationPropertiesFileName);
			}
		}
		load(is);
	}

	public void load(InputStream is) {
		// double-checked locking ensures that multiple threads cannot load the
		// properties, and it can be loaded only once
		if (!loaded) {
			synchronized (this) {
				if (!loaded) {
					Properties properties = new Properties();
					try {
						properties.load(is);
						// Because Properties extends Hashtable, all accesses
						// are
						// synchronized
						// Putting the properties into a ConcurrentHashMap
						// allows for the
						// most efficient access via multiple threads
						// without any synchronization. This is safe because the
						// values are
						// not expected to change.
						propertyMap = new ConcurrentHashMap<Object, Object>(properties);
						for (Entry<?, ?> entry : properties.entrySet()) {
							if (entry.getKey().toString().toLowerCase().startsWith("hibernate.")) {
								hibernateProperties.put(entry.getKey().toString(), entry.getValue().toString());
							}
						}
					} catch (IOException e) {
						LOG.error("Cannot read " + DEFAULT_PROPERTIES_FILE + " file; application will fail to start.", e);
						throw new ExceptionInInitializerError(e);
					} catch (Throwable t) {
						LOG.error("Problem in reading " + DEFAULT_PROPERTIES_FILE + " file; application will fail to start.",
								t);
						throw new ExceptionInInitializerError(t);
					}
				}
			}
		}
	}

	public static Properties getHibernateProperties() {
		return hibernateProperties;
	}
	
	public static String getProperty(String propertyKey) {
		return propertyMap.get(propertyKey).toString();
	}
	
	public static String getDatasource() {
		return propertyMap.get(DATASOURCE).toString();
	}

	public static String getDatasourceUrl() {
		return propertyMap.get(DATASOURCE_URL).toString();
	}

	public static String getDatasourceUser() {
		return propertyMap.get(DATASOURCE_USER).toString();
	}

	public static String getDatasourcePassword() {
		return propertyMap.get(DATASOURCE_USER).toString();
	}

}
