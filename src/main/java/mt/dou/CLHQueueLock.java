package mt.dou;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 10/5/14
 */
public class CLHQueueLock {
	private final AtomicReference<Qnode> tail = new AtomicReference<Qnode>();

	private final ThreadLocal<Qnode> myNode = new ThreadLocal<Qnode>() {
		@Override
		protected Qnode initialValue() {
			return new Qnode();
		}
	};

	private final ThreadLocal<Qnode> myPred = new ThreadLocal<Qnode>();

	public CLHQueueLock() {
		final Qnode qnode = new Qnode();
		qnode.locked = false;

		tail.set(qnode);
	}

	public void lock() {
		final Qnode localNode = myNode.get();
		localNode.locked = true;

		final Qnode pred = tail.getAndSet(localNode);
		myPred.set(pred);

		while (pred.locked);
	}

	public void unlock() {
		myNode.get().locked = false;

		myNode.set(myPred.get());
	}

	static final class Qnode {
		volatile boolean locked = true;
	}
}