package com.other.layton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_43 {
	private final static int OYSTERS=3;
	private final static int[] GOAL={5,5,5};
	private final static int MAX_PEARLS=8;
	
	private static interface Move7_43	{
		public boolean available(int[] status);
		public int[] transform(int[] status);
	}
	
	private static class PearlMovement implements Move7_43	{
		private final int source;
		private final int target;
		private PearlMovement(int source,int target)	{
			this.source=source;
			this.target=target;
		}
		@Override
		public boolean available(int[] status) {
			return (status[source]>=1)&&(status[target]<MAX_PEARLS);
		}
		@Override
		public int[] transform(int[] status) {
			int[] result=Arrays.copyOf(status,OYSTERS);
			--result[source];
			++result[target];
			return result;
		}
		@Override
		public String toString()	{
			return ""+source+" => "+target;
		}
		public static List<PearlMovement> getAll(int maxOysters)	{
			List<PearlMovement> result=new ArrayList<>();
			for (int i=0;i<maxOysters;++i) for (int j=0;j<maxOysters;++j) if (i!=j) result.add(new PearlMovement(i,j));
			return result;
		}
	}
	
	private static class DoubleOyster implements Move7_43	{
		private final int oyster;
		private DoubleOyster(int oyster)	{
			this.oyster=oyster;
		}
		@Override
		public boolean available(int[] status) {
			int currentPearls=status[oyster];
			return (currentPearls>0)&&((2*currentPearls)<=MAX_PEARLS);
		}
		@Override
		public int[] transform(int[] status) {
			int[] result=Arrays.copyOf(status,OYSTERS);
			result[oyster]*=2;
			return result;
		}
		@Override
		public String toString()	{
			return "["+oyster+"]x2";
		}
		public static List<DoubleOyster> getAll(int maxOysters)	{
			List<DoubleOyster> result=new ArrayList<>();
			for (int i=0;i<maxOysters;++i) result.add(new DoubleOyster(i));
			return result;
		}
	}
	
	private static class Status7_43 implements ProblemStatus<Move7_43,Status7_43>	{
		private Iterable<Move7_43> ALL_MOVES=Iterables.concat(PearlMovement.getAll(OYSTERS),DoubleOyster.getAll(OYSTERS));
		private final int pearls[];
		private final int hashCode;
		public Status7_43()	{
			pearls=new int[OYSTERS];
			for (int i=0;i<OYSTERS;++i) pearls[i]=1;
			hashCode=calculateHashCode();
		}
		private Status7_43(int[] pearls)	{
			this.pearls=pearls;
			hashCode=calculateHashCode();
		}
		@Override
		public boolean isFinal() {
			return Arrays.equals(pearls,GOAL);
		}
		@Override
		public List<Move7_43> availableMoves() {
			List<Move7_43> result=new ArrayList<>();
			for (Move7_43 move:ALL_MOVES) if (move.available(pearls)) result.add(move);
			return result;
		}
		@Override
		public Status7_43 move(Move7_43 move) {
			return new Status7_43(move.transform(pearls));
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status7_43 s=(Status7_43)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return Arrays.toString(pearls);
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<OYSTERS;++i)	{
				result+=pearls[i]*multiplier;
				multiplier*=MAX_PEARLS+1;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		Status7_43 initial=new Status7_43();
		BreadthSearch<Move7_43,Status7_43> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move7_43,Status7_43>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move7_43,Status7_43> path:result) path.print(System.out);
	}
}
