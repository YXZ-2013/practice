package com.yin.myproject.practice.common.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class QueryParams implements Iterable<QueryParam> {

	private List<QueryParam> queryChain;

	private QueryParams(List<QueryParam> aQueryChain) {
		queryChain = aQueryChain;
	}

	public Iterator<QueryParam> iterator() {
		return queryChain.iterator();
	}

	public List<QueryParam> getQueryChain() {
		return queryChain;
	}

	public static class Builder {
		List<QueryParam> queryChain = new LinkedList<QueryParam>();

		public Builder all(String key) {
			queryChain.add(new QueryParam(key, Operator.ALL));
			return this;
		}

		public Builder gt(String key) {
			queryChain.add(new QueryParam(key, Operator.GT));
			return this;
		}

		public Builder gte(String key) {
			queryChain.add(new QueryParam(key, Operator.GTE));
			return this;
		}

		public Builder in(String key) {
			queryChain.add(new QueryParam(key, Operator.IN));
			return this;
		}

		public Builder lt(String key) {
			queryChain.add(new QueryParam(key, Operator.LT));
			return this;
		}

		public Builder lte(String key) {
			queryChain.add(new QueryParam(key, Operator.LTE));
			return this;
		}

		public Builder ne(String key) {
			queryChain.add(new QueryParam(key, Operator.NE));
			return this;
		}

		public Builder nin(String key) {
			queryChain.add(new QueryParam(key, Operator.NIN));
			return this;
		}

		public Builder btw(String key) {
			queryChain.add(new QueryParam(key, Operator.BTW));
			return this;
		}

		public Builder like(String key) {
			queryChain.add(new QueryParam(key, Operator.LIKE));
			return this;
		}

		public Builder skip(int key) {
			queryChain.add(new QueryParam(String.valueOf(key), Operator.SKIP));
			return this;
		}

		public Builder limit(int key) {
			queryChain.add(new QueryParam(String.valueOf(key), Operator.LIMIT));
			return this;
		}

		public Builder equal(String key) {
			queryChain.add(new QueryParam(String.valueOf(key), Operator.EQ));
			return this;
		}

		public Builder isnull(String key) {
			queryChain.add(new QueryParam(String.valueOf(key), Operator.ISNULL));
			return this;
		}

		public Builder sortAscending(String key) {
			queryChain.add(new QueryParam(String.valueOf(key), Operator.SORT_ASCENDING));
			return this;
		}

		public Builder sortDescending(String key) {
			queryChain.add(new QueryParam(String.valueOf(key), Operator.SORT_DESCENDING));
			return this;
		}

		public Builder or(QueryParams queryParams) {

			List<QueryParam> chain = queryParams.getQueryChain();

			if (chain.size() < 2) {
				throw new IllegalArgumentException("operator <or> needs at least two params in query params chain");
			}

			queryChain.add(new QueryParam(Operator.OR, chain));
			return this;
		}

		public Builder and(QueryParams queryParams) {

			List<QueryParam> chain = queryParams.getQueryChain();

			if (chain.size() < 2) {
				throw new IllegalArgumentException("operator <and> needs at least two params in query params chain");
			}

			queryChain.add(new QueryParam(Operator.AND, chain));
			return this;
		}

		public void remove(String key) {

			for (QueryParam qp : queryChain) {
				if (qp.getKey() != null && qp.getKey().equals(key)) {
					queryChain.remove(qp);
				}
			}
		}

		public QueryParams build() {
			return new QueryParams(queryChain);
		}

		@Override
		public String toString() {
			return "Builder{" + "queryChain=" + queryChain + '}';
		}
	}
}
