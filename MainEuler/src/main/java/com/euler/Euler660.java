package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler660 {
	private final static int MIN_BASE=9;
	private final static int MAX_BASE=24;
	
	private static class Enumerator	{
		public long currentValue;
		public final long lastValue;
		public final long increment;
		public Enumerator(long initialValue,long increment,long lastValue)	{
			currentValue=initialValue;
			this.increment=increment;
			this.lastValue=lastValue;
		}
		public boolean hasFinished()	{
			return currentValue>=lastValue;
		}
		public long nextValue()	{
			long result=currentValue;
			currentValue+=increment;
			return result;
		}
	}
	
	private static interface ArithmeticSequence	{
		public Enumerator getForRange(long start,long end);
	}
	
	private static enum EmptyArithmeticSequence implements ArithmeticSequence	{
		INSTANCE;
		private final static Enumerator EMPTY_ENUMERATOR=new Enumerator(1,0,0);
		@Override
		public Enumerator getForRange(long start, long end) {
			return EMPTY_ENUMERATOR;
		}
	}
	private static class SimpleArithmeticSequence implements ArithmeticSequence	{
		private final long x;
		public SimpleArithmeticSequence(long x)	{
			this.x=x;
		}
		@Override
		public Enumerator getForRange(long start,long end)	{
			long sMod=start%x;
			long seqStart=(sMod==0)?start:(start+x-sMod);
			return new Enumerator(seqStart,x,end);
		}
	}
	private static class OddArithmeticSequence implements ArithmeticSequence	{
		private final long x;
		public OddArithmeticSequence(long x)	{
			this.x=x;
		}
		@Override
		public Enumerator getForRange(long start,long end)	{
			long sMod=start%(2*x);
			long seqStart;
			if (sMod==x) seqStart=start;
			else if (sMod<x) seqStart=start+x-sMod;
			else seqStart=start+3*x-sMod;
			return new Enumerator(seqStart,2*x,end);
		}
	}
	private static interface ArithmeticSequenceGenerator	{
		public ArithmeticSequence getForMod(long mod);
	}
	private static class SimpleArithmeticSequenceGenerator implements ArithmeticSequenceGenerator	{
		private final long base;
		public SimpleArithmeticSequenceGenerator(long base)	{
			this.base=base;
		}
		@Override
		public ArithmeticSequence getForMod(long mod)	{
			long g=(mod==0)?base:EulerUtils.gcd(mod,base);
			return new SimpleArithmeticSequence(base/g);
		}
	}
	private static class OddArithmeticSequenceGenerator implements ArithmeticSequenceGenerator	{
		private final int gamma;
		private final long delta;
		public OddArithmeticSequenceGenerator(long base)	{
			int exp=0;
			long s=base;
			do	{
				++exp;
				s/=2;
			}	while ((s%2)==0);
			gamma=exp;
			delta=s;
		}
		@Override
		public ArithmeticSequence getForMod(long mod)	{
			if (mod==0) return EmptyArithmeticSequence.INSTANCE;
			int alpha=0;
			long beta=mod;
			while ((beta%2)==0)	{
				++alpha;
				beta/=2;
			}
			if (alpha>=gamma) return EmptyArithmeticSequence.INSTANCE;	// Can this actually happen? I should be covered by the if (mod==0) case.
			long p=1<<(gamma-alpha-1);
			long g=EulerUtils.gcd(beta,delta);
			long q=delta/g;
			return new OddArithmeticSequence(p*q);
		}
	}
	
	private static long[][] createArraysCache(int maxSize)	{
		long[][] result=new long[1+maxSize][];
		for (int i=0;i<=maxSize;++i) result[i]=new long[i];
		return result;
	}
	
	private static class PandigitalChecker	{
		private final static long[][] ARRAYS_CACHE=createArraysCache(3*MAX_BASE);
		private final int base;
		private final int reducedBase;
		private final long[] powers;
		private final ArithmeticSequence[] sequences;
		private boolean canEnd;
		public PandigitalChecker(int base)	{
			this.base=base;
			reducedBase=base-1;
			int maxPower=(base+1)/2;
			powers=new long[maxPower];
			powers[0]=1l;
			for (int i=1;i<maxPower;++i) powers[i]=base*powers[i-1];
			sequences=new ArithmeticSequence[reducedBase];
			ArithmeticSequenceGenerator generator=((reducedBase%2)==1)?new SimpleArithmeticSequenceGenerator(reducedBase):new OddArithmeticSequenceGenerator(reducedBase);
			for (int i=0;i<reducedBase;++i) sequences[i]=generator.getForMod(i);
			canEnd=false;
		}
		public void initMValue()	{
			/*
			 * The canEnd variable keeps track of whether there has been a case for this "m" where the total amount of digits is acceptable. It's
			 * updated each time getNumDigits is called. If no valid value has been found when canBeRemoved() is called, then we can discard this
			 * checker.
			 */
			canEnd=true;
		}
		public boolean canBeRemoved()	{
			return canEnd;
		}
		public int getNumDigits(long in)	{
			int index=Arrays.binarySearch(powers,in);
			return (index>0)?(1+index):(-1-index);
		}
		public boolean isBelowLimits(long a,long b,long c)	{
			boolean result=(getNumDigits(a)+getNumDigits(b)+getNumDigits(c)<=base);
			canEnd&=!result;
			return result;
		}
		public Enumerator getValidMultiples(long a,long b,long c,long s)	{
			int numA=getNumDigits(a);
			int numB=getNumDigits(b);
			int numC=getNumDigits(c);
			int totDigits=numA+numB+numC;
			int newDigits=3*powers.length-totDigits;
			int index=0;
			long[] tmpArray=ARRAYS_CACHE[newDigits];
			for (int i=numA;i<powers.length;++i)	{
				tmpArray[index]=LongMath.divide(powers[i],a,RoundingMode.UP);
				++index;
			}
			for (int i=numB;i<powers.length;++i)	{
				tmpArray[index]=LongMath.divide(powers[i],b,RoundingMode.UP);
				++index;
			}
			for (int i=numC;i<powers.length;++i)	{
				tmpArray[index]=LongMath.divide(powers[i],c,RoundingMode.UP);
				++index;
			}
			Arrays.sort(tmpArray);
			long infLimit=(totDigits==base)?1:tmpArray[base-totDigits-1];
			long supLimit=tmpArray[base-totDigits];
			int mod=(int)((a+b+c)%reducedBase);
			ArithmeticSequence seq=sequences[mod];
			return seq.getForRange(infLimit,supLimit);
		}
		private void updateDigits(long number,BitSet digitTally)	{
			do	{
				int mod=(int)(number%base);
				number/=base;
				digitTally.set(mod);
			}	while (number>0);
		}
		private boolean isPandigital(long ka,long kb,long kc)	{
			BitSet digits=new BitSet(base);
			updateDigits(ka,digits);
			updateDigits(kb,digits);
			updateDigits(kc,digits);
			if (digits.cardinality()==base)	{
				String msg=String.format("Pandigital triangle found: (%s, %s, %s) in base %d (or (%d, %d, %d) in base 10).",Long.toString(ka,base),Long.toString(kb,base),Long.toString(kc,base),base,ka,kb,kc);
				System.out.println(msg);
			}
			return digits.cardinality()==base;
		}
		public long getValue(long a,long b,long c,long s)	{
			long result=0;
			Enumerator enumerator=getValidMultiples(a,b,c,s);
			while (!enumerator.hasFinished())	{
				long k=enumerator.nextValue();
				long ka=k*a;
				if (isPandigital(ka,k*b,k*c)) result+=ka;
			}
			return result;
		}
	}
	
	// The code is a bit long, but I got this on the first try!! :)
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		List<PandigitalChecker> checkers=new ArrayList<>();
		for (int i=MIN_BASE;i<=MAX_BASE;++i) checkers.add(new PandigitalChecker(i));
		int nextDisp=2;
		int currentDispIncr=1;
		int nextDispLimit=10;
		for (long m=2;!checkers.isEmpty();++m)	{
			if (m==nextDisp)	{
				System.out.println("m="+m+"...");
				if (nextDisp==nextDispLimit)	{
					currentDispIncr=nextDispLimit;
					nextDispLimit*=10;
				}
				nextDisp+=currentDispIncr;
			}
			checkers.forEach(PandigitalChecker::initMValue);
			long m2=m*m;
			int nDecr=1;
			for (long n=m-1;n>0;n-=nDecr,nDecr=3-nDecr) if (EulerUtils.areCoprime(m,n))	{
				long n2=n*n;
				long mn=m*n;
				long a=m2+mn+n2;
				long b=2*mn+n2;
				long c=m2-n2;
				long s=a+b+c;
				for (PandigitalChecker checker:checkers) if (checker.isBelowLimits(a,b,c)) result+=checker.getValue(a,b,c,s);
			}
			if ((m%3)!=1) checkers.removeIf(PandigitalChecker::canBeRemoved);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
