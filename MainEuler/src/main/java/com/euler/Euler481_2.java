package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.euler.common.BigRational;
import com.euler.common.BitSetCursor;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler481_2 {
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
		public final BigRational[] probabilities;
		public int chosenLoser;	// This is the index of the player that will be eliminated if this turn's owner wins this round.
		private final IntIntMap indexMap;
		public StateData(int[] players)	{
			this.players=players;
			probabilities=new BigRational[players.length];
			chosenLoser=-1;
			indexMap=HashIntIntMaps.newMutableMap();
			for (int i=0;i<players.length;++i) indexMap.put(players[i],i);
		}
		private int getIndex(int player)	{
			return indexMap.getOrDefault(player,-1);
		}
		public BigRational getProbability(int player)	{
			int index=getIndex(player);
			return (index<0)?BigRational.ZERO:probabilities[getIndex(player)];
		}
		public void setProbability(int player,BigRational value)	{
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
	}
	
	private static BigRational[] getBaseProbabilities(int n)	{
		int[] fibos=new int[n+1];
		fibos[0]=1;
		fibos[1]=1;
		for (int i=2;i<=n;++i) fibos[i]=fibos[i-1]+fibos[i-2];
		BigInteger denom=BigInteger.valueOf(fibos[n]);
		BigRational[] result=new BigRational[n];
		for (int i=0;i<n;++i) result[i]=new BigRational(BigInteger.valueOf(fibos[i]),denom);
		return result;
	}
	
	public static void main(String[] args)	{
		StateSet states=new StateSet(N);
		BigRational[] baseProbs=getBaseProbabilities(N);
		// Base cases: probabilities for the turns that have exactly two players.
		for (IntObjCursor<IntObjMap<StateData>> cursor2=states.getStatesWithNPlayers(2).cursor();cursor2.moveNext();)	{
			int playerBits=cursor2.key();
			IntObjMap<StateData> statePerPlayer=cursor2.value();
			int[] players=states.getPlayers(playerBits);
			int p1=players[0];
			int p2=players[1];
			BigRational s1=baseProbs[p1];
			BigRational s2=baseProbs[p2];
			BigRational denom=s1.sum(s2).subtract(s1.multiply(s2));
			StateData player1State=statePerPlayer.get(p1);
			player1State.chosenLoser=p2;
			player1State.setProbability(p1,s1.divide(denom));
			player1State.setProbability(p2,denom.subtract(s1).divide(denom));
			StateData player2State=statePerPlayer.get(p2);
			player2State.chosenLoser=p1;
			player2State.setProbability(p1,denom.subtract(s2).divide(denom));
			player2State.setProbability(p2,s2.divide(denom));
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
				BigRational maxProb=BigRational.ZERO;
				int maxPlayer=-1;
				for (int q=1;q<players.length;++q)	{
					int other=players[(p+q)%nPlayers];
					int playerBitsAfter=playerBits-(1<<other);
					int nextTurnIndex=(p+((q==1)?2:1))%nPlayers;
					int nextTurn=players[nextTurnIndex];
					StateData turnIfWins=states.getState(playerBitsAfter,nextTurn);
					BigRational prob=turnIfWins.getProbability(me);
					if (prob.compareTo(maxProb)>0)	{
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
				BigRational[] numerators=new BigRational[nPlayers];
				Arrays.fill(numerators,BigRational.ZERO);
				BigRational factor=BigRational.ONE;
				/*
				 * "i" is the current turn's owner.
				 * For each turn, we add some probability (multiplied by "factor") to each player.
				 * Finally we divide by 1-factor.
				 */
				for (int j=0;j<nPlayers;++j)	{
					int playerBeingAdded=players[j];
					BigRational s=baseProbs[playerBeingAdded];
					int thisChosenLoser=chosenLosers.get(playerBeingAdded);
					int nextTurn=players[(j+1)%nPlayers];
					if (thisChosenLoser==nextTurn) nextTurn=players[(j+2)%nPlayers];
					int playerBitsAfter=playerBits-(1<<thisChosenLoser);
					StateData stateIfRemoved=states.getState(playerBitsAfter,nextTurn);
					BigRational totalFactor=factor.multiply(s);
					for (int k=0;k<nPlayers;++k) numerators[k]=numerators[k].sum(stateIfRemoved.getProbability(players[k]).multiply(totalFactor));
					factor=factor.multiply(BigRational.ONE.subtract(s));
				}
				BigRational denom=BigRational.ONE.subtract(factor);
				for (int j=0;j<nPlayers;++j) affectedState.setProbability(players[j],numerators[j].divide(denom));
			}
		}
		// Some sanity checks...
		for (StateData cosa:states.validStates)	{
			if (cosa.players.length<2) continue;
			BigRational probSum=BigRational.ZERO;
			for (BigRational p:cosa.probabilities) probSum=probSum.sum(p);
			// System.out.println(probSum);
			if (Arrays.binarySearch(cosa.players,cosa.chosenLoser)<0) throw new RuntimeException("Vaya.");
			if (!probSum.equals(BigRational.ONE)) throw new RuntimeException("Y cuándo no.");
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
		 */
		// Ooooh, the probabilities are not correct :(.
		System.out.println(Arrays.toString(baseProbs));
		BigRational[] finalProbs=states.getState((1<<N)-1,0).probabilities;
		MathContext context=new MathContext(10,RoundingMode.DOWN);
		Function<BigRational,BigDecimal> converter=(BigRational x)->new BigDecimal(x.num).divide(new BigDecimal(x.den),context);
		System.out.println(Arrays.toString(finalProbs));
		System.out.println(Arrays.stream(finalProbs).map(converter).collect(Collectors.toList()));
	}
}
