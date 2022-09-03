package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.euler.common.Primes;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.math.LongMath;

// Esto es ridículamente lento incluso para lo que me esperaba.
public class Euler642 extends InterruptableProgram<Euler642.Euler642State> {
	private final static long LIMIT=201820182018l;
	private final static long LAST_PRIME_LIMIT=5*LongMath.pow(10l,8);
	
	private static class HighestPrimeFinder	{
		private final long[] lastPrimes;
		public HighestPrimeFinder(long maxCache)	{
			lastPrimes=Primes.trueLastPrimeSieve(maxCache);
		}
		public long getHighestPrime(long in)	{
			if (in<lastPrimes.length) return lastPrimes[(int)in];
			while ((in%2)==0)	{
				in/=2;
				if (in<lastPrimes.length) return lastPrimes[(int)in];
			}
			while ((in%3)==0)	{
				in/=3;
				if (in<lastPrimes.length) return Math.max(3,lastPrimes[(int)in]);
			}
			boolean add4=false;
			for (int i=5;;i+=add4?4:2,add4=!add4)	{
				if ((i*i)>in) return in;
				while ((in%i)==0)	{
					in/=i;
					if (in<lastPrimes.length) return Math.max(i,lastPrimes[(int)in]);
				}
			}
		}
	}
	
	public static class Euler642State implements ProgramState	{
		private long currentSum;
		private long currentNumber;
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			System.out.println("Saving to file: ("+currentSum+","+currentNumber+").");
			writer.write(Long.toString(currentSum));
			writer.newLine();
			writer.write(Long.toString(currentNumber));
		}

		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			// Throws NumberFormatException
			currentSum=Long.parseLong(reader.readLine());
			currentNumber=Long.parseLong(reader.readLine());
			System.out.println("Read from file: ("+currentSum+","+currentNumber+").");
		}
	}

	private Euler642State state;

	public Euler642() {
		super(new InputThreadStopper());
		state=getInitialState();
	}
	
	@Override
	public void main(Euler642State state) {
		try	{
			this.state=state;
			HighestPrimeFinder primeFinder=new HighestPrimeFinder(LAST_PRIME_LIMIT);
			for (;state.currentNumber<=LIMIT;++state.currentNumber)	{
				if ((state.currentNumber%10000000)==0) System.out.println(state.currentNumber);
				state.currentSum+=primeFinder.getHighestPrime(state.currentNumber);
				if (checkAndStop()) return;
			}
			System.out.println(state.currentSum);
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}

	@Override
	public Path getFileName() {
		return Paths.get("C:\\642tmp.txt");
	}

	@Override
	public Euler642State getInitialState() {
		Euler642State state=new Euler642State();
		state.currentSum=0l;
		state.currentNumber=2l;
		return state;
	}

	@Override
	public Euler642State getCurrentState() {
		return state;
	}

	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler642().start(Euler642State.class);
	}
}
