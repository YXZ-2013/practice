package com.yin.myproject.practice.common.storage.mysql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yin.myproject.practice.common.query.Operator;
import com.yin.myproject.practice.common.query.QueryParam;
import com.yin.myproject.practice.common.query.QueryParams;
import com.yin.myproject.practice.common.storage.StorageProvider;
import com.yin.myproject.practice.common.storage.mysql.persisted.CRUDProvider;
import com.yin.myproject.practice.common.util.DSLUtility;
import com.yin.myproject.practice.model.Entity;
import com.yin.myproject.practice.util.mapper.Mapper;
import com.yin.myproject.practice.util.result.PaginatedResult;
import com.yin.myproject.practice.util.result.StorageResult;
import com.yin.myproject.practice.util.status.StatusCodes;

public class JooqMysqlProvider<E extends Entity> extends CRUDProvider
		implements StorageProvider<E>, UnitOfWorkProvider<E> {
	private static final Logger logger = LoggerFactory.getLogger(JooqMysqlProvider.class);

	private Class<E> entityClass;

	protected String providerTableName;

	private final Table<Record> getProviderTable() {
		return DSL.table(providerTableName);
	}

	public JooqMysqlProvider() {
	}

	public void persistCreate(E entity) throws UnitOfWorkException {
		try {
			this.createRecord(entity);
		} catch (Exception e) {
			throw new UnitOfWorkException("Unit of Work create Error.", e);
		}
	}

	public void persistDelete(E entity) throws UnitOfWorkException {
		try {
			Condition c = DSL.field("id").eq(entity.getId());
			this.deleteRecord(c);
		} catch (Exception ex) {
			throw new UnitOfWorkException("Unit of Work Delete Error.", ex);
		}
	}

	public void persistUpdate(E entity, QueryParams queryParams, Map<String, Object> queryMap)
			throws UnitOfWorkException {
		try {
			Condition c;
			if (queryParams != null) {
				c = this.addCondition(queryParams, queryMap);
			} else {
				c = DSL.field("id").eq(entity.getId()); // 根据Id更新
			}
			this.updateRecord(entity, c);
		} catch (Exception ex) {
			throw new UnitOfWorkException("Unit of Work Update Error.", ex);
		}
	}

	public StorageResult<E> create(E entity) {
		logger.info("create table {} entry by  {}", getProviderTable(), entity);
		try {
			int affectedRows = this.createRecord(entity);

			logger.info("create table {} entity return {}", getProviderTable(), affectedRows);

			if (affectedRows != -3) {
				if (affectedRows != 1) {
					return new StorageResult<E>(StatusCodes.COMMAND_FAILED, false);
				}
				return new StorageResult<E>(StatusCodes.CREATED, true);
			} else {
				return new StorageResult<E>(StatusCodes.INTERNAL_SERVER_ERROR, false);
			}
		} catch (Exception e) {
			logger.error("CREATE {} ERROR { }", entity, e);
		}
		return new StorageResult<E>(StatusCodes.INTERNAL_SERVER_ERROR, false);
	}

	public StorageResult<E> update(E entity, QueryParams queryParams, Map<String, Object> queryMap) {
		logger.info("Update table {} entry by  {}", getProviderTable(), entity);
		try {
			int affectedRows = -1;

			Condition c = null;
			if (queryParams != null) {
				c = addCondition(queryParams, queryMap);
			} else {
				c = DSL.field("id").eq(entity.getId()); // 根据Id更新
			}
			affectedRows = this.updateRecord(entity, c);

			if (affectedRows == -1) {
				return new StorageResult<E>(StatusCodes.COMMAND_FAILED, false);
			}
			StorageResult<E> updRes = new StorageResult<E>(StatusCodes.NO_CONTENT, true);
			// updRes.setCount((long) affectedRows);
			return updRes;
		} catch (Exception e) {
			logger.error("PUT {} ERROR { }", entity, e);
		}
		return new StorageResult<E>(StatusCodes.INTERNAL_SERVER_ERROR, false);
	}

	public StorageResult<E> delete(Object... ids) {
		logger.info("Delete table {} entry by  {}", getProviderTable(), ids);
		try {
			if (ids == null) {
				new StorageResult<E>(StatusCodes.NO_CONTENT, true);
			}
			Condition c = DSL.field("id").in(ids);
			int affectedRows = -1;

			affectedRows = this.deleteRecord(c);

			if (affectedRows == -1) {
				return new StorageResult<E>(StatusCodes.COMMAND_FAILED, false);
			}
			StorageResult<E> updRes = new StorageResult<E>(StatusCodes.NO_CONTENT, true);
			return updRes;
		} catch (Exception e) {
			logger.error("Delte {} ERROR { }", ids, e);
		}
		return new StorageResult<E>(StatusCodes.INTERNAL_SERVER_ERROR, false);
	}

	public StorageResult<E> get(String id) {
		logger.info("GET TABLE {} BY ID {}", getProviderTable(), id);
		try {
			Record record = this.getRecord(id);
			if (record == null) {
				StorageResult<E> res = new StorageResult<E>(StatusCodes.NO_CONTENT, false);
				return res;
			}

			E entity = (E) Mapper.map2Object(converMap(record), entityClass);
			StorageResult<E> res = new StorageResult<E>(StatusCodes.OK, true);
			if (entity == null) {
				res.setStatus(StatusCodes.NOT_FOUND);
				return res;
			}
			res.setEntity(entity);

			return res;
		} catch (Exception e) {
			logger.error("GET TABLE {} BY ID {} ERROR {}", getProviderTable(), id, e);
		}

		return new StorageResult<E>(StatusCodes.INTERNAL_SERVER_ERROR, false);
	}

	public StorageResult<E> getAll(List<String> viewer, QueryParams queryParams, Map<String, Object> queryMap) {
		logger.info("GET ALL TABLE {} QUERY {}", getProviderTable(), queryMap);
		try {
			Condition c = this.addCondition(queryParams, queryMap);

			List<Field<Object>> fieldList = new LinkedList<Field<Object>>();
			if (viewer != null) {
				for (String key : viewer) {
					fieldList.add(DSL.field(key));
				}
			}

			SortField<?>[] sorts = this.addSortFields(queryParams);

			Result<Record> results = this.getRecords(fieldList, c, sorts);
			StorageResult<E> res = new StorageResult<E>(StatusCodes.OK, true);
			if (results != null) {
				logger.info("GET ALL TABLE RECORDS is {}:", res);
				// --- 转换Result<record>
				List<Map<String, Object>> list = DSLUtility.convertList(results);
				List<E> items = new ArrayList<E>();
				for (Map<String, Object> record : list) {
					try {
						items.add(Mapper.map2Object(record, entityClass));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				res.setEntities(items);
			}
			return res;
		} catch (Exception e) {
			logger.error("GET ALL TABLE {} BY  {} ERROR {}", getProviderTable(), queryMap, e);
		}

		return new StorageResult<E>(StatusCodes.INTERNAL_SERVER_ERROR, false);
	}

	public PaginatedResult<E> flipPage(List<String> viewer, QueryParams queryParams, Map<String, Object> queryMap,
			int pageNum, int pageSize) {
		logger.info(" Flippage  TABLE {} QUERY {}", getProviderTable(), queryMap);
		try {
			int page = (pageNum > 1 ? pageNum : 1);
			int form = (page - 1) * pageSize;
			// int to = page * pageSize;

			Condition c = this.addCondition(queryParams, queryMap);
			int count = this.getCount(c);

			SortField<?>[] sorts = this.addSortFields(queryParams);

			List<Field<Object>> fieldList = new LinkedList<Field<Object>>();
			if (viewer != null) {
				for (String key : viewer) {
					fieldList.add(DSL.field(key));
				}
			}

			Result<Record> results = this.getRecordsByPage(fieldList, c, form, pageSize, sorts);
			if (results == null) {
				logger.warn("Flippage TABLE RECORDS is {}:", results);
				return new PaginatedResult<E>(null, pageNum, pageSize, 0);
			}
			List<Map<String, Object>> list = DSLUtility.convertList(results);
			List<E> items = new ArrayList<E>();
			for (Map<String, Object> record : list) {
				try {
					items.add(Mapper.map2Object(record, entityClass));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			PaginatedResult<E> res = new PaginatedResult<E>(items, pageNum, pageSize, count);

			return res;
		} catch (Exception e) {
			logger.error("GET ALL TABLE {} BY  {} ERROR {}", getProviderTable(), queryMap, e);
		}
		return new PaginatedResult<E>(null, pageNum, pageSize, 0);
	}

	protected int createRecord(E entity) {
		int affectedRows = -1;
		affectedRows = this._c(getProviderTable(), DSLUtility.autoMatchDSL(entity)).execute();
		return affectedRows;
	}

	protected int updateRecord(E entity, Condition c) {
		String id = (String) entity.getId();

		Record originalRecord = getRecord(id);

		Object freshMap = this.merageRecord(originalRecord, entity);

		return this._u(getProviderTable(), DSLUtility.autoMatchDSL(freshMap)).where(c).execute();
	}

	protected int deleteRecord(Condition c) {
		return this._d(getProviderTable()).where(c).execute();
	}

	protected Record getRecord(String id) {
		return _r(getProviderTable(), null).where((DSL.field("id").equal(id))).fetchOne();
	}

	protected int getCount(Condition c) {
		return context.selectCount().from(getProviderTable()).where(c).fetchOne().value1();
	}

	protected Result<Record> getRecordsByPage(List<Field<Object>> viewer, Condition c, int form, int to,
			SortField<?>... sorts) {
		if (sorts != null && sorts.length != 0) {
			return _r(getProviderTable(), viewer).where(c).orderBy(sorts).limit(form, to).fetch();
		} else {
			return _r(getProviderTable(), viewer).where(c).limit(form, to).fetch();
		}
	}

	protected Result<Record> getRecords(List<Field<Object>> viewer, Condition c, SortField<?>... sorts) {
		if (sorts != null && sorts.length != 0) {
			return _r(getProviderTable(), viewer).where(c).orderBy(sorts).fetch();
		} else {
			return _r(getProviderTable(), viewer).where(c).fetch();
		}
	}

	public <T> List<T> getAllTable(String tableName, Class<T> clazz) {
		if (null == tableName || "".equals(tableName)) {
			return null;
		}
		if (clazz == null) {
			return null;
		}
		Result<Record> record = context.select().from(DSL.table(tableName)).fetch();
		List<Map<String, Object>> list = DSLUtility.convertList(record);
		List<T> returnList = new ArrayList<T>();
		if (list != null) {
			for (Map<String, Object> thisMap : list) {
				try {
					T t = Mapper.map2Object(thisMap, clazz);
					returnList.add(t);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			}
		}
		return returnList;
	}

	private Map<String, Object> merageRecord(Record originalRecord, E freshEntity) {

		try {
			Map<String, Object> originalMap = this.converMap(originalRecord);
			Map<String, Object> freshMap = Mapper.object2Map(freshEntity);
			logger.debug("freshMap:{} ", freshMap);
			for (String oriKey : originalMap.keySet()) {
				Object oriValue = originalMap.get(oriKey);
				Object freshValue = freshMap.get(oriKey);
				if (freshValue == null && oriValue != null) {
					freshMap.put(oriKey, oriValue);
				} else if (freshValue == null && oriValue == null) {
					freshMap.remove(oriKey);
				} else if (freshValue.equals(oriValue)) {
					freshMap.remove(oriKey);
				}
			}
			logger.debug("change freshMap:{} ", freshMap);
			return freshMap;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	private SortField<?>[] addSortFields(QueryParams queryParams) {
		ArrayList<SortField<?>> sortFields = new ArrayList<SortField<?>>();
		for (QueryParam queryParam : queryParams) {
			switch (queryParam.getOperator()) {
			case SORT_ASCENDING:
				sortFields.add(org.jooq.impl.DSL.field(queryParam.getKey()).asc());
				break;
			case SORT_DESCENDING:
				sortFields.add(org.jooq.impl.DSL.field(queryParam.getKey()).desc());
				break;
			default:
				break;
			}
		}
		SortField<?>[] rs = new SortField<?>[sortFields.size()];
		return sortFields.toArray(rs);
	}

	private Condition addCondition(QueryParams queryParams, Map<String, Object> queryMap) {

		List<Condition> ccc = new LinkedList<Condition>();
		String queryOperator = null;
		for (QueryParam queryParam : queryParams) {
			queryOperator = queryParam.getOperator().toString();
			switch (queryParam.getOperator()) {
			case EQ:
			case GT:
			case GTE:
			case LT:
			case LTE:
			case NE:
			case IN:
			case LIKE:
			case NIN:
				ccc.add(getSimpleCondition(queryParam.getOperator(), DSL.field(queryParam.getKey()),
						queryMap.get(queryParam.getKey())));
				break;
			case OR:
			case AND:

				QueryParam qp = queryParam.getParams().get(0);

				Condition condition = getSimpleCondition(qp.getOperator(), DSL.field(qp.getKey()),
						queryMap.get(qp.getKey()));

				for (int i = 1; i < queryParam.getParams().size(); i++) {

					QueryParam orQueryParam = queryParam.getParams().get(i);

					Condition c = getSimpleCondition(orQueryParam.getOperator(), DSL.field(orQueryParam.getKey()),
							queryMap.get(orQueryParam.getKey()));

					switch (queryParam.getOperator()) {
					case OR:
						condition = condition.or(c);
						break;
					case AND:
						condition = condition.and(c);
						break;
					default:
						break;
					}
				}

				ccc.add(condition);
				break;
			default:
				break;
			}
		}
		Condition rc = null;
		for (Condition xc : ccc) {
			if (rc == null) {
				rc = xc;
			} else {
				if (queryOperator.equals("LIKE")) {
					rc = rc.and(xc);
				} else {
					rc = rc.or(xc);
				}
			}
		}
		return rc;
	}

	private Condition getSimpleCondition(Operator operator, Field<Object> field, Object value) {
		switch (operator) {
		case LIKE:
			return field.like(value.toString());
		case EQ:
			return field.eq(value);
		case GT:
			return field.gt(value);
		case GTE:
			return field.ge(value);
		case LT:
			return field.lt(value);
		case LTE:
			return field.le(value);
		case NE:
			return field.ne(value);
		case IN:
			if (value instanceof List) {
				return field.in(((ArrayList<?>) value).toArray());
			} else {
				return field.in(value);
			}
		case NIN:
			return field.notIn(value);
		case BTW:
			return field.between(((List<?>) value).get(0), ((List<?>) value).get(1));
		default:
			throw new IllegalArgumentException("");
		}
	}

	private Map<String, Object> converMap(Record record) {
		if (record == null)
			return null;
		int n = record.fields().length;
		Map<String, Object> reMap = new LinkedHashMap<String, Object>();
		for (int i = 0; i < n; i++) {
			String kn = record.field(i).getName().toString();
			// String kn = SystemUtility.firstLower(record.field(i).getName());
			Object kv = record.getValue(record.field(i));
			reMap.put(kn, kv);
		}
		return reMap;
	}

	public void setTableName(String tName) {
		this.providerTableName = tName;
	}

	public void setEntityClass(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

}
