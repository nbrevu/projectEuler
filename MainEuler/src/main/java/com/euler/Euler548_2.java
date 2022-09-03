package com.euler;

import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler548_2 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static int PRIME_LIMIT=100;
	private final static List<Integer> PRIMES=Primes.listIntPrimes(PRIME_LIMIT);
	
	private static class PrimePowers	{
		public final int[] exponents;
		public final long representative;
		private PrimePowers(int[] exponents,long representative)	{
			this.exponents=exponents;
			this.representative=representative;
		}
		public static PrimePowers getInitial()	{
			return new PrimePowers(new int[] {},1l);
		}
		@Override
		public boolean equals(Object other)	{
			PrimePowers ppOther=(PrimePowers)other;
			return exponents.equals(ppOther.exponents);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(exponents);
		}
		@Override
		public String toString()	{
			return Arrays.toString(exponents);
		}
		private Deque<PrimePowers> getChildren(long limit)	{
			Deque<PrimePowers> result=new ArrayDeque<>();
			if ((exponents.length==1)||((exponents.length>=2)&&(exponents[exponents.length-1]<exponents[exponents.length-2])))	{
				long newRep=representative*PRIMES.get(exponents.length-1);
				if (newRep<=limit)	{
					int[] newArray=Arrays.copyOf(exponents,exponents.length);
					++newArray[newArray.length-1];
					result.add(new PrimePowers(newArray,newRep));
				}
			}
			long newRep2=representative*PRIMES.get(exponents.length);
			if (newRep2<=limit)	{
				int[] newArray=new int[exponents.length+1];
				System.arraycopy(exponents,0,newArray,0,exponents.length);
				newArray[exponents.length]=1;
				result.add(new PrimePowers(newArray,newRep2));
			}
			return result;
		}
		public NavigableMap<Long,PrimePowers> getAllDescendants(long limit)	{
			NavigableMap<Long,PrimePowers> result=new TreeMap<>();
			result.put(representative,this);
			Deque<PrimePowers> pending=getChildren(limit);
			while (!pending.isEmpty())	{
				PrimePowers pp=pending.poll();
				result.put(pp.representative,pp);
				pending.addAll(pp.getChildren(limit));
			}
			return result;
		}
		public boolean match(int[] array)	{
			if (exponents.length!=array.length) return false;
			for (int i=0;i<array.length;++i) if (exponents[i]!=array[i]) return false;
			return true;
		}
	}
	
	private static class IntArray	{
		public final int[] array;
		public IntArray(int[] array)	{
			this.array=array;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			if (!(other instanceof IntArray)) return false;
			IntArray iaOther=(IntArray)other;
			return Arrays.equals(array,iaOther.array);
		}
		public int[] getNormalized()	{
			List<Integer> list=new ArrayList<>();
			for (int n:array) if (n>0) list.add(n);
			list.sort(Collections.reverseOrder());
			int[] result=new int[list.size()];
			for (int i=0;i<result.length;++i) result[i]=list.get(i);
			return result;
		}
	}
	
	private static int getDivisors(int[] array)	{
		int descendants=1;
		for (int exp:array) descendants*=exp+1;
		--descendants;
		return descendants;
	}
	
	private static void getDescendantsRecursive(int[] array,List<IntArray> result,int index)	{
		if (index<array.length-1) getDescendantsRecursive(array,result,1+index);
		for (int value=array[index]-1;value>=0;--value)	{
			int[] newArray=Arrays.copyOf(array,array.length);
			newArray[index]=value;
			result.add(new IntArray(newArray));
			if (index<array.length-1) getDescendantsRecursive(newArray,result,1+index);
		}
	}
	
	private static List<IntArray> getDescendants(int[] array)	{
		List<IntArray> result=new ArrayList<>(getDivisors(array));
		getDescendantsRecursive(array,result,0);
		return result;
	}
	
	private static long expand(int[] exponents)	{
		long result=1l;
		for (int i=0;i<exponents.length;++i) result*=LongMath.pow(PRIMES.get(i),exponents[i]);
		return result;
	}
	
	private static LongLongMap calculateGozintas(NavigableMap<Long,PrimePowers> allCases,long limit)	{
		LongLongMap result=HashLongLongMaps.newMutableMap();
		ObjLongMap<IntArray> representativeCache=HashObjLongMaps.newMutableMap();
		representativeCache.put(new IntArray(new int[] {}),1l);
		for (Map.Entry<Long,PrimePowers> entry:allCases.entrySet())	{
			long rep=entry.getKey();
			int[] baseArray=entry.getValue().exponents;
			if (baseArray.length==0) result.put(rep,1l);
			else	{
				long gozinta=0l;
				for (IntArray descendant:getDescendants(baseArray))	{
					long representative=representativeCache.computeIfAbsent(descendant,(IntArray toCompute)->	{
						int[] normalized=toCompute.getNormalized();
						return expand(normalized);
					});
					long repGozinta=result.get(representative);
					if (repGozinta==-1)	{
						gozinta=-1;
						break;
					}	else	{
						gozinta+=repGozinta;
						if (gozinta>=limit)	{
							gozinta=-1;
							break;
						}
					}
				}
				result.put(rep,gozinta);
			}
		}
		return result;
	}
	
	private static int[] getPrimePowers(long in,List<Long> primes)	{
		List<Integer> factors=new ArrayList<>();
		for (long prime:primes) if ((prime*prime)>in) break;
		else if ((in%prime)==0l)	{
			int exp=1;
			in/=prime;
			while ((in%prime)==0l)	{
				++exp;
				in/=prime;
			}
			factors.add(exp);
		}
		if (in>1) factors.add(1);
		factors.sort(Collections.reverseOrder());
		int[] result=new int[factors.size()];
		for (int i=0;i<result.length;++i) result[i]=factors.get(i);
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PrimePowers base=PrimePowers.getInitial();
		NavigableMap<Long,PrimePowers> allCases=base.getAllDescendants(LIMIT);
		LongLongMap gozintas=calculateGozintas(allCases,LIMIT);
		long sum=0l;
		LongLongCursor cursor=gozintas.cursor();
		List<Long> primes=Primes.listLongPrimes(LongMath.sqrt(LIMIT,RoundingMode.UP));
		while (cursor.moveNext())	{
			long gozinta=cursor.value();
			if (gozinta==-1l) continue;
			long rep=cursor.key();
			PrimePowers pow=allCases.get(rep);
			int[] gozintaPrimePowers=getPrimePowers(gozinta,primes);
			if (pow.match(gozintaPrimePowers)) sum+=gozinta;
		}
		long tac=System.nanoTime();
		System.out.println(sum);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
