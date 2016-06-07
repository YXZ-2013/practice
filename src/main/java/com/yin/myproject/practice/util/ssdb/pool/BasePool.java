package com.yin.myproject.practice.util.ssdb.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

public class BasePool<T> {

	private GenericObjectPool internalPool;

    private final PoolableObjectFactoryManager factorysManager;
    private Config config = null;

	public BasePool(final Config poolConfig, final PoolableObjectFactoryManager fm) {

        this.factorysManager = fm;

        this.config = poolConfig;

        this.internalPool = new GenericObjectPool(factorysManager.getNext(), config);

        // 守护进程
        startProtected();
    }

    private void startProtected() {

        try {
            // 启动守护进程，监控连接是否失效，如果当前服务器失效，则连接下一台；采用ScheduledExecutorService，避免系统时钟敏感性问题
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleWithFixedDelay(new Runnable() {
                @SuppressWarnings("unchecked")
				public void run() {
                    if (internalPool != null) {
                        if (!isConnected()) {
                            try {
                                // 先关闭已失效的连接池
                                internalPool.clear();
                                internalPool.close();
                                internalPool = new GenericObjectPool(factorysManager.getNext(), config);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // internalPool.setFactory(factorysManager.getNext());
                            System.out.println("connection refused!");
                        }
                    }
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {

        }
    }

    @SuppressWarnings("unchecked")
    public T getConnection() {
        try {
            T temp = (T) internalPool.borrowObject();
            return temp;
        } catch (Exception e) {
            return null;
        }
    }

    public void returnResourceObject(final Object resource) {

        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            // throw new JedisException( "Could not return the resource to the pool", e);
        }
    }

    public void returnBrokenResource(final T resource) {
        returnBrokenResourceObject(resource);
    }

    public void returnResource(final T resource) {
        returnResourceObject(resource);
    }

    protected void returnBrokenResourceObject(final Object resource) {
        try {
            // 失效
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            // throw new JedisException("Could not return the resource to the pool", e);
        }

    }

    public void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            // throw new JedisException("Could not destroy the pool", e);
        }
    }

    public boolean isConnected() {
        T connection = null;
        try {
            connection = getConnection();
            boolean isc = ((SSdbConnection) connection).testConnected();
            if (isc) {
                returnResource(connection);
            } else {
                returnBrokenResourceObject(connection);
            }
            return isc;
        } catch (Exception e) {
            return false;
        }
    }
}
