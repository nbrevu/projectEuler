package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler200 {
	public static boolean composites[];
	public static void init(int maxNumber)	{
		composites=Primes.sieve(maxNumber);
	}
	private static long square(long n)	{
		return n*n;
	}
	private static long cube(long n)	{
		return n*n*n;
	}
	private static interface SqubeFactory	{
		long getSqube();
		void addChildren(Set<SqubeFactory> chain);
		String debug();
	}
	private static class SquareCubeSqubeFactory implements SqubeFactory	{
		// In this class, the squared prime is HIGHER than the
		// cubed prime.
		private final long squared;
		private final long cubed;
		private final long sqube;
		public SquareCubeSqubeFactory(long sq,long c)	{
			squared=sq;
			cubed=c;
			sqube=square(sq)*cube(c);
		}
		public boolean equals(Object o)	{
			if (!(o instanceof SquareCubeSqubeFactory)) return false;
			SquareCubeSqubeFactory other=(SquareCubeSqubeFactory)o;
			return sqube==other.sqube;
		}
		public int hashCode()	{
			return (int)(squared+cubed);
		}
		@Override
		public long getSqube()	{
			return sqube;
		}
		@Override
		public
		void addChildren(Set<SqubeFactory> chain)	{
			int nextSquare=(int)squared+1;
			while (composites[nextSquare]) ++nextSquare;
			chain.add(new SquareCubeSqubeFactory(nextSquare, cubed));
			int nextCube=(int)cubed+1;
			while (composites[nextCube]) ++nextCube;
			if (nextCube<squared) chain.add(new SquareCubeSqubeFactory(squared,nextCube));
		}
		@Override
		public String debug()	{
			return ""+squared+"^2 * "+cubed+"^3";
		}
	}
	private static class CubeSquareSqubeFactory implements SqubeFactory	{
		// In this class, the cubed prime is HIGHER than the
		// squared prime.
		private final long squared;
		private final long cubed;
		private final long sqube;
		public CubeSquareSqubeFactory(long sq,long c)	{
			squared=sq;
			cubed=c;
			sqube=square(sq)*cube(c);
		}
		public boolean equals(Object o)	{
			if (!(o instanceof CubeSquareSqubeFactory)) return false;
			CubeSquareSqubeFactory other=(CubeSquareSqubeFactory)o;
			return sqube==other.sqube;
		}
		public int hashCode()	{
			return (int)(squared+cubed);
		}
		@Override
		public long getSqube()	{
			return sqube;
		}
		@Override
		public
		void addChildren(Set<SqubeFactory> chain)	{
			int nextSquare=(int)squared+1;
			while (composites[nextSquare]) ++nextSquare;
			if (nextSquare<cubed) chain.add(new CubeSquareSqubeFactory(nextSquare, cubed));
			int nextCube=(int)cubed+1;
			while (composites[nextCube]) ++nextCube;
			chain.add(new CubeSquareSqubeFactory(squared,nextCube));
		}
		@Override
		public String debug()	{
			return ""+squared+"^2 * "+cubed+"^3";
		}
	}
	public static class SqubeComparator implements Comparator<SqubeFactory>	{
		@Override
		public int compare(SqubeFactory o1, SqubeFactory o2) {
			long s1=o1.getSqube();
			long s2=o2.getSqube();
			if (s1<s2) return -1;
			else if (s1>s2) return 1;
			else return 0;
		}
	}
	private static Primes.RabinMiller rabinMiller=new Primes.RabinMiller();
	private static Collection<Integer> witnesses=Arrays.asList(2,3,5,7,11,13,17,19,23,29,31,37);
	private static boolean isPrime(BigInteger in)	{
		long l=in.longValue();
		for (Integer i:witnesses) if ((l%((long)i))==0) return false;
		return rabinMiller.isPrime(in, witnesses);
	}
	private static boolean isPrimeProof(String s)	{
		int N=s.length();
		for (int i=0;i<N;++i)	{
			if (i==0) for (char j='1';j<='9';++j)	{
				StringBuilder sb=new StringBuilder(s);
				sb.setCharAt(i,j);
				if (isPrime(new BigInteger(sb.toString()))) return false;
			}	else if (i==(N-1)) for (char j='1';j<='9';++j,++j)	{
				if (j!='5')	{
					StringBuilder sb=new StringBuilder(s);
					sb.setCharAt(i,j);
					if (isPrime(new BigInteger(sb.toString()))) return false;
				}
			}	else for (char j='0';j<='9';++j) 	{
				StringBuilder sb=new StringBuilder(s);
				sb.setCharAt(i,j);
				if (isPrime(new BigInteger(sb.toString()))) return false;
			}
		}
		return true;
	}
	public static void main(String[] args)	{
		init(1000000);
		Set<SqubeFactory> squbeFactories=new TreeSet<SqubeFactory>(new SqubeComparator());
		squbeFactories.add(new SquareCubeSqubeFactory(3,2));
		squbeFactories.add(new CubeSquareSqubeFactory(2,3));
		int counter=0;
		for (;;)	{
			SqubeFactory sq=(squbeFactories.iterator().next());
			String s=Long.toString(sq.getSqube());
			if (s.contains("200")&&isPrimeProof(s))	{
				++counter;
				if (counter==200)	{
					System.out.println(s);
					return;
				}
			}
			sq.addChildren(squbeFactories);
			squbeFactories.remove(sq);
		}
	}
}
