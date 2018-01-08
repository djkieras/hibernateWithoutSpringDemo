/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.dao;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.davekieras.hwsd.dataSource.EntityManagerFactory;
import net.davekieras.hwsd.persistence.TransactionCallback;
import net.davekieras.hwsd.persistence.entity.AbstractEntity;

public abstract class AbstractDao<T extends AbstractEntity<T>> implements Dao<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

	private final Class<T> type;

	private EntityManager entityManager;

	public AbstractDao(Class<T> type) {
		this.type = type;
	}

	protected EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = EntityManagerFactory.createEntityManager();
		}
		return entityManager;
	}

	public Class<T> getType() {
		return this.type;
	}

	protected T doInTransaction(TransactionCallback<T> callback) throws Throwable {
		T result = null;
		getEntityManager().getTransaction().begin();
		try {
			result = callback.execute();
		} catch (Throwable t) {
			LOG.error("Error in transaction; rolling back: " + t.getMessage(), t);
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().rollback();
			}
			throw t;
		} finally {
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().commit();
			}
		}
		return result;
	}

	public T retrieveById(final Long id) throws Throwable {
		return doInTransaction(new TransactionCallback<T>() {
			@Override
			public T execute() throws Throwable {
				return (T) getEntityManager().find(getType(), id);
			}
		});
	}

	@Override
	public void save(final T entity) throws Throwable {
		doInTransaction(new TransactionCallback<T>() {
			@Override
			public T execute() throws Throwable {
				getEntityManager().persist(entity);
				return null;
			}
		});
	}

	@Override
	public void delete(T entity) throws Throwable {
		doInTransaction(new TransactionCallback<T>() {
			@Override
			public T execute() throws Throwable {
				getEntityManager().remove(entity);
				return null;
			}
		});
	}

}
