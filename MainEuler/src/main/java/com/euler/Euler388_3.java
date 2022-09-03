package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.euler.common.MoebiusCalculator;
import com.euler.common.Primes;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.math.LongMath;

public class Euler388_3 extends InterruptableProgram<Euler388_3.Euler388State>	{
	private final static long LIMIT=LongMath.pow(10l,10);

	public static class Euler388State implements ProgramState	{
		public long currentIndex;
		public BigInteger currentSum;
		
		public Euler388State()	{
			currentIndex=0;
			currentSum=BigInteger.ZERO;
		}
		
		public Euler388State(long currentIndex,BigInteger currentSum)	{
			this.currentIndex=currentIndex;
			this.currentSum=currentSum;
		}
		
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			System.out.println("Saving to file: ("+currentIndex+","+currentSum+").");
			writer.write(Long.toString(currentIndex));
			writer.newLine();
			writer.write(currentSum.toString());
		}

		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			// Throws NumberFormatException
			currentIndex=Long.parseLong(reader.readLine());
			currentSum=new BigInteger(reader.readLine());
			System.out.println("Read from file: ("+currentIndex+","+currentSum+").");
		}
		
	}
	
	private static BigInteger getCube(BigInteger in)	{
		BigInteger square=in.multiply(in);
		return square.multiply(in);
	}
	
	public Euler388_3() {
		super(new InputThreadStopper());
		i=0;
		result=BigInteger.ZERO;
	}
	
	// Instance members, must be accessible from other methods so they aren't declared in main.
	private long i;
	private BigInteger result;
	
	@Override
	public void main(Euler388State state) {
		List<Long> primes=Primes.listLongPrimes(100+LongMath.sqrt(LIMIT,RoundingMode.DOWN));
		try	{
			result=state.currentSum;
			for (i=1+state.currentIndex;i<=LIMIT;++i)	{
				if ((i%10000000)==0) System.out.println(i);
				int moebius=MoebiusCalculator.getMoebiusFunction(i,primes);
				if (moebius==0) continue;
				BigInteger multiples=BigInteger.valueOf(1+(LIMIT/i));
				BigInteger addend=getCube( multiples).subtract(BigInteger.ONE);
				result=(moebius==1)?result.add(addend):result.subtract(addend);
				if (checkAndStop()) return;
			}
			System.out.println(result);
		}	catch (IOException exc)	{
			System.out.println("Error de I/O: "+exc);
		}
	}

	@Override
	public Path getFileName() {
		return Paths.get("C:\\388tmp.txt");
	}

	@Override
	public Euler388State getInitialState() {
		return new Euler388State();
	}

	@Override
	public Euler388State getCurrentState() {
		return new Euler388State(i,result);
	}

	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler388_3().start(Euler388State.class);
	}
}
