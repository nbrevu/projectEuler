package com.euler;

import java.math.RoundingMode;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler269_5 {
	private final static int DIGITS=16;
	
	private static class CounterResult	{
		public final long totalSolution;
		public final long solutionWith0;
		public CounterResult(long totalSolution,long solutionWith0)	{
			this.totalSolution=totalSolution;
			this.solutionWith0=solutionWith0;
		}
	}
	
	private static long meetInTheMiddle(LongLongMap lowerMap,LongLongMap upperMap,long toMultiply)	{
		long result=0;
		for (LongLongCursor cursor=upperMap.cursor();cursor.moveNext();)	{
			long newKey=-toMultiply*cursor.key();
			result+=cursor.value()*lowerMap.get(newKey);
		}
		return result;
	}
	private static long meetInTheMiddle(LongObjMap<LongLongMap> lowerMap,LongObjMap<LongLongMap> upperMap,long toMultiply1,long toMultiply2)	{
		long result=0;
		for (LongObjCursor<LongLongMap> cursor=upperMap.cursor();cursor.moveNext();)	{
			long newKey=-toMultiply1*cursor.key();
			LongLongMap lowerSubMap=lowerMap.get(newKey);
			if (lowerSubMap==null) continue;
			LongLongMap upperSubMap=cursor.value();
			result+=meetInTheMiddle(lowerSubMap,upperSubMap,toMultiply2);
		}
		return result;
	}
	private static long meetInTheMiddle(LongObjMap<LongObjMap<LongLongMap>> lowerMap,LongObjMap<LongObjMap<LongLongMap>> upperMap,long toMultiply1,long toMultiply2,long toMultiply3)	{
		long result=0;
		for (LongObjCursor<LongObjMap<LongLongMap>> cursor=upperMap.cursor();cursor.moveNext();)	{
			long newKey=-toMultiply1*cursor.key();
			LongObjMap<LongLongMap> lowerSubMap=lowerMap.get(newKey);
			if (lowerSubMap==null) continue;
			LongObjMap<LongLongMap> upperSubMap=cursor.value();
			result+=meetInTheMiddle(lowerSubMap,upperSubMap,toMultiply2,toMultiply3);
		}
		return result;
	}
	private abstract static class CaseCounter<T>	{
		protected final int digits;
		public CaseCounter(int digits)	{
			this.digits=digits;
		}
		protected abstract T getOneDigitCases();
		protected abstract T evolve(T previous);
		protected abstract long countZeroes(T map);
		protected abstract long countMeetInTheMiddle(IntObjMap<T> casesPerDigit,int upper,int lower);
		public CounterResult count()	{
			IntObjMap<T> casesPerDigit=HashIntObjMaps.newMutableMap();
			T cases=getOneDigitCases();
			casesPerDigit.put(1,cases);
			int lastNormal=IntMath.divide(digits,2,RoundingMode.UP);
			long prevCount=0;
			long curCount=0;
			for (int i=2;i<=lastNormal;++i)	{
				cases=evolve(cases);
				casesPerDigit.put(i,cases);
				prevCount=curCount;
				curCount+=countZeroes(cases);
			}
			for (int i=lastNormal+1;i<=digits;++i)	{
				prevCount=curCount;
				curCount+=countMeetInTheMiddle(casesPerDigit,i-lastNormal,lastNormal)+countZeroes(casesPerDigit.get(i-lastNormal));
			}
			return new CounterResult(curCount,prevCount);
		}
	}
	private static class CaseCounter1Digit extends CaseCounter<LongLongMap>	{
		private final int root;
		public CaseCounter1Digit(int root,int digits) {
			super(digits);
			this.root=root;
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
				long count=cursor.value();
				for (int i=0;i<=9;++i) result.addValue(base+i,count);
			}
			return result;
		}
		@Override
		protected long countZeroes(LongLongMap map) {
			return map.get(0l);
		}
		@Override
		protected long countMeetInTheMiddle(IntObjMap<LongLongMap> casesPerDigit,int upper,int lower)	{
			long result=0l;
			long toMultiply=LongMath.pow(root,lower);
			LongLongMap upperMap=casesPerDigit.get(upper);
			for (int i=1;i<=lower;++i) result+=meetInTheMiddle(casesPerDigit.get(i),upperMap,toMultiply);
			return result;
		}
	}
	private static class CaseCounter2Digits extends CaseCounter<LongObjMap<LongLongMap>>	{
		private final int root1,root2;
		public CaseCounter2Digits(int root1,int root2,int digits)	{
			super(digits);
			this.root1=root1;
			this.root2=root2;
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
				for (int i=0;i<=9;++i)	{
					LongLongMap subPrevious=cursor1.value();
					LongLongMap subResult=result.computeIfAbsent(base+i,(long unused)->HashLongLongMaps.newMutableMap());
					for (LongLongCursor cursor2=subPrevious.cursor();cursor2.moveNext();)	{
						long newKey=cursor2.key()*root2+i;
						subResult.addValue(newKey,cursor2.value());
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
		@Override
		protected long countMeetInTheMiddle(IntObjMap<LongObjMap<LongLongMap>> casesPerDigit,int upper,int lower)	{
			long toMultiply1=LongMath.pow(root1,lower);
			long toMultiply2=LongMath.pow(root2,lower);
			LongObjMap<LongLongMap> upperMap=casesPerDigit.get(upper);
			long result=0l;
			for (int i=1;i<=lower;++i) result+=meetInTheMiddle(casesPerDigit.get(i),upperMap,toMultiply1,toMultiply2);
			return result;
		}
	}
	private static class CaseCounter3Digits extends CaseCounter<LongObjMap<LongObjMap<LongLongMap>>>	{
		private final int root1,root2,root3;
		public CaseCounter3Digits(int root1,int root2,int root3,int digits)	{
			super(digits);
			this.root1=root1;
			this.root2=root2;
			this.root3=root3;
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
				for (int i=0;i<=9;++i)	{
					LongObjMap<LongLongMap> subPrevious=cursor1.value();
					LongObjMap<LongLongMap> subResult=result.computeIfAbsent(base+i,(long unused)->HashLongObjMaps.newMutableMap());
					for (LongObjCursor<LongLongMap> cursor2=subPrevious.cursor();cursor2.moveNext();)	{
						long base2=cursor2.key()*root2+i;
						LongLongMap subSubPrevious=cursor2.value();
						LongLongMap subSubResult=subResult.computeIfAbsent(base2,(long unused)->HashLongLongMaps.newMutableMap());
						for (LongLongCursor cursor3=subSubPrevious.cursor();cursor3.moveNext();)	{
							long newKey=cursor3.key()*root3+i;
							subSubResult.addValue(newKey,cursor3.value());
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
		@Override
		protected long countMeetInTheMiddle(IntObjMap<LongObjMap<LongObjMap<LongLongMap>>> casesPerDigit,int upper,int lower)	{
			long toMultiply1=LongMath.pow(root1,lower);
			long toMultiply2=LongMath.pow(root2,lower);
			long toMultiply3=LongMath.pow(root3,lower);
			LongObjMap<LongObjMap<LongLongMap>> upperMap=casesPerDigit.get(upper);
			long result=0l;
			for (int i=1;i<=lower;++i) result+=meetInTheMiddle(casesPerDigit.get(i),upperMap,toMultiply1,toMultiply2,toMultiply3);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * Inclusion-exclusion! Since this is a very simple binary lattice, all of the coefficients are either +1 or -1. Weeee!
		 */
		long tic=System.nanoTime();
		long result=LongMath.pow(10l,DIGITS-1);	// Cases with 0.
		for (int i=-1;i>=-9;--i)	{
			CaseCounter<?> counter=new CaseCounter1Digit(i,DIGITS);
			CounterResult tmpResult=counter.count();
			result+=tmpResult.totalSolution;
			result-=tmpResult.solutionWith0;
		}
		for (int i=-1;i>=-3;--i) for (int j=i-1;j*i<10;--j)	{
			CaseCounter<?> counter=new CaseCounter2Digits(i,j,DIGITS);
			CounterResult tmpResult=counter.count();
			result-=tmpResult.totalSolution;
			result+=tmpResult.solutionWith0;
		}
		for (int i=-3;i>=-4;--i)	{
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
