package com.phys;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public class TiplerMosca35_19 {
	private final static int MAX=10;
	private final static double LONG1=0.25;
	private final static double LONG2=0.5;
	private final static double LONG3=1;
	
	public static class QuantumNumbers	{
		private static double L1;
		private static double L2;
		private static double L3;
		public static void setL1(double newL1)	{
			L1=newL1;
		}
		public static void setL2(double newL2)	{
			L2=newL2;
		}
		public static void setL3(double newL3)	{
			L3=newL3;
		}
		public int n1,n2,n3;
		private QuantumNumbers(int n1,int n2,int n3)	{
			this.n1=n1;
			this.n2=n2;
			this.n3=n3;
		}
		public static QuantumNumbers baseState()	{
			return new QuantumNumbers(1,1,1);
		}
		public List<QuantumNumbers> nextStates()	{
			QuantumNumbers c1=new QuantumNumbers(1+n1,n2,n3);
			QuantumNumbers c2=new QuantumNumbers(n1,1+n2,n3);
			QuantumNumbers c3=new QuantumNumbers(n1,n2,1+n3);
			return Arrays.asList(c1,c2,c3);
		}
		public double getEnergy()	{
			return squareSum(n1/L1,n2/L2,n3/L3);
		}
		private static double squareSum(double d1,double d2,double d3)	{
			return (d1*d1)+(d2*d2)+(d3*d3);
		}
		@Override
		public int hashCode()	{
			return Integer.hashCode(n1)+Integer.hashCode(n2)+Integer.hashCode(n3);
		}
		@Override
		public boolean equals(Object other)	{
			QuantumNumbers o=(QuantumNumbers)other;
			return (n1==o.n1)&&(n2==o.n2)&&(n3==o.n3);
		}
		@Override
		public String toString()	{
			return "["+n1+","+n2+","+n3+"]";
		}
	}
	
	private static <T,U> void addToMapSet(Map<T,Set<U>> map,T key,U value)	{
		Set<U> set=map.get(key);
		if (set==null)	{
			set=new HashSet<>();
			map.put(key,set);
		}
		set.add(value);
	}
	
	public static void main(String[] args)	{
		QuantumNumbers.setL1(LONG1);
		QuantumNumbers.setL2(LONG2);
		QuantumNumbers.setL3(LONG3);
		NavigableMap<Double,Set<QuantumNumbers>> pending=new TreeMap<>();
		QuantumNumbers initial=QuantumNumbers.baseState();
		addToMapSet(pending,initial.getEnergy(),initial);
		for (int i=0;i<MAX;++i)	{
			Map.Entry<Double,Set<QuantumNumbers>> next=pending.firstEntry();
			double energy=next.getKey();
			Set<QuantumNumbers> nums=next.getValue();
			QuantumNumbers candidate=nums.iterator().next();
			nums.remove(candidate);
			if (nums.isEmpty()) pending.remove(energy);
			System.out.println(candidate.toString()+": "+energy+".");
			for (QuantumNumbers child:candidate.nextStates()) addToMapSet(pending,child.getEnergy(),child);
		}
	}
}
