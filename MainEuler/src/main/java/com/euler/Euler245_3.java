package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.google.common.math.LongMath;

public class Euler245_3 {
	/*
	Tenemos p·q = A
	phi(p·q) = (p-1)·(q-1)
	pq - (p-1)·(q-1) = p+q-1
	
	(pq-1) es múltiplo de (p+q-1)
	
	En particular supongamos (pq-1) = B·(p+q-1)
	
	Veamos que pasa si p=B+1.
	
	Entonces, (B+1)q-1 = B(B+1+q-1) -> Bq+q-1 = B^2+B+Bq-B -> q-1 = B^2 -> q=B^2+1, ¡pues claro!
	
	Y ahora a ver cuando p=B+7.
	
	(B+7)q-1=B·(B+7+q-1) -> Bq+7q-1 = B^2+7B+Bq-B -> 7q-1 = B^2+6B, así que q=(B^2+6B+1)/7
	
	Habíamos visto el caso B=822 y p=829. Entonces, q=(822^2 + 6·822 + 1)/7 = (675684 + 4932 + 1)/7 = 680617/7 = 97231, correcto.
	
	Y en general supongamos p=B+n.
	
	(B+n)q-1=B·(B+n+q-1) -> Bq+nq-1 = B^2+B(n-1)+Bq -> nq-1 = B^2+B(n-1) -> q=[B^2+B(n-1)+1]/n
	 */
	private final static long LIMIT=200000000000l;
	private final static long SQRT_LIMIT=LongMath.sqrt(LIMIT, RoundingMode.CEILING);
	private final static int PRIME_LIMIT=(int)(Math.ceil(Math.pow(LIMIT,2.0/3.0)));
	private final static boolean[] COMPOSITES=Primes.sieve(PRIME_LIMIT);
	
	private final static RabinMiller RABIN_MILLER=new RabinMiller();
	private final static List<Integer> WITNESSES=Arrays.asList(2,3,5,7,11);
	
	private static boolean isPrime(long l)	{
		if (l<COMPOSITES.length) return !COMPOSITES[(int)l];
		else return RABIN_MILLER.isPrime(BigInteger.valueOf(l),WITNESSES);
	}
	
	private static interface PrimesProduct	{
		public long getProduct();
		public List<PrimesProduct> expand();
	}
	
	private static class SeveralPrimesProduct implements PrimesProduct,Comparable<PrimesProduct>	{
		private final SortedSet<Long> primes;
		private SeveralPrimesProduct(Collection<Long> ps)	{
			primes=new TreeSet<>(ps);
		}
		@Override
		public long getProduct()	{
			long p=1;
			for (long l:primes) p*=l;
			return p;
		}
		@Override
		public List<PrimesProduct> expand()	{
			return Collections.emptyList();
		}
		@Override
		public int compareTo(PrimesProduct other)	{
			long diff=getProduct()-other.getProduct();
			return (diff>0)?1:((diff<0)?-1:0);
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(getProduct());
		}
		@Override
		public boolean equals(Object other)	{
			return getProduct()==((PrimesProduct)other).getProduct();
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			boolean first=true;
			for (long p:primes)	{
				if (first) first=false;
				else sb.append('·');
				sb.append(p);
			}
			sb.append('=').append(getProduct()).append('.');
			return sb.toString();
		}
	}
	
