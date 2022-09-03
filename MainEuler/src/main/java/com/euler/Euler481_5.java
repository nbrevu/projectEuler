package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import com.euler.common.BitSetCursor;
import com.euler.common.DoubleMatrix;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjDoubleCursor;
import com.koloboke.collect.map.ObjDoubleMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjDoubleMaps;

public class Euler481_5 {
	/*
	 * For N=20 the memory is almost exhausted, but it works!
	 * 7282.08798346
	 * Elapsed 237.128526499 seconds.
	 */
	private final static int N=14;
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
		public static IntCursor playersAsCursor(int playerBits)	{
			return new BitSetCursor(playerBits,true);
		}
		public static int[] playersAsArray(int playerBits)	{
			IntStream.Builder builder=IntStream.builder();
			for (IntCursor cursor=playersAsCursor(playerBits);cursor.moveNext();) builder.accept(cursor.elem());
			return builder.build().toArray();
		}
		public int howManyPlayers(int playerBits)	{
			return Integer.bitCount(playerBits);
		}
	}
	
	private static class BaseStateData	{
		public final int turnOwner;
		public final double[] probabilities;
		public int chosenLoser;	// This is the index of the player that will be eliminated if this turn's owner wins this round.
		public int nextPlayer;
		private final IntIntMap indexMap;
		public BaseStateData(int turnOwner,int[] players)	{
			this.turnOwner=turnOwner;
			probabilities=new double[players.length];
			chosenLoser=-1;
			nextPlayer=-1;
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
		private final BaseStateData[] states;
		private final List<BaseStateData> validStates;
		/*
		 * First index: amount of players in the turn.
		 * Second index: "playerBits" identifier.
		 * Third index: current turn owner.
		 * This looks complex but it makes iterating easier and kind of obvious for the groupings we need.
		 */
		private final IntObjMap<IntObjMap<IntObjMap<BaseStateData>>> indexedStates;
		private final IndexManager indices;
		public StateSet(int n)	{
			indices=new IndexManager(n);
			int howManyStates=indices.getSize();
			states=new BaseStateData[howManyStates];
			validStates=new ArrayList<>(howManyStates/2);
			indexedStates=HashIntObjMaps.newMutableMap();
			for (int i=0;i<howManyStates;++i) if (indices.isValid(i))	{
				int playerBits=indices.extractPlayerBits(i);
				int currentPlayer=indices.extractCurrentPlayer(i);
				int howManyPlayers=indices.howManyPlayers(playerBits);
				BaseStateData state=new BaseStateData(currentPlayer,IndexManager.playersAsArray(playerBits));
				states[i]=state;
				validStates.add(state);
				IntObjMap<IntObjMap<BaseStateData>> innerMap=indexedStates.computeIfAbsent(howManyPlayers,(int unused)->HashIntObjMaps.newMutableMap());
				IntObjMap<BaseStateData> evenInnerMap=innerMap.computeIfAbsent(playerBits,(int unused)->HashIntObjMaps.newMutableMap());
				evenInnerMap.put(currentPlayer,state);
			}
		}
		public BaseStateData getState(int playerBits,int currentPlayer)	{
			return states[indices.getStateIndex(playerBits,currentPlayer)];
		}
		public IntObjMap<IntObjMap<BaseStateData>> getStatesWithNPlayers(int n)	{
			return indexedStates.get(n);
		}
		public int[] getPlayers(int playerBits)	{
			return IndexManager.playersAsArray(playerBits);
		}
		public IntObjMap<BaseStateData> getStatesFromPlayerBits(int nPlayers,int playerBits)	{
			return indexedStates.get(nPlayers).get(playerBits);
		}
	}
	private static class MatrixStateData	{
		private static class MapKey	{
			public final MatrixStateData originState;
			public final int originPlayerIndex;
			public final int targetPlayerIndex;
			public MapKey(MatrixStateData originState,int originPlayerIndex,int targetPlayerIndex)	{
				this.originState=originState;
				this.originPlayerIndex=originPlayerIndex;
				this.targetPlayerIndex=targetPlayerIndex;
			}
		}
		/*
		 * ACHTUNG! Unlike the BaseStateData, here each object represents the whole set of turns for a given set of players.
		 */
		private final int[] players;
		private final ObjDoubleMap<MapKey> transitions;	// Transitions TO this state!
		private double[] vector;
		public MatrixStateData(int playerBits)	{
			players=IndexManager.playersAsArray(playerBits);
			transitions=HashObjDoubleMaps.newMutableMap();
		}
		private int getIndex(int player)	{
			return Arrays.binarySearch(players,player);
		}
		public void addTransition(MatrixStateData originState,int originPlayer,int targetPlayer,double value)	{
			int originPlayerIndex=originState.getIndex(originPlayer);
			int targetPlayerIndex=getIndex(targetPlayer);
			MapKey key=new MapKey(originState,originPlayerIndex,targetPlayerIndex);
			transitions.put(key,value);
		}
		private DoubleMatrix generateInverseMatrix(double[] baseProbs)	{
			int n=players.length;
			DoubleMatrix mainMatrix=new DoubleMatrix(n);
			for (int i=0;i<n;++i) mainMatrix.assign(i,i,1d);
			for (int i=0;i<n-1;++i) mainMatrix.assign(i+1,i,baseProbs[players[i]]-1d);
			mainMatrix.assign(0,n-1,baseProbs[players[n-1]]-1d);
			return DoubleMatrix.destructiveInverse(mainMatrix);
		}
		public void generateMatrixData(double[] baseProbs)	{
			generateInverseMatrix(baseProbs);
			double[] baseVector=new double[players.length];
			for (ObjDoubleCursor<MapKey> cursor=transitions.cursor();cursor.moveNext();)	{
				MapKey key=cursor.key();
				baseVector[key.targetPlayerIndex]+=cursor.value()*key.originState.vector[key.originPlayerIndex];
			}
			vector=generateInverseMatrix(baseProbs).multiply(baseVector);
		}
		public void generateMatrixDataSpecialCase(double[] baseProbs)	{
			generateInverseMatrix(baseProbs);
			double[] baseVector=new double[players.length];
			baseVector[0]=1d;
			vector=generateInverseMatrix(baseProbs).multiply(baseVector);
		}
		public double getVectorSum()	{
			double result=0d;
			for (double val:vector) result+=val;
			return result;
		}
	}
	
	private static double[] getBaseProbabilities(int n)	{
		double[] fibos=new double[n+1];
		fibos[0]=1d;
		fibos[1]=1d;
		for (int i=2;i<fibos.length;++i) fibos[i]=fibos[i-1]+fibos[i-2];
		double[] result=new double[n];
		for (int i=0;i<n;++i) result[i]=fibos[i]/fibos[n];
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		StateSet states=new StateSet(N);
		double[] baseProbs=getBaseProbabilities(N);
		// Base cases: probabilities for the turns that have exactly two players.
		for (IntObjCursor<IntObjMap<BaseStateData>> cursor2=states.getStatesWithNPlayers(2).cursor();cursor2.moveNext();)	{
			int playerBits=cursor2.key();
			IntObjMap<BaseStateData> statePerPlayer=cursor2.value();
			int[] players=states.getPlayers(playerBits);
			int p1=players[0];
			int p2=players[1];
			double s1=baseProbs[p1];
			double s2=baseProbs[p2];
			double denom=s1+s2-s1*s2;
			BaseStateData player1State=statePerPlayer.get(p1);
			player1State.chosenLoser=p2;
			player1State.nextPlayer=p1;
			player1State.setProbability(p1,s1/denom);
			player1State.setProbability(p2,(denom-s1)/denom);
			BaseStateData player2State=statePerPlayer.get(p2);
			player2State.chosenLoser=p1;
			player2State.nextPlayer=p2;
			player2State.setProbability(p1,(denom-s2)/denom);
			player2State.setProbability(p2,s2/denom);
		}
		// Complicated cases: probabilities for the turns that have more than two players. Finesse required. Will take a few tries to get right.
		for (int nPlayers=3;nPlayers<=N;++nPlayers) for (IntObjCursor<IntObjMap<BaseStateData>> cursorN=states.getStatesWithNPlayers(nPlayers).cursor();cursorN.moveNext();)	{
			int playerBits=cursorN.key();
			IntObjMap<BaseStateData> statePerPlayer=cursorN.value();
			int[] players=states.getPlayers(playerBits);
			IntIntMap chosenLosers=HashIntIntMaps.newMutableMap();
			IntObjMap<BaseStateData> nextStatesIfWin=HashIntObjMaps.newMutableMap();
			/*
			 * Careful step: each player X chooses the player they will choose to remove if they win their turn. The process is:
			 * 1) For each other player Y, go to the state resulting of removing X. The next player to play is the one that goes after X.
			 * 2) In that state, get the probability of X winning.
			 * 3) After all these have been gathered, choose the player with the minimum probability.
			 */
			for (int p=0;p<players.length;++p)	{
				int me=players[p];
				double bestProb=-1d;
				int bestPlayer=-1;
				BaseStateData bestState=null;
				for (int q=1;q<players.length;++q)	{
					int toDelete=players[(p+q)%nPlayers];
					if ((playerBits&(1<<toDelete))==0) throw new RuntimeException("Mal.");
					int playerBitsAfter=playerBits-(1<<toDelete);
					int nextTurnIndex=(p+((q==1)?2:1))%nPlayers;
					int nextTurn=players[nextTurnIndex];
					BaseStateData turnIfWins=states.getState(playerBitsAfter,nextTurn);
					double prob=turnIfWins.getProbability(me);
					if (prob>bestProb)	{
						bestProb=prob;
						bestPlayer=toDelete;
						bestState=turnIfWins;
					}
				}
				chosenLosers.put(me,bestPlayer);
				BaseStateData meState=statePerPlayer.get(me);
				meState.chosenLoser=bestPlayer;
				meState.nextPlayer=bestState.turnOwner;
				nextStatesIfWin.put(me,bestState);
			}
			/*
			 * OK. Second try. We generate an equation system. I strongly believe that this is equivalent to the previous scheme, but let's see.
			 * If we have N players, we need to create a square matrix of side N*N. That's N^4 elements.
			 * The row/column in position i=A*N+B (with A,B in [0,N-1]) indicates that the turn belongs to player A and the probability to player
			 * B. So the first row/col is for player 1 turn, player 1 probability; the second one is for player 1 turn, player 2 probability;
			 * and so on.
			 * 
			 * Every equation is of the form P(ij) = S(i)*P(zj)+(1-S(i))*P(kj), or alternatively,
			 * P(ij)+(S(i)-1)*P(ik) = S(i)*P(z)
			 * Here, P(ij) is the probability of (j) winning when the turn belongs to i.
			 * P(kj) is the probability of (j) winning when the turn belongs to k, the next player after i.
			 * P(zj) is the probability of (j) winning in the state z, resulting of i winning its turn and removing one player.
			 * 
			 * Well, something was wrong before, because THE NUMBERS ARE RIGHT NOW!
			 */
			int size=nPlayers*nPlayers;
			DoubleMatrix mainMatrix=new DoubleMatrix(size);
			double[] vector=new double[size];
			for (int i=0;i<nPlayers;++i) for (int j=0;j<nPlayers;++j)	{
				int row=i*nPlayers+j;
				int i2=(i+1)%nPlayers;
				int nextRow=i2*nPlayers+j;
				mainMatrix.assign(row,row,1d);
				double s=baseProbs[players[i]];
				mainMatrix.assign(row,nextRow,s-1d);
				vector[row]=s*nextStatesIfWin.get(players[i]).getProbability(players[j]);
			}
			DoubleMatrix inverse=DoubleMatrix.destructiveInverse(mainMatrix);
			for (int i=0;i<nPlayers;++i) for (int j=0;j<nPlayers;++j)	{
				int row=i*nPlayers+j;
				double result=0;
				for (int k=0;k<size;++k) result+=vector[k]*inverse.get(row,k);
				statePerPlayer.get(players[i]).setProbability(players[j],result);
			}
		}
		double result=0d;
		int topStateBits=(1<<N)-1;
		MatrixStateData topLevelMatrix=new MatrixStateData(topStateBits);
		topLevelMatrix.generateMatrixDataSpecialCase(baseProbs);
		result+=topLevelMatrix.getVectorSum();
		IntObjMap<MatrixStateData> thisGeneration=HashIntObjMaps.newMutableMap();
		thisGeneration.put(topStateBits,topLevelMatrix);
		for (int i=N-1;i>=2;--i)	{
			IntObjMap<MatrixStateData> nextGeneration=HashIntObjMaps.newMutableMap();
			// First: generate the (visited) children states from the next generation, and add the transitions as needed.
			for (IntObjCursor<MatrixStateData> cursor=thisGeneration.cursor();cursor.moveNext();)	{
				int playerBits=cursor.key();
				MatrixStateData parentState=cursor.value();
				IntObjMap<BaseStateData> parentStateData=states.getStatesFromPlayerBits(i+1,playerBits);
				for (IntCursor playerCursor=IndexManager.playersAsCursor(playerBits);playerCursor.moveNext();)	{
					int player=playerCursor.elem();
					BaseStateData substate=parentStateData.get(player);
					int toDelete=substate.chosenLoser;
					int nextState=playerBits-(1<<toDelete);
					MatrixStateData childState=nextGeneration.computeIfAbsent(nextState,MatrixStateData::new);
					childState.addTransition(parentState,player,substate.nextPlayer,baseProbs[player]);
				}
			}
			// Second: calculate all the vector data.
			for (MatrixStateData subState:nextGeneration.values())	{
				subState.generateMatrixData(baseProbs);
				result+=subState.getVectorSum();
			}
			// Third: prepare for the next execution.
			thisGeneration=nextGeneration;
		}
		String stringResult=String.format(Locale.UK,"%.8f",result);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(stringResult);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
