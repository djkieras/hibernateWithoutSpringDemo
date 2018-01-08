/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.dao;

import java.util.Map;
import java.util.Map.Entry;

import net.davekieras.hwsd.config.registry.FactoryRegistry;

public final class DaoFactory {

	private static class LazyHolder {
		static final DaoFactory INSTANCE = new DaoFactory();
	}

	public static DaoFactory getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void register(Class<? extends Dao<?>> clazz, Dao<?> dao) {
		FactoryRegistry.getInstance().register(clazz, dao);
	}
	
	public void register(Map<Class<? extends Dao<?>>, Dao<?>> daoMap) {
		for (Entry<Class<? extends Dao<?>>, Dao<?>> entry : daoMap.entrySet()) {
			FactoryRegistry.getInstance().register(entry.getKey(), entry.getValue());
		}
	}
	
	public static Class<?> getDao(Class<?> clazz) {
		return clazz.getClass().cast(FactoryRegistry.getInstance().get(clazz));
	}
	
	public DogDao getDogDao() {
		return DogDao.class.cast(FactoryRegistry.getInstance().get(DogDao.class));
	}
}
