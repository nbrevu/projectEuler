package com.euler.threads;

public abstract class ThreadStopper implements Runnable {
	public abstract boolean mustStop();
	
	public void start()	{
		new Thread(this).start();
	}
}
