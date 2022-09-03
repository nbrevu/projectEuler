package com.euler.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class InterruptableProgram<T extends ProgramState> {
	protected final ThreadStopper stopper;
	
	public InterruptableProgram(ThreadStopper stopper)	{
		this.stopper=stopper;
		stopper.start();
	}
	
	public abstract void main(T state);
	
	public abstract Path getFileName();
	
	public abstract T getInitialState();
	
	public abstract T getCurrentState();
	
	public void start(Class<T> classT) throws IOException,ReflectiveOperationException	{
		Path fileName=getFileName();
		T state=Files.exists(fileName)?getStateFromFile(fileName,classT):getInitialState();
		main(state);
	}
	
	protected void stop() throws IOException	{
		try	{
			T state=getCurrentState();
			try (BufferedWriter bw=Files.newBufferedWriter(getFileName()))	{
				state.saveToFile(bw);
			}
		}	finally	{
			Thread.currentThread().interrupt();
		}
	}
	
	public boolean checkAndStop() throws IOException	{
		if (stopper.mustStop())	{
			stop();
			return true;
		}
		return false;
	}
	
	private T getStateFromFile(Path fileName,Class<T> classT) throws IOException,ReflectiveOperationException	{
		try (BufferedReader br=Files.newBufferedReader(fileName))	{
			T state=instantiateT(classT);
			state.readStateFromFile(br);
			return state;
		}
	}
	
	private T instantiateT(Class<T> classT) throws ReflectiveOperationException	{
		Constructor<T> constructor=classT.getConstructor();
		return constructor.newInstance();
	}
}
