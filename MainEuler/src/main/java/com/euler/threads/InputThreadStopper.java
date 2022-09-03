package com.euler.threads;

import java.util.Scanner;

public class InputThreadStopper extends ThreadStopper {
	private boolean mustStop;
	
	public InputThreadStopper()	{
		mustStop=false;
	}
	@Override
	public void run() {
		// Stops upon line read, i.e., enter key.
		try (Scanner scanner=new Scanner(System.in))	{
			@SuppressWarnings("unused")
			String dummy=scanner.nextLine();
			synchronized(this)	{
				mustStop=true;
			}
		}
	}
	@Override
	public boolean mustStop() {
		synchronized(this)	{
			return mustStop;
		}
	}
}
