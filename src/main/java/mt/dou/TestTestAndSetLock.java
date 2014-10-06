package mt.dou;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 10/5/14
 */
public class TestTestAndSetLock {
	AtomicBoolean state = new AtomicBoolean(false);
	public void lock() {
		while (true) {
			while (state.get()) {};
			if (!state.getAndSet(true))
				return;
		}
	}
	public void unlock() {
		state.set(false);
	}
}