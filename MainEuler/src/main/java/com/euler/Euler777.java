package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.Rational;

public class Euler777 {
	private final static class RationalCosineArg	{
		public final Rational argX;
		public final Rational argY;
		public RationalCosineArg(Rational argX,Rational argY)	{
			this.argX=argX;
			this.argY=argY;
		}
		private static boolean isMultipleOfTwo(Rational x)	{
			return x.isInteger()&&(((x.getIntegerValue())%2)==0);
		}
		private static boolean areCompatible(Rational a,Rational b)	{
			return isMultipleOfTwo(a.sum(b))||isMultipleOfTwo(a.subtract(b));
		}
		public boolean areCompatible(RationalCosineArg other)	{
			return areCompatible(argX,other.argX)&&areCompatible(argY,other.argY);
		}
	}
	
	private static class PointChecker	{
		private final static Rational TENTH=new Rational(1l,10l);
		public final long a;
		public final long b;
		public PointChecker(long a,long b)	{
			this.a=a;
			this.b=b;
		}
		private Map<Rational,RationalCosineArg> getAllPoints()	{
			Rational rA=new Rational(a);
			Rational rB=new Rational(b);
			Map<Rational,RationalCosineArg> result=new LinkedHashMap<>();
			long denom=a*b;
			for (long i=0;i<2*denom;++i)	{
				Rational r=new Rational(i,denom);
				Rational argCosX=r.multiply(rA);
				Rational argCosY=r.subtract(TENTH).multiply(rB);
				result.put(r,new RationalCosineArg(argCosX,argCosY));
			}
			return result;
		}
		public List<Rational> getValidPoints()	{
			int confluencesNeeded=(((a*b)%10)==0)?4:2;
			List<Rational> result=new ArrayList<>();
			Map<Rational,RationalCosineArg> basePoints=getAllPoints();
			for (Map.Entry<Rational,RationalCosineArg> entry:basePoints.entrySet())	{
				int counter=0;
				for (Map.Entry<Rational,RationalCosineArg> entry2:basePoints.entrySet()) if (entry.getValue().areCompatible(entry2.getValue())) ++counter;
				if (counter==confluencesNeeded) result.add(entry.getKey());
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		PointChecker checker=new PointChecker(10,7);
		List<Rational> list=checker.getValidPoints();
		System.out.println(list);
		System.out.println("Found "+list.size()+" intersections.");
		Rational denom=new Rational(70);
		long[] indices=list.stream().mapToLong((Rational x)->x.multiply(denom).getIntegerValue()).toArray();
		System.out.println(Arrays.toString(indices));
	}
}
