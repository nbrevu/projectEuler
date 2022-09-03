package com.euler;

import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.CombinationIterator;
import com.euler.common.EulerUtils;

public class Euler409_3 {
	public final static int N=7;
	
	private static boolean isValid(int[] arr)	{
		for (int i=0;i<arr.length;++i) for (int j=i+1;j<arr.length;++j) if (arr[i]==arr[j]) return false;
		return true;
	}
	
	private static boolean isWinning(int[] arr)	{
		int result=0;
		for (int v:arr) result^=v+1;
		return (result!=0);
	}
	
	private static class Identifier implements Comparable<Identifier>	{
		private final int extraBits;
		private final int repetitions;
		private final boolean zero;
		public Identifier(int extraBits,int repetitions,boolean zero)	{
			this.extraBits=extraBits;
			this.repetitions=repetitions;
			this.zero=zero;
		}
		@Override
		public int hashCode()	{
			return (zero?25:0)+5*extraBits+repetitions;
		}
		@Override
		public boolean equals(Object other)	{
			Identifier iOther=(Identifier)other;
			return (extraBits==iOther.extraBits)&&(repetitions==iOther.repetitions)&&(zero==iOther.zero);
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append('(').append(extraBits).append(" extra bits, ").append(repetitions).append(" repetitions");
			if (zero) sb.append(", with 0");
			sb.append(')');
			return sb.toString();
		}
		@Override
		public int compareTo(Identifier other)	{
			int diff=repetitions-other.repetitions;
			if (diff!=0) return diff;
			diff=extraBits-other.extraBits;
			if (diff!=0) return diff;
			return (zero?1:0)-(other.zero?1:0);
		}
	}
	
	/*-
	Losing cases for N=2:
	Nonzero cases for N=2: 6 valid positions, 6 winning ones (0 losing).
	Summary:
	Losing cases for N=3:
	Nonzero cases for N=3: 210 valid positions, 168 winning ones (42 losing).
	Summary:
		(0 extra bits, 0 repetitions) => 6 cases.
		(2 extra bits, 0 repetitions) => 18 cases.
		(2 extra bits, 1 repetitions, with 0) => 18 cases.
	Losing cases for N=4:
	Nonzero cases for N=4: 32760 valid positions, 30240 winning ones (2520 losing).
	Summary:
		(0 extra bits, 0 repetitions) => 168 cases.
		(2 extra bits, 0 repetitions) => 1008 cases.
		(2 extra bits, 0 repetitions, with 0) => 504 cases.
		(4 extra bits, 0 repetitions) => 168 cases.
		(4 extra bits, 0 repetitions, with 0) => 168 cases.
		(2 extra bits, 2 repetitions) => 504 cases.
	Losing cases for N=5:
	Nonzero cases for N=5: 20389320 valid positions, 19764360 winning ones (624960 losing).
	Summary:
		(0 extra bits, 0 repetitions) => 20160 cases.
		(2 extra bits, 0 repetitions) => 201600 cases.
		(2 extra bits, 0 repetitions, with 0) => 50400 cases.
		(4 extra bits, 0 repetitions) => 100800 cases.
		(4 extra bits, 0 repetitions, with 0) => 50400 cases.
		(2 extra bits, 1 repetitions) => 151200 cases.
		(4 extra bits, 1 repetitions) => 50400 cases.
	Losing cases for N=6:
	Nonzero cases for N=6: 48920775120 valid positions, 48159573840 winning ones (761201280 losing).
	Summary:
		(0 extra bits, 0 repetitions) => 16248960 cases.
		(2 extra bits, 0 repetitions) => 243734400 cases.
		(2 extra bits, 0 repetitions, with 0) => 18748800 cases.
		(4 extra bits, 0 repetitions) => 243734400 cases.
		(4 extra bits, 0 repetitions, with 0) => 37497600 cases.
		(6 extra bits, 0 repetitions) => 16248960 cases.
		(6 extra bits, 0 repetitions, with 0) => 3749760 cases.
		(2 extra bits, 1 repetitions) => 84369600 cases.
		(2 extra bits, 1 repetitions, with 0) => 3124800 cases.
		(4 extra bits, 1 repetitions) => 84369600 cases.
		(4 extra bits, 1 repetitions, with 0) => 9374400 cases.
	Losing cases for N=7:
	Nonzero cases for N=7: 450356335506000 valid positions, 446837728055040 winning ones (3518607450960 losing).
	Summary:
		(0 extra bits, 0 repetitions) => 43592366160 cases.
		(2 extra bits, 0 repetitions) => 915439689360 cases.
		(2 extra bits, 0 repetitions, with 0) => 31970453760 cases.
		(4 extra bits, 0 repetitions) => 1525732815600 cases.
		(4 extra bits, 0 repetitions, with 0) => 106568179200 cases.
		(6 extra bits, 0 repetitions) => 305146563120 cases.
		(6 extra bits, 0 repetitions, with 0) => 31970453760 cases.
		(2 extra bits, 1 repetitions) => 159852268800 cases.
		(2 extra bits, 1 repetitions, with 0) => 2903720400 cases.
		(4 extra bits, 1 repetitions) => 319704537600 cases.
		(4 extra bits, 1 repetitions, with 0) => 17422322400 cases.
		(6 extra bits, 1 repetitions) => 31970453760 cases.
		(6 extra bits, 1 repetitions, with 0) => 2903720400 cases.
		(2 extra bits, 2 repetitions) => 5807440800 cases.
		(4 extra bits, 2 repetitions) => 17422322400 cases.
		(4 extra bits, 3 repetitions, with 0) => 200143440 cases.
	Elapsed 2346.7027052000003 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long factorial=1;
		for (int i=2;i<=N;++i)	{
			int bit=1<<(i-1);
			System.out.println("Losing cases for N="+i+":");
			int[] working=new int[i];
			factorial*=i;
			long valid=0;
			long winning=0;
			SortedMap<Identifier,Long> counters=new TreeMap<>();
			for (int[] arr:new CombinationIterator(i,(1<<i)-1,false)) if (isValid(arr))	{
				++valid;
				boolean isWinning=isWinning(arr);
				if (isWinning) ++winning;
				else	{
					for (int j=0;j<i;++j) working[j]=arr[j]+1;
					StringBuilder sb=new StringBuilder();
					sb.append('\t');
					sb.append(Arrays.toString(working));
					int extraBit=0;
					for (int j=0;j<i;++j) if (working[j]>=bit)	{
						working[j]-=bit;
						++extraBit;
					}
					sb.append(" => ");
					sb.append(Arrays.toString(working));
					sb.append(" with ").append(extraBit).append(" extra bits (");
					int reps=0;
					boolean zero=false;
					for (int j=0;j<i;++j)	{
						if (working[j]==0)	{
							if (!zero) zero=true;
							else throw new IllegalArgumentException(":O :O :O");
						}
						for (int k=j+1;k<i;++k) if (working[j]==working[k]) ++reps;
					}
					sb.append(reps).append(" repetitions");
					if (zero) sb.append(", also 0");
					sb.append(").");
					// System.out.println(sb.toString());
					EulerUtils.increaseCounter(counters,new Identifier(extraBit,reps,zero),factorial);
				}
			}
			valid*=factorial;
			winning*=factorial;
			System.out.println(String.format("Nonzero cases for N=%d: %d valid positions, %d winning ones (%d losing).",i,valid,winning,valid-winning));
			System.out.println("Summary:");
			for (Map.Entry<Identifier,Long> entry:counters.entrySet()) System.out.println(String.format("\t%s => %d cases.",entry.getKey().toString(),entry.getValue().longValue()));
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
