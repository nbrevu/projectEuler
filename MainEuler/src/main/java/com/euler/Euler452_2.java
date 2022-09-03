package com.euler;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class Euler452_2 {
	private final static int N=1000000000;
	private final static long MOD=1234567891l;
	private static class FactorDecomposition	{
		private List<Integer> primePowers;
		public <T> FactorDecomposition(Map<T,Integer> primeDecomposition)	{
			this(primeDecomposition.values());
		}
		public FactorDecomposition(Collection<Integer> primePowers)	{
			this.primePowers=orderDescending(primePowers);
		}
		private List<Integer> orderDescending(Collection<Integer> list)	{
			List<Integer> result=new ArrayList<>(list);
			result.sort(Collections.reverseOrder());
			return result;
		}
		@Override
		public int hashCode()	{
			return primePowers.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				FactorDecomposition pdOther=(FactorDecomposition)other;
				return primePowers.equals(pdOther.primePowers);
			}	catch (ClassCastException cce)	{
				return false;
			}
		}
		@Override
		public String toString()	{
			return primePowers.toString();
		}
		// This shameless exposure shouldn't happen, but eh.
		public List<Integer> getPowers()	{
			return primePowers;
		}
	}
	
	private static class MultiplicativePartition	{
		// Map from counter to amount: if the key is X and the value is Y, then this partition
		// contains "Y" groups of "X" elements.
		// For example, a partition of the form 2*2*2 * 3 * 5 * 6*6 would have 1->2; 2->1; 3->1.
		private SortedMap<Integer,Integer> repetitions;
		public MultiplicativePartition(SortedMap<Integer,Integer> repetitions)	{
			this.repetitions=repetitions;
		}
		@Override
		public int hashCode()	{
			return repetitions.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				MultiplicativePartition mpOther=(MultiplicativePartition)other;
				return repetitions.equals(mpOther.repetitions);
			}	catch (ClassCastException cce)	{
				return false;
			}
		}
	}
	
	// ZUTUN! Die neue Ahnung!
	// Almacenamos sólo las particiones para potencias puras (2^29 es lo máximo)
	// y luego las combinamos como veamos factible.
	// Paso 1) Generar combinaciones para p^n para cada n entre 1 y 29.
	// Paso 2) Combinar las combinaciones según toque.
		// Esto es particularmente chungo porque entran en juego las repeticiones.
	private static class MultiplicativePartitionList	{
		// For each partition scheme, stores how many times it appears.
		// These classes ARE getting more and more abstract and complicated. Sorry.
		private Map<MultiplicativePartition,Long> partitionCounter;
		public MultiplicativePartitionList()	{
			partitionCounter=Collections.emptyMap();
		}
		public MultiplicativePartitionList(Map<MultiplicativePartition,Long> partitionCounter)	{
			this.partitionCounter=partitionCounter;
		}
		public static MultiplicativePartitionList combine(MultiplicativePartitionList mpl1,MultiplicativePartitionList mpl2)	{
			if (mpl1.partitionCounter.isEmpty()) return mpl2;
			else if (mpl2.partitionCounter.isEmpty()) return mpl1;
			// ZUTUN! TEHDÄ! TODO! THIS IS THE METHOD THAT WILL DISTINGUISH BETWEEN MEN AND MICE.
			// I'm not sure if I will be able to pull this :(.
			// "Do or do not. There is no try".
			throw new UnsupportedOperationException();
		}
		public void sumMultiplied(MultiplicativePartitionList other,int factor)	{
			for (Map.Entry<MultiplicativePartition,Long> entry:other.partitionCounter.entrySet()) EulerUtils.increaseCounter(partitionCounter,entry.getKey(),entry.getValue()*factor);
		}
	}
	
	private static class SinglePrimeMultiplicativePartitionHolder	{
		// This stores FULL partitions in the old format, but only returns partitions in the
		// new one. It has a cache. It shouldn't need to store more than 29 objects since
		// 2^30 > 10^9.
		private static class PartitionOldStyle	{
			// This stores powers. [1->2, 2->5, 3->1] stores the following partition:
			//	{p, p, p^2, p^2, p^2, p^2, p^2, p^2, p^3}.
			private SortedMap<Integer,Integer> powerMap;
			public PartitionOldStyle(SortedMap<Integer,Integer> powerMap)	{
				this.powerMap=powerMap;
			}
			@Override
			public int hashCode()	{
				return powerMap.hashCode();
			}
			@Override
			public boolean equals(Object other)	{
				try	{
					PartitionOldStyle posOther=(PartitionOldStyle)other;
					return powerMap.equals(posOther.powerMap);
				}	catch (ClassCastException cce)	{
					return false;
				}
			}
			public List<PartitionOldStyle> getChildren()	{
				// This gets the partitions that result when we add a factor.
				List<PartitionOldStyle> result=new ArrayList<>();
				for (Integer key:powerMap.keySet())	{
					SortedMap<Integer,Integer> newMap=new TreeMap<>(powerMap);
					EulerUtils.decreaseCounter(newMap,key);
					EulerUtils.increaseCounter(newMap,1+key);
					result.add(new PartitionOldStyle(newMap));
				}
				SortedMap<Integer,Integer> newMap=new TreeMap<>(powerMap);
				EulerUtils.increaseCounter(newMap,1);
				result.add(new PartitionOldStyle(newMap));
				return result;
			}
			public MultiplicativePartition getAsMultiplicativePartition()	{
				SortedMap<Integer,Integer> repetitionCounter=new TreeMap<>();
				for (Integer repeatedPower:powerMap.values()) EulerUtils.increaseCounter(repetitionCounter,repeatedPower);
				return new MultiplicativePartition(repetitionCounter);
			}
		}
		private SetMultimap<Integer,PartitionOldStyle> cache;
		private Map<Integer,MultiplicativePartitionList> results;
		public SinglePrimeMultiplicativePartitionHolder()	{
			cache=HashMultimap.create();
			SortedMap<Integer,Integer> trivialPartition=new TreeMap<>();
			trivialPartition.put(1,1);
			cache.put(1,new PartitionOldStyle(trivialPartition));
			results=new HashMap<>();
		}
		private Set<PartitionOldStyle> getFromCache(int key)	{
			if (!cache.containsKey(key))	{
				Set<PartitionOldStyle> previous=getFromCache(key-1);
				cache.putAll(key,getChildren(previous));
			}
			return cache.get(key);
		}
		private static Set<PartitionOldStyle> getChildren(Set<PartitionOldStyle> previous)	{
			Set<PartitionOldStyle> result=new HashSet<>();
			for (PartitionOldStyle parent:previous) result.addAll(parent.getChildren());
			return result;
		}
		public MultiplicativePartitionList getPartitionListForPower(int pow)	{
			if (!results.containsKey(pow))	{
				MultiplicativePartitionList partitionList=translate(getFromCache(pow));
				results.put(pow,partitionList);
			}
			return results.get(pow);
		}
		private MultiplicativePartitionList translate(Set<PartitionOldStyle> oldStylePartitions)	{
			Map<MultiplicativePartition,Long> map=new HashMap<>();
			for (PartitionOldStyle partition:oldStylePartitions) EulerUtils.increaseCounter(map,partition.getAsMultiplicativePartition(),1);
			return new MultiplicativePartitionList(map);
		}
	}
	
	private static class PrimeDecompositionGenerator	{
		private int firstPrimes[];
		public PrimeDecompositionGenerator(int max)	{
			System.out.println("Computing initial prime set...");
			firstPrimes=Primes.firstPrimeSieve(1+max);
		}
		public FactorDecomposition getDecomposition(int in)	{
			Map<Integer,Integer> factors=new HashMap<>();
			while (in>1)	{
				int firstPrime=firstPrimes[in];
				if (firstPrime==0)	{
					EulerUtils.increaseCounter(factors,in);
					break;
				}	else	{
					EulerUtils.increaseCounter(factors,firstPrime);
					in/=firstPrime;
				}
			}
			return new FactorDecomposition(factors);
		}
		private void clear()	{
			firstPrimes=new int[0];
		}
	}
	
	private static class FactorialCache	{
		private final Map<Integer,BigInteger> cache;
		public FactorialCache()	{
			cache=new HashMap<>();
			cache.put(0,BigInteger.ONE);
		}
		public BigInteger getFactorial(int k)	{
			BigInteger result=cache.get(k);
			if (result==null)	{
				result=BigInteger.valueOf(k).multiply(getFactorial(k-1));
				cache.put(k,result);
			}
			return result;
		}
	}
	
	private static class CombinatorialNumberCache	{
		// This stores N*(N-1)*...(N-k) for N fixed and k given.
		private final Map<Integer,BigInteger> cache;
		public CombinatorialNumberCache()	{
			cache=new HashMap<>();
			cache.put(0,BigInteger.ONE);
		}
		public BigInteger getProduct(int k)	{
			BigInteger result=cache.get(k);
			if (result==null)	{
				result=getProduct(k-1).multiply(BigInteger.valueOf(N+1-k));
				cache.put(k,result);
			}
			return result;
		}
	}
	
	private static Map<FactorDecomposition,Integer> getDecompositionCounter(int n)	{
		PrimeDecompositionGenerator gen=new PrimeDecompositionGenerator(n);
		System.out.println("Generating prime decompositions...");
		Map<FactorDecomposition,Integer> result=new HashMap<>();
		for (int i=1;i<=n;++i) EulerUtils.increaseCounter(result,gen.getDecomposition(i));
		gen.clear();
		return result;
	}
	
	private static MultiplicativePartitionList getPartitionListFromPrimeDecomposition(FactorDecomposition fd,SinglePrimeMultiplicativePartitionHolder cache)	{
		/*
		 *  Yeah, some temporary results MIGHT be cached as well...
		 *  Like, if we are going to calculate [4,3,2,1] and [4,3,1], the [4,3] part is common.
		 *  But I'm aiming for MEMORY reduction, not time reduction.
		 *  This could take a LOT of time, depending on how well I manage to code the monster method...
		 */
		MultiplicativePartitionList result=new MultiplicativePartitionList();
		for (int primePower:fd.getPowers()) result=MultiplicativePartitionList.combine(result,cache.getPartitionListForPower(primePower));
		return result;
	}
	
	public static void main(String[] args)	{
		try	{
			PrintStream ps=new PrintStream("C:\\out452_3.txt");
			System.setOut(ps);
		}	catch (Exception exc)	{
			System.out.println("D'oh!");
		}
		// First we create an object which counts how many times does each distinct prime
		// decomposition appear.
		Map<FactorDecomposition,Integer> decompCounter=getDecompositionCounter(N);
		// Now, for each distinct decomposition, we get the list of partitions, which are
		// aggregated.
		SinglePrimeMultiplicativePartitionHolder cache=new SinglePrimeMultiplicativePartitionHolder();
		MultiplicativePartitionList aggregated=new MultiplicativePartitionList();
		for (Map.Entry<FactorDecomposition,Integer> decomposition:decompCounter.entrySet())	{
			MultiplicativePartitionList allPartitions=getPartitionListFromPrimeDecomposition(decomposition.getKey(),cache);
			aggregated.sumMultiplied(allPartitions,decomposition.getValue());
		}
		// FINALLY we can sum.
		
		// ZUTUN! The commented code is not right but it's close.
		/*
		BigInteger sum=BigInteger.ZERO;
		for (Map.Entry<FactorDecomposition,Long> entry:partitionCounter.entrySet())	{
			BigInteger multiplier=BigInteger.valueOf(entry.getValue());
			FactorDecomposition decomp=entry.getKey();
			BigInteger augend=combinatorials.getProduct(decomp.totalSize());
			for (int pow:decomp.getPowers()) augend=augend.divide(factorials.getFactorial(pow));
			augend=augend.multiply(multiplier);
			sum=sum.add(augend);
		}
		sum=sum.mod(BigInteger.valueOf(MOD));
		System.out.println(sum);
		*/
	}
}
