package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.Primes;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;


public class Euler245_4 extends InterruptableProgram<Euler245_4.Euler245State> {
	private final static long LIMIT=2*LongMath.pow(10l,11);
	
	@SuppressWarnings("rawtypes")
	private static class OddPrimeFactorer	{
		private final List<Long> primes;
		private int maxInCache;
		private final Set[] factorsCache;
		private static int getIndex(long n)	{
			// The first is for 3, the second one for 5, etc.
			return (int)((n-3)/2);
		}
		public OddPrimeFactorer(long n)	{
			long sn=LongMath.sqrt(n,RoundingMode.DOWN);
			primes=Primes.listLongPrimes(sn);
			primes.remove(0);	// We don't need the 2 here.
			maxInCache=(int)sn-1;
			factorsCache=new Set[1+getIndex(maxInCache)];
			factorsCache[0]=Collections.emptySet();
			maxInCache=1;
			for (int i=3;i<sn;i+=2)	{
				factorsCache[getIndex(i)]=calculateFactors(i);
				maxInCache=i;
			}
		}
		@SuppressWarnings("unchecked")
		public Set<Long> getFactorsOrNull(long in)	{
			return (in>maxInCache)?calculateFactors(in):factorsCache[getIndex(in)];
		}
		@SuppressWarnings("unchecked")
		private Set<Long> calculateFactors(long in)	{
			Set<Long> result=new HashSet<>();
			for (Long p:primes)	{
				long pp=p.longValue();
				if ((pp*pp)>in)	{
					if (result.contains(in)) return null;
					result.add(in);
					return result;
				}
				if ((in%pp)==0l)	{
					in/=pp;
					if ((in%pp)==0) return null;
					result.add(pp);
					if (in<=maxInCache)	{
						int index=getIndex(in);
						Set<Long> previous=factorsCache[index];
						if (previous==null) return null;
						else if (!Sets.intersection(result,previous).isEmpty()) return null;
						result.addAll(previous);
						return result;
					}
				}
			}
			if (in>1) result.add(in);	// Can't be repeated at this point.
			return result;
		}
	}
	
	public static class Euler245State implements ProgramState	{
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

	private Euler245State state;

	public Euler245_4() {
		super(new InputThreadStopper());
		state=getInitialState();
	}
	
	private static long getTotient(Set<Long> factors)	{
		long result=1;
		for (long i:factors) result*=i-1;
		return result;
	}
	
	private boolean verifiesCondition(long in,OddPrimeFactorer factorer)	{
		Set<Long> factors=factorer.getFactorsOrNull(in);
		if ((factors==null)||(factors.size()==1)) return false;
		long totient=getTotient(factors);
		long num=in-totient;
		long den=in-1;
		return ((den%num)==0);
	}

	@Override
	public void main(Euler245State state) {
		try	{
			this.state=state;
			OddPrimeFactorer factorer=new OddPrimeFactorer(LIMIT);
			for (;state.currentNumber<LIMIT;state.currentNumber+=2)	{
				if ((state.currentNumber%10000000)==1) System.out.println(state.currentNumber);
				if (verifiesCondition(state.currentNumber,factorer)) state.currentSum+=state.currentNumber;
				if (checkAndStop()) return;
			}
			System.out.println(state.currentSum);
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}

	@Override
	public Path getFileName() {
		return Paths.get("C:\\245tmp.txt");
	}

	@Override
	public Euler245State getInitialState() {
		Euler245State state=new Euler245State();
		state.currentSum=0l;
		state.currentNumber=15l;
		return state;
	}

	@Override
	public Euler245State getCurrentState() {
		return state;
	}

	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler245_4().start(Euler245State.class);
	}
}
