package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes;
import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.base.Splitter;

public class Euler308_2 extends InterruptableProgram<Euler308_2.FractranProgram> {
	private final static String PROGRAM_CODE="17/91,78/85,19/51,23/38,29/33,77/29,95/23,77/19,1/17,11/13,13/11,15/2,1/7,55/1";
	private final static int GOAL=104743;	// 7th prime.
	private final static int MAX_PRIME=29;
	
	private final static class Fraction	{
		public final int num;
		public final int den;
		public Fraction(int num,int den)	{
			this.num=num;
			this.den=den;
		}
	}
	
	public final static class FractranProgram implements ProgramState	{
		private final static List<Integer> PRIMES=Primes.listIntPrimes(MAX_PRIME);
		private final int[][] primeIncreases;
		private int[] internalState;
		private int[] swapper;
		private long iterations;
		public FractranProgram()	{
			this(PROGRAM_CODE);
		}
		public FractranProgram(String string)	{
			this(parse(string));
		}
		public FractranProgram(List<Fraction> fractions)	{
			primeIncreases=new int[fractions.size()][];
			for (int i=0;i<fractions.size();++i) primeIncreases[i]=transform(fractions.get(i));
			internalState=new int[PRIMES.size()];
			internalState[0]=1;
			swapper=new int[PRIMES.size()];
			iterations=0;
		}
		private static List<Fraction> parse(String in)	{
			List<Fraction> result=new ArrayList<>();
			Splitter fracSplitter=Splitter.on('/');
			for (String frac:Splitter.on(',').split(in))	{
				List<String> str=fracSplitter.splitToList(frac);
				if (str.size()!=2) throw new IllegalArgumentException();
				int num=Integer.parseInt(str.get(0));
				int den=Integer.parseInt(str.get(1));
				result.add(new Fraction(num,den));
			}
			return result;
		}
		private static int[] transform(Fraction fraction)	{
			int P=PRIMES.size();
			int[] result=new int[P];
			int n=fraction.num;
			if (n>1) for (int i=0;i<P;++i) while ((n%PRIMES.get(i))==0)	{
				n/=PRIMES.get(i);
				++result[i];
				if (n==1) break;
			}
			if (n>1) throw new IllegalArgumentException();
			n=fraction.den;
			if (n>1) for (int i=0;i<P;++i) while ((n%PRIMES.get(i))==0)	{
				n/=PRIMES.get(i);
				--result[i];
				if (n==1) break;
			}
			if (n>1) throw new IllegalArgumentException();
			return result;
		}
		public void iterate()	{
			for (int[] fraction:primeIncreases) if (iterate(fraction))	{
				int[] ptr=swapper;
				swapper=internalState;
				internalState=ptr;
				++iterations;
				return;
			}
			throw new IllegalStateException();
		}
		public int isPowerOfTwo()	{
			for (int i=1;i<internalState.length;++i) if (internalState[i]!=0) return -1;
			return internalState[0];
		}
		private boolean iterate(int[] increases)	{
			System.arraycopy(internalState,0,swapper,0,internalState.length);
			for (int i=0;i<increases.length;++i)	{
				swapper[i]+=increases[i];
				if (swapper[i]<0) return false;
			}
			return true;
		}
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			StringBuilder sb=new StringBuilder();
			boolean first=true;
			for (int number:internalState)	{
				if (first) first=false;
				else sb.append(',');
				sb.append(number);
			}
			writer.write(sb.toString());
			writer.newLine();
			writer.write(Long.toString(iterations));
		}
		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			String firstLine=reader.readLine();
			List<String> elements=Splitter.on(',').splitToList(firstLine);
			assert elements.size()==PRIMES.size();
			for (int i=0;i<PRIMES.size();++i) internalState[i]=Integer.parseInt(elements.get(i));
			iterations=Long.parseLong(reader.readLine());
		}
		public long getIterations()	{
			return iterations;
		}
	}
	
	@Override
	public Path getFileName() {
		return Paths.get("C:\\tmp308.txt");
	}

	public Euler308_2() {
		super(new InputThreadStopper());
		state=getInitialState();
	}

	@Override
	public void main(FractranProgram state) {
		this.state=state;
		try	{
			for (;;)	{
				state.iterate();
				int pow=state.isPowerOfTwo();
				if (pow>0)	{
					double log1=Math.log(state.getIterations());
					double log2=Math.log(pow);
					double logQ=log1/log2;
					System.out.println("Iteration "+state.getIterations()+": found 2^"+pow+". Log quotient: "+logQ+".");
					if (pow==GOAL) System.exit(0);
				}
				if (checkAndStop()) return;
			}
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}

	@Override
	public FractranProgram getInitialState() {
		String program=PROGRAM_CODE;
		return new FractranProgram(program);
	}

	@Override
	public FractranProgram getCurrentState() {
		return state;
	}
	
	private FractranProgram state;

	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler308_2().start(FractranProgram.class);
	}
}
