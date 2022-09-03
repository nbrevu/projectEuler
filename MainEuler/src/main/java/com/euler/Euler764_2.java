package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.euler.common.PythagoreanTriples.ExtendedPythagoreanTriple;
import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;
import com.google.common.math.LongMath;

public class Euler764_2 {
	// If solving Project Euler problems with overengineered object oriented programming is wrong, I don't want to be right.
	private final static long LIMIT=LongMath.pow(10l,7);
	private final static long MOD=LongMath.pow(10l,9);
	
	private static class SpecialTriple	{
		public final long x;
		public final long y;
		public final long z;
		public SpecialTriple(long x,long y,long z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
	}
	
	private static interface TripleExaminationResult	{
		public SpecialTriple getTriple();
		public boolean isTooBig();
	}
	
	private static enum ExaminationResultWithoutTriple implements TripleExaminationResult	{
		TOO_BIG(true),INVALID_BUT_CAN_CONTINUE(false);
		private final boolean isTooBig;
		private ExaminationResultWithoutTriple(boolean isTooBig)	{
			this.isTooBig=isTooBig;
		}
		@Override
		public SpecialTriple getTriple() {
			return null;
		}
		@Override
		public boolean isTooBig() {
			return isTooBig;
		}
	}
	
	private static class ValidTriple implements TripleExaminationResult	{
		private final SpecialTriple triple;
		public ValidTriple(SpecialTriple triple)	{
			this.triple=triple;
		}
		@Override
		public SpecialTriple getTriple() {
			return triple;
		}
		@Override
		public boolean isTooBig() {
			return false;
		}
	}
	
	private static interface TripleExaminer extends Function<ExtendedPythagoreanTriple,TripleExaminationResult>	{}
	
	private static TripleExaminationResult standardExamination(ExtendedPythagoreanTriple triple)	{
		long z=triple.b*triple.b+triple.c*triple.c;
		if (z>LIMIT) return ExaminationResultWithoutTriple.TOO_BIG;
		long x=triple.m*triple.n*triple.c;
		long y=triple.a;
		return new ValidTriple(new SpecialTriple(x,y,z));
	}
	
	private static TripleExaminationResult specialExamination(ExtendedPythagoreanTriple triple)	{
		long z=4*triple.c;
		if (z>LIMIT) return ExaminationResultWithoutTriple.TOO_BIG;
		long y2=4*triple.b;
		long y=LongMath.sqrt(y2,RoundingMode.DOWN);
		if (y*y!=y2) return ExaminationResultWithoutTriple.INVALID_BUT_CAN_CONTINUE;
		long x=triple.a;
		return new ValidTriple(new SpecialTriple(x,y,z));
	}
	
	public static void main(String[] args)	{
		List<TripleExaminer> tripleConverters=new ArrayList<>();
		tripleConverters.add(Euler764_2::standardExamination);
		tripleConverters.add(Euler764_2::specialExamination);
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		List<TripleExaminer> currentList=new ArrayList<>(tripleConverters);
		long sum=0;
		boolean isFinished=false;
		boolean advanceM=false;
		int counter=0;
		while (!isFinished)	{
			if (!advanceM) iterator.next();
			else	{
				iterator.nextM();
				advanceM=false;
			}
			boolean isSmallestN=iterator.isSmallestN();
			if (isSmallestN&&(currentList.size()!=tripleConverters.size())) currentList=new ArrayList<>(tripleConverters);
			ExtendedPythagoreanTriple baseTriple=iterator.getExtendedTriple();
			Iterator<TripleExaminer> examiners=currentList.iterator();
			while (examiners.hasNext())	{
				TripleExaminer examiner=examiners.next();
				TripleExaminationResult examinationResult=examiner.apply(baseTriple);
				if (examinationResult.isTooBig())	{
					examiners.remove();
					if (currentList.isEmpty())	{
						System.out.println(baseTriple.m+"...");
						if (isSmallestN) isFinished=true;
						else advanceM=true;
					}
					continue;
				}
				SpecialTriple curatedTriple=examinationResult.getTriple();
				if (curatedTriple!=null)	{
					++counter;
					sum+=curatedTriple.x+curatedTriple.y+curatedTriple.z;
				}
			}
		}
		System.out.println(counter);
		System.out.println(sum);
		System.out.println(sum%MOD);
	}
}
