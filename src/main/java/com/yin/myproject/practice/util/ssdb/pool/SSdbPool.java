package com.yin.myproject.practice.util.ssdb.pool;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

/**
 * ssdb¡¨Ω”≥ÿ
 */
public class SSdbPool extends BasePool<SSdbConnection> {

    public SSdbPool(final Config poolConfig, final String hostPortTimeout) {
        this(poolConfig, hostPortTimeout, Protocol.DEFAULT_DATABASE);
    }

    public SSdbPool(final Config poolConfig, final String hostPortTimeout, final int database) {
        super(poolConfig, new PoolableObjectFactoryManager(hostPortTimeout));
    }

    public void returnBrokenResource(final SSdbConnection resource) {
        returnBrokenResourceObject(resource);
    }

    public void returnResource(final SSdbConnection resource) {
        returnResourceObject(resource);
    }

}