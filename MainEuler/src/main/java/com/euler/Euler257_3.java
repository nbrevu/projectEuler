package com.euler;

import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;

public class Euler257_3 {
	private final static int N=10000;

	private static class Triangle	{
		public final int a;
		public final int b;
		public final int c;
		public Triangle(int a,int b,int c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		@Override
		public boolean equals(Object other)	{
			Triangle bOther=(Triangle)other;
			return (a==bOther.a)&&(b==bOther.b)&&(c==bOther.c);
		}
		@Override
		public int hashCode()	{
			return Objects.hash(a,b,c);
		}
	}
	
	private static Set<Triangle> bruteForceSet(int NN)	{
		Set<Triangle> result=new HashSet<>();
		int lastA=NN/3;
		for (int a=1;a<=lastA;++a)	{
			int lastB=(NN-a)/2;
			for (int b=a;b<=lastB;++b)	{
				int lastC=Math.min(a+b-1,NN-(a+b));
				for (int c=b;c<=lastC;++c)	{
					int p1=(a+b)*(a+c);
					int p2=b*c;
					if ((p1%p2)==0) if ((p1/p2)==2) result.add(new Triangle(a,b,c));
				}
			}
		}
		return result;
	}
	
	private static Set<Triangle> finesseSet(int NN)	{
		Set<Triangle> result=new HashSet<>();
		{
			int maxM=DoubleMath.roundToInt(Math.sqrt(NN/3.0),RoundingMode.DOWN);
			for (int m=3;m<=maxM;m+=2)	{
				int minK=IntMath.divide(m,2,RoundingMode.UP);
				int maxK=DoubleMath.roundToInt(m/Math.sqrt(2.0),RoundingMode.DOWN);
				for (int k=minK;k<=maxK;++k) if (EulerUtils.areCoprime(m,k))	{
					/*
					 * n=2k+m
					 * a=km
					 * b=kn=2k^2+km
					 * c=m*(m+k)=m^2+km
					 * P=m^2+2k^2+3km
					 */
					int a=k*m;
					int b=k*(2*k+m);
					int c=m*(k+m);
					int P=a+b+c;
					if (P>NN) break;
					int q=NN/P;
					for (int i=1;i<=q;++i) if (!result.add(new Triangle(i*a,i*b,i*c))) System.out.println("¿PERO ESTO QUÉ ES? "+a+" "+b+" "+c+" "+i+".");
				}
			}
		}	{
			int maxM=DoubleMath.roundToInt(Math.sqrt(NN/6.0),RoundingMode.DOWN);
			for (int m=3;m<=maxM;++m)	{
				int maxK=DoubleMath.roundToInt(Math.sqrt(2.0)*m,RoundingMode.DOWN);
				int minK=m+1;
				if ((minK%2)==0) ++minK;
				for (int k=minK;k<=maxK;k+=2) if (EulerUtils.areCoprime(m,k))	{
					/*
					 * n=k+m
					 * a=km
					 * b=kn=k^2+km
					 * c=m*(2*m+k)=2m^2+km
					 * P=2*m^2+k^2+3km
					 */
					int a=k*m;
					int b=k*(k+m);
					int c=m*(2*m+k);
					int P=a+b+c;
					if (P>NN) break;
					int q=NN/P;
					for (int i=1;i<=q;++i) if (!result.add(new Triangle(i*a,i*b,i*c))) System.out.println("ACHTUNG, WAS IST DAS? "+a+" "+b+" "+c+" "+i+".");
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Set<Triangle> s1=bruteForceSet(N);
		Set<Triangle> s2=finesseSet(N);
		System.out.println("Brute force: "+s1.size());
		System.out.println("Careful method: "+s2.size());
		s1.removeAll(s2);
		System.out.println("Symmetric difference: "+s1.size());
	}
}
