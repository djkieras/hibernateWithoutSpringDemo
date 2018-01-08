/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.config.registry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class FactoryRegistry {

	private static final ConcurrentMap<Class<?>, Object> registryMap = new ConcurrentHashMap<>();
	
	private FactoryRegistry() {}
	
    private static class LazyHolder {
        static final FactoryRegistry INSTANCE = new FactoryRegistry();
    }

	public static FactoryRegistry getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void register(Class<?> key, Object value) {
		registryMap.putIfAbsent(key, value);
	}
	
	public Object get(Class<?> key) {
		return registryMap.get(key);
	}
}
