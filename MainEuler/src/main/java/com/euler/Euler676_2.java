package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.IntFunction;

import com.euler.common.EulerUtils;
import com.google.common.collect.Range;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler676_2 {
	/*
	 * Todavía no he hecho un esquema para resolver esto, pero lo tengo cerca.
	 * 
	 * El truqui está en que si hay que calcular M(A,2^b,2^c), usamos una representación en base 2^(mcm(b,c)). Entonces, cada dígito en esa base
	 * se traduce directamente en un conjunto de dígitos en las bases 2^b y 2^c, y por tanto a partir de él podemos calcular la diferencia
	 * asociada a ese dígito.
	 * 
	 * Por ejemplo, para b=5, c=3, usaríamos base 2^15=32768. Si cogemos, por ejemplo, el dígito 23456, al pasarlo a base 32 obtenemos (22,29,0);
	 * y al pasarlo a base 8 obtenemos (5,5,6,4,0). Así que la diferencia es (51-20)=31. Y a partir de aquí buscaríamos combinaciones (esto es
	 * complicadillo, sobre todo teniendo en cuenta que el límite no es una potencia de 2, pero se puede hacer).
	 */
	private final static long LIMIT=LongMath.pow(10l,16);
	
	private static class MultipleBaseTranslator	{
		// "Multiple" as in "múltiplo", not as in "múltiple" ;).
		private final int shift;
		private final int mod;
		public MultipleBaseTranslator(int smallBase)	{
			shift=smallBase;
			mod=(1<<smallBase)-1;
		}
		public int getDigitSum(int bigBaseDigit)	{
			int result=0;
			while (bigBaseDigit>0)	{
				result+=(bigBaseDigit&mod);
				bigBaseDigit>>=shift;
			}
			return result;
		}
	}
	
	private static int sum(IntSet elements)	{
		int result=0;
		for (IntCursor cursor=elements.cursor();cursor.moveNext();) result+=cursor.elem();
		return result;
	}
	
	private static class CountAndSum	{
		private BigInteger count;
		private BigInteger sum;
		public CountAndSum()	{
			count=BigInteger.ZERO;
			sum=BigInteger.ZERO;
		}
		public CountAndSum(long count,long sum)	{
			this.count=BigInteger.valueOf(count);
			this.sum=BigInteger.valueOf(sum);
		}
		public CountAndSum(IntSet set)	{
			this(set.sizeAsLong(),sum(set));
		}
		public CountAndSum(IntSet set,int limit,boolean inclusive)	{
			int tmpSum=0;
			int tmpCount=0;
			for (IntCursor cursor=set.cursor();cursor.moveNext();)	{
				int value=cursor.elem();
				if ((value<limit)||(inclusive&&(value==limit)))	{
					++tmpCount;
					tmpSum+=value;
				}
			}
			count=BigInteger.valueOf(tmpCount);
			sum=BigInteger.valueOf(tmpSum);
		}
		public BigInteger getSum()	{
			return sum;
		}
		public void add(BigInteger newCount,BigInteger newSum)	{
			count=count.add(newCount);
			sum=sum.add(newSum);
		}
		public void add(CountAndSum other)	{
			add(other.count,other.sum);
		}
		public void addPrefix(BigInteger in)	{
			sum=sum.add(count.multiply(in));
		}
		public void addPrefixFactors(long prefix,CountAndSum factor)	{
			sum=sum.multiply(factor.count).add(count.multiply(factor.sum).multiply(BigInteger.valueOf(prefix)));
			count=count.multiply(factor.count);
		}
		@Override
		public String toString()	{
			return "Count="+count+", sum="+sum;
		}
	}

	private static class FullBaseDefinition	{
		private final int bigBase;
		/*
		 * Using int[] despite every array having single elements has a reason to be: it serves as a base for the getCombinations() method.
		 * This is the most important method of this class, so it makes sense to use this structure from the start.
		 */
		private final NavigableMap<Integer,IntSet> singleSums;
		public FullBaseDefinition(int baseA,int baseB)	{
			if (baseA<=baseB) throw new IllegalArgumentException("The first argument should be the bigger one.");
			bigBase=(baseA*baseB)/EulerUtils.gcd(baseA,baseB);
			MultipleBaseTranslator translatorA=new MultipleBaseTranslator(baseA);
			MultipleBaseTranslator translatorB=new MultipleBaseTranslator(baseB);
			int maxNumber=1<<bigBase;
			singleSums=new TreeMap<>();
			for (int i=0;i<maxNumber;++i)	{
				int diff=translatorA.getDigitSum(i)-translatorB.getDigitSum(i);
				IntSet toAdd=singleSums.computeIfAbsent(diff,(Integer unused)->HashIntSets.newMutableSet());
				toAdd.add(i);
			}
		}
		public List<int[]> getZeroCombinations(int order)	{
			if (order<=1) throw new IllegalArgumentException("Order must be 2 or greater.");
			int minAbsoluteValue=-singleSums.firstKey();
			IntFunction<Range<Integer>> getUsableRange=(int theOrder)->	{
				int absoluteValueLimit=(order-theOrder)*minAbsoluteValue;
				return Range.closed(-absoluteValueLimit,absoluteValueLimit);
			};
			IntObjMap<NavigableMap<Integer,List<int[]>>> combinations=HashIntObjMaps.newMutableMap();
			NavigableMap<Integer,List<int[]>> base=new TreeMap<>();
			for (Integer key:singleSums.keySet()) base.put(key,Arrays.asList(new int[] {key.intValue()}));
			combinations.put(1,base);
			getCombinations(combinations,order,getUsableRange);
			return combinations.get(order).get(0);
		}
		private void getCombinations(IntObjMap<NavigableMap<Integer,List<int[]>>> combinations,int maxOrder,IntFunction<Range<Integer>> getUsableRange)	{
			if (combinations.containsKey(maxOrder)) return;
			int half=maxOrder/2;
			int otherHalf=maxOrder-half;
			// Small hack to reuse temporary results if possible.
			for (int i=1;i<half;++i) if (combinations.containsKey(i)&&combinations.containsKey(maxOrder-i))	{
				half=i;
				otherHalf=maxOrder-i;
				break;
			}
			getCombinations(combinations,half,getUsableRange);
			getCombinations(combinations,otherHalf,getUsableRange);
			combinations.put(maxOrder,combine2(combinations.get(half),combinations.get(otherHalf),getUsableRange.apply(maxOrder)));
		}
		private NavigableMap<Integer,List<int[]>> combine2(NavigableMap<Integer,List<int[]>> comb1,NavigableMap<Integer,List<int[]>> comb2,Range<Integer> validRange)	{
			NavigableMap<Integer,List<int[]>> result=new TreeMap<>();
			for (Map.Entry<Integer,List<int[]>> entry:comb1.entrySet())	{
				int value=entry.getKey();
				int minimum=validRange.lowerEndpoint()-value;
				int maximum=validRange.upperEndpoint()-value;
				if (minimum<=maximum)	{
					NavigableMap<Integer,List<int[]>> subMap=comb2.subMap(minimum,true,maximum,true);
					for (Map.Entry<Integer,List<int[]>> entry2:subMap.entrySet())	{
						int newValue=entry2.getKey();
						int target=value+newValue;
						List<int[]> toAdd=result.computeIfAbsent(target,(Integer unused)->new ArrayList<>());
						concatenateAll(toAdd,entry.getValue(),entry2.getValue());
					}
				}
			}
			return result;
		}
		private void concatenateAll(List<int[]> target,List<int[]> base1,List<int[]> base2)	{
			for (int[] a1:base1) for (int[] a2:base2)	{
				int[] result=Arrays.copyOf(a1,a1.length+a2.length);
				System.arraycopy(a2,0,result,a1.length,a2.length);
				target.add(result);
			}
		}
		public int[] decompose(long in)	{
			int size=1+(int)LongMath.log2(in,RoundingMode.DOWN)/bigBase;
			int[] result=new int[size];
			int mod=(1<<bigBase)-1;
			for (int i=size-1;i>=0;--i)	{
				result[i]=(int)(in&mod);
				in>>=bigBase;
			}
			return result;
		}
		public CountAndSum countAndSum(long limit)	{
			int[] decomposition=decompose(limit);
			CountAndSum result=new CountAndSum();
			for (int[] combination:getZeroCombinations(decomposition.length)) result.add(countAndSum(decomposition,combination,0));
			return result;
		}
		private CountAndSum countAndSum(int[] decomposition,int[] combination,int currentIndex)	{
			if (currentIndex>=decomposition.length) throw new IllegalArgumentException();
			else if (currentIndex==decomposition.length-1)	{
				IntSet set=singleSums.get(combination[currentIndex]);
				int limit=decomposition[currentIndex];
				return new CountAndSum(set,limit,true);
			}
			CountAndSum result=new CountAndSum(1,0);
			long prefix=1;
			for (int i=decomposition.length-1;i>currentIndex;--i)	{
				CountAndSum prefixSum=new CountAndSum(singleSums.get(combination[i]));
				result.addPrefixFactors(prefix,prefixSum);
				prefix<<=bigBase;
			}
			int limit=decomposition[currentIndex];
			IntSet thisLevel=singleSums.get(combination[currentIndex]);
			CountAndSum prefixFactor=new CountAndSum(thisLevel,limit,false);
			result.addPrefixFactors(prefix,prefixFactor);
			if (thisLevel.contains(limit))	{
				CountAndSum deeper=countAndSum(decomposition,combination,1+currentIndex);
				deeper.addPrefix(BigInteger.valueOf(limit).multiply(BigInteger.valueOf(prefix)));
				result.add(deeper);
			}
			return result;
		}
	}
	
	/*
	k=3, l=1 (base=3): 4^18=68719476736 combinations.
	k=4, l=1 (base=4): 8^14=4398046511104 combinations.
	k=4, l=2 (base=4): 4^14=268435456 combinations.
	k=5, l=1 (base=5): 16^11=17592186044416 combinations.
	k=5, l=2 (base=10): 50^6=15625000000 combinations.
	k=5, l=3 (base=15): 71^4=25411681 combinations.
	k=6, l=1 (base=6): 32^9=35184372088832 combinations.
	k=6, l=2 (base=6): 16^9=68719476736 combinations.
	k=6, l=3 (base=6): 8^9=134217728 combinations.
	k=6, l=4 (base=12): 34^5=45435424 combinations.
	 */
	public static void main(String[] args)	{
		/*
		 * 135672002596825561407425792
		 * 6825561407425792
		 * Sigue estando mal :(. Al menos es muy rápido.
		 */
		BigInteger result=BigInteger.ZERO;
		for (int k=3;k<=6;++k) for (int l=1;l<=k-2;++l)	{
			FullBaseDefinition base=new FullBaseDefinition(k,l);
			CountAndSum tmpResult=base.countAndSum(LIMIT);
			System.out.println("k="+k+", l="+l+" => "+tmpResult+".");
			result=result.add(tmpResult.getSum());
		}
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(LIMIT)));
	}
}
