package com.other.layton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_37 {
	private final static int POSITIONS=5;
	
	private enum Creature	{
		NOTHING(0),COW(1),ALIEN(2);
		
		public final int value;
		
		Creature(int value)	{
			this.value=value;
		}
		
		public Creature swap()	{
			switch (this)	{
				case COW:return ALIEN;
				case ALIEN:return COW;
				case NOTHING:default:throw new IllegalArgumentException();
			}
		}
	}
	
	private static class Status_7_37 implements ProblemStatus<Integer,Status_7_37>	{
		private final Creature[] positions;
		private final int hashCode;
		public Status_7_37()	{
			positions=new Creature[POSITIONS];
			positions[0]=Creature.NOTHING;
			for (int i=1;i<POSITIONS;++i) positions[i]=Creature.COW;
			hashCode=calculateHashCode(positions);
		}
		private Status_7_37(Creature[] positions)	{
			this.positions=positions;
			hashCode=calculateHashCode(positions);
		}
		@Override
		public boolean isFinal()	{
			if (positions[0]!=Creature.NOTHING) return false;
			for (int i=1;i<POSITIONS;++i) if (positions[i]!=Creature.ALIEN) return false;
			return true;
		}
		@Override
		public List<Integer> availableMoves() {
			int unpossible=getEmptyPosition();	// There's always room for a Ralph Wiggum reference
			List<Integer> result=new ArrayList<>();
			for (int i=0;i<POSITIONS;++i) if (i!=unpossible) result.add(i);
			return result;
		}
		@Override
		public Status_7_37 move(Integer move) {
			int otherPosition=getEmptyPosition();
			if (move==otherPosition) throw new IllegalArgumentException();
			Creature[] newArray=Arrays.copyOf(positions, POSITIONS);
			Creature oldCreature=newArray[move];
			Creature newCreature=oldCreature.swap();
			newArray[move]=Creature.NOTHING;
			newArray[otherPosition]=newCreature;
			return new Status_7_37(newArray);
		}
		private int getEmptyPosition()	{
			for (int i=0;i<POSITIONS;++i) if (positions[i]==Creature.NOTHING) return i;
			throw new IllegalStateException();
		}
		private int calculateHashCode(Creature[] positions)	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<POSITIONS;++i)	{
				result+=positions[i].value*multiplier;
				multiplier*=3;
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status_7_37 o=(Status_7_37)other;
			return o.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return Arrays.toString(positions);
		}
	}
	
	public static void main(String[] args)	{
		Status_7_37 initial=new Status_7_37();
		BreadthSearch<Integer,Status_7_37> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Integer,Status_7_37>> result=search.solve();
		if (result.isEmpty()) System.out.println("Ich habe keine Lösung gefunden!!!!!");
		else for (SearchPath<Integer,Status_7_37> path:result) path.print(System.out);
	}
}
