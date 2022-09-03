package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Euler750_9 {
	private final static int N=976;
	
	private static class ArrayWrapper	{
		public final int[] array;
		public ArrayWrapper(int[] array)	{
			this.array=array;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			ArrayWrapper awOther=(ArrayWrapper)other;
			return Arrays.equals(array,awOther.array);
		}
	}
	
	private static class CardArrangementMutator	{
		public final int[] cards;
		public final int[] positions;
		public final int[] tmpLowCard;
		public final int[] tmpReversePos;
		/*
		 * "currentArrangement" is a shuffle of the N-1 numbers between 0 and N-2. First we move the card with number currentArrangement[0] to
		 * whichever place currentArrangement[0]+1 is, then we move the card with number currentArrangement[1] to the position of
		 * currentArrangement[1]+1, etc.
		 */
		public CardArrangementMutator(int size)	{
			cards=new int[size];
			positions=new int[size];
			int p=1;
			int mod=size+1;
			for (int i=0;i<size;++i)	{
				p*=3;
				p%=mod;
				cards[i]=p-1;
				positions[p-1]=i;
			}
			tmpLowCard=new int[size];
			tmpReversePos=new int[size];
		}
		private int[] getInitialArrangement()	{
			int[] result=new int[cards.length-1];
			for (int i=0;i<result.length;++i) result[i]=i;
			return result;
		}
		public int[] mutate(int[] currentArrangement,int p1,int p2)	{
			int[] result=Arrays.copyOf(currentArrangement,currentArrangement.length);
			int swap=result[p1];
			result[p1]=result[p2];
			result[p2]=swap;
			return result;
		}
		public int[] mutate(int[] currentArrangement,Random r)	{
			int p1,p2;
			int n=currentArrangement.length;
			do	{
				p1=r.nextInt(n);
				p2=r.nextInt(n);
			}	while (p1==p2);
			return mutate(currentArrangement,p1,p2);
		}
		public int getCost(int[] arrangement)	{
			System.arraycopy(cards,0,tmpLowCard,0,cards.length);
			System.arraycopy(positions,0,tmpReversePos,0,positions.length);
			int result=0;
			for (int i=0;i<arrangement.length;++i)	{
				int currentCard=arrangement[i];
				int nextCard=1+currentCard;
				int currentPos=positions[currentCard];
				int nextPos=tmpReversePos[nextCard];
				result+=Math.abs(nextPos-currentPos);
				int lowerMovedCard=tmpLowCard[currentPos];
				tmpLowCard[nextPos]=lowerMovedCard;
				tmpReversePos[lowerMovedCard]=nextPos;
			}
			return result;
		}
	}
	
	public static class Population	{
		public final int maxSize;
		public final Map<ArrayWrapper,Integer> population;	// Used for direct queries and to cache cost.
		public final List<ArrayWrapper> sortedPopulation;	// Used for random access and for culling.
		private final Comparator<ArrayWrapper> comparator;
		public Population(int maxSize)	{
			this.maxSize=maxSize;
			population=new HashMap<>();
			sortedPopulation=new ArrayList<>();
			comparator=Comparator.comparingInt(population::get);
		}
		public int[] getRandomElement(Random r)	{
			return sortedPopulation.get(r.nextInt(sortedPopulation.size())).array;
		}
		public int getBestCost()	{
			return population.get(sortedPopulation.get(0));
		}
		public void insert(int[] element,int cost)	{
			ArrayWrapper wrapped=new ArrayWrapper(element);
			if (population.containsKey(wrapped)) return;
			population.put(wrapped,cost);
			int position=Collections.binarySearch(sortedPopulation,wrapped,comparator);
			if (position<0) position=-1-position;
			sortedPopulation.add(position,wrapped);
			while (sortedPopulation.size()>maxSize)	{
				ArrayWrapper toDelete=sortedPopulation.get(sortedPopulation.size()-1);
				population.remove(toDelete);
				sortedPopulation.remove(sortedPopulation.size()-1);
			}
		}
		public String getBestElement()	{
			return Arrays.toString(sortedPopulation.get(0).array);
		}
	}
	
	public static void main(String[] args)	{
		CardArrangementMutator mutator=new CardArrangementMutator(N);
		Population population=new Population(500);
		Random r=new Random();
		int[] initialArrangement=mutator.getInitialArrangement();
		int cost=mutator.getCost(initialArrangement);
		population.insert(initialArrangement,cost);
		int nextToShow=100000;
		for (int i=0;i<60000000;++i)	{
			int[] newArrangement=mutator.mutate(population.getRandomElement(r),r);
			cost=mutator.getCost(newArrangement);
			population.insert(newArrangement,cost);
			if (i==nextToShow)	{
				StringBuilder sb=new StringBuilder();
				sb.append("Current stage: ").append(i).append(". Current best cost: ").append(population.getBestCost()).append('.');
				System.out.println(sb.toString());
				System.out.println(population.getBestElement());
				nextToShow+=100000;
			}
		}
		System.out.println(population.getBestCost());
	}
}
