package com.yin.myproject.practice.common.storage.mysql.persisted;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jooq.DSLContext;
import org.jooq.DeleteWhereStep;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.Table;
import org.jooq.UpdateWhereStep;

public abstract class CRUDProvider {
	
	@Inject
	protected DSLContext context;
	
	protected InsertQuery<Record> _c(Table<Record> table,Map<Field<Object>, Object> mapRecord){
		InsertQuery<Record> insertQuery = context.insertQuery(table);
		for(Field<Object> field : mapRecord.keySet()){
			insertQuery.addValue(field, mapRecord.get(field));
		}
		return insertQuery;
	}
	
	protected UpdateWhereStep<Record> _u(Table<Record> table,Map<Field<Object>, Object> mapRecord){
		return context.update(table).set(mapRecord);
	}
	
	protected DeleteWhereStep<Record> _d(Table<Record> table){
		return context.delete(table);
	}
	
	protected SelectWhereStep<Record> _r(Table<Record> table,List<Field<Object>> viewer){
		SelectWhereStep<Record> whereStep;
		if(viewer != null){
			whereStep = context.select(viewer).from(table);
		}else{
			whereStep = context.selectFrom(table);
		}
		return whereStep;
	}
	
	public DSLContext getDSLContext() {
		return context;
	}
	
}
