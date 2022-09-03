package com.euler;

import java.util.NavigableSet;
import java.util.TreeSet;

public class Euler750_4 {
	private final static int N=976;

	private static int solve(int size)	{
		// This is a greedy algorithm. It's not intended to return the proper solution, but I'd like to see how close it gets to the actual solution.
		int p=1;
		int mod=size+1;
		int[] reverseArray=new int[size];
		for (int i=0;i<size;++i)	{
			p*=3;
			p%=mod;
			reverseArray[p-1]=i;
		}
		int result=0;
		NavigableSet<Integer> visitedPositions=new TreeSet<>();
		visitedPositions.add(reverseArray[size-1]);
		for (int i=size-2;i>=0;--i)	{
			Integer currentPosition=reverseArray[i];
			Integer lastBefore=visitedPositions.floor(currentPosition);
			Integer firstAfter=visitedPositions.ceiling(currentPosition);
			int diff1=(lastBefore==null)?Integer.MAX_VALUE:(currentPosition-lastBefore);
			int diff2=(firstAfter==null)?Integer.MAX_VALUE:(firstAfter-currentPosition);
			result+=Math.min(diff1,diff2);
			visitedPositions.add(currentPosition);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		/*
		 * OK, this doesn't work, but I have an idea using dynamic programming (of sorts) that MIGHT just work.
		 * 
		 * First idea: work with positions, i.e. using the "reverse array" of the method above.
		 * Second idea: for every position we will store an array with the positions on which this card is found.
		 * 	For example, let's say that we have cards "123". If we do 2->3 and then 1->2, the positions of "1" are first and third, but if we do
		 *  1->2 first and then 2->3, the positions for "1" are the first, second and third.
		 * 	So, a possible "solution" for a given position includes the cost so far and the list of visited positions.
		 * Third idea: to have some leeway, in fact we can have, why not, several solutions for each position. With these, we can span the "best"
		 *  solutions for the next position. At each step, if we have N solutions, we can get somewhere between N and 2N solutions for the next one.
		 * With infinite memory/time, this always finds the solution. Unfortunately, we must cull, potentially losing the best solution.
		 * Potential drawbacks: it looks like this algorithm would prioritise solutions with low cost over "larger" solutions that allow more
		 * spaces to be used in the later stages. To mitigate this, instead of cost, we can choose the best X (say, 10000) solutions after sorting
		 * with a clever "sorting" function. In any case this looks promising.
		 */
		System.out.println(solve(N));
	}
}
