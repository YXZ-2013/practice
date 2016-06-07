package com.yin.myproject.practice.common.query;

import java.util.List;

public class QueryParam {
	private String key;
	private Operator operator;
	
	private List<QueryParam> params;

	public QueryParam(String key, Operator oprator) {
		super();
		this.key = key;
		this.operator = oprator;
	}

	public QueryParam(Operator operator, List<QueryParam> params) {
		super();
		this.operator = operator;
		this.params = params;
	}

	public String getKey() {
		return key;
	}

	public Operator getOperator() {
		return operator;
	}

	public List<QueryParam> getParams() {
		return params;
	}
	
	@Override
	public String toString() {
		return "QueryParam{" + "key='" + key + '\'' + ", operator=" + operator + ", params=" + params + '}';
	}
	
}
