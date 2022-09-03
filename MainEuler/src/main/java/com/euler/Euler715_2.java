package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.euler.common.CombinationIterator;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.LongSet;

public class Euler715_2 {
	private final static long[] FACTORIALS=new long[] {1,1,2,6,24,120,720};
	
	private static long getCombinations(int n)	{
		long result=0;
		for (int[] combination:new CombinationIterator(6,n,true))	{
			int sumSqs=0;
			for (int i:combination) sumSqs+=i*i;
			if (EulerUtils.areCoprime(n,sumSqs))	{
				int lastNumber=-1;
				int currentCounter=0;
				long multiplier=720;
				for (int i:combination) if (i==lastNumber) ++currentCounter;
				else	{
					multiplier/=FACTORIALS[currentCounter];
					lastNumber=i;
					currentCounter=1;
				}
				multiplier/=FACTORIALS[currentCounter];
				result+=multiplier;
			}
		}
		return result;
	}
	
	private static class CubeDecompositionElement implements Comparable<CubeDecompositionElement>	{
		public final long value;
		public final boolean isSubtraction;
		public CubeDecompositionElement(long value,boolean isSubtraction)	{
			this.value=value;
			this.isSubtraction=isSubtraction;
		}
		@Override
		public int compareTo(CubeDecompositionElement other)	{
			int result=Long.compare(other.value,value);	// Reverse order!
			return (result==0)?Boolean.compare(isSubtraction,other.isSubtraction):result;
		}
	}
	
