/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.facade;

import net.davekieras.hwsd.persistence.entity.AbstractEntity;

public interface Facade<T extends AbstractEntity<T>> {

	T retrieveById(final Long id) throws Throwable;
	
	void save(T entity) throws Throwable;
	
	void delete(T entity) throws Throwable;
	
}
