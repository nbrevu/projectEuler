package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.math.LongMath;

public class Euler342 extends InterruptableProgram<Euler342.Euler342State> {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	public static class Euler342State implements ProgramState	{
		public long currentSum;
		public long currentNumber;
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
	
	@SuppressWarnings("rawtypes")
	private static class PrimeFactorer	{
		private final List<Long> primes;
		private int maxInCache;
		private final Map[] factorsCache;
		public PrimeFactorer(long n)	{
			long sn=LongMath.sqrt(n,RoundingMode.DOWN);
			primes=Primes.listLongPrimes(sn);
			maxInCache=(int)sn-1;
			factorsCache=new Map[(int)sn];
			factorsCache[1]=Collections.emptyMap();
			maxInCache=1;
			for (int i=2;i<sn;++i)	{
				factorsCache[i]=calculateFactors(i);
				++maxInCache;
			}
		}
		@SuppressWarnings("unchecked")
		public Map<Long,Integer> getFactors(long in)	{
			return (in>maxInCache)?calculateFactors(in):factorsCache[(int)in];
		}
		public Map<Long,Integer> getTotientOfSquareFactors(Map<Long,Integer> factors)	{
			Map<Long,Integer> result=new HashMap<>();
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				EulerUtils.increaseCounter(result,entry.getKey(),2*entry.getValue()-1);
				Map<Long,Integer> newFactor=getFactors(entry.getKey()-1);
				for (Map.Entry<Long,Integer> entry2:newFactor.entrySet()) EulerUtils.increaseCounter(result,entry2.getKey(),entry2.getValue());
			}
			return result;
		}
		@SuppressWarnings("unchecked")
		private Map<Long,Integer> calculateFactors(long in)	{
			Map<Long,Integer> result=new HashMap<>();
			for (Long p:primes)	{
				long pp=p.longValue();
				if ((pp*pp)>in)	{
					EulerUtils.increaseCounter(result,in);
					return result;
				}
				if ((in%pp)==0l)	{
					in/=pp;
					result.put(p,1);
					for (;;)	{
						if (in<=maxInCache)	{
							addAll(result,factorsCache[(int)in]);
							return result;
						}
						if ((in%pp)==0)	{
							in/=pp;
							EulerUtils.increaseCounter(result,p);
						}	else break;
					}
				}
			}
			if (in>1)	{
				EulerUtils.increaseCounter(result,in);
			}
			return result;
		}
		private static void addAll(Map<Long,Integer> dest,Map<Long,Integer> addend)	{
			for (Map.Entry<Long,Integer> entry:addend.entrySet()) EulerUtils.increaseCounter(dest,entry.getKey(),entry.getValue());
		}
	}
	
	private Euler342State state;

	@Override
	public Path getFileName() {
		return Paths.get("C:\\342tmp.txt");
	}

	@Override
	public Euler342State getInitialState() {
		Euler342State result=new Euler342State();
		result.currentSum=0l;
		result.currentNumber=2l;
		return result;
	}

	@Override
	public Euler342State getCurrentState() {
		return state;
	}

	public Euler342() {
		super(new InputThreadStopper());
		state=getInitialState();
	}
	
	private boolean isCube(Map<Long,Integer> factors)	{
		for (Integer i:factors.values()) if ((i%3)!=0) return false;
		return true;
	}
	
	private boolean verifiesCondition(long number,PrimeFactorer factorer)	{
		Map<Long,Integer> factors=factorer.getFactors(number);
		Map<Long,Integer> squareFactors=factorer.getTotientOfSquareFactors(factors);
		return isCube(squareFactors);
	}

	@Override
	public void main(Euler342State state) {
		try	{
			this.state=state;
			PrimeFactorer factorer=new PrimeFactorer(LIMIT);
			for (;state.currentNumber<LIMIT;++state.currentNumber)	{
				if ((state.currentNumber%10000000)==0) System.out.println(state.currentNumber);
				if (verifiesCondition(state.currentNumber,factorer)) state.currentSum+=state.currentNumber;
				if (checkAndStop()) return;
			}
			System.out.println(state.currentSum);
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}
	
	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler342().start(Euler342State.class);
	}
}