	private static class TwoPrimesProduct implements PrimesProduct,Comparable<PrimesProduct>	{
		// We have a BASE and two primes.
		// The base is B.
		// The first prime is p=B+6k+1 where k is an integer (possibly 0).
		// Then, q=[B^2+B*6k+1]/(6k+1).
		private final long B,p,q;
		private TwoPrimesProduct(long B,long p,long q)	{
			this.B=B;
			this.p=p;
			this.q=q;
		}
		@Override
		public long getProduct()	{
			return p*q;
		}
		@Override
		public int compareTo(PrimesProduct other)	{
			long diff=getProduct()-other.getProduct();
			return (diff>0)?1:((diff<0)?-1:0);
		}
		public static TwoPrimesProduct generateIfPossible(long B,long k)	{
			long k6=6*k;
			long denQ=k6+1;
			long numQ=B*(B+k6)+1;
			long p=B+denQ;
			if ((numQ%denQ)!=0) return null;
			if (!isPrime(p)) return null;
			long q=numQ/denQ;
			if (p*q>LIMIT) return null;
			if (!isPrime(q)) return null;
			return new TwoPrimesProduct(B,p,q);
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append(p).append('·').append(q).append('=').append(getProduct()).append(" (base=").append(B).append(").");
			return sb.toString();
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(getProduct());
		}
		@Override
		public boolean equals(Object other)	{
			return getProduct()==((PrimesProduct)other).getProduct();
		}
		@Override
		public List<PrimesProduct> expand()	{
			if (!isPerfectSquare(B)) return Collections.emptyList();
			List<PrimesProduct> expanded=new ArrayList<>();
			long m=B;
			List<Long> factors=new ArrayList<>();
			factors.add(p);
			factors.add(q);
			do	{
				long sq=LongMath.sqrt(m,RoundingMode.UNNECESSARY);
				if (isPrime(sq+1))	{
					factors.add(sq+1);
					PrimesProduct pp=new SeveralPrimesProduct(factors);
					if (pp.getProduct()<=LIMIT) expanded.add(pp);
					else break;
				}
				m=sq;
			}	while (isPerfectSquare(m));
			return expanded;
		}
		private static boolean isPerfectSquare(long l)	{
			long sq=LongMath.sqrt(l,RoundingMode.DOWN);
			return (sq*sq)==l;
		}
	}
	
	private static void findGroupsOf3(SortedSet<PrimesProduct> goal)	{
		long limit=(long)Math.floor(Math.pow(LIMIT,1.0/3.0));
		boolean add4=false;
		for (long p1=5;p1<limit;p1+=(add4?4:2),add4=!add4) if (isPrime(p1))	{
			System.out.println(" "+p1+"!");
			long limit2=p1*p1;
			long limit3=p1*p1*p1*p1;
			boolean add4_2=!add4;
			for (long p2=p1+(add4?4:2);p2<limit2;p2+=(add4_2?4:2),add4_2=!add4_2) if (isPrime(p2))	{
				boolean add4_3=!add4_2;
				limit3=Math.min(limit3,LIMIT/(p1*p2));
				for (long p3=p2+(add4_2?4:2);p3<limit3;p3+=(add4_3?4:2),add4_3=!add4_3) if (isPrime(p3))	{
					long num=(p1*p2+p1*p3+p2*p3)-(p1+p2+p3)+1;
					long den=p1*p2*p3-1;
					if ((den%num)==0)	{
						long q=den/num;
						List<Long> factors=Arrays.asList(p1,p2,p3);
						System.out.println("Encontrado caso raro: "+factors+" ["+q+"]");
						goal.add(new SeveralPrimesProduct(factors));
					}
				}
			}
		}
	}
	
	public static void main(String[] args)	{
		SortedSet<PrimesProduct> foundProducts=new TreeSet<>();
		final long MAX_B=SQRT_LIMIT/2;
		for (long B=2;B<=MAX_B;B+=2)	{
			if (B%1000==0) System.out.println(""+B+"...");
			for (long k=(B-1)/6;k>=0;--k)	{
				TwoPrimesProduct tpp=TwoPrimesProduct.generateIfPossible(B,k);
				if (tpp!=null)	{
					foundProducts.add(tpp);
					foundProducts.addAll(tpp.expand());
				}
			}
		}
		
		findGroupsOf3(foundProducts);
		
		System.out.println("He encontrado "+foundProducts.size()+" cosas. ATIENDE QUÉ GAÑANAZO.");
		try (PrintStream ps=new PrintStream("C:\\out254_3.txt"))	{
			for (PrimesProduct ttp:foundProducts) ps.println(ttp.toString());
		}	catch (IOException exc)	{
			System.out.println("D'oh!");
		}
		long s=0;
		for (PrimesProduct p:foundProducts) s+=p.getProduct();
		System.out.println(s);
	}
}
