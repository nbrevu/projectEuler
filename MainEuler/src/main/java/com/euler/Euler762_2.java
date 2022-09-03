package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

/*
Diagonal successions: 
i=0 => [1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608].
i=1 => [0, 0, 0, 2, 8, 24, 64, 160, 384, 896, 2048, 4608, 10240, 22528, 49152, 106496, 229376, 491520, 1048576, 2228224, 4718592, 9961472, 20971520].
i=2 => [0, 2, 4, 8, 20, 50, 124, 304, 736, 1760, 4160, 9728, 22528, 51712, 117760, 266240, 598016, 1335296, 2965504, 6553600, 14417920, 31588352].
i=3 => [0, 4, 10, 22, 48, 114, 272, 650, 1552, 3696, 8768, 20704, 48640, 113664, 264192, 610816, 1404928, 3215360, 7323648, 16605184, 37486592].
i=4 => [0, 10, 24, 56, 126, 284, 660, 1540, 3604, 8450, 19828, 46520, 109040, 255168, 595840, 1387776, 3223040, 7462400, 17222656, 39618560].
i=5 => [0, 22, 55, 128, 298, 682, 1562, 3620, 8408, 19566, 45600, 106386, 248344, 579784, 1353088, 3155392, 7350016, 17095936, 39696384].
i=6 => [0, 52, 130, 304, 705, 1632, 3756, 8648, 20002, 46318, 107384, 249232, 578996, 1346066, 3130924, 7284192, 16946368, 39413312].
i=7 => [0, 118, 298, 700, 1633, 3790, 8785, 20312, 46962, 108748, 251930, 583922, 1354128, 3141882, 7293296, 16936570, 39341088].
i=8 => [0, 276, 695, 1634, 3811, 8854, 20532, 47588, 110187, 255114, 590988, 1369288, 3173286, 7355976, 17056796, 39562220].
i=9 => [0, 636, 1609, 3784, 8839, 20554, 47729, 110734, 256825, 595362, 1379971, 3198986, 7415762, 17191766, 39858730].
i=10 => [0, 1480, 3741, 8804, 20558, 47812, 111035, 257714, 597942, 1387098, 3217026, 7460360, 17300737, 40119230].
i=11 => [0, 3426, 8673, 20416, 47705, 110976, 257820, 598608, 1389484, 3224654, 7482766, 17361226, 40277273].
i=12 => [0, 7962, 20150, 47438, 110831, 257844, 598998, 1390834, 3228688, 7494258, 17393727, 40367090].
i=13 => [0, 18464, 46755, 110086, 257256, 598554, 1390718, 3229456, 7497781, 17405696, 40403962].
i=14 => [0, 42882, 108573, 255652, 597398, 1389980, 3229524, 7499616, 17412028, 40422762].
i=15 => [0, 99516, 252020, 593438, 1386838, 3226904, 7497861, 17412138, 40427836].
i=16 => [0, 231068, 585142, 1377884, 3219996, 7492358, 17408772, 40428318].
i=17 => [0, 536372, 1358382, 3198736, 7475407, 17394158, 40416724].
i=18 => [0, 1245314, 3153752, 7426552, 17355640, 40384090].
i=19 => [0, 2890982, 7321613, 17241250, 40292776].
i=20 => [0, 6711884, 16998204, 40028214].
i=21 => [0, 15582118, 39462980].
i=22 => [0, 36175988].
i=23 => [0].

Sigo sin ver nada claro :(. El caso es que la dificultad está en 50%, así que debe de haber truco. O un paper que lo cuenta todo, claro.

Posible truqui: contamos la cantidad de "transiciones" (grupos de columna X/columna Y). ¿Necesito también la columna "anterior"? Probablemente.
Pero entonces necesito información potencialmente infinita en cada punto...
 */
public class Euler762_2 {
	private final static int NBITS=4;
	private final static int STEPS=24;
	
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
		private void getTransitions(Amoebas state,Set<Amoebas> states,Map<Integer,Integer> counters)	{
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
					EulerUtils.increaseCounter(counters,i);
					newArray[i]=t.remaining;
					newArray[i+1]=nextColumn|target;
					states.add(new Amoebas(newArray));
				}
			}
		}
		public Map<Integer,Integer> nextGeneration()	{
			Map<Integer,Integer> result=new TreeMap<>();
			Set<Amoebas> newStates=new HashSet<>();
			for (Amoebas state:states) getTransitions(state,newStates,result);
			states=newStates;
			return result;
		}
		public int getSize()	{
			return states.size();
		}
	}
	
	public static void main(String[] args)	{
		Simulator sim=new Simulator(NBITS);
		System.out.println(String.format("C(%d)=%d.",0,sim.getSize()));
		List<Map<Integer,Integer>> theTransitions=new ArrayList<>();
		for (int i=1;i<=STEPS;++i)	{
			theTransitions.add(sim.nextGeneration());
			System.out.println(String.format("C(%d)=%d.",i,sim.getSize()));
		}
		System.out.println("Diagonal successions: ");
		for (int i=0;i<STEPS;++i)	{
			int[] succession=new int[STEPS-i];
			for (int j=i;j<STEPS;++j) succession[j-i]=theTransitions.get(j).getOrDefault(j-i,0).intValue();
			System.out.println(String.format("i=%d => %s.",i,Arrays.toString(succession)));
		}
	}
}
