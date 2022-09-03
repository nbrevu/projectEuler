package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.FactorialCache;
import com.euler.common.Primes;
import com.google.common.base.Optional;

public class Euler598_3 {
	private final static int LIMIT=100;
	
	private static class TemporaryProduct	{
		private final static boolean[] composites=Primes.sieve(LIMIT);
		public long prod1;
		public long prod2;
		public TemporaryProduct()	{
			prod1=1;
			prod2=1;
		}
		public TemporaryProduct(TemporaryProduct previous,long f1,long f2)	{
			prod1=previous.prod1*(1+f1);
			prod2=previous.prod2*(1+f2);
			simplify();
		}
		private void simplify()	{
			long gcd=EulerUtils.gcd(prod1,prod2);
			if (gcd>1)	{
				prod1/=gcd;
				prod2/=gcd;
			}
		}
		public long getMaxNeededPrimeFactor()	{
			return Math.max(getMaxNeededPrimeFactor(prod1),getMaxNeededPrimeFactor(prod2));
		}
		private long getMaxNeededPrimeFactor(long in)	{
			if (in==1) return 1;
			for (int i=LIMIT;i>=2;--i) if ((!composites[i])&&((in%i)==0)) return i;
			return 1;
		}
		public Optional<int[]> getAsDifferenceOf2And3()	{
			int twos=0;
			int threes=0;
			while ((prod1%2)==0)	{
				++twos;
				prod1/=2;
			}
			while ((prod1%3)==0)	{
				++threes;
				prod1/=3;
			}
			if (prod1!=1) return Optional.absent();
			while ((prod2%2)==0)	{
				--twos;
				prod2/=2;
			}
			while ((prod2%3)==0)	{
				--threes;
				prod2/=3;
			}
			if (prod2!=1) return Optional.absent();
			twos=Math.abs(twos);
			threes=Math.abs(threes);
			return Optional.of(new int[]{twos,threes});
		}
	}
	
	private static class CompressedDecomposition	{
		private static CombinatorialNumberCache combCache=new CombinatorialNumberCache(1);
		private static FactorialCache factCache=new FactorialCache(1);
		public final int singlePrimes;
		public final int doublePrimes;
		public final List<Integer> allPowers;
		private CompressedDecomposition(int singlePrimes,int doublePrimes,List<Integer> allPowers)	{
			this.singlePrimes=singlePrimes;
			this.doublePrimes=doublePrimes;
			this.allPowers=allPowers;
		}
		public static CompressedDecomposition of(int in)	{
			List<Integer> primes=Primes.listIntPrimes(in);
			int sp=0;
			int dp=0;
			List<Integer> remaining=new ArrayList<>();
			for (int p:primes)	{
				int pow=primesInFactorial(p,in);
				if (pow==1) ++sp;
				else if (pow==2) ++dp;
				else remaining.add(pow);	
			}
			return new CompressedDecomposition(sp,dp,remaining);
		}
		private static int primesInFactorial(int prime,int factorial)	{
			int result=0;
			for (int div=prime;div<=factorial;div*=prime) result+=factorial/div;
			return result;
		}
		public long getAcceptableDecompositions()	{
			return getAcceptableDecompositions(new TemporaryProduct(),0)/2;
		}
		private long getAcceptableDecompositions(TemporaryProduct tp,int index)	{
			if (index>=allPowers.size()) return getAcceptableDecompositionsUsing2And3(tp);
			long maxFactor=tp.getMaxNeededPrimeFactor();
			int nextPower=allPowers.get(index);
			if (maxFactor>1+nextPower) return 0;
			long result=0;
			for (int i=0;i<=nextPower;++i)	{
				TemporaryProduct next=new TemporaryProduct(tp,i,nextPower-i);
				result+=getAcceptableDecompositions(next,1+index);
			}
			return result;
		}
		private long getAcceptableDecompositionsUsing2And3(TemporaryProduct tp)	{
			Optional<int[]> decomposition=tp.getAsDifferenceOf2And3();
			if (!decomposition.isPresent()) return 0;
			int[] dec=decomposition.get();
			int twos=dec[0];
			int threes=dec[1];
			if ((twos>singlePrimes)||(threes>doublePrimes)) return 0;
			// Combinaciones de primos dobles: si tenemos X y la diferencia que buscamos es N,
			// entonces para cada i entre 0 y (X-N)/2: uno se lleva N+i, otro i, los demás (X-N-2i) se
			// reparten. Así que el resultado es X!/[(N+i)!*i!*(X-N-2i)!].
			int diff2=singlePrimes-twos;
			if ((diff2%2)!=0) return 0;
			long result=0;
			for (int i=0;;++i)	{
				int b=doublePrimes-threes-2*i;
				if (b<0) break;
				int a=threes+i;
				long tmpRes=factCache.get(doublePrimes);
				tmpRes/=factCache.get(a)*factCache.get(b)*factCache.get(i);
				result+=tmpRes;
			}
			// Finalmente multiplicamos por las combinaciones de primos simples.
			result*=combCache.get(singlePrimes,diff2/2);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		/*
		for (int i=2;i<=LIMIT;++i)	{
			CompressedDecomposition cd=CompressedDecomposition.of(i);
			long result=cd.getAcceptableDecompositions();
			System.out.println("f("+i+")="+result);
		}
		*/
		long tic=System.nanoTime();
		CompressedDecomposition cd=CompressedDecomposition.of(LIMIT);
		long result=cd.getAcceptableDecompositions();
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println(result);
		System.out.println("Calculated in "+seconds+" seconds.");
	}
}
