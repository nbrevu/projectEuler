package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

/*
 * Esto se sale de mi nivel de conocimiento.
 * Está muy relacionado con un problema conocido (chessboard pebbling), pero no es exactamente igual debido al límite de tamaño.
 * Hay un paper que da la solución cerrada al problema original en forma de integral tocha de las de sudar sangre.
 * También hay un montón de secuencias en OEIS con un crecimiento similar (se pueden comprobar buscando por "4,9,20,46", por ejemplo).
 * La más cercana es https://oeis.org/A007902.
 */
public class Euler762 {
	private final static int NBITS=4;
	
	private static class Transition	{
		public final int remaining;
		public final int nextColumn;
		public Transition(int remaining,int nextColumn)	{
			this.remaining=remaining;
			this.nextColumn=nextColumn;
		}
	}
	
	private static Transition[][] getAllTransitions(int bits)	{
		IntIntMap baseTransitions=HashIntIntMaps.newMutableMap();
		int bit=1;
		for (int i=1;i<bits;++i)	{
			baseTransitions.put(bit,bit*3);
			bit<<=1;
		}
		baseTransitions.put(bit,bit+1);	// Last transition that "rolls over".
		int limit=(1<<bits);
		Transition[][] result=new Transition[limit][];
		for (int i=0;i<limit;++i)	{
			List<Transition> thisList=new ArrayList<>();
			for (IntIntCursor cursor=baseTransitions.cursor();cursor.moveNext();)	{
				int originBit=cursor.key();
				if ((i&originBit)==0) continue;
				Transition target=new Transition(i-originBit,cursor.value());
				thisList.add(target);
			}
			result[i]=thisList.toArray(Transition[]::new);
		}
		return result;
	}
	
	private static class Simulator	{
		private static class Amoebas	{
			public final int[] columns;
			public Amoebas(int[] columns)	{
				this.columns=columns;
			}
			@Override
			public int hashCode()	{
				return Arrays.hashCode(columns);
			}
			@Override
			public boolean equals(Object other)	{
				return Arrays.equals(columns,((Amoebas)other).columns);
			}
		}
		private final Transition[][] transitions;
		private Set<Amoebas> states;
		public Simulator(int nBits)	{
			transitions=getAllTransitions(nBits);
			states=Set.of(new Amoebas(new int[] {1}));
		}
		private void getTransitions(Amoebas state,Set<Amoebas> states)	{
			int n=state.columns.length;
			for (int i=0;i<n;++i)	{
				boolean isLast=(i>=n-1);
				int column=state.columns[i];
				int nextColumn=(isLast?0:state.columns[i+1]);
				int nextSize=(isLast?(n+1):n);
				for (Transition t:transitions[column])	{
					int target=t.nextColumn;
					if ((target&nextColumn)!=0) continue;
					int[] newArray=Arrays.copyOf(state.columns,nextSize);
					newArray[i]=t.remaining;
					newArray[i+1]=nextColumn|target;
					states.add(new Amoebas(newArray));
				}
			}
		}
		public void nextGeneration()	{
			Set<Amoebas> result=new HashSet<>();
			for (Amoebas state:states) getTransitions(state,result);
			states=result;
		}
		public int getSize()	{
			return states.size();
		}
	}
	
	public static void main(String[] args)	{
		Simulator sim=new Simulator(NBITS);
		System.out.println(String.format("C(%d)=%d.",0,sim.getSize()));
		for (int i=1;i<=20;++i)	{
			sim.nextGeneration();
			System.out.println(String.format("C(%d)=%d.",i,sim.getSize()));
		}
	}
}
