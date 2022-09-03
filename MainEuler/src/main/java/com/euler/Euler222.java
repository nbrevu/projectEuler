package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class Euler222 {
	// THIS... IS... <strike>SPARTA</strike>TSP!!!!!
	private final static int BALLS = 21;
	private final static double FIRST_RADIUS = 30.0;
	private final static double TUBE = 50.0;

	private static double[] generateRadii(int howManyBalls,double firstRadius)	{
		double result[]=new double[howManyBalls];
		for (int i=0;i<howManyBalls;++i) result[i]=firstRadius+(double)i;
		return result;
	}

	private static double getDistance(double radius1,double radius2,double tube)	{
		double totalDist=radius1+radius2;
		double vDist=(2*tube)-(radius1+radius2);
		return Math.sqrt(totalDist*totalDist-vDist*vDist);
	}

	private static Table<Integer,Integer,Double> generateDistances(double[] radii,double tube)	{
		int size=radii.length;
		Table<Integer,Integer,Double> result=HashBasedTable.create(size,size-1);
		for (int i=0;i<size;++i) for (int j=0;j<size;++j) if (i!=j) result.put(i,j,getDistance(radii[i],radii[j],tube));
		return result;
	}

	private static Multimap<Integer, BitSet> getBitsetsByWeight(int bits)	{
		long limit=1l<<bits;
		long[] holder=new long[1];
		Multimap<Integer,BitSet> result=LinkedListMultimap.create(bits);
		for (holder[0]=1l;holder[0]<limit;++holder[0])	{
			BitSet set=BitSet.valueOf(holder);
			result.put(set.cardinality(),set);
		}
		return result;
	}

	public static double[] scramble(double[] old,int newFirst)	{
		double[] result=old.clone();
		result[0]=old[newFirst];
		result[newFirst]=old[0];
		return result;
	}

	private static Multimap<Integer,BitSet> allBitsets;

	private static List<BitSet> getParents(BitSet in)	{
		List<BitSet> parents=new ArrayList<>(in.cardinality());
		for (int i=in.nextSetBit(0);i>=0;i=in.nextSetBit(i+1))	{
			BitSet newSet=(BitSet)(in.clone());
			newSet.flip(i);
			parents.add(newSet);
		}
		return parents;
	}
	
	private static int getMissingBit(BitSet set,BitSet parent)	{
		for (int i=set.nextSetBit(0);i>=0;i=set.nextSetBit(i+1)) if (!parent.get(i)) return i;
		throw new IllegalArgumentException("Nicht möglich!!!!!");
	}

	private static Map<Integer,Double> tsp(double[] radii,double tube)	{
		Table<Integer,Integer,Double> baseDistances=generateDistances(radii,tube);
		int size=radii.length-1;
		Table<BitSet,Integer,Double> allDistances=HashBasedTable.create(1<<size,size-1);
		for (BitSet initial:allBitsets.get(1))	{
			int position=initial.nextSetBit(0);
			allDistances.put(initial,position,baseDistances.get(0,position+1));
		}
		for (int i=2;i<=size;++i) for (BitSet set:allBitsets.get(i)) for (BitSet parent:getParents(set))	{
			int missing=getMissingBit(set,parent);
			double min=Double.MAX_VALUE;
			for (Map.Entry<Integer,Double> entry:allDistances.row(parent).entrySet()) min=Math.min(min,entry.getValue()+baseDistances.get(1+entry.getKey(),1+missing));
			allDistances.put(set,missing,min);
		}
		BitSet lastBitSet=allBitsets.get(size).iterator().next();
		return allDistances.row(lastBitSet);
	}

	public static void main(String[] args)	{
		double[] radii=generateRadii(BALLS,FIRST_RADIUS);
		allBitsets=getBitsetsByWeight(BALLS-1);
		double minimum=Double.MAX_VALUE;
		for (int i=0;i<BALLS;++i)	{
			double[] scrambled=scramble(radii,i);
			Map<Integer,Double> minima=tsp(scrambled,TUBE);
			double thisMin=Double.MAX_VALUE;
			for (Map.Entry<Integer,Double> entry:minima.entrySet()) thisMin=Math.min(thisMin,entry.getValue()+scrambled[entry.getKey()+1]);
			minimum=Math.min(minimum,thisMin+scrambled[0]);
			System.out.println(""+i+": "+minimum+"...");
		}
		System.out.println(minimum);
	}
}