package com.euler;

public class Euler760_4 {
	private static class ElementMultiplier	{
		public final long f2s;
		public final long normalPrefix;
		public final long normalSuffix;
		public final long specialPrefix;
		public final long specialSuffix;
		public ElementMultiplier(int s)	{
			long power2=1l<<s;
			long power2Plus=power2<<1;
			f2s=power2*(power2Plus+2-s);
			normalPrefix=power2Plus*(power2+1);
			normalSuffix=f2s;
			specialPrefix=power2Plus*(power2-1);
			specialSuffix=f2s+power2Plus*(power2-3);
		}
	}
	
	private static class BlockData	{
		public final long indexCount;
		public final long indexSum;
		public final long functionSum;
		public final long previousLastValue;
		public final long currentLastValue;
		public BlockData(long indexCount,long indexSum,long functionSum,long previousLastValue,long currentLastValue)	{
			this.indexCount=indexCount;
			this.indexSum=indexSum;
			this.functionSum=functionSum;
			this.previousLastValue=previousLastValue;
			this.currentLastValue=currentLastValue;
		}
	}
	
	private static class SumCalculator	{
		private final ElementMultiplier[] multipliers;
		private final BlockData[] data;
		public SumCalculator(int maxSize)	{
			multipliers=new ElementMultiplier[maxSize];
			data=new BlockData[maxSize];
			for (int i=0;i<maxSize;++i) multipliers[i]=new ElementMultiplier(i);
			data[0]=new BlockData(1l,1l,4l,0l,4l);
			for (int i=1;i<maxSize;++i)	{
				long thisFunctionSum=multipliers[i].f2s;
				for (int j=1;j<=i;++j)	{
					ElementMultiplier multiplier=multipliers[j-1];
					BlockData prevBlock=data[i-j];
					thisFunctionSum+=multiplier.normalPrefix*prevBlock.functionSum;
					thisFunctionSum+=multiplier.normalSuffix*(prevBlock.indexSum+prevBlock.indexCount);
					thisFunctionSum+=multiplier.specialPrefix*(prevBlock.functionSum+prevBlock.previousLastValue-prevBlock.currentLastValue);
					thisFunctionSum+=multiplier.specialSuffix*prevBlock.indexSum;
				}
				BlockData lastBlock=data[i-1];
				long prevCount=lastBlock.indexCount;
				long thisIndexCount=prevCount<<1l;
				long firstVal=1l<<i;
				long lastVal=(firstVal*2-1);
				long indexSum=(firstVal+lastVal)*prevCount;
				long prevLastValue=lastBlock.currentLastValue;
				long currLastValue=lastBlock.currentLastValue*4l+(1l<<(i+2));
				data[i]=new BlockData(thisIndexCount,indexSum,thisFunctionSum,prevLastValue,currLastValue);
			}
		}
		public long getFullSum()	{
			long result=0l;
			for (BlockData block:data) result+=block.functionSum;
			return result;
		}
	}
	
	private static long calculateFBruteForce(int n)	{
		long result=0;
		for (int k=0;k<=n;++k)	{
			int nk=n-k;
			result+=k^nk;
			result+=k|nk;
			result+=k&nk;
		}
		return result;
	}
	
	private static long calculateSumBruteForce(int limit)	{
		long result=0;
		for (int i=1;i<=limit;++i) result+=calculateFBruteForce(i);
		return result;
	}
	
	public static void main(String[] args)	{
		// Yeah, it works now :).
		for (int x=1;x<=16;++x)	{
			long realSol=calculateSumBruteForce((1<<x)-1);
			long fineSol=new SumCalculator(x).getFullSum();
			System.out.println(String.format("%d==%d?",realSol,fineSol));
		}
	}
}
