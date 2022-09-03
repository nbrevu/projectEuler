package com.euler;

import java.util.Arrays;
import java.util.NavigableSet;
import java.util.OptionalInt;
import java.util.TreeSet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class Euler750_3 {
	private final static int N=976;
	
	private static class CardPosition	{
		public final short lower;
		public final short upper;
		public CardPosition(short lower,short upper)	{
			this.lower=lower;
			this.upper=upper;
		}
		public boolean canConnectTo(CardPosition other)	{
			return (other!=null)&&((upper+1)==other.lower);
		}
	}
	private static class CardPositionCache	{
		private CardPosition[][] objects;
		public CardPositionCache(int size)	{
			objects=new CardPosition[1+size][];
			for (short i=1;i<=size;++i)	{
				objects[i]=new CardPosition[i+1];
				for (short j=1;j<=i;++j) objects[i][j]=new CardPosition(j,i);
			}
		}
		public CardPosition get(int lower,int upper)	{
			return objects[upper][lower];
		}
		public CardPosition get(int fixed)	{
			return objects[fixed][fixed];
		}
	}
	
	private static class CardArrangement implements Comparable<CardArrangement>	{
		private final CardPosition[] cards;
		private CardArrangement(CardPosition[] cards)	{
			this.cards=cards;
		}
		public static CardArrangement initialState(int size,CardPositionCache cache)	{
			int p=1;
			int mod=size+1;
			CardPosition[] cards=new CardPosition[size];
			for (int i=0;i<size;++i)	{
				p*=3;
				p%=mod;
				cards[i]=cache.get(p);
			}
			return new CardArrangement(cards);
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(cards,((CardArrangement)other).cards);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(cards);
		}
		public boolean isFinal()	{
			boolean someFound=false;
			for (CardPosition c:cards) if (c!=null)	{
				if (someFound) return false;
				else someFound=true;
			}
			return someFound;
		}
		// Super-inefficient in terms of space, but somewhat better in terms of time.
		public CardArrangement move(int pos1,int pos2,CardPositionCache cache)	{
			CardPosition[] result=Arrays.copyOf(cards,cards.length);
			result[pos1]=null;
			result[pos2]=cache.get(cards[pos1].lower,cards[pos2].upper);
			return new CardArrangement(result);
		}
		@Override
		public int compareTo(CardArrangement other)	{
			for (int i=0;i<cards.length;++i)	{
				CardPosition p=cards[i];
				CardPosition q=other.cards[i];
				if ((p!=null)&&(q!=null))	{
					int result=p.lower-q.lower;
					if (result!=0) return result;
				}	else if (p!=null) return 1;
				else if (q!=null) return -1;
			}
			return 0;
		}
	}
	
	private static class CardState implements Comparable<CardState>	{
		public final int currentCost;
		public final int heuristic;
		public final CardArrangement cards;
		public CardState(int currentCost,CardArrangement cards)	{
			this.currentCost=currentCost;
			this.cards=cards;
			heuristic=currentCost+calculateHeuristic(cards.cards);
		}
		// Kinda horrible, to be honest.
		private static int calculateHeuristic(CardPosition[] cards)	{
			int result=0;
			for (int i=0;i<cards.length;++i)	{
				if (cards[i]==null) continue;
				int value=cards[i].upper;
				OptionalInt minLeft=OptionalInt.empty();
				OptionalInt minRight=OptionalInt.empty();
				for (int j=i-1;j>=0;--j) if ((cards[j]!=null)&&(cards[j].lower>value))	{
					minLeft=OptionalInt.of(i-j);
					break;
				}
				for (int j=i+1;j<cards.length;++j) if ((cards[j]!=null)&&(cards[j].lower>value))	{
					minRight=OptionalInt.of(j-i);
					break;
				}
				int distance=0;
				if (minLeft.isPresent()&&minRight.isPresent()) distance=Math.min(minLeft.getAsInt(),minRight.getAsInt());
				else if (minLeft.isPresent()) distance=minLeft.getAsInt();
				else if (minRight.isPresent()) distance=minRight.getAsInt();
				result+=distance;
			}
			return result;
		}
		@Override
		public int compareTo(CardState other)	{
			int result=heuristic-other.heuristic;
			if (result!=0) return result;
			result=currentCost-other.currentCost;
			if (result!=0) return result;
			return cards.compareTo(other.cards);
		}
		public boolean isFinal()	{
			return cards.isFinal();
		}
	}
	
	private static void takeHead(NavigableSet<CardState> source,int howMany,NavigableSet<CardState> destination) {
		int i=0;
		for (CardState state:source)	{
			destination.add(state);
			++i;
			if (i>=howMany) break;
		}
	}
	
	private static class CardStateManager	{
		private final CardPositionCache cache;
		private final NavigableSet<CardState> states;
		private final int size;
		private final Cache<CardArrangement,Integer> visited;
		public CardStateManager(int size)	{
			cache=new CardPositionCache(size);
			states=new TreeSet<>();
			states.add(new CardState(0,CardArrangement.initialState(size,cache)));
			this.size=size;
			visited=CacheBuilder.newBuilder().concurrencyLevel(1).maximumSize(1000).initialCapacity(1000).build();
		}
		public int solve()	{
			for (;;)	{
				CardState state=states.pollFirst();
				System.out.println(state.heuristic);
				Integer knownCost=visited.getIfPresent(state.cards);
				if ((knownCost!=null)&&(knownCost.intValue()<state.currentCost)) continue;
				if (state.isFinal()) return state.currentCost;
				// Also kinda horrible.
				NavigableSet<CardState> children=new TreeSet<>();
				for (int i=0;i<size;++i)	{
					CardPosition p=state.cards.cards[i];
					if (p==null) continue;
					for (int j=0;j<size;++j)	{
						CardPosition q=state.cards.cards[j];
						if (p.canConnectTo(q))	{
							CardArrangement newCards=state.cards.move(i,j,cache);
							knownCost=visited.getIfPresent(newCards);
							int newCost=state.currentCost+Math.abs(j-i);
							if ((knownCost==null)||(knownCost.intValue()>newCost)) children.add(new CardState(newCost,newCards));
						}
					}
				}
				takeHead(children,1,states);
			}
		}
	}
	
	public static void main(String[] args)	{
		// I could swear that I'm having a lot of repetition, but I can't afford the whole cache. What about... a "last 1000 used" cache?
		long tic=System.nanoTime();
		CardStateManager aStar=new CardStateManager(N);
		int result=aStar.solve();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
