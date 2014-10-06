package mt.dou;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;


/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 10/5/14
 */
public class TestTestAndSetLockBenchmark {
	private final TestTestAndSetLock spinLock = new TestTestAndSetLock();
	private final ExecutorService executorService = Executors.newCachedThreadPool();

	private final CountDownLatch latch = new CountDownLatch(1);


	@Test
	public void benchmark() throws Exception {
		final long iterations = 1 << 24;

		List<Future<Long>> futures = new ArrayList<Future<Long>>();

		for (int i = 0; i < 8; i++)
			futures.add(executorService.submit(new Countdown(iterations)));

		latch.countDown();

		long total = 0;
		for (Future<Long> future : futures)
			total += future.get();


		System.out.println("Average execution time : " + (total / (8 * iterations)) + " ns per operation.");
	}

	public final class Countdown implements Callable<Long> {
		private final long iterations;

		public Countdown(long iterations) {
			this.iterations = iterations;
		}

		@Override
		public Long call() throws Exception {
			final long start = System.nanoTime();

			long cnt = iterations;
			while (cnt > 0) {
				spinLock.lock();
				cnt--;
				spinLock.unlock();
			}

			final long end = System.nanoTime();
			return end - start;
		}
	}
}