package com.yin.myproject.practice.util.ssdb.pool;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class SSdbFactory extends BasePoolableObjectFactory<Object> {
	private final String host;
	private final int port;
	private final int timeout;

	public SSdbFactory(final String host, final int port, final int timeout) {
		super();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	@Override
	public Object makeObject() throws Exception {
		final SSdbConnection connection = new SSdbConnection(this.host, this.port, this.timeout);
        return connection;
	}
	
	public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof SSdbConnection) {
            final SSdbConnection connection = (SSdbConnection) obj;
            if (connection.isOpen()) {
                connection.close();
            }
        }
    }
	
	public boolean validateObject(final Object obj) {
        if (obj instanceof SSdbConnection) {
            final SSdbConnection connection = (SSdbConnection) obj;
            try {
                return connection.isOpen();
            } catch (final Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

}
