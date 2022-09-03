package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler706_2 {
	private final static int BASE_NUMBER=3;
	private final static int DIGIT_BASE=10;
	private final static int SIZE=IntMath.pow(10,5);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static class StateAnalyser	{
		private final int N;
		private final State[] states;
		public class State	{
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
				for (int i=0;i<N;++i) result=(result*N)+terminatedCounters[i];
				for (int i=0;i<N;++i) result=(result*N)+counters[i];
				return result;
			}
			public int getId()	{
				return id;
			}
			public boolean isATargetState()	{
				return (terminatedCounters[0]+counters[0])%N==0;
			}
			public State nextState(int digit)	{
				digit%=N;
				int[] newTerminatedCounters=new int[N];
				int[] newCounters=new int[N];
				for (int i=0;i<N;++i) newTerminatedCounters[i]=(terminatedCounters[i]+counters[i])%N;
				for (int i=0;i<N;++i) newCounters[(i+digit)%N]=counters[i];
				newCounters[digit]=(1+newCounters[digit])%N;
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
		}
		private State getStateFromId(int id)	{
			int[] terminatedCounters=new int[N];
			int[] counters=new int[N];
			for (int i=N-1;i>=0;--i)	{
				counters[i]=id%N;
				id/=N;
			}
			for (int i=N-1;i>=0;--i)	{
				terminatedCounters[i]=id%N;
				id/=N;
			}
			return new State(terminatedCounters,counters);
		}
		public StateAnalyser(int baseNumber)	{
			N=baseNumber;
			int nStates=IntMath.pow(N,2*N);
			states=new State[nStates];
			for (int i=0;i<nStates;++i) states[i]=getStateFromId(i);
		}
		public List<IntIntMap> getTransitionMatrix(int digitBase)	{
			int nStates=states.length;
			List<IntIntMap> result=new ArrayList<>(nStates);
			for (int i=0;i<nStates;++i)	{
				State origin=states[i];
				IntIntMap transitions=HashIntIntMaps.newMutableMap();
				for (int j=0;j<digitBase;++j)	{
					State transition=origin.nextState(j);
					transitions.addValue(transition.getId(),1,0);
				}
				result.add(transitions);
			}
			return result;
		}
		public long[] getInitialState(int digitBase)	{
			long[] result=new long[states.length];
			int[] empty=new int[N];
			for (int i=1;i<digitBase;++i)	{
				int[] currentState=new int[N];
				currentState[i%N]=1;
				State associatedState=new State(empty,currentState);
				++result[associatedState.getId()];
			}
			return result;
		}
		public IntSet getTargetValues()	{
			IntSet result=HashIntSets.newMutableSet();
			for (State state:states) if (state.isATargetState()) result.add(state.getId());
			return result;
		}
	}
	
	private static long[] getNextLevel(long[] currentLevel,List<IntIntMap> transitionMatrix,long mod)	{
		int N=currentLevel.length;
		long[] result=new long[N];
		for (int i=0;i<N;++i)	{
			long currentValue=currentLevel[i];
			if (currentValue==0) continue;
			for (IntIntCursor cursor=transitionMatrix.get(i).cursor();cursor.moveNext();) result[cursor.key()]+=cursor.value()*currentValue;
		}
		for (int i=0;i<N;++i) result[i]%=mod;
		return result;
	}
	
	private static long solveProblem(int baseNumber,int digitBase,int size,long mod)	{
		StateAnalyser analyser=new StateAnalyser(baseNumber);
		List<IntIntMap> transitionMatrix=analyser.getTransitionMatrix(digitBase);
		long[] currentState=analyser.getInitialState(digitBase);
		for (int i=2;i<=size;++i) currentState=getNextLevel(currentState,transitionMatrix,mod);
		long result=0;
		IntSet targetValues=analyser.getTargetValues();
		for (IntCursor cursor=targetValues.cursor();cursor.moveNext();) result+=currentState[cursor.elem()];
		result%=mod;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=solveProblem(BASE_NUMBER,DIGIT_BASE,SIZE,MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
