package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Euler327 {
	private final static int C=4;
	private final static int R=7;
	private final static int EXPECTED=45;
	
	private static class InternalState	{
		private final int remaining;
		private final int position;
		private final int atHand;
		private final int[] stored;
		public InternalState(int remaining,int position,int atHand,int[] stored)	{
			this.remaining=remaining;
			this.position=position;
			this.atHand=atHand;
			this.stored=stored;
		}
		@Override
		public int hashCode()	{
			return remaining+7*position+49*atHand+343*Arrays.hashCode(stored);
		}
		@Override
		public boolean equals(Object other)	{
			InternalState isOther=(InternalState)other;
			return (remaining==isOther.remaining)&&(position==isOther.position)&&(atHand==isOther.atHand)&&Arrays.equals(stored,isOther.stored);
		}
		public boolean isFinal()	{
			return position>=R;
		}
		public static InternalState initial()	{
			return new InternalState(EXPECTED,-1,0,new int[R]);
		}
	}
	private static interface Operation	{
		public boolean canDo(InternalState state);
		public default boolean canDo(List<Operation> historic)	{
			return true;
		}
		public InternalState resultAfter(InternalState prev);
	}
	private static class Dispenser implements Operation	{
		private final int howMany;
		public Dispenser(int howMany)	{
			this.howMany=howMany;
		}
		@Override
		public boolean canDo(InternalState state) {
			return (state.position<0)&&(howMany<=state.remaining)&&(state.atHand+howMany<=C);
		}
		@Override
		public InternalState resultAfter(InternalState prev) {
			return new InternalState(prev.remaining-howMany,prev.position,prev.atHand+howMany,prev.stored);
		}
		@Override
		public String toString()	{
			return String.format("Taking %d cards from the dispenser.",howMany);
		}
	}
	private static enum Store implements Operation	{
		INSTANCE;
		@Override
		public boolean canDo(InternalState state) {
			return (state.position>=0)&&(state.atHand>=1);
		}
		public boolean canDo(List<Operation> historic)	{
			for (int i=historic.size()-1;i>=0;--i)	{
				Operation op=historic.get(i);
				if (op==Take.INSTANCE) return false;
				else if (op!=Store.INSTANCE) break;
			}
			return true;
		}
		@Override
		public InternalState resultAfter(InternalState prev) {
			int[] newStored=Arrays.copyOf(prev.stored,prev.stored.length);
			++newStored[prev.position];
			return new InternalState(prev.remaining,prev.position,prev.atHand-1,newStored);
		}
		@Override
		public String toString()	{
			return "Store a card in this room.";
		}
	}
	private static enum Take implements Operation	{
		INSTANCE;
		@Override
		public boolean canDo(InternalState state) {
			return (state.atHand<C)&&(state.position>=0)&&(state.stored[state.position]>0);
		}
		public boolean canDo(List<Operation> historic)	{
			for (int i=historic.size()-1;i>=0;--i)	{
				Operation op=historic.get(i);
				if (op==Store.INSTANCE) return false;
				else if (op!=Take.INSTANCE) break;
			}
			return true;
		}
		@Override
		public InternalState resultAfter(InternalState prev) {
			int[] newStored=Arrays.copyOf(prev.stored,prev.stored.length);
			--newStored[prev.position];
			return new InternalState(prev.remaining,prev.position,prev.atHand+1,newStored);
		}
		@Override
		public String toString()	{
			return "Take a card from this room.";
		}
	}
	private static enum Enter implements Operation	{
		INSTANCE;
		@Override
		public boolean canDo(InternalState state) {
			return state.atHand>0;
		}
		@Override
		public InternalState resultAfter(InternalState prev) {
			return new InternalState(prev.remaining,prev.position+1,prev.atHand-1,prev.stored);
		}
		@Override
		public String toString()	{
			return "Go into the NEXT room.";
		}
	}
	private static enum Leave implements Operation	{
		INSTANCE;
		@Override
		public boolean canDo(InternalState state) {
			return (state.atHand>0)&&(state.position>=0);
		}
		@Override
		public InternalState resultAfter(InternalState prev) {
			return new InternalState(prev.remaining,prev.position-1,prev.atHand-1,prev.stored);
		}
		@Override
		public String toString()	{
			return "Go into the PREVIOUS room.";
		}
	}
	private final static List<Operation> OPERATIONS=List.of(new Dispenser(3),new Dispenser(4),Store.INSTANCE,Take.INSTANCE,Enter.INSTANCE,Leave.INSTANCE);
	
	private static List<List<Operation>> findSequences()	{
		List<List<Operation>> result=new ArrayList<>();
		findSequencesRecursive(InternalState.initial(),new ArrayList<>(),result);
		return result;
	}
	
	private static void findSequencesRecursive(InternalState state,List<Operation> currentSequence,List<List<Operation>> result)	{
		for (Operation op:OPERATIONS) if (op.canDo(state)&&op.canDo(currentSequence))	{
			currentSequence.add(op);
			InternalState newState=op.resultAfter(state);
			if (newState.isFinal())	{
				System.out.println("ENDUT!");
				result.add(List.copyOf(currentSequence));
			}
			else findSequencesRecursive(newState,currentSequence,result);
			currentSequence.remove(currentSequence.size()-1);
		}
	}
	
	public static void main(String[] args)	{
		List<List<Operation>> sequences=findSequences();
		for (List<Operation> s:sequences)	{
			System.out.println("Sequence found:");
			for (Operation o:s) System.out.println("\t"+o.toString());
			System.out.println();
		}
	}
}
