package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler483_3 {
	private final static int N=60;
	
	private final static Collector<CharSequence,?,String> PLUS_COLLECTOR=Collectors.joining("+");
	
	private static class PartitionElement	{
		public final static Comparator<PartitionElement> COMPARATOR=Comparator.comparingInt((PartitionElement x)->x.element);
		private final static IntObjMap<IntObjMap<PartitionElement>> CACHE=HashIntObjMaps.newMutableMap();
		public final int element;
		public final int repetitions;
		private PartitionElement(int element,int repetitions)	{
			this.element=element;
			this.repetitions=repetitions;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append(element);
			for (int i=1;i<repetitions;++i) sb.append('+').append(element);
			return sb.toString();
		}
		public static PartitionElement get(int element,int repetitions)	{
			return CACHE.computeIfAbsent(element,(int unused)->HashIntObjMaps.newMutableMap()).computeIfAbsent(repetitions,(int unused2)->new PartitionElement(element,repetitions));
		}
	}
	
	private static class Partition	{
		public final static Comparator<Partition> COMPARATOR=Comparator.comparingInt(Partition::lastElement);
		public final List<PartitionElement> elements;
		private Partition(List<PartitionElement> elements)	{
			this.elements=elements;
		}
		public int lastElement()	{
			return elements.get(elements.size()-1).element;
		}
		@Override
		public int hashCode()	{
			return elements.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			return elements.equals(((Partition)other).elements);
		}
		@Override
		public String toString()	{
			return elements.stream().map(Object::toString).collect(PLUS_COLLECTOR);
		}
		public static Partition singleton(int n)	{
			return new Partition(List.of(PartitionElement.get(n,1)));
		}
		public static Partition expand(Partition p,int n)	{
			List<PartitionElement> copy=new ArrayList<>(p.elements);
			for (int i=0;i<copy.size();++i)	{
				PartitionElement el=copy.get(i);
				if (el.element==n)	{
					copy.set(i,PartitionElement.get(n,el.repetitions+1));
					return new Partition(Collections.unmodifiableList(copy));
				}	else if (el.element>n) break;
			}
			copy.add(PartitionElement.get(n,1));
			copy.sort(PartitionElement.COMPARATOR);
			return new Partition(Collections.unmodifiableList(copy));
		}
	}
	
	private static List<List<Partition>> generatePartitions(int upTo)	{
		List<List<Partition>> result=new ArrayList<>(upTo+1);
		result.add(new ArrayList<>());
		for (int i=1;i<=upTo;++i)	{
			List<Partition> thisList=new ArrayList<>();
			thisList.add(Partition.singleton(i));
			for (int j=1;j<i;++j)	{
				int toAdd=i-j;
				for (Partition p:result.get(j))	{
					if (p.lastElement()>toAdd) break;
					thisList.add(Partition.expand(p,toAdd));
				}
			}
			thisList.sort(Partition.COMPARATOR);
			result.add(thisList);
			System.out.println(i+"...");
		}
		return result;
	}
	
	private static class AverageCalculator	{
		private final double[] factorials;
		private static double pow(double base,int exponent)	{
			double result=base;
			for (int i=2;i<=exponent;++i) result*=base;
			return result;
		}
		public AverageCalculator(int n)	{
			factorials=new double[1+n];
			factorials[0]=1d;
			for (int i=1;i<=n;++i) factorials[i]=i*factorials[i-1];
		}
		private double calculateAppearances(Partition p)	{
			int sum=0;
			double denom=1d;
			for (PartitionElement elem:p.elements)	{
				sum+=elem.element*elem.repetitions;
				/*-
				num*=Math.pow(factorials[elem.element-1],elem.repetitions);
				denom*=Math.pow(factorials[elem.element],elem.repetitions)*factorials[elem.repetitions];
				*/
				denom*=pow(elem.element,elem.repetitions)*factorials[elem.repetitions];
			}
			return factorials[sum]/denom;
		}
		private long calculateValue(Partition p)	{
			long result=1l;
			for (PartitionElement elem:p.elements) result=EulerUtils.lcm(result,elem.element);
			return result;
		}
		private double calculateFullValue(List<Partition> parts,int n)	{
			double result=0;
			for (Partition p:parts) result+=calculateAppearances(p)*pow(calculateValue(p),2);
			return result/factorials[n];
		}
	}
	
	public static void main(String[] args)	{
		List<List<Partition>> partitions=generatePartitions(N);
		System.out.println(Arrays.toString(partitions.stream().mapToInt(List::size).toArray()));
		AverageCalculator calc=new AverageCalculator(N);
		for (int i=1;i<=N;++i) System.out.println(String.format(Locale.UK,"f(%d)=%.12f.",i,calc.calculateFullValue(partitions.get(i),i)));
	}
}
