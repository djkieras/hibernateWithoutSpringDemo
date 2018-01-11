/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.davekieras.hwsd.test.config.ApplicationConfig;

public class ApplicationBootstrap {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationBootstrap.class);
	
	private volatile Boolean loaded = Boolean.FALSE;

	private static class LazyHolder {
		static final ApplicationBootstrap INSTANCE = new ApplicationBootstrap();
	}

	public static ApplicationBootstrap getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void initialize() {
		initialize(null);
	}

	public void initialize(String applicationPropertiesFileName) {
		// double-checked locking ensures that multiple threads cannot load the
		// config, and it can be loaded only once
		if (!loaded) {
			synchronized (this) {
				if (!loaded) {
					try {
						// This loads the application properties
						if (applicationPropertiesFileName == null) {
							ApplicationProperties.getInstance().load();
						} else {
							ApplicationProperties.getInstance().load(applicationPropertiesFileName);
						}
						// This builds the database connections
						ApplicationConfig.getInstance().load();
					} catch (Throwable t) {
						LOG.error("Exception initializing application for test", t);
						throw new ExceptionInInitializerError(t);
					}
				}
			}
		}
	}
	
}
