package mt.dou;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 10/5/14
 */
public class TestReadLockCountdown {
	private final ReadWriteLock spinLock = new ReentrantReadWriteLock();
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final CountDownLatch latch = new CountDownLatch(1);

	@Test
	public void benchmark() throws Exception {
		List<Future<Long>> futures = new ArrayList<Future<Long>>();

		for (int i = 0; i < 8; i++)
			futures.add(executorService.submit(new CountDown(2000000)));

		long star = System.currentTimeMillis();
		latch.countDown();


		long total = 0;

		for (Future<Long> future : futures)
			total += future.get();

		long end = System.currentTimeMillis();

		System.out.println("Count down for : " + total + " ns.");
		System.out.println("Execution time is : " + (end - star) + " ms.");
	}

	public final class CountDown implements Callable<Long> {

		private final long counter;

		public CountDown(long counter) {
			this.counter = counter;
		}

		@Override
		public Long call() throws Exception {
			latch.await();

			long cnt = counter;
			final long start = System.nanoTime();
			while (cnt > 0) {
				spinLock.readLock().lock();
				cnt--;
				spinLock.readLock().unlock();
			}
			final long end = System.nanoTime();

			return (end - start);
		}
	}
}