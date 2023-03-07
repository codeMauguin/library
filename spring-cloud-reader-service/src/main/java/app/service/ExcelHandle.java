package app.service;

import app.dao.AdminBookDao;
import app.pojo.Book;
import app.pojo.Inventory;
import app.utils.SnowFlakeIdWorker;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ExcelHandle implements ReadListener<Book> {
	private final int MAX_COUNT = 100;
	private final SnowFlakeIdWorker snowFlakeIdWorker;
	private final Boolean errorStop;
	private final SqlSession sqlSession;
	private final AtomicInteger fail;
	private final AdminBookDao adminBookDao;
	private final CountDownLatch countDownLatch;
	private List<Book> data = new ArrayList<>(MAX_COUNT);
	
	public ExcelHandle(SnowFlakeIdWorker snowFlakeIdWorker, Boolean errorStop,
					   SqlSession sqlSession, AtomicInteger fail, AdminBookDao adminBookDao,
					   CountDownLatch countDownLatch) {
		this.snowFlakeIdWorker = snowFlakeIdWorker;
		this.errorStop = errorStop;
		this.sqlSession = sqlSession;
		this.fail = fail;
		this.adminBookDao = adminBookDao;
		this.countDownLatch = countDownLatch;
	}
	
	/**
	 * When analysis one row trigger invoke function.
	 *
	 * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
	 * @param context analysis context
	 */
	@Override
	public void invoke(Book data, AnalysisContext context) {
		this.data.add(data);
		if (this.data.size() >= MAX_COUNT) {
			ArrayList<Book> books = new ArrayList<>(this.data);
			CompletableFuture.runAsync(() -> commit(new ArrayList<>(books)));
			this.data = new ArrayList<>();
		}
	}
	
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		try {
			if (errorStop && fail.get() > 0) {
				sqlSession.rollback(true);
				return;
			}
			if (!data.isEmpty()) {
				commit(this.data);
			}
			sqlSession.commit();
		} finally {
			sqlSession.close();
			countDownLatch.countDown();
		}
	}
	
	private void commit(List<Book> data) {
		if (errorStop && fail.get() > 0) return;// 已经存在失败且不允许有失败存在
		var size = data.size();
		data = data.stream().filter(this::valid).peek(book -> {
			long bid = snowFlakeIdWorker.nextId();
			book.setId(bid);
			Inventory inventory = new Inventory();
			inventory.setBookId(bid);
			inventory.setTotal(book.getState());
			book.setInventory(inventory);
		}).toList();
		if (errorStop && size != data.size()) {
			fail.set(size - data.size());
			return;
		}
		try {
			for (Book datum : data) {
				adminBookDao
						.insert(datum);
				adminBookDao.initStock(datum.getInventory());
			}
		} catch (Exception e) {
			if (this.errorStop) {
				sqlSession.rollback(true);
			}
			fail.addAndGet(data.size());
		} finally {
			List<BatchResult> batchResults = sqlSession.flushStatements();
			A:
			{
				if (batchResults.isEmpty()) break A;
				BatchResult batchResult = batchResults.get(batchResults.size() - 1);
				int[] updateCounts = batchResult.getUpdateCounts();
				long count = IntStream.of(updateCounts).filter(v -> (v != -2 && v <= 0)).count();
				if (count > 0 && errorStop) {
					sqlSession.rollback(true);
				}
			}
		}
	}
	
	private boolean valid(Book book) {
		return StringUtils.hasText(book.getName()) && StringUtils.hasText(book.getAuthor()) && StringUtils.hasText(book.getPress())
					   && StringUtils.hasText(book.getCategory()) && StringUtils.hasText(book.getInfo());
	}
}
