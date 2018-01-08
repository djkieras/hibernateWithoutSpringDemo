/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.dataSource;

import javax.persistence.EntityManager;

import net.davekieras.hwsd.config.registry.FactoryRegistry;

public class EntityManagerFactory {

	private static final Class<javax.persistence.EntityManagerFactory> REGISTRY_KEY = javax.persistence.EntityManagerFactory.class;

	private static class LazyHolder {
		static final EntityManagerFactory INSTANCE = new EntityManagerFactory();
	}

	public static EntityManagerFactory getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public static javax.persistence.EntityManagerFactory getEntityManagerFactory() {
		return REGISTRY_KEY.cast(FactoryRegistry.getInstance().get(REGISTRY_KEY));
	}
	
	public void register(javax.persistence.EntityManagerFactory entityManagerFactory) {
		FactoryRegistry.getInstance().register(REGISTRY_KEY, entityManagerFactory);
	}
	
	public static EntityManager createEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}
	
}
