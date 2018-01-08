/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.dao.impl;

import net.davekieras.hwsd.persistence.dao.AbstractDao;
import net.davekieras.hwsd.persistence.dao.DogDao;
import net.davekieras.hwsd.persistence.entity.impl.Dog;

public class DogDaoImpl extends AbstractDao<Dog> implements DogDao {

	public DogDaoImpl() {
		super(Dog.class);
	}

	public DogDaoImpl(Class<Dog> type) {
		super(type);
	}

}
