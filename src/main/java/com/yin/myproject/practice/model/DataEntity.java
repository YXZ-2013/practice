package com.yin.myproject.practice.model;

import java.util.Date;

public class DataEntity extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -441467365349959692L;

	@MapperKey(value = "LogicalDel", type = Integer.class)
	protected Integer logicalDel = 0; // 1 deleteflag

	@MapperKey(value = "CreateTime")
	protected Date createTime;

	@MapperKey(value = "UpdateTime")
	protected Date updateTime;

	@MapperKey(value = "Version", type = Long.class)
	protected Long version;

	public DataEntity() {
		init();
	}
	
	protected void init() {
        long now = currentTimeMillis();
        this.createTime = new Date(now);
        this.updateTime = new Date(now);
        this.version = now;
    }

	public Integer getLogicalDel() {
		return logicalDel;
	}

	public void setLogicalDel(Integer logicalDel) {
		this.logicalDel = logicalDel;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
