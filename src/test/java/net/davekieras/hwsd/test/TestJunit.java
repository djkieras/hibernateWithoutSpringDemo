/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.davekieras.hwsd.persistence.entity.impl.Dog;
import net.davekieras.hwsd.persistence.facade.impl.DogFacade;

public class TestJunit extends AbstractDaoTest {

	private static final Logger LOG = LoggerFactory.getLogger(TestJunit.class);
	
	@Test
	public void testLogger() throws Throwable {
		//Dog dog = DaoFactory.getDogDao().retrieveById(1L);
		Dog dog = new DogFacade().retrieveById(1L);
		LOG.info("DOG IS " + dog);
	}
	
}
