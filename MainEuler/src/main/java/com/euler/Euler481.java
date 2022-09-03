package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.euler.common.BitSetCursor;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler481 {
	private final static int N=7;
	/*
	 * First, the definition of the states. There are 2^N-1 possible states regarding the amount of players (since the state with N=0 players
	 * doesn't make sense), N of which are final (states with a single player). If a state has X players remaining, then there are X substates,
	 * each one indicating whom does the current turn belong. So the amount of total states to be considered is Sum(i,0,N,nchoosek(N,i)*i),
	 * which is 2^N*N/2=N*2^(N-1) if I have done my mental math correctly. In any case we are going to have an array of size N*2^N, because that
	 * way we can easily retrieve the turn owner and the set of players cleanly from the index. The lowest N bits represent the available players
	 * and the remaining chunk is the current player (from 0 to N-1).
	 */
	private static class IndexManager	{
		private final int n;
		private final int mask;
		public IndexManager(int n)	{
			this.n=n;
			mask=(1<<n)-1;
		}
		public int getSize()	{
			return n<<n;
		}
		public boolean isValid(int index)	{
			int playerBits=extractPlayerBits(index);
			int currentPlayer=extractCurrentPlayer(index);
			return (playerBits&(1<<currentPlayer))!=0;
		}
		public int extractPlayerBits(int index)	{
			return index&mask;
		}
		public int extractCurrentPlayer(int index)	{
			return index>>n;
		}
		public int getStateIndex(int playerBits,int currentPlayer)	{
			return (currentPlayer<<n)+playerBits;
		}
		public IntCursor playersAsCursor(int playerBits)	{
			return new BitSetCursor(playerBits,true);
		}
		public int[] playersAsArray(int playerBits)	{
			IntStream.Builder builder=IntStream.builder();
			for (IntCursor cursor=playersAsCursor(playerBits);cursor.moveNext();) builder.accept(cursor.elem());
			return builder.build().toArray();
		}
		public int howManyPlayers(int playerBits)	{
			return Integer.bitCount(playerBits);
		}
	}
	
	private static class StateData	{
		public final int[] players;
		public final double[] probabilities;
		public int chosenLoser;	// This is the index of the player that will be eliminated if this turn's owner wins this round.
		private final IntIntMap indexMap;
		public StateData(int[] players)	{
			this.players=players;
			probabilities=new double[players.length];
			chosenLoser=-1;
			indexMap=HashIntIntMaps.newMutableMap();
			for (int i=0;i<players.length;++i) indexMap.put(players[i],i);
		}
		private int getIndex(int player)	{
			return indexMap.getOrDefault(player,-1);
		}
		public double getProbability(int player)	{
			int index=getIndex(player);
			return (index<0)?0:probabilities[getIndex(player)];
		}
		public void setProbability(int player,double value)	{
			probabilities[getIndex(player)]=value;
		}
	}
	private static class StateSet	{
		private final StateData[] states;
		private final List<StateData> validStates;
		/*
		 * First index: amount of players in the turn.
		 * Second index: "playerBits" identifier.
		 * Third index: current turn owner.
		 * This looks complex but it makes iterating easier and kind of obvious for the groupings we need.
		 */
		private final IntObjMap<IntObjMap<IntObjMap<StateData>>> indexedStates;
		private final IndexManager indices;
		public StateSet(int n)	{
			indices=new IndexManager(n);
			int howManyStates=indices.getSize();
			states=new StateData[howManyStates];
			validStates=new ArrayList<>(howManyStates/2);
			indexedStates=HashIntObjMaps.newMutableMap();
			for (int i=0;i<howManyStates;++i) if (indices.isValid(i))	{
				int playerBits=indices.extractPlayerBits(i);
				int currentPlayer=indices.extractCurrentPlayer(i);
				int howManyPlayers=indices.howManyPlayers(playerBits);
				StateData state=new StateData(indices.playersAsArray(playerBits));
				states[i]=state;
				validStates.add(state);
				IntObjMap<IntObjMap<StateData>> innerMap=indexedStates.computeIfAbsent(howManyPlayers,(int unused)->HashIntObjMaps.newMutableMap());
				IntObjMap<StateData> evenInnerMap=innerMap.computeIfAbsent(playerBits,(int unused)->HashIntObjMaps.newMutableMap());
				evenInnerMap.put(currentPlayer,state);
			}
		}
		public StateData getState(int playerBits,int currentPlayer)	{
			return states[indices.getStateIndex(playerBits,currentPlayer)];
		}
		public IntObjMap<IntObjMap<StateData>> getStatesWithNPlayers(int n)	{
			return indexedStates.get(n);
		}
		public int[] getPlayers(int playerBits)	{
			return indices.playersAsArray(playerBits);
		}
		public List<StateData> getReachableStates()	{
			Set<StateData> result=new LinkedHashSet<>();
			getReachableStatesRecursive((1<<N)-1,result);
			return new ArrayList<>(result);
		}
		private void getReachableStatesRecursive(int playerBits,Set<StateData> currentSet)	{
			int nPlayers=Integer.bitCount(playerBits);
			Collection<StateData> states=indexedStates.get(nPlayers).get(playerBits).values();
			if (nPlayers<=2) return;
			if (!currentSet.addAll(states))	return;
			IntSet removedPlayers=HashIntSets.newMutableSet();
			for (StateData state:states) removedPlayers.add(state.chosenLoser);
			for (IntCursor cursor=removedPlayers.cursor();cursor.moveNext();) getReachableStatesRecursive(playerBits-(1<<cursor.elem()),currentSet);
		}
	}
	
	private static double[] getBaseProbabilities(int n)	{
		double[] fibos=new double[n+2];
		fibos[0]=1d;
		fibos[1]=1d;
		for (int i=2;i<fibos.length;++i) fibos[i]=fibos[i-1]+fibos[i-2];
		double[] result=new double[n];
		for (int i=0;i<n;++i) result[i]=fibos[i]/fibos[n];
		return result;
	}
	
	public static void main(String[] args)	{
		StateSet states=new StateSet(N);
		double[] baseProbs=getBaseProbabilities(N);
		// Base cases: probabilities for the turns that have exactly two players.
		for (IntObjCursor<IntObjMap<StateData>> cursor2=states.getStatesWithNPlayers(2).cursor();cursor2.moveNext();)	{
			int playerBits=cursor2.key();
			IntObjMap<StateData> statePerPlayer=cursor2.value();
			int[] players=states.getPlayers(playerBits);
			int p1=players[0];
			int p2=players[1];
			double s1=baseProbs[p1];
			double s2=baseProbs[p2];
			double denom=s1+s2-s1*s2;
			StateData player1State=statePerPlayer.get(p1);
			player1State.chosenLoser=p2;
			player1State.setProbability(p1,s1/denom);
			player1State.setProbability(p2,(denom-s1)/denom);
			StateData player2State=statePerPlayer.get(p2);
			player2State.chosenLoser=p1;
			player2State.setProbability(p1,(denom-s2)/denom);
			player2State.setProbability(p2,s2/denom);
		}
		// Complicated cases: probabilities for the turns that have more than two players. Finesse required. Will take a few tries to get right.
		for (int nPlayers=3;nPlayers<=N;++nPlayers) for (IntObjCursor<IntObjMap<StateData>> cursorN=states.getStatesWithNPlayers(nPlayers).cursor();cursorN.moveNext();)	{
			int playerBits=cursorN.key();
			IntObjMap<StateData> statePerPlayer=cursorN.value();
			int[] players=states.getPlayers(playerBits);
			IntIntMap chosenLosers=HashIntIntMaps.newMutableMap();
			/*
			 * Careful step: each player X chooses the player they will choose to remove if they win their turn. The process is:
			 * 1) For each other player Y, go to the state resulting of removing X. The next player to play is the one that goes after X.
			 * 2) In that state, get the probability of X winning.
			 * 3) After all these have been gathered, choose the player with the minimum probability.
			 */
			for (int p=0;p<players.length;++p)	{
				int me=players[p];
				double maxProb=-1d;
				int maxPlayer=-1;
				for (int q=1;q<players.length;++q)	{
					int other=players[(p+q)%nPlayers];
					if ((playerBits&(1<<other))==0) throw new RuntimeException("Mal.");
					int playerBitsAfter=playerBits-(1<<other);
					int nextTurnIndex=(p+((q==1)?2:1))%nPlayers;
					int nextTurn=players[nextTurnIndex];
					StateData turnIfWins=states.getState(playerBitsAfter,nextTurn);
					double prob=turnIfWins.getProbability(me);
					if (prob>maxProb)	{
						maxProb=prob;
						maxPlayer=other;
					}
				}
				chosenLosers.put(me,maxPlayer);
			}
			/*
			 * Next part. Having chosen these losers, create the equations to determine the probabilities.
			 * I'm not sure at all that the scheme I have in mind is right.
			 */
			for (int i=0;i<nPlayers;++i)	{
				int me=players[i];
				StateData affectedState=statePerPlayer.get(me);
				affectedState.chosenLoser=chosenLosers.get(me);
				double[] numerators=new double[nPlayers];
				double factor=1d;
				/*
				 * "i" is the current turn's owner.
				 * For each turn, we add some probability (multiplied by "factor") to each player.
				 * Finally we divide by 1-factor.
				 */
				for (int j=0;j<nPlayers;++j)	{
					int playerBeingAdded=players[j];
					double s=baseProbs[playerBeingAdded];
					int thisChosenLoser=chosenLosers.get(playerBeingAdded);
					int nextTurn=players[(j+1)%nPlayers];
					if (thisChosenLoser==nextTurn) nextTurn=players[(j+2)%nPlayers];
					if ((playerBits&(1<<thisChosenLoser))==0)	{
						throw new RuntimeException("Mal.");
					}
					int playerBitsAfter=playerBits-(1<<thisChosenLoser);
					StateData stateIfRemoved=states.getState(playerBitsAfter,nextTurn);
					double totalFactor=factor*s;
					for (int k=0;k<nPlayers;++k) numerators[k]+=stateIfRemoved.getProbability(players[k])*totalFactor;
					factor*=1-s;
				}
				double denom=1-factor;
				for (int j=0;j<nPlayers;++j) affectedState.setProbability(players[j],numerators[j]/denom);
			}
		}
		// Some sanity checks...
		for (StateData cosa:states.validStates)	{
			if (cosa.players.length<2) continue;
			double probSum=0d;
			for (double p:cosa.probabilities) probSum+=p;
			// System.out.println(probSum);
			if (Arrays.binarySearch(cosa.players,cosa.chosenLoser)<0) throw new RuntimeException("Vaya.");
			if (Math.abs(probSum-1)>1e-3) throw new RuntimeException("Y cuándo no.");
		}
		/*
		 * Ok. This is PROMISING, but it's not even close to being finished. After all the probabilities have been chosen, we need to:
		 * 1) Generate the sparse matrix. Size about 1e5*1e5, but very sparse (two entries per row, three after doing I-Q).
		 * 2) Do the absorbing Markov chain magic. Hopefully in O(n^2) instead of O(n^3) (is this even possible?).
		 * 3) ENDUT! HOCH HECH!
		 * By the way I will need this: https://math.stackexchange.com/questions/952870/finding-only-first-row-in-a-matrix-inverse.
		 * This is not the first time I do this IIRC.
		 * 
		 * WAIT, I JUST HAD A REVELATION: let's try culling states. Maybe most of the time the removed players are the same and there is
		 * a substantial reduction on the actually reachable set of states. If I get to something like 2000 or 3000 states, this might be doable.
		 * 104074. JAJA SI :(.
		 */
		/*
		 * Ooooh, the probabilities are not correct :(. And after using BigDecimal, they remain the same, so it's not a precision issue.
		 * I must be doing something wrong, probably when creating the equations, but I can't see the mistake.
		 * Maybe try generating an explicit equation system as I wanted to do at the start.
		 */
		System.out.println(Arrays.toString(baseProbs));
		System.out.println(Arrays.toString(states.getState((1<<N)-1,0).probabilities));
		System.out.println(String.format("Inicialmente tengo %d estados.",states.validStates.size()));
		System.out.println(String.format("Después de la purga, me quedo con %d estados.",states.getReachableStates().size()));
	}
}
