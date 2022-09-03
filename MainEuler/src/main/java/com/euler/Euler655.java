package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.math.IntMath;

public class Euler655 {
	private final static int N=IntMath.pow(10,7)+19;
	private final static int POWER_10=32;
	
	private static int[] powerOf10Moduli(int n,int maxPower)	{
		int[] result=new int[maxPower];
		result[0]=1;
		for (int i=1;i<maxPower;++i) result[i]=(10*result[i-1])%n;
		return result;
	}
	
	private static class IntPair	{
		public final int a;
		public final int b;
		public IntPair(int a,int b)	{
			this.a=a;
			this.b=b;
		}
	}
	
	private static List<int[]> getSumDistributions(int n,int[] moduli,List<IntPair> listFrom1,List<IntPair> listFrom0)	{
		List<int[]> pairedSums=new ArrayList<>(listFrom1.size()+listFrom0.size());
		for (IntPair pairFrom1:listFrom1)	{
			int[] array=new int[9];
			if (pairFrom1.b>=0) array[0]=(moduli[pairFrom1.a]+moduli[pairFrom1.b])%n;
			else array[0]=moduli[pairFrom1.a];
			for (int i=1;i<9;++i) array[i]=(array[i-1]+array[0])%n;
			pairedSums.add(array);
		}
		for (IntPair pairFrom0:listFrom0)	{
			int[] array=new int[10];
			array[0]=0;
			if (pairFrom0.b>=0) array[1]=(moduli[pairFrom0.a]+moduli[pairFrom0.b])%n;
			else array[1]=moduli[pairFrom0.a];
			for (int i=2;i<10;++i) array[i]=(array[i-1]+array[1])%n;
			pairedSums.add(array);
		}
		return pairedSums;
	}
	
	private static long[] getTotalDistribution(List<int[]> pairedSums,int n)	{
		long[] result=new long[n];
		if (pairedSums.isEmpty())	{
			result[0]=1l;
			return result;
		}
		getTotalDistributionRecursive(result,n,0,pairedSums,0);
		return result;
	}
	
	private static long[] getSums(int n,int[] moduli,List<IntPair> listFrom1,List<IntPair> listFrom0)	{
		List<int[]> partialSums=getSumDistributions(n,moduli,listFrom1,listFrom0);
		return getTotalDistribution(partialSums,n);
	}
	
	private static void getTotalDistributionRecursive(long[] result,int n,int currentSum,List<int[]> pairedSums,int psIndex)	{
		boolean isFinal=(psIndex==(pairedSums.size()-1));
		for (int toSum:pairedSums.get(psIndex))	{
			int nextSum=(currentSum+toSum)%n;
			if (isFinal) ++result[nextSum];
			else getTotalDistributionRecursive(result,n,nextSum,pairedSums,1+psIndex);
		}
	}
	
	private static long meetInTheMiddle(int n,long[] s1,long[] s2)	{
		long result=s1[0]*s2[0];
		for (int i=1;i<n;++i) result+=s1[i]*s2[n-i];
		return result;
	}
	
	private static class DigitDivision	{
		public final IntPair basePairA;
		public final List<IntPair> remainingPairsA;
		public final List<IntPair> pairsB;
		public DigitDivision(IntPair basePairA,List<IntPair> remainingPairsA,List<IntPair> pairsB)	{
			this.basePairA=basePairA;
			this.remainingPairsA=remainingPairsA;
			this.pairsB=pairsB;
		}
	}
	
	private static List<DigitDivision> generateDivisions(int n,int minN)	{
		List<DigitDivision> result=new ArrayList<>();
		for (int i=minN;i<=n;++i)	{
			List<IntPair> allPairs=new ArrayList<>();
			int j=0;
			int nj=i-1-j;
			while (j<nj)	{
				allPairs.add(new IntPair(j,nj));
				++j;
				--nj;
			}
			if (j==nj) allPairs.add(new IntPair(j,-1));
			List<IntPair> pairsA=new ArrayList<>();
			List<IntPair> pairsB=new ArrayList<>();
			IntPair firstPair=allPairs.get(0);
			for (int k=1;k<allPairs.size();++k) (((k%2)==1)?pairsB:pairsA).add(allPairs.get(k));
			result.add(new DigitDivision(firstPair,pairsA,pairsB));
		}
		return result;
	}
	
	private static long getPalindromes(int n,int maxPow)	{
		int minPow=1+IntMath.log10(n,RoundingMode.DOWN);
		List<DigitDivision> divisions=generateDivisions(maxPow,minPow);
		long result=0;
		int[] moduli=powerOf10Moduli(n,maxPow);
		for (DigitDivision division:divisions)	{
			long[] sumsA=getSums(n,moduli,Collections.singletonList(division.basePairA),division.remainingPairsA);
			long[] sumsB=getSums(n,moduli,Collections.emptyList(),division.pairsB);
			result+=meetInTheMiddle(n,sumsA,sumsB);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=getPalindromes(N,POWER_10);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
