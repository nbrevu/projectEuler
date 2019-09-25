package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.math.LongMath;

public class Primes {
	public static class RabinMiller	{
		private final Random rand;
		private static BigInteger powMod(BigInteger a,BigInteger d,BigInteger in)	{
			BigInteger res=BigInteger.ONE;
			BigInteger power=a;
			do	{
				BigInteger[] division=d.divideAndRemainder(BigInteger.TWO);
				if (division[1].equals(BigInteger.ONE)) res=res.multiply(power).mod(in);
				power=power.multiply(power).mod(in);
				d=division[0];
			}	while (!d.equals(BigInteger.ZERO));
			return res;
		}
		private static int powMod(int a,int d,int in)	{
			long res=a;
			while (d>1)	{
				res*=a;
				res%=in;
				--d;
			}
			return (int)res;
		}
		private static int squareMod(int a,int in)	{
			long res=a*a;
			return (int)(res%in);
		}
		public RabinMiller()	{
			rand=new Random();
		}
		public boolean isPrime(int in,int k)	{
			int d=in-1;
			int s=0;
			while ((d%2)==0)	{
				d/=2;
				++s;
			}
			for (int i=0;i<k;++i)	{
				int a=rand.nextInt(in-4)+2;
				int x=powMod(a,d,in);
				if ((x==1)||(x==(in-1))) continue;
				boolean anyNeg=false;
				for (int r=1;r<s;++r)	{
					x=squareMod(x,in);
					if (x==1) return false;
					if (x==(in-1))	{
						anyNeg=true;
						break;
					}
				}
				if (!anyNeg) return false;
			}
			return true;
		}
		public boolean isPrime(BigInteger in,Collection<Integer> witnesses)	{
			for (Integer i:witnesses) if (in.equals(BigInteger.valueOf((long)i))) return true;
			BigInteger inMinusOne=in.subtract(BigInteger.ONE);
			BigInteger d=inMinusOne;
			int s=0;
			for (;;)	{
				BigInteger[] division=d.divideAndRemainder(BigInteger.TWO);
				if (!division[1].equals(BigInteger.ZERO)) break;
				d=division[0];
				++s;
			}
			for (Integer a:witnesses)	{
				BigInteger x=powMod(BigInteger.valueOf(a),d,in);
				if (x.equals(BigInteger.ONE)||x.equals(inMinusOne)) continue;
				boolean anyNeg=false;
				for (int r=1;r<s;++r)	{
					x=x.multiply(x).mod(in);
					if (x.equals(BigInteger.ONE)) return false;
					if (x.equals(inMinusOne))	{
						anyNeg=true;
						break;
					}
				}
				if (!anyNeg) return false;
			}
			return true;
		}
	}
	
	public static class PrimeContainer<T extends Collection<Integer>>	{
		private int currentLimit;
		private final T collection;
		private boolean add4;
		public PrimeContainer(T collection)	{
			this.collection=collection;
			collection.clear();
			collection.add(2);
			collection.add(3);
			currentLimit=5;
			add4=false;
		}
		public void generatePrimesUpTo(int limit)	{
			if (currentLimit>=limit) return;
			int p=currentLimit;
			for (p=currentLimit;p<limit;p+=add4?4:2,add4=!add4)	{
				boolean isPrime=true;
				for (int i:collection)	{
					if ((p%i)==0)	{
						isPrime=false;
						break;
					}
					if (i*i>p) break;
				}
				if (isPrime) collection.add(p);
			}
			currentLimit=p;
		}
		
		public Collection<Integer> getPrimes()	{
			return Collections.unmodifiableCollection(collection);
		}
	}
	
	public static boolean[] sieve(int maxNumber)	{
		// Defaults to false!
		boolean[] composites=new boolean[1+maxNumber];
		composites[0]=composites[1]=true;
		for (int j=4;j<=maxNumber;j+=2) composites[j]=true;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (!composites[i]) for (int j=i*i;j<=maxNumber;j+=i+i) composites[j]=true;
		return composites;
	}
	
