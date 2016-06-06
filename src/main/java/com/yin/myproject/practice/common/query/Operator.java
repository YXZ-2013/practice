package com.yin.myproject.practice.common.query;

public enum Operator {
	ALL("$all"),

	GT("$gt"),

	GTE("$gte"),

	IN("$in"),

	LT("$lt"),

	LTE("$lte"),

	NE("$ne"),

	NIN("$nin"),

	EQ("$eq"),

	BTW("between"),

	ISNULL("is null"),

	LIKE("like"),

	SKIP("skip"),

	LIMIT("limit"),

	SORT("sort"),

	SORT_ASCENDING("sortA"),

	SORT_DESCENDING("sortD"),

	ELEM_MATCH("$elemMatch"),

	OR("$or"),

	AND("$and");

	private String field;

	private Operator(String aField) {
		field = aField;
	}

	public String getValue() {
		return field;
	}
}
