package com.yin.myproject.practice.util.result;

import java.util.Collections;
import java.util.List;

import com.yin.myproject.practice.util.status.StatusCodes;

public class PaginatedResult<T> extends Result {

	private static final long serialVersionUID = 1L;

	protected final List<T> items;
	private final int pageSize;
	private final int pageNumber;
	private final int pagesCount;
	private final int totalItemsCount;

	public PaginatedResult(int pageNumber, int pageSize) {
		super(StatusCodes.NOT_FOUND, false);
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		items = Collections.emptyList();
		pagesCount = 0;
		totalItemsCount = 0;
	}

	public PaginatedResult(List<T> items, int pageSize, int pageNumber, int totalItemsCount) {
		super(StatusCodes.OK, true);
		this.items = items;
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.pagesCount = countPages(pageSize, totalItemsCount);
		this.totalItemsCount = totalItemsCount;
	}

	private int countPages(int size, int itemsCount) {
		return (int) Math.ceil((double) itemsCount / size);
	}

	public List<T> getItems() {
		return items;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPagesCount() {
		return pagesCount;
	}

	public int getTotalItemsCount() {
		return totalItemsCount;
	}
}
