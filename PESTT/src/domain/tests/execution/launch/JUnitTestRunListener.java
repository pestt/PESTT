package domain.tests.execution.launch;

import java.util.Observable;

import org.eclipse.jdt.junit.ITestRunListener;

import domain.events.EndTestsExecutionEvent;
import domain.events.TestStartEvent;

@SuppressWarnings("deprecation")
public class JUnitTestRunListener extends Observable implements
		ITestRunListener {

	@Override
	public void testStarted(String arg0, String arg1) {
		setChanged();
		notifyObservers(new TestStartEvent(arg1));
	}

	@Override
	public void testRunTerminated() {
		// does nothing.
	}

	@Override
	public void testRunStopped(long arg0) {
		// does nothing.
	}

	@Override
	public void testRunStarted(int arg0) {
		// does nothing.
	}

	@Override
	public void testRunEnded(long arg0) {
		setChanged();
		notifyObservers(new EndTestsExecutionEvent());
	}

	@Override
	public void testReran(String arg0, String arg1, String arg2, int arg3,
			String arg4) {
		// does nothing.
	}

	@Override
	public void testFailed(int arg0, String arg1, String arg2, String arg3) {
		// does nothing.
	}

	@Override
	public void testEnded(String arg0, String arg1) {
		// does nothing.
	}
}