	private static class CubeDecomposer	{
		private final int[] firstPrimes;
		private final long[] cubes;
		private final IntLongMap gs;
		private final IntObjMap<List<CubeDecompositionElement>> standardDecompositions;
		public CubeDecomposer(int n)	{
			firstPrimes=Primes.lastPrimeSieve(n);
			cubes=new long[1+n];
			for (long i=1;i<=n;++i) cubes[(int)i]=i*i*i;
			gs=HashIntLongMaps.newMutableMap();
			standardDecompositions=HashIntObjMaps.newMutableMap();
		}
		public String getCasesWithAddedCube(int x,boolean isSubtraction)	{
			int cube=x*x*x;
			IntStream.Builder builder=IntStream.builder();
			for (int i=x;i<cubes.length;i+=x) for (CubeDecompositionElement elem:standardDecompositions.get(i)) if (elem.value==cube)	{
				if (elem.isSubtraction==isSubtraction) builder.accept(i);
				break;
			}
			return Arrays.toString(builder.build().toArray());
		}
		public long getDenominator(int n)	{
			return n*n*DivisorHolder.getFromFirstPrimes(n,firstPrimes).getTotient();
		}
		public List<List<CubeDecompositionElement>> findAllDecompositions(long sum,int n)	{
			List<List<CubeDecompositionElement>> decomps=new ArrayList<>();
			LongSet allDivisors=DivisorHolder.getFromFirstPrimes(n,firstPrimes).getUnsortedListOfDivisors();
			long[] cubesToCount=allDivisors.stream().mapToLong((Long x)->cubes[x.intValue()]).toArray();
			decomposeAllRecursive(sum,cubesToCount,0,new ArrayList<>(),decomps);
			return decomps;
		}
		private void decomposeAllRecursive(long remainingElements,long[] cubesToCount,int index,List<CubeDecompositionElement> currentList,List<List<CubeDecompositionElement>> result)	{
			if (index>=cubesToCount.length) return;
			// First case: don't include this cube.
			decomposeAllRecursive(remainingElements,cubesToCount,1+index,currentList,result);
			long cube=cubesToCount[index];
			// Second case: add.
			{
				CubeDecompositionElement added=new CubeDecompositionElement(cube,false);
				currentList.add(added);
				if (remainingElements==cube) result.add(new ArrayList<>(currentList));
				decomposeAllRecursive(remainingElements-cube,cubesToCount,1+index,currentList,result);
				currentList.remove(currentList.size()-1);
			}
			// Third case: subtract.
			{
				CubeDecompositionElement subtracted=new CubeDecompositionElement(cube,true);
				currentList.add(subtracted);
				if (remainingElements==-cube) result.add(new ArrayList<>(currentList));
				decomposeAllRecursive(remainingElements+cube,cubesToCount,1+index,currentList,result);
				currentList.remove(currentList.size()-1);
			}
		}
		public String storeGAndGetDecomp(long g,int n)	{
			gs.put(n,g);
			List<CubeDecompositionElement> decomposition=new ArrayList<>();
			if (((n%2)==0)&&(g==(8*gs.get(n/2)))) for (CubeDecompositionElement element:standardDecompositions.get(n/2)) decomposition.add(new CubeDecompositionElement(element.value*8,element.isSubtraction));
			else	{
				LongSet allDivisors=DivisorHolder.getFromFirstPrimes(n,firstPrimes).getUnsortedListOfDivisors();
				long[] cubesToCount=allDivisors.stream().mapToLong((Long x)->cubes[x.intValue()]).toArray();
				if (!decomposeRecursive(g,cubesToCount,0,decomposition)) throw new RuntimeException(":O");
				decomposition.sort(null);
			}
			standardDecompositions.put(n,decomposition);
			StringBuilder numbersSb=new StringBuilder();
			StringBuilder cubesSb=new StringBuilder();
			boolean isFirst=true;
			for (CubeDecompositionElement elem:decomposition)	{
				long value=(elem.isSubtraction)?(-elem.value):elem.value;
				if ((!isFirst)&&(value>0))	{
					numbersSb.append('+');
					cubesSb.append('+');
				}
				numbersSb.append(value);
				int root=(int)Math.round(Math.cbrt(value));
				cubesSb.append(root).append("^3");
				isFirst=false;
			}
			numbersSb.append(" = ").append(cubesSb.toString());
			return numbersSb.toString();
		}
		private boolean decomposeRecursive(long remainingElements,long[] cubesToCount,int index,List<CubeDecompositionElement> result)	{
			if (index>=cubesToCount.length) return false;
			// First case: don't include this cube.
			if (decomposeRecursive(remainingElements,cubesToCount,1+index,result)) return true;
			long cube=cubesToCount[index];
			// Second case: add.
			{
				CubeDecompositionElement added=new CubeDecompositionElement(cube,false);
				result.add(added);
				if (remainingElements==cube) return true;
				else if (decomposeRecursive(remainingElements-cube,cubesToCount,1+index,result)) return true;
				result.remove(result.size()-1);
			}
			// Third case: subtract.
			{
				CubeDecompositionElement subtracted=new CubeDecompositionElement(cube,true);
				result.add(subtracted);
				if (remainingElements==-cube) return true;
				else if (decomposeRecursive(remainingElements+cube,cubesToCount,1+index,result)) return true;
				result.remove(result.size()-1);
			}
			return false;
		}
	}
	
	public static void main(String[] args)	{
		int maxN=100;
		CubeDecomposer decomposer=new CubeDecomposer(maxN);
		long sumG=0;
		for (int n=1;n<=maxN;++n)	{
			long counters=getCombinations(n);
			long denom=decomposer.getDenominator(n);
			if (counters%denom!=0) throw new RuntimeException("¿Pero por qué D:?");
			long g=counters/denom;
			System.out.println(String.format("g(%d) = %d = %s.",n,g,decomposer.storeGAndGetDecomp(g,n)));
			List<List<CubeDecompositionElement>> allDecomps=decomposer.findAllDecompositions(g,n);
			if (allDecomps.size()!=1) System.out.println("\tAy mecachis. Para el "+n+" tengo "+allDecomps.size()+" descomposiciones.");
			sumG+=g;
		}
		System.out.println("Total: G("+maxN+")="+sumG+".");
		for (int n=1;n<=maxN;++n)	{
			System.out.println("+"+n+"^3:");
			System.out.println("\t"+decomposer.getCasesWithAddedCube(n,false));
			System.out.println("-"+n+"^3:");
			System.out.println("\t"+decomposer.getCasesWithAddedCube(n,true));
		}
	}
}
