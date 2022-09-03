package com.euler;

import java.util.Arrays;

import com.euler.common.LongMatrix;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler706 {
	private final static int BASE_NUMBER=3;
	private final static int DIGIT_BASE=10;
	private final static int N=2;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static class State	{
		/*
		 * The length of counters and terminatedCounters is BASE_NUMBER, and each value can be anything from 0 to BASE_NUMBER-1. Therefore, there
		 * are BASE_NUMBER^(2*BASE_NUMBER) possible states. For BASE_NUMBER=3, we have 729 states; for BASE_NUMBER=4, we have 65536, and for
		 * BASE_NUMBER=5, we have 9765625.
		 */
		private final int[] terminatedCounters;
		private final int[] counters;
		private final int id;
		private State(int[] terminatedCounters,int[] counters)	{
			this.terminatedCounters=terminatedCounters;
			this.counters=counters;
			id=calculateId();
		}
		private int calculateId()	{
			int result=0;
			for (int i=0;i<BASE_NUMBER;++i) result=(result*BASE_NUMBER)+terminatedCounters[i];
			for (int i=0;i<BASE_NUMBER;++i) result=(result*BASE_NUMBER)+counters[i];
			return result;
		}
		public int getId()	{
			return id;
		}
		public boolean isATargetState()	{
			return (terminatedCounters[0]+counters[0])%BASE_NUMBER==0;
		}
		public State nextState(int digit)	{
			digit%=BASE_NUMBER;
			int[] newTerminatedCounters=new int[BASE_NUMBER];
			int[] newCounters=new int[BASE_NUMBER];
			for (int i=0;i<BASE_NUMBER;++i) newTerminatedCounters[i]=(terminatedCounters[i]+counters[i])%BASE_NUMBER;
			for (int i=0;i<BASE_NUMBER;++i) newCounters[(i+digit)%BASE_NUMBER]=counters[i];
			newCounters[digit]=(1+newCounters[digit])%BASE_NUMBER;
			return new State(newTerminatedCounters,newCounters);
		}
		@Override
		public boolean equals(Object other)	{
			State sOther=(State)other;
			return Arrays.equals(terminatedCounters,sOther.terminatedCounters)&&Arrays.equals(counters,sOther.counters);
		}
		@Override
		public int hashCode()	{
			return id;
		}
		public static State getFromId(int id)	{
			int[] terminatedCounters=new int[BASE_NUMBER];
			int[] counters=new int[BASE_NUMBER];
			/*-
			int result=0;
			for (int i=0;i<BASE_NUMBER;++i) result=(result*BASE_NUMBER)+terminatedCounters[i];
			for (int i=0;i<BASE_NUMBER;++i) result=(result*BASE_NUMBER)+counters[i];
			return result;
			 */
			for (int i=BASE_NUMBER-1;i>=0;--i)	{
				counters[i]=id%BASE_NUMBER;
				id/=BASE_NUMBER;
			}
			for (int i=BASE_NUMBER-1;i>=0;--i)	{
				terminatedCounters[i]=id%BASE_NUMBER;
				id/=BASE_NUMBER;
			}
			return new State(terminatedCounters,counters);
		}
	}
	
	private static LongMatrix getTransitionMatrix()	{
		int n=IntMath.pow(BASE_NUMBER,2*BASE_NUMBER);
		LongMatrix result=new LongMatrix(n);
		for (int i=0;i<n;++i)	{
			State state=State.getFromId(i);
			for (int j=0;j<DIGIT_BASE;++j)	{
				int target=state.nextState(j).getId();
				result.assign(i,target,1+result.get(i,target));
			}
		}
		return result;
	}
	
	private static IntSet getValidStates()	{
		int n=IntMath.pow(BASE_NUMBER,2*BASE_NUMBER);
		IntSet result=HashIntSets.newMutableSet();
		for (int i=0;i<n;++i) if (State.getFromId(i).isATargetState()) result.add(i);
		return result;
	}
	
	private static long sumValues(LongMatrix matrix,IntSet validStates)	{
		long result=0;
		for (IntCursor cursor=validStates.cursor();cursor.moveNext();) result+=matrix.get(0,cursor.elem());
		return result;
	}
	
	public static void main(String[] args)	{
		// ZUTUN! Algo va mal :(. Pero voy por buen camino.
		IntSet validStates=getValidStates();
		LongMatrix baseMatrix=getTransitionMatrix();
		LongMatrix currentMatrix=baseMatrix;
		long toSubtract=1;
		for (int i=1;i<N;++i)	{
			toSubtract+=sumValues(currentMatrix,validStates);
			currentMatrix=currentMatrix.multiply(baseMatrix);
		}
		long result=sumValues(currentMatrix,validStates)-toSubtract;
		result%=MOD;
		System.out.println(result);
	}
}