	public static int[] firstPrimeSieve(int maxNumber)	{
		int[] firstPrimes=new int[1+maxNumber];
		firstPrimes[0]=firstPrimes[1]=1;
		for (int j=4;j<=maxNumber;j+=2) firstPrimes[j]=2;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (firstPrimes[i]==0) for (int j=i*i;j<=maxNumber;j+=i+i) if (firstPrimes[j]==0) firstPrimes[j]=i;
		return firstPrimes;
	}
	public static int[] lastPrimeSieve(int maxNumber)	{
		int[] firstPrimes=new int[1+maxNumber];
		firstPrimes[0]=firstPrimes[1]=1;
		for (int j=4;j<=maxNumber;j+=2) firstPrimes[j]=2;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (firstPrimes[i]==0) for (int j=i*i;j<=maxNumber;j+=i+i) firstPrimes[j]=i;
		return firstPrimes;
	}
	public static long[] firstPrimeSieve(long maxNumber)	{
		long[] firstPrimes=new long[1+(int)maxNumber];
		firstPrimes[0]=firstPrimes[1]=1;
		for (int j=4;j<=maxNumber;j+=2) firstPrimes[j]=2;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (firstPrimes[i]==0) for (int j=i*i;j<=maxNumber;j+=i+i) if (firstPrimes[j]==0) firstPrimes[j]=i;
		return firstPrimes;
	}
	public static long[] lastPrimeSieve(long maxNumber)	{
		long[] lastPrimes=new long[1+(int)maxNumber];
		lastPrimes[0]=lastPrimes[1]=1;
		for (int j=2;j<=maxNumber;j+=2) lastPrimes[j]=2;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (lastPrimes[i]==0) for (int j=2*i;j<=maxNumber;j+=i) lastPrimes[j]=i;
		return lastPrimes;
	}
	// The difference between this one and the previous is that this one doesn't use zeros.
	public static long[] trueLastPrimeSieve(long maxNumber)	{
		long[] lastPrimes=new long[1+(int)maxNumber];
		lastPrimes[0]=lastPrimes[1]=1;
		for (int j=2;j<=maxNumber;j+=2) lastPrimes[j]=2;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (lastPrimes[i]==0)	{
			lastPrimes[i]=i;
			for (int j=2*i;j<=maxNumber;j+=i) lastPrimes[j]=i;
		}
		return lastPrimes;
	}
	public static List<Integer> listIntPrimes(int maxNumber)	{
		boolean[] composites=sieve(maxNumber);
		List<Integer> result=new ArrayList<>();
		for (int i=2;i<composites.length;++i) if (!composites[i]) result.add(i);
		return result;
	}
	public static int[] listIntPrimesAsArray(int maxNumber)	{
		boolean[] composites=sieve(maxNumber);
		int size=2;
		boolean add4=false;
		for (int i=5;i<=maxNumber;i+=(add4?4:2),add4=!add4) if (!composites[i]) ++size;
		int[] result=new int[size];
		result[0]=2;
		result[1]=3;
		add4=false;
		int index=2;
		for (int i=5;i<=maxNumber;i+=(add4?4:2),add4=!add4) if (!composites[i])	{
			result[index]=i;
			++index;
		}
		return result;
	}
	public static List<Long> listLongPrimes(long maxNumber)	{
		boolean[] composites=sieve((int)maxNumber);
		List<Long> result=new ArrayList<>();
		for (int i=2;i<composites.length;++i) if (!composites[i]) result.add((long)i);
		return result;
	}
	
	public static List<Long> getDistinctPrimeFactors(long n,List<Long> primes)	{
		List<Long> result=new ArrayList<>();
		for (long p:primes) if ((n%p)==0)	{
			result.add(p);
			do n/=p; while ((n%p)==0);
		}	else if ((p*p)>n)	{
			result.add(n);
			break;
		}
		return result;
	}
	
	public static class PrimeCandidatesIterator implements Iterator<Long>,Iterable<Long>	{
		private Iterator<Long> internalIterator;
		public PrimeCandidatesIterator()	{
			internalIterator=new InitialPrimesIterator();
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Long next() {
			return internalIterator.next();
		}
		@Override
		public Iterator<Long> iterator() {
			return this;
		}
		private class InitialPrimesIterator implements Iterator<Long>	{
			private boolean is2;
			public InitialPrimesIterator()	{
				is2=true;
			}
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Long next() {
				if (is2)	{
					is2=false;
					return 2l;
				}	else	{
					// Dirty but works. Replace the internal representation with the actual iterator.
					internalIterator=new TruePrimesIterator();
					return 3l;
				}
			}
		}
		private class TruePrimesIterator implements Iterator<Long>	{
			private boolean add2;
			private long current;
			public TruePrimesIterator()	{
				add2=true;
				current=5l;
			}
			@Override
			public boolean hasNext() {
				return true;
			}
			@Override
			public Long next() {
				long result=current;
				current+=(add2?2l:4l);
				add2=(!add2);	// Useless parentheses, but it's clear that I mean =! and not !=.
				return result;
			}
		}
	}

	// Thanks, Lucy_Hedgehog. See Project Euler 10.
	public static long sumPrimes(long upTo)	{
		long r=LongMath.sqrt(upTo,RoundingMode.DOWN);
		List<Long> v=new ArrayList<>();
		for (long i=1;i<=r;++i) v.add(upTo/i);
		long v_1=v.get(v.size()-1)-1;
		while (v_1>0)	{
			v.add(v_1);
			--v_1;
		}
		Map<Long,Long> s=new HashMap<>();
		for (long vi:v) s.put(vi,(vi*(vi+1))/2-1);
		for (long p=2;p<=r;++p) if (s.get(p)>s.get(p-1))	{
			long sp=s.get(p-1);
			long p2=p*p;
			for (long vi:v) if (vi<p2) break;
			else s.put(vi,s.get(vi)-p*(s.get(vi/p)-sp));
		}
		return s.get(upTo);
	}
	
	public static BigInteger sumPrimesBig(long upTo)	{
		long r=LongMath.sqrt(upTo,RoundingMode.DOWN);
		List<Long> v=new ArrayList<>();
		for (long i=1;i<=r;++i) v.add(upTo/i);
		long v_1=v.get(v.size()-1)-1;
		while (v_1>0)	{
			v.add(v_1);
			--v_1;
		}
		Map<Long,BigInteger> s=new HashMap<>();
		BigInteger TWO=BigInteger.valueOf(2l);
		for (long vi:v)	{
			BigInteger bVi=BigInteger.valueOf(vi);
			s.put(vi,bVi.multiply(bVi.add(BigInteger.ONE)).divide(TWO).subtract(BigInteger.ONE));
		}
		for (long p=2;p<=r;++p) if (s.get(p).compareTo(s.get(p-1))>0)	{
			BigInteger sp=s.get(p-1);
			long p2=p*p;
			BigInteger bP=BigInteger.valueOf(p);
			for (long vi:v) if (vi<p2) break;
			else	{
				BigInteger curVal=s.get(vi);
				BigInteger bVi_p=s.get(vi/p);
				s.put(vi,curVal.subtract(bVi_p.subtract(sp).multiply(bP)));
			}
		}
		return s.get(upTo);
	}
}
