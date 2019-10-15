package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Function;
import com.google.common.collect.Multiset;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class EulerUtils {
	public static long product(Collection<Long> longs)	{
		long res=1l;
		for (long p:longs) res*=p;
		return res;
	}
	
	public static long sum(Collection<Long> longs)	{
		long res=0l;
		for (long f:longs) res+=f;
		return res;
	}
	
	public static int intSum(Collection<Integer> ints)	{
		int res=0;
		for (int f:ints) res+=f;
		return res;
	}
	
	public static BigInteger gcd(BigInteger a,BigInteger b)	{
		for (;;)	{
			BigInteger rem=a.mod(b);
			if (rem.equals(BigInteger.ZERO)) return b;
			a=b;
			b=rem;
		}
	}
	
	public static long gcd(long a,long b)	{
		for (;;)	{
			long rem=a%b;
			if (rem==0l) return b;
			a=b;
			b=rem;
		}
	}
	
	public static int gcd(int a,int b)	{
		for (;;)	{
			int rem=a%b;
			if (rem==0) return b;
			a=b;
			b=rem;
		}
	}
	
	public static boolean areCoprime(long a,long b)	{
		return gcd(a,b)==1l;
	}
	
	public static long lcm(long a,long b)	{
		return (a*b)/gcd(a,b);
	}

	public static long modulusInverse(long operand,long mod)	{
		long store=mod;
		long x=0;
		long y=1;
		long lastX=1;
		long lastY=0;
		long q,tmp;
		while (operand!=0)	{
			q=mod/operand;
			tmp=mod%operand;
			mod=operand;
			operand=tmp;
			tmp=lastX-q*x;
			lastX=x;
			x=tmp;
			tmp=lastY-q*y;
			lastY=y;
			y=tmp;
		}
		while (lastY<0) lastY+=store;
		return lastY%store;
	}
	
	// Assumes n and m coprime!
	public static long solveChineseRemainder(long a,long x,long b,long y)	{
		long y_x=modulusInverse(y%x,x);
		long x_y=modulusInverse(x%y,y);
		long prod=x*y;
		long m=(y*y_x)%prod;
		long n=(x*x_y)%prod;
		long result=(a*m+b*n)%prod;
		assert (result%x)==a;
		assert (result%y)==b;
		return result;
	}
	
	public static long expMod(long base,long exp,long mod)	{
		long current=base;
		long prod=1;
		while (exp>0)	{
			if ((exp%2)==1) prod=(prod*current)%mod;
			current=(current*current)%mod;
			exp/=2;
		}
		return prod;
	}
	
	public static BigInteger expMod(BigInteger base,long exp,BigInteger mod)	{
		BigInteger current=base;
		BigInteger prod=BigInteger.ONE;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(current).mod(mod);
			current=current.multiply(current).mod(mod);
			exp/=2;
		}
		return prod;
	}

	public static boolean nextPermutation(int[] arr)	{
		// Thanks, indy256 from http://codeforces.com/blog/entry/3980.
		for (int i=arr.length-2;i>=0;--i) if (arr[i]<arr[i+1]) for (int j=arr.length-1;;--j) if (arr[j]>arr[i])	{
			int sw=arr[i];
			arr[i]=arr[j];
			arr[j]=sw;
			for (++i,j=arr.length-1;i<j;++i,--j)	{
				sw=arr[i];
				arr[i]=arr[j];
				arr[j]=sw;
			}
			return true;
		}
		return false;
	}
	
	/*
	 * This is kind of brittle but it does the job. It will work as long as hasNext() is called exactly one between each call to
	 * next(). This is, fortunately, the standard way in which the : operator works.
	 */
	public static class DestructiveIntArrayPermutationGenerator implements Iterable<int[]>	{
		private static class InternalIterator implements Iterator<int[]>	{
			private final int[] holder;
			private boolean first;
			public InternalIterator(int[] holder)	{
				this.holder=holder;
				first=true;
			}
			@Override
			public boolean hasNext()	{
				if (first) return true;
				else return nextPermutation(holder);
			}
			@Override
			public int[] next()	{
				if (first) first=false;
				return holder;
			}
		}
		private final int[] holder;
		public DestructiveIntArrayPermutationGenerator(int[] holder)	{
			this.holder=holder;
		}
		@Override
		public Iterator<int[]> iterator()	{
			return new InternalIterator(holder);
		}
	}

	public static boolean nextPermutation(long[] arr)	{
		// Thanks, indy256 from http://codeforces.com/blog/entry/3980.
		for (int i=arr.length-2;i>=0;--i) if (arr[i]<arr[i+1]) for (int j=arr.length-1;;--j) if (arr[j]>arr[i])	{
			long sw=arr[i];
			arr[i]=arr[j];
			arr[j]=sw;
			for (++i,j=arr.length-1;i<j;++i,--j)	{
				sw=arr[i];
				arr[i]=arr[j];
				arr[j]=sw;
			}
			return true;
		}
		return false;
	}
	
	public static class IntPermutation	{
		private final int[] numbers;
		public IntPermutation(int[] numbers)	{
			int N=numbers.length;
			this.numbers=new int[N];
			System.arraycopy(numbers,0,this.numbers,0,N);
		}
		// Unsafe... but FAST!!!!!
		public int[] getNumbers()	{
			return numbers;
		}
		@Override
		public String toString()	{
			return Arrays.toString(numbers);
		}
	}
	
	public static class LongPermutation	{
		private final long[] numbers;
		public LongPermutation(long[] numbers)	{
			int N=numbers.length;
			this.numbers=new long[N];
			System.arraycopy(numbers,0,this.numbers,0,N);
		}
		public long[] getNumbers()	{
			return numbers;
		}
		@Override
		public String toString()	{
			return Arrays.toString(numbers);
		}
	}
	
	public static class IntPermutationGenerator implements Iterable<IntPermutation>	{
		private static class ActualGenerator implements Iterator<IntPermutation>	{
			public int[] numbers;
			public boolean hasMore;
			public ActualGenerator(int N)	{
				numbers=new int[N];
				for (int i=0;i<N;++i) numbers[i]=i;
				hasMore=(N>0);
			}
			@Override
			public boolean hasNext() {
				return hasMore;
			}

			@Override
			public IntPermutation next() {
				IntPermutation result=new IntPermutation(numbers);
				hasMore=nextPermutation(numbers);
				return result;
			}
		}
		private final int N;
		public IntPermutationGenerator(int N)	{
			this.N=N;
		}
		@Override
		public Iterator<IntPermutation> iterator() {
			return new ActualGenerator(N);
		}
	}
	
	public static class LongPermutationGenerator implements Iterable<LongPermutation>	{
		private static class ActualGenerator implements Iterator<LongPermutation>	{
			public long[] numbers;
			public boolean hasMore;
			public ActualGenerator(int N)	{
				numbers=new long[N];
				for (int i=0;i<N;++i) numbers[i]=i;
				hasMore=(N>0);
			}
			@Override
			public boolean hasNext() {
				return hasMore;
			}

			@Override
			public LongPermutation next() {
				LongPermutation result=new LongPermutation(numbers);
				hasMore=nextPermutation(numbers);
				return result;
			}
		}
		private final int N;
		public LongPermutationGenerator(int N)	{
			this.N=N;
		}
		@Override
		public Iterator<LongPermutation> iterator() {
			return new ActualGenerator(N);
		}
	}
	
	public static class BooleanPermutationGenerator implements Iterable<boolean[]>	{
		private static class ActualGenerator implements Iterator<boolean[]>	{
			public int[] numbers;
			public boolean hasMore;
			public ActualGenerator(int size,int howMany)	{
				numbers=new int[size];
				/*
				 * Reversed! 1 is "false", 0 is "true". This is so the first permutation has all true at the beginning,
				 * and the last one has all true at the end. This is convenient for Euler280.
				 */
				for (int i=howMany;i<size;++i) numbers[i]=1;
				hasMore=howMany<size;
			}
			@Override
			public boolean hasNext()	{
				return hasMore;
			}
			@Override
			public boolean[] next()	{
				boolean[] result=translate(numbers);
				hasMore=nextPermutation(numbers);
				return result;
			}
			private static boolean[] translate(int[] numbers)	{
				boolean[] result=new boolean[numbers.length];
				for (int i=0;i<result.length;++i) result[i]=(numbers[i]==0);
				return result;
			}
		}
		private final int size;
		private final int howMany;
		public BooleanPermutationGenerator(int size,int howMany)	{
			this.size=size;
			this.howMany=howMany;
		}
		@Override
		public Iterator<boolean[]> iterator()	{
			return new ActualGenerator(size,howMany);
		}
	}
	
	public static <T> List<Set<T>> getExponentialSet(Set<T> base,final boolean ordered)	{
		Function<Set<T>,Set<T>> setGenerator=new Function<Set<T>,Set<T>>()	{
			@Override
			public Set<T> apply(Set<T> in)	{
				return ordered?(new TreeSet<>(in)):(new HashSet<>(in));
			}
		};
		List<Set<T>> currentList=new ArrayList<>(1);
		currentList.add(Collections.emptySet());
		for (T element:base)	{
			List<Set<T>> newList=new ArrayList<>(2*currentList.size());
			for (Set<T> oldSet:currentList)	{
				Set<T> set1=setGenerator.apply(oldSet);
				Set<T> set2=setGenerator.apply(oldSet);
				set2.add(element);
				newList.add(set1);
				newList.add(set2);
			}
			currentList=newList;
		}
		return currentList;
	}

	public static class FactorialCache	{
		private List<Long> cache;
		public FactorialCache(int precalculation)	{
			cache=new ArrayList<>();
			cache.add(1l);
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			long last=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				last*=i;
				cache.add(last);
			}
		}
		public long get(int index)	{
			if (index>=cache.size()) addToCache(cache.size(),index);
			return cache.get(index);
		}
	}
	
	public static class FactorialCacheMod	{
		private List<Long> cache;
		private final long mod;
		public FactorialCacheMod(int precalculation,long mod)	{
			this.mod=mod;
			cache=new ArrayList<>();
			cache.add(1l);
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			long last=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				last*=i;
				last%=mod;
				cache.add(last);
			}
		}
		public long get(int index)	{
			if (index>=cache.size()) addToCache(cache.size(),index);
			return cache.get(index);
		}
	}
	
	public static class CombinatorialNumberCache	{
		private List<long[]> cache;
		public CombinatorialNumberCache(int precalculation)	{
			cache=new ArrayList<>();
			cache.add(new long[] {1l});
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			long[] lastRow=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				long[] nextRow=new long[lastRow.length+1];
				nextRow[0]=1l;
				for (int j=1;j<i;++j) nextRow[j]=lastRow[j]+lastRow[j-1];
				nextRow[i]=1l;
				cache.add(nextRow);
				lastRow=nextRow;
			}
		}
		public long get(int n,int k)	{
			if (k>n) return 0l;
			if (n>=cache.size()) addToCache(cache.size(),n);
			return cache.get(n)[k];
		}
	}
	
	public static class CombinatorialNumberModCache	{
		private final List<long[]> cache;
		private final long mod;
		public CombinatorialNumberModCache(int precalculation,long mod)	{
			this.mod=mod;
			cache=new ArrayList<>();
			cache.add(new long[]{1l});
			addToCache(1,precalculation);
		}
		private void addToCache(int start,int end)	{
			long[] lastRow=cache.get(cache.size()-1);
			for (int i=start;i<=end;++i)	{
				long[] nextRow=new long[lastRow.length+1];
				nextRow[0]=1l;
				for (int j=1;j<i;++j) nextRow[j]=(lastRow[j]+lastRow[j-1])%mod;
				nextRow[i]=1l;
				cache.add(nextRow);
				lastRow=nextRow;
			}
		}
		public long get(int n,int k)	{
			if (k>n) return 0l;
			if (n>=cache.size()) addToCache(cache.size(),n);
			return cache.get(n)[k];
		}
	}
	
	public static void increaseCounter(IntIntMap map,int key)	{
		increaseCounter(map,key,1);
	}
	
	public static void increaseCounter(IntIntMap map,int key,int tally)	{
		map.compute(key,(int unused,int oldValue)->oldValue+tally);
	}
	
	public static void increaseCounter(LongIntMap map,long key)	{
		increaseCounter(map,key,1);
	}
	
	public static void increaseCounter(LongIntMap map,long key,int tally)	{
		map.compute(key,(long unused,int oldValue)->oldValue+tally);
	}
	
	public static <K> void increaseCounter(Map<K,Integer> map,K key)	{
		Integer counter=map.get(key);
		int newValue=1+((counter==null)?0:counter.intValue());
		map.put(key,newValue);
	}
	public static <K> void increaseCounter(Map<K,Integer> map,K key,int tally)	{
		Integer counter=map.get(key);
		int newValue=tally+((counter==null)?0:counter.intValue());
		map.put(key,newValue);
	}
	public static <K> void increaseCounter(Map<K,Long> map,K key,long tally)	{
		Long counter=map.get(key);
		long newValue=tally+((counter==null)?0:counter.longValue());
		map.put(key,newValue);
	}
	public static <K> void increaseCounter(Map<K,Long> map,K key,long tally,long mod)	{
		Long counter=map.get(key);
		long newValue=tally+((counter==null)?0:counter.longValue());
		map.put(key,newValue%mod);
	}
	public static <K> void increaseCounter(Map<K,Integer> map,Map<K,Integer> toAdd)	{
		for (Map.Entry<K,Integer> entry:toAdd.entrySet()) EulerUtils.increaseCounter(map,entry.getKey(),entry.getValue());
	}
	public static <K> void increaseCounter(Map<K,BigInteger> map,K key,BigInteger tally)	{
		BigInteger counter=map.getOrDefault(key,BigInteger.ZERO);
		map.put(key,counter.add(tally));
	}
	public static <K> void increaseCounter(Map<K,BigInteger> map,K key,BigInteger tally,BigInteger mod)	{
		BigInteger counter=map.getOrDefault(key,BigInteger.ZERO);
		map.put(key,counter.add(tally).mod(mod));
	}
	public static <K> void decreaseCounter(Map<K,Integer> map,K key)	{
		Integer counter=map.get(key);
		if (counter==null) throw new IllegalStateException("Trying to decrease a non-existing counter.");
		int newValue=counter.intValue()-1;
		if (newValue==0) map.remove(key);
		else map.put(key,newValue);
	}
	public static <K> void decreaseCounter(Map<K,Integer> map,K key,int tally)	{
		Integer counter=map.get(key);
		if (counter==null) throw new IllegalStateException("Trying to decrease a non-existing counter.");
		int newValue=counter.intValue()-tally;
		if (newValue==0) map.remove(key);
		else map.put(key,newValue);
	}
	public static long getTotient(Map<Long,Integer> divs)	{
		long result=1;
		for (Map.Entry<Long,Integer> entry:divs.entrySet())	{
			long prime=entry.getKey();
			result*=prime-1;
			int power=entry.getValue();
			if (power>1) result*=LongMath.pow(prime,power-1);
		}
		return result;
	}
	public static long getTotient(Map<Long,Integer> divs,long mod)	{
		long result=1;
		for (Map.Entry<Long,Integer> entry:divs.entrySet())	{
			long prime=entry.getKey();
			int power=entry.getValue();
			result=(result*(prime-1))%mod;
			if (power>1)	{
				long newFactor=expMod(prime,power-1,mod);
				result=(result*newFactor)%mod;
			}
		}
		return result;
	}
	public static BigInteger getSumOfTotientsUpTo(long n)	{
		BigSumOfTotientCalculator calc=new BigSumOfTotientCalculator();
		return calc.getTotientSum(n);
	}
	public static <T> void trimToSize(NavigableSet<T> set,int desiredSize)	{
		// This is horrible but it does the job. Java, you stupid, stupid language.
		int toRemove=set.size()-desiredSize;
		for (int i=0;i<toRemove;++i) set.pollLast();
	}
	public static <T extends Comparable<T>> T max(T a,T b)	{
		return (a.compareTo(b)>0)?a:b;
	}
	// In a fair world, this wouldn't be necessary.
	public static class Pair<T,U>	{
		public T first;
		public U second;
		public Pair(T first,U second)	{
			this.first=first;
			this.second=second;
		}
	}

	public static class FastGrowingHierarchyCalculator	{
		private final long mod;
		public FastGrowingHierarchyCalculator(long mod)	{
			this.mod=mod;
		}
		public long get(long n,int k)	{
			return get(n,k,1);
		}
		public long get(long n,int k,long p)	{
			if (p>1) return get(get(n,k,p-1),k,1);
			else if (k==0) return (n+1)%mod;
			else if (k==1) return (n*2)%mod;
			else if (k==2) return (n*expMod(2l,n,mod))%mod;
			else return get(n,k-1,n);
		}
		public long getGoldsteinLength(long n)	{
			boolean[] base2=translateToBase2(n);
			long result=3;
			for (int i=0;i<base2.length;++i) if (base2[i]) result=get(result,i);
			return (result+mod-3)%mod;	// "(result-3)%mod" won't always work! What if the result is 0, 1 or 2? Won't anybody think of the children?
		}
		private static boolean[] translateToBase2(long in)	{
			int howManyBits=LongMath.log2(in+1,RoundingMode.UP);
			boolean[] result=new boolean[howManyBits];
			int i=0;
			while (in>0)	{
				if ((in%2)==1) result[i]=true;
				in/=2;
				++i;
			}
			return result;
		}
	}

	public static NavigableMap<Long,Integer> getFactorialFactors(int fact)	{
		long[] firstPrimes=Primes.firstPrimeSieve((long)fact);
		NavigableMap<Long,Integer> result=new TreeMap<>();
		for (int i=2;i<=fact;++i)	{
			int n=i;
			while (firstPrimes[n]!=0)	{
				long factor=firstPrimes[n];
				increaseCounter(result,factor);
				n/=(int)factor;
			}
			if (n>1) increaseCounter(result,(long)n);
		}
		return result;
	}
	
	public static long factorialMod(long n,long mod)	{
		long result=1;
		for (long i=2;i<=n;++i) result=(result*i)%mod;
		return result;
	}
	
	public static <T> void removeCarefully(Multiset<T> multiset,T element)	{
		int times=multiset.count(element);
		multiset.remove(element);
		if (times>1) multiset.add(element,times-1);
	}
	
	public static <T> void removeCarefully(Multiset<T> multiset,Collection<T> elements)	{
		for (T element:elements) removeCarefully(multiset,element);
	}

	public static long primeAppearancesInFactorial(long prime,long factorial)	{
		long res=0;
		while (factorial>=prime)	{
			factorial/=prime;
			res+=factorial;
		}
		return res;
	}

	public static int primeAppearancesInFactorial(int prime,int factorial)	{
		int res=0;
		while (factorial>=prime)	{
			factorial/=prime;
			res+=factorial;
		}
		return res;
	}

	public static NavigableMap<Integer,Integer> getPrimeFactorsInFactorial(int factOperand)	{
		NavigableMap<Integer,Integer> result=new TreeMap<>();
		for (int prime:Primes.listIntPrimes(factOperand)) result.put(prime,primeAppearancesInFactorial(prime,factOperand));
		return result;
	}
	
	// This class can be used for any kind of search where, if X is not an acceptable number, then any multiple of X can't be accepted either.
	// See problem 545.
	public static abstract class MultiplicativeSieve	{
		private final boolean[] sieved;
		public MultiplicativeSieve(int N)	{
			sieved=new boolean[1+N];
		}
		protected abstract boolean test(int in);
		public boolean trySieve(int in)	{
			boolean result=test(in);
			if (result) for (int i=in;i<sieved.length;i+=in) sieved[in]=true;
			return result;
		}
		public void fullSieve()	{
			for (int i=2;i<sieved.length;++i) if (!sieved[i]) trySieve(i);
		}
		public boolean[] getSieved()	{
			return sieved;
		}
		public int getNthUnsieved(int n)	{
			int counter=0;
			for (int i=2;i<sieved.length;++i) if (!sieved[i])	{
				boolean result=trySieve(i);
				if (!result)	{
					++counter;
					if (counter>=n) return i;
				}
			}
			throw new RuntimeException("Not enough unsieved numbers in the given interval. Only "+counter+" have been found.");
		}
	}

	public static BigInteger exp(BigInteger base,long exp)	{
		BigInteger current=base;
		BigInteger prod=BigInteger.ONE;
		while (exp>0)	{
			if ((exp%2)==1) prod=prod.multiply(current);
			current=current.multiply(current);
			exp/=2;
		}
		return prod;
	}

	private static boolean isQuadraticResidue(long n,long prime)	{
		return EulerUtils.expMod(n,(prime-1)/2,prime)==1l;
	}
	
	private static long findFirstNonResidue(long prime)	{
		long exp=(prime-1)/2;
		for (long q=2;;++q) if (EulerUtils.expMod(q,exp,prime)!=1) return q;
	}
	
	public static LongSet squareRootModuloPrime(long n,long prime)	{
		// https://en.wikipedia.org/wiki/Tonelli%E2%80%93Shanks_algorithm
		// Assumes that the prime is odd!
		if (n==0) return HashLongSets.newImmutableSetOf(0l);	// Yes! this happens. For example for 13 we need this case.
		LongSet result=HashLongSets.newMutableSet();
		// There are solutions if an only if n^((prime-1)/2)==1 mod prime.
		if (!isQuadraticResidue(n,prime)) return result;
		long q=prime-1;
		int s=0;
		while ((q%2)==0)	{
			q/=2;
			++s;
		}
		long z=findFirstNonResidue(prime);
		int m=s;
		long c=EulerUtils.expMod(z,q,prime);
		long t=EulerUtils.expMod(n,q,prime);
		long r=EulerUtils.expMod(n,(q+1)/2,prime);
		for (;;)	{
			if (t==1l)	{
				result.add(r);
				result.add(prime-r);
				return result;
			}
			int i=1;
			long t2i=t;
			for (;i<m;++i)	{
				t2i=(t2i*t2i)%prime;
				if (t2i==1l) break;
			}
			if (i>=m) return result;	// No solutions!
			long b=c;
			for (int j=0;j<m-i-1;++j) b=(b*b)%prime;
			m=i;
			c=(b*b)%prime;
			t=(t*c)%prime;
			r=(r*b)%prime;
		}
	}
	public static IntIntMap getDigitDistribution(int n)	{
		IntIntMap result=HashIntIntMaps.newMutableMap();
		while (n>0)	{
			int d=n%10;
			n/=10;
			if (d>0) EulerUtils.increaseCounter(result,d);
		}
		return result;
	}

	public static int minRepunitDivisible(int n)	{
		int count=1;
		int mod=1;
		for (;;)	{
			mod%=n;
			if (mod==0) return count;
			++count;
			mod=10*mod+1;
		}
	}
}
