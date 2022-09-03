package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.output.NullOutputStream;

import com.euler.common.Primes;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class Euler308_3 {
	private final static int MAX_PRIME=29;
	private final static int GOAL=37;
	
	private final static class Fraction	{
		public final int num;
		public final int den;
		public Fraction(int num,int den)	{
			this.num=num;
			this.den=den;
		}
	}
	
	private final static class FractranProgram	{
		public final static List<Integer> PRIMES=Primes.listIntPrimes(MAX_PRIME);
		private final List<String> identifiers;
		private final int[][] primeIncreases;
		private int[] internalState;
		private int[] swapper;
		public FractranProgram(String string)	{
			this(parse(string),Arrays.asList(string.split(",")));
		}
		public FractranProgram(List<Fraction> fractions,List<String> identifiers)	{
			this.identifiers=identifiers;
			primeIncreases=new int[fractions.size()][];
			for (int i=0;i<fractions.size();++i) primeIncreases[i]=transform(fractions.get(i));
			internalState=new int[PRIMES.size()];
			internalState[0]=1;
			swapper=new int[PRIMES.size()];
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
		public String iterate()	{
			for (int i=0;i<primeIncreases.length;++i)	{
				int[] fraction=primeIncreases[i];
				if (iterate(fraction))	{
					int[] ptr=swapper;
					swapper=internalState;
					internalState=ptr;
					return identifiers.get(i);
				}
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
		private void storeState(List<String> goal)	{
			for (int s:internalState) goal.add(Integer.toString(s));
		}
	}
	
	private static String quoteStringSoThatFuckingExcelDoesntFuckingCorruptMyFuckingData(String str)	{
		return String.format("=\"%s\"",str);
	}
	
	private static PrintStream getPrintStream(boolean actuallyDoIt) throws IOException	{
		return actuallyDoIt?new PrintStream("C:\\out308.csv"):new PrintStream(NullOutputStream.nullOutputStream());
	}
	
	public static void main(String[] args)	{
		Joiner joiner=Joiner.on(';');
		try	(PrintStream ps=getPrintStream(false))	{
			List<String> header=new ArrayList<>();
			header.add("Iteration");
			header.add("Instruction");
			header.addAll(Lists.transform(FractranProgram.PRIMES,(Integer prime)->String.format("Exponent of %d",prime)));
			header.add("Power of 2?");
			ps.println(joiner.join(header));
			String program="17/91,78/85,19/51,23/38,29/33,77/29,95/23,77/19,1/17,11/13,13/11,15/2,1/7,55/1";
			FractranProgram fractranProgram=new FractranProgram(program);
			long iterations=0;
			for (;;)	{
				List<String> iterationData=new ArrayList<>(header.size());
				++iterations;
				iterationData.add(Long.toString(iterations));
				iterationData.add(quoteStringSoThatFuckingExcelDoesntFuckingCorruptMyFuckingData(fractranProgram.iterate()));
				int pow=fractranProgram.isPowerOfTwo();
				fractranProgram.storeState(iterationData);
				if (pow>0)	{
					iterationData.add(Integer.toString(pow));
					System.out.println("Iteration "+iterations+": found 2^"+pow+".");
				}	else iterationData.add("NO");
				ps.println(joiner.join(iterationData));
				if (pow==GOAL) break;
			}
		}	catch (IOException exc)	{
			System.out.println("OH, NEIN! LEIDER KANN ICH NICHT INS ARCHIVEN SCHREIBEN! ES GEFÄLLT MIR ÜBERHAUPT NICHT!!!!!");
		}
	}
}
