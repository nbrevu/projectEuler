package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.EulerUtils.Pair;
import com.euler.common.OtherUtils;
import com.google.common.collect.ImmutableSet;

public class RecursiveSolution implements DiophantineSolution {
	private final static String[] RECURSION_VARS=new String[] {"x_n","y_n",""};
	
	private final static Comparator<FixedSolution> SOLUTION_COMPARATOR=new Comparator<>()	{
		@Override
		public int compare(FixedSolution o1,FixedSolution o2) {
			BigInteger x1=o1.x;
			BigInteger y1=o1.y;
			BigInteger x2=o2.x;
			BigInteger y2=o2.y;
			int absX=x1.abs().compareTo(x2.abs());
			if (absX!=0) return absX;
			int absY=y1.abs().compareTo(y2.abs());
			if (absY!=0) return absY;
			int signX=x1.signum()-x2.signum();
			if (signX!=0) return signX;
			return y1.signum()-y2.signum();
		}
	};
	private final Set<FixedSolution> fixedSols;
	private final List<DiophantineSolutionRecursion> recursions;
	public RecursiveSolution(Set<FixedSolution> fixedSols,List<DiophantineSolutionRecursion> recursions)	{
		this.fixedSols=ImmutableSet.copyOf(fixedSols);
		this.recursions=recursions;
	}
	public Set<FixedSolution> getBaseSolutions()	{
		return fixedSols;
	}
	public List<DiophantineSolutionRecursion> getRecursions()	{
		return recursions;
	}
	@Override
	public Iterator<Pair<BigInteger,BigInteger>> iterator() {
		NavigableSet<FixedSolution> sols=new TreeSet<>(SOLUTION_COMPARATOR);
		Set<FixedSolution> alreadyIterated=new HashSet<>();
		sols.addAll(fixedSols);
		return new Iterator<>()	{
			@Override
			public boolean hasNext() {
				return !sols.isEmpty();
			}
			@Override
			public Pair<BigInteger, BigInteger> next() {
				FixedSolution sol=sols.pollFirst();
				alreadyIterated.add(sol);
				for (DiophantineSolutionRecursion recursion:recursions)	{
					FixedSolution newSolution=recursion.apply(sol);
					if (!alreadyIterated.contains(newSolution)) sols.add(newSolution);
				}
				return new Pair<>(sol.x,sol.y);
			}
		};
	}
	@Override
	public String toString()	{
		StringBuilder builder=new StringBuilder();
		builder.append("Base solutions:").append(System.lineSeparator()).append('\t').append(fixedSols.toString()).append(System.lineSeparator());
		builder.append("Recursions:");
		for (DiophantineSolutionRecursion r:recursions)	{
			builder.append(System.lineSeparator()).append("\tx_{n+1}=");
			OtherUtils.appendCustomPolynomial(builder,new BigInteger[] {r.p,r.q,r.k},RECURSION_VARS);
			builder.append(';').append(System.lineSeparator()).append("\ty_{n+1}=");
			OtherUtils.appendCustomPolynomial(builder,new BigInteger[] {r.r,r.s,r.l},RECURSION_VARS);
			builder.append('.').append(System.lineSeparator());
		}
		return builder.toString();
	}
}
