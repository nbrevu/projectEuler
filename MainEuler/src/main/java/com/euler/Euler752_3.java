package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.LongMatrix;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler752_3 {
	/*
	 * "Elapsed 33423.212533204 seconds." JAJA SI.
	 */
	private final static int LIMIT=IntMath.pow(10,6);
	
	private static class State	{
		public final int a;
		public final int b;
		private State(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		public State()	{
			this(1,1);
		}
		public State next(int mod)	{
			int newA=(a+7*b)%mod;
			int newB=(a+b)%mod;
			return new State(newA,newB);
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d)",a,b);
		}
	}
	
	private static class VisitedValuesCache	{
		private final IntObjMap<IntSet> storage;
		public VisitedValuesCache()	{
			storage=HashIntObjMaps.newMutableMap();
		}
		public void clear()	{
			storage.values().forEach(IntSet::clear);
		}
		public boolean add(State s)	{
			IntSet subStorage=storage.computeIfAbsent(s.a,(int unused)->HashIntSets.newMutableSet());
			return subStorage.add(s.b);
		}
	}
	
	private static class CycleCalculator	{
		private final static LongMatrix M=createMatrix();
		private final static State INITIAL_STATE=new State();
		private final static VisitedValuesCache CACHE=new VisitedValuesCache();
		private static LongMatrix createMatrix()	{
			LongMatrix result=new LongMatrix(2);
			result.assign(0,0,1);
			result.assign(0,1,7);
			result.assign(1,0,1);
			result.assign(1,1,1);
			return result;
		}
		public static long calculateCycle(int x)	{
			/*
			 * Paso 1: iteramos hasta que b=0.
			 * 	Si encontramos un valor ya repetido de (a,b), RETURN!
			 * 	Esto podría ser complicado. ¿Cómo de largos son los ciclos? Si no son lineales, esto no es factible para 10^6 :(.
			 * Paso 2: sea N el valor del ciclo para el cual b=0. Calculamos la matriz A=M^N.
			 * Paso 3: asumimos que A=M^N es de la forma r*I(2). Si no es así, ABORT!!!!!
			 * Paso 4: calculamos el menor valor P tal que r^P==1 (mod x). Debe ser un divisor de x-1, pero también se puede comprobar linealmente.
			 * Paso 5: g(n)=N*P. ENDUT, HOCH HECH!
			 */
			CACHE.clear();
			State current=INITIAL_STATE;
			CACHE.add(current);
			for (int i=2;;++i)	{
				State next=current.next(x);
				if (next.b==0) return findSecondCycle(x,i);
				else if (!CACHE.add(next)) return 0l;
				current=next;
			}
		}
		private static long findExponent(long r,long mod)	{
			// Stupid method, but it works.
			if (r==1) return 1l;
			else if (EulerUtils.gcd(r,mod)!=1l) return 0l;
			long current=r;
			for (int i=2;;++i)	{
				current=(current*r)%mod;
				if (current==1) return i;
			}
		}
		private static long findSecondCycle(long x,long firstCycle)	{
			LongMatrix exp=M.pow(firstCycle,x);
			long r=exp.get(0,0);
			// if ((exp.get(0,1)!=0)||(exp.get(1,0)!=0)||(exp.get(1,1)!=r)) throw new IllegalStateException("Esto no es tan fácil como pensaba.");
			return firstCycle*findExponent(r,x);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		for (int i=5;i<=LIMIT;++i)	{
			if (((i%2)==0)||((i%3)==0)) continue;
			long r=CycleCalculator.calculateCycle(i);
			result+=r;
			System.out.println("g("+i+")="+r+".");
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
