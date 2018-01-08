/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.test.dbunit;

import org.dbunit.dataset.datatype.IDataTypeFactory;

import net.davekieras.hwsd.config.registry.FactoryRegistry;

public class DbUnitDatatypeFactoryFactory {

	private static final Class<IDataTypeFactory> REGISTRY_KEY = IDataTypeFactory.class;

	private static class LazyHolder {
		static final DbUnitDatatypeFactoryFactory INSTANCE = new DbUnitDatatypeFactoryFactory();
	}

	public static DbUnitDatatypeFactoryFactory getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public static IDataTypeFactory getDatatypeFactory() {
		return REGISTRY_KEY.cast(FactoryRegistry.getInstance().get(REGISTRY_KEY));
	}
	
	public void register(IDataTypeFactory datatypeFactory) {
		FactoryRegistry.getInstance().register(REGISTRY_KEY, datatypeFactory);
	}
}
