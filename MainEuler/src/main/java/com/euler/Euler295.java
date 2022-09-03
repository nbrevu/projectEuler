package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;

public class Euler295 {
	private final static long N=100000;
	
	private static class HoleShapeData	{
		public final long a;
		public final long b;
		public final long x0;
		public final long y0;
		public final long k0;
		public final long kMax;
		private long calculateBound(long p,long q)	{
			long num=-(p*(2*x0+p)+q*(2*y0+q));
			long den=2*(b*p+a*q);
			return LongMath.divide(num,den,RoundingMode.CEILING);
		}
		private static long solveEquation(long a,long b,long c)	{
			// We want to get the upper bound for the high solution of the quadratic equation ax^2+bx+c=0. "a" is assumed positive.
			return LongMath.divide(-b+LongMath.sqrt(b*b-4*a*c,RoundingMode.CEILING),2*a,RoundingMode.CEILING);
		}
		public HoleShapeData(long a,long b)	{
			// Assumes a>b>1, a and b odd, a and b coprime.
			this.a=a;
			this.b=b;
			long sqSum=a*a+b*b;
			long hSqSum=sqSum/2;
			long c=hSqSum%a;
			long b_1a=EulerUtils.modulusInverse(b,a);
			y0=((a-c)*b_1a)%a;
			long x0Num=hSqSum+b*y0;
			x0=x0Num/a;
			if (b>1)	{
				long bestQ=EulerUtils.modulusInverse(a,b);
				long bestP=LongMath.divide(-a*bestQ,b,RoundingMode.CEILING);
				long k0_1=calculateBound(bestP,bestQ);
				long k0_2=LongMath.divide(a-x0,b,RoundingMode.CEILING);
				k0=Math.max(k0_1,k0_2);
			}	else k0=LongMath.divide(a-x0,b,RoundingMode.CEILING);
			long quadA=sqSum;
			long quadB=2*(b*x0+a*y0);
			long quadC=x0*x0+y0*y0-N*N;
			// Maybe, if R(K)==N^2, I need to add one! This doesn't actually happen though.
			kMax=solveEquation(quadA,quadB,quadC);
		}
	}
	
	private static class Intersection	{
		private final BitSet sets;
		private final Set<Long> elements;
		private Intersection(BitSet sets,Set<Long> elements)	{
			this.sets=sets;
			this.elements=elements;
		}
		public static Optional<Intersection> of(Set<Long> setA,Set<Long> setB,int elemA,int elemB)	{
			Set<Long> elements=new HashSet<>(Sets.intersection(setA,setB));
			if (elements.isEmpty()) return Optional.empty();
			BitSet sets=new BitSet();
			sets.set(elemA);
			sets.set(elemB);
			return Optional.of(new Intersection(sets,elements));
		}
		public Optional<Intersection> intersect(Set<Long> other,int otherIndex)	{
			if (otherIndex>=sets.nextSetBit(0)) return Optional.empty();
			Set<Long> newIntersection=new HashSet<>(Sets.intersection(elements,other));
			if (newIntersection.isEmpty()) return Optional.empty();
			BitSet newSets=(BitSet)(sets.clone());
			newSets.set(otherIndex);
			return Optional.of(new Intersection(newSets,newIntersection));
		}
		public static List<Intersection> getAllIntersections(List<Set<Long>> originalSets)	{
			List<Intersection> result=new ArrayList<>();
			for (int i=0;i<originalSets.size();++i) for (int j=i+1;j<originalSets.size();++j)	{
				Optional<Intersection> intersect=of(originalSets.get(i),originalSets.get(j),i,j);
				intersect.ifPresent(result::add);
			}
			return result;
		}
		public static List<Intersection> getAllIntersections(List<Intersection> previousGen,List<Set<Long>> originalSets)	{
			List<Intersection> result=new ArrayList<>();
			for (int i=0;i<previousGen.size();++i)	{
				Intersection inter=previousGen.get(i);
				int upperBound=inter.sets.nextSetBit(0);
				for (int j=0;j<upperBound;++j)	{
					Optional<Intersection> newInter=inter.intersect(originalSets.get(j),j);
					newInter.ifPresent(result::add);
				}
			}
			return result;
		}
	}
	
	public static long countCombinations(Set<Long> elements)	{
		long size=elements.size();
		return (size*(size+1))/2;
	}
		
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<Set<Long>> rSets=new ArrayList<>();
		for (long b=1;;b+=2)	{
			boolean any=false;
			for (long a=b;;a+=2) if (EulerUtils.areCoprime(a,b))	{
				HoleShapeData data=new HoleShapeData(a,b);
				if (data.kMax<=data.k0)	{
					/*
					 * The optimal case appears when a=n*b-1 for some even n. If even in this case there aren't results, there won't be any
					 * additional results.
					 */
					if (((a+1)%b)==0) break;
					else continue;
				}	else	{
					Set<Long> rs=new HashSet<>();
					long qA=a*a+b*b;
					long qB=2*(b*data.x0+a*data.y0);
					long qC=data.x0*data.x0+data.y0*data.y0;
					for (long k=data.k0;k<data.kMax;++k)	{
						long r2=qC+k*(qB+k*qA);
						rs.add(r2);
					}
					rSets.add(rs);
					any=true;
				}
			}
			if (!any) break;
		}
		long result=0;
		for (Set<Long> set:rSets) result+=countCombinations(set);
		long sign=-1;
		List<Intersection> currentGeneration=Intersection.getAllIntersections(rSets);
		while (!currentGeneration.isEmpty())	{
			long thisCount=0;
			for (Intersection inter:currentGeneration) thisCount+=countCombinations(inter.elements);
			result+=sign*thisCount;
			sign=-sign;
			currentGeneration=Intersection.getAllIntersections(currentGeneration,rSets);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
