/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.dataSource;

import javax.sql.DataSource;

import net.davekieras.hwsd.config.registry.FactoryRegistry;

public class DataSourceFactory {

	private static final Class<DataSource> REGISTRY_KEY = DataSource.class;

	private static class LazyHolder {
		static final DataSourceFactory INSTANCE = new DataSourceFactory();
	}

	public static DataSourceFactory getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public static DataSource getDataSource() {
		return REGISTRY_KEY.cast(FactoryRegistry.getInstance().get(REGISTRY_KEY));
	}
	
	public void register(DataSource dataSource) {
		FactoryRegistry.getInstance().register(REGISTRY_KEY, dataSource);
	}
	
}
