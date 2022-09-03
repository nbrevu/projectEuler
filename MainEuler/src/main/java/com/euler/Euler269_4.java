package com.euler;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler269_4 {
	private final static int DIGITS=5;
	
	private static class CounterResult	{
		public final long totalSolution;
		public final long solutionWith0;
		public CounterResult(long totalSolution,long solutionWith0)	{
			this.totalSolution=totalSolution;
			this.solutionWith0=solutionWith0;
		}
	}
	
	private abstract static class CaseCounter<T>	{
		protected static long getMaxValue(int root,int digits)	{
			int posRoot=-root;
			long result=LongMath.pow(posRoot,digits/2);
			if (posRoot<=3) result*=100l;
			return result;
		}
		protected final int digits;
		public CaseCounter(int digits)	{
			this.digits=digits;
		}
		protected abstract T getOneDigitCases();
		protected abstract T evolve(T previous);
		protected abstract long countZeroes(T map);
		public CounterResult count()	{
			T cases=getOneDigitCases();
			long prevCount=0;
			long curCount=0;
			for (int i=2;i<=digits;++i)	{
				// System.out.println("En el caso de notarme petadísimo, que sepas que soy un "+getClass().getName()+" y estoy haciendo cosas con "+i+" dígitos.");
				cases=evolve(cases);
				prevCount=curCount;
				curCount+=countZeroes(cases);
				System.out.println("Digits="+i+": "+(curCount-prevCount));
			}
			return new CounterResult(curCount,prevCount);
		}
	}
	private static class CaseCounter1Digit extends CaseCounter<LongLongMap>	{
		private final int root;
		private final long maxValue;
		public CaseCounter1Digit(int root,int digits) {
			super(digits);
			this.root=root;
			maxValue=getMaxValue(root,digits);
		}
		@Override
		protected LongLongMap getOneDigitCases() {
			LongLongMap result=HashLongLongMaps.newMutableMap();
			for (long i=1;i<=9;++i) result.put(i,1l);
			return result;
		}
		@Override
		protected LongLongMap evolve(LongLongMap previous) {
			LongLongMap result=HashLongLongMaps.newMutableMap();
			for (LongLongCursor cursor=previous.cursor();cursor.moveNext();)	{
				long base=cursor.key()*root;
				if (Math.abs(base)>=maxValue) continue;
				long count=cursor.value();
				for (int i=0;i<=9;++i) result.addValue(base+i,count);
			}
			return result;
		}
		@Override
		protected long countZeroes(LongLongMap map) {
			return map.get(0l);
		}
	}
	private static class CaseCounter2Digits extends CaseCounter<LongObjMap<LongLongMap>>	{
		private final int root1,root2;
		private final long maxValue1,maxValue2;
		public CaseCounter2Digits(int root1,int root2,int digits)	{
			super(digits);
			this.root1=root1;
			this.root2=root2;
			maxValue1=getMaxValue(root1,digits);
			maxValue2=getMaxValue(root2,digits);
		}
		@Override
		protected LongObjMap<LongLongMap> getOneDigitCases() {
			LongObjMap<LongLongMap> result=HashLongObjMaps.newMutableMap();
			for (int i=1;i<=9;++i)	{
				LongLongMap subMap=HashLongLongMaps.newMutableMap();
				subMap.put(i,1l);
				result.put(i,subMap);
			}
			return result;
		}
		@Override
		protected LongObjMap<LongLongMap> evolve(LongObjMap<LongLongMap> previous) {
			LongObjMap<LongLongMap> result=HashLongObjMaps.newMutableMap();
			for (LongObjCursor<LongLongMap> cursor1=previous.cursor();cursor1.moveNext();)	{
				long base=cursor1.key()*root1;
				if (Math.abs(base)>=maxValue1) continue;
				for (int i=0;i<=9;++i)	{
					LongLongMap subPrevious=cursor1.value();
					LongLongMap subResult=result.computeIfAbsent(base+i,(long unused)->HashLongLongMaps.newMutableMap());
					for (LongLongCursor cursor2=subPrevious.cursor();cursor2.moveNext();)	{
						long newKey=cursor2.key()*root2+i;
						if (Math.abs(newKey)<=maxValue2) subResult.addValue(newKey,cursor2.value());
					}
				}
			}
			return result;
		}
		@Override
		protected long countZeroes(LongObjMap<LongLongMap> map) {
			LongLongMap zeroMap=map.get(0l);
			return (zeroMap==null)?0l:zeroMap.get(0l);
		}
	}
	private static class CaseCounter3Digits extends CaseCounter<LongObjMap<LongObjMap<LongLongMap>>>	{
		private final int root1,root2,root3;
		private final long maxValue1,maxValue2,maxValue3;
		public CaseCounter3Digits(int root1,int root2,int root3,int digits)	{
			super(digits);
			this.root1=root1;
			this.root2=root2;
			this.root3=root3;
			maxValue1=getMaxValue(root1,digits);
			maxValue2=getMaxValue(root2,digits);
			maxValue3=getMaxValue(root3,digits);
		}
		@Override
		protected LongObjMap<LongObjMap<LongLongMap>> getOneDigitCases() {
			LongObjMap<LongObjMap<LongLongMap>> result=HashLongObjMaps.newMutableMap();
			for (int i=1;i<=9;++i)	{
				LongObjMap<LongLongMap> subMap=HashLongObjMaps.newMutableMap();
				LongLongMap subSubMap=HashLongLongMaps.newMutableMap();
				subSubMap.put(i,1l);
				subMap.put(i,subSubMap);
				result.put(i,subMap);
			}
			return result;
		}
		@Override
		protected LongObjMap<LongObjMap<LongLongMap>> evolve(LongObjMap<LongObjMap<LongLongMap>> previous) {
			LongObjMap<LongObjMap<LongLongMap>> result=HashLongObjMaps.newMutableMap();
			for (LongObjCursor<LongObjMap<LongLongMap>> cursor1=previous.cursor();cursor1.moveNext();)	{
				long base=cursor1.key()*root1;
				if (Math.abs(base)>=maxValue1) continue;
				for (int i=0;i<=9;++i)	{
					LongObjMap<LongLongMap> subPrevious=cursor1.value();
					LongObjMap<LongLongMap> subResult=result.computeIfAbsent(base+i,(long unused)->HashLongObjMaps.newMutableMap());
					for (LongObjCursor<LongLongMap> cursor2=subPrevious.cursor();cursor2.moveNext();)	{
						long base2=cursor2.key()*root2+i;
						if (Math.abs(base2)<=maxValue2)	{
							LongLongMap subSubPrevious=cursor2.value();
							LongLongMap subSubResult=subResult.computeIfAbsent(base2,(long unused)->HashLongLongMaps.newMutableMap());
							for (LongLongCursor cursor3=subSubPrevious.cursor();cursor3.moveNext();)	{
								long newKey=cursor3.key()*root3+i;
								if (Math.abs(newKey)<=maxValue3) subSubResult.addValue(newKey,cursor3.value());
							}
						}
					}
				}
			}
			return result;
		}
		@Override
		protected long countZeroes(LongObjMap<LongObjMap<LongLongMap>> map) {
			LongObjMap<LongLongMap> zeroMap=map.get(0l);
			if (zeroMap==null) return 0l;
			LongLongMap zeroSubMap=zeroMap.get(0l);
			return (zeroSubMap==null)?0l:zeroSubMap.get(0l);
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * Inclusion-exclusion! Since this is a very simple binary lattice, all of the coefficients are either +1 or -1. Weeee!
		 */
		long tic=System.nanoTime();
		long result=LongMath.pow(10l,DIGITS-1);	// Cases with 0.
		for (int i=-1;i>=-10;--i)	{
			System.out.println("Caso 1, "+i+".");
			CaseCounter<?> counter=new CaseCounter1Digit(i,DIGITS);
			CounterResult tmpResult=counter.count();
			result+=tmpResult.totalSolution;
			result-=tmpResult.solutionWith0;
		}
		for (int i=-1;i>=-10;--i) for (int j=i-1;j*i<10;--j)	{
			System.out.println("Caso 2, "+i+", "+j+".");
			CaseCounter<?> counter=new CaseCounter2Digits(i,j,DIGITS);
			CounterResult tmpResult=counter.count();
			result-=tmpResult.totalSolution;
			result+=tmpResult.solutionWith0;
		}
		for (int i=-3;i>=-4;--i)	{
			System.out.println("Caso 3, "+i+".");
			CaseCounter<?> counter=new CaseCounter3Digits(-1,-2,i,DIGITS);
			CounterResult tmpResult=counter.count();
			result+=tmpResult.totalSolution;
			result-=tmpResult.solutionWith0;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
