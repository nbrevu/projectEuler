package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.EulerUtils;

public class Euler236 {
	private static class Fraction implements Comparable<Fraction>	{
		public final int num;
		public final int den;
		public Fraction(int num,int den)	{
			this.num=num;
			this.den=den;
		}
		@Override
		public String toString()	{
			return ""+num+"/"+den;
		}
		@Override
		public int compareTo(Fraction other) {
			return num*other.den-den*other.num;
		}
	}
	
	private static class Spoilage	{
		public final int a;
		public final int b;
		public Spoilage(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		@Override
		public String toString()	{
			return "("+a+","+b+")";
		}
	}
	
	private static class Ratio	{
		public final int a;
		public final int b;
		public Ratio(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		public List<Spoilage> acceptableSpoilages(Fraction ratio)	{
			List<Spoilage> result=new ArrayList<>();
			int n=ratio.num;
			int d=ratio.den;
			while ((n<=b)&&(d<=a))	{
				result.add(new Spoilage(n,d));
				n+=ratio.num;
				d+=ratio.den;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		Ratio ratioA=new Ratio(5248,640);
		Ratio ratioB=new Ratio(7872,11328);
		Ratio ratioC=new Ratio(5760,3776);
		Fraction global=new Fraction(18880,15744);
		Fraction minimum=new Fraction(1,1);
		for (int i=41;i<=64;++i) for (int j=i+1;j<=3*i;++j)	{
			if (EulerUtils.gcd(i,j)!=1) continue;
			Fraction frac=new Fraction(j,i);
			if (frac.compareTo(minimum)<0) continue;
			for (Spoilage sa:ratioA.acceptableSpoilages(frac)) for (Spoilage sb:ratioB.acceptableSpoilages(frac)) for (Spoilage sc:ratioC.acceptableSpoilages(frac))	{
				long a=sa.a+sb.a+sc.a;
				long b=sa.b+sb.b+sc.b;
				long num=frac.den*a*global.den;
				long den=frac.num*b*global.num;
				if (num==den) minimum=frac;
			}
		}
		System.out.println(minimum.toString());
	}
}
