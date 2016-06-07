package com.yin.myproject.practice.pattern.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yin.myproject.practice.common.storage.mysql.JooqMysqlProvider;
import com.yin.myproject.practice.common.storage.mysql.UnitOfWorkProvider;
import com.yin.myproject.practice.common.storage.ssdb.SSdbProvider;
import com.yin.myproject.practice.model.Entity;

public abstract class GenericRepositoryImpl<E extends Entity> implements GenericRepository<E>, UnitOfWorkProvider<E> {

	private static final Logger logger = LoggerFactory.getLogger(GenericRepositoryImpl.class);
	
	protected JooqMysqlProvider<E> myqlProvider;

    private SSdbProvider<E> ssdbProvider;

    
}
