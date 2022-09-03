package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Euler796 {
	private final static int NUM_DECKS=10;
	private final static int NUM_SUITS=4;
	private final static int NUM_RANKS=13;
	private final static int NUM_JOKERS=2;
	
	private abstract static class Card	{
		public final int deck;
		protected Card(int deck)	{
			this.deck=deck;
		}
	}
	private static class NormalCard extends Card	{
		public final int suit;
		public final int rank;
		public NormalCard(int deck,int suit,int rank)	{
			super(deck);
			this.suit=suit;
			this.rank=rank;
		}
	}
	private static class Joker extends Card	{
		public Joker(int deck)	{
			super(deck);
		}
	}
	private static List<Card> generateDecks(int decks,int suits,int ranks,int jokers)	{
		int cardsPerDeck=suits*ranks+jokers;
		int totalCards=cardsPerDeck*decks;
		List<Card> result=new ArrayList<>(totalCards);
		for (int i=0;i<decks;++i)	{
			Card joker=new Joker(i);
			for (int j=0;j<jokers;++j) result.add(joker);
			for (int j=0;j<suits;++j) for (int k=0;k<ranks;++k) result.add(new NormalCard(i,j,k));
		}
		return result;
	}
	private static class CardCounter	{
		private final int numDecks;
		private final int numSuits;
		private final int numRanks;
		private final List<Card> cards;
		private final BitSet decks;
		private final BitSet suits;
		private final BitSet ranks;
		public CardCounter(int numDecks,int numSuits,int numRanks,int numJokers)	{
			this.numDecks=numDecks;
			this.numSuits=numSuits;
			this.numRanks=numRanks;
			cards=generateDecks(numDecks,numSuits,numRanks,numJokers);
			decks=new BitSet(numDecks);
			suits=new BitSet(numSuits);
			ranks=new BitSet(numRanks);
		}
		public int count()	{
			decks.clear();
			suits.clear();
			ranks.clear();
			Collections.shuffle(cards);
			for (int i=0;;++i)	{
				Card c=cards.get(i);
				decks.set(c.deck);
				if (c instanceof NormalCard)	{
					NormalCard nc=(NormalCard)c;
					suits.set(nc.suit);
					ranks.set(nc.rank);
				}
				if ((decks.cardinality()==numDecks)&&(suits.cardinality()==numSuits)&&(ranks.cardinality()==numRanks)) return i+1;
			}
		}
		public double monteCarlo(long numTries)	{
			long sum=0;
			for (long i=0;i<numTries;++i) sum+=count();
			return ((double)sum)/(double)numTries;
		}
	}
	
	/*
	 * There is no way this returns the correct result, but it's a good estimate. The integer part will be probably correct, and maybe the first
	 * one or two decimal digits.
	 * 
	 * 43.20538243
	 * Elapsed 279.6885358 seconds.
	 * 
	 * 43.207104862
	 * Elapsed 2767.8984406 seconds.
	 * 
	 * 43.2066102235
	 * Elapsed 5727.007183000001 seconds.
	 * 
	 * The real solution is around 43.206, probably. If the amount of states is small enough, this can be done with Markov chains.
	 * There are four possible cases:
	 * 1) the amount of states is low and the matrix is manageable. Probably not the case, but who knows.
	 * 2) the amount of states is lowish but the matrix is too big. Magic will be required. 481 again?
	 * 3) the amount of states is too big, and I will need something like dynamic programming.
	 * 4) the amount of states is actually really fucking big and dynamic programming won't help me. I would need to resort to probability.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CardCounter c=new CardCounter(NUM_DECKS,NUM_SUITS,NUM_RANKS,NUM_JOKERS);
		double result=c.monteCarlo(2_000_000_000l);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
