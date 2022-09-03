package com.euler;

import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;

public class Euler257 {
	private final static int N=10000;

	private static class Tracer	{
		private int count1=0,count2=0,count3=0,count6=0;
		private static class Base	{
			public final int a;
			public final int b;
			public final int c;
			public Base(int a,int b,int c)	{
				this.a=a;
				this.b=b;
				this.c=c;
			}
			@Override
			public boolean equals(Object other)	{
				Base bOther=(Base)other;
				return (a==bOther.a)&&(b==bOther.b)&&(c==bOther.c);
			}
			@Override
			public int hashCode()	{
				return Objects.hash(a,b,c);
			}
		}
		private final Set<Base> known;
		public Tracer()	{
			known=new HashSet<>();
		}
		public void finalTrace()	{
			System.out.println("Count 1="+count1+", count 2="+count2+", count 3="+count3+", count 6="+count6+".");
		}
		public void log(int a,int b,int c)	{
			assert a<=b;
			assert b<=c;
			{
				int g=EulerUtils.gcd(a,EulerUtils.gcd(b,c));
				int aa=a/g;
				int bb=b/g;
				int k=EulerUtils.gcd(aa,bb);
				int m=aa/k;
				int n=bb/k;
				assert ((2*n-m)%k)==0;
				int q=(2*n-m)/k;
				switch (q)	{
					case 1:++count1;break; 
					case 2:++count2;break; 
					case 3:++count3;break; 
					case 6:++count6;break;
					default:assert false;
				}
			}
			if (EulerUtils.gcd(a,EulerUtils.gcd(b,c))==1)	{
				known.add(new Base(a,b,c));
				int k=EulerUtils.gcd(a,b);
				int m=a/k;
				int n=b/k;
				System.out.println("ICH HABE DAS PRIMITIVE DREIECKE GEFUNDEN!!!!! ODER?");
				System.out.println("\ta="+a+"; b="+b+"; c="+c+".");
				System.out.println("\tk="+k+"; m="+m+"; n="+n+".");
				assert ((2*n-m)%k)==0;
				int q=(2*n-m)/k;
				switch (q)	{
					case 1:	{
						assert n==(k+m)/2;
						assert k>m;
						assert k<=DoubleMath.roundToInt(Math.sqrt(3.0)*m,RoundingMode.DOWN);
						assert m<=DoubleMath.roundToInt(Math.sqrt(N/4),RoundingMode.DOWN);
						assert (k%2)==(m%2);
						assert EulerUtils.areCoprime(k,m);
						int predictedC=m*((3*m+k)/2);
						assert predictedC==c;
						break;
					}	case 2:	{
						assert n==k+(m/2);
						assert k>=1+IntMath.divide(m,2,RoundingMode.DOWN);
						assert k<=DoubleMath.roundToInt(Math.sqrt(0.75)*m,RoundingMode.DOWN);
						assert m<=DoubleMath.roundToInt(Math.sqrt(N/2),RoundingMode.DOWN);
						assert (m%2)==0;
						assert EulerUtils.areCoprime(k,m/2);
						int predictedC=(m/2)*(3*(m/2)+k);
						assert predictedC==c;
						break;
					}	case 3:	{
						assert n==(3*k+m)/2;
						assert k>=1+IntMath.divide(m,3,RoundingMode.DOWN);
						assert k<=DoubleMath.roundToInt(m/Math.sqrt(3.0),RoundingMode.DOWN);
						assert m<=DoubleMath.roundToInt(Math.sqrt(3*N/4),RoundingMode.DOWN);
						assert (k%2)==(m%2);
						assert EulerUtils.areCoprime(k,m);
						int predictedC=m*((k+m)/2);
						assert predictedC==c;
						break;
					}	case 6:	{
						assert n==3*k+(m/2);
						assert k>=1+IntMath.divide(m,6,RoundingMode.DOWN);
						assert k<=DoubleMath.roundToInt(m/Math.sqrt(12.0),RoundingMode.DOWN);
						assert m<=DoubleMath.roundToInt(Math.sqrt(3*N/2),RoundingMode.DOWN);
						assert (m%2)==0;
						assert EulerUtils.areCoprime(k,m/2);
						int predictedC=(m/2)*((m/2)+k);
						assert predictedC==c;
						break;
					}
				}
			}	else	{
				int r=EulerUtils.gcd(a,EulerUtils.gcd(b,c));
				Base reduced=new Base(a/r,b/r,c/r);
				assert known.contains(reduced);
			}
		}
	}
	
	public static void main(String[] args)	{
		int count=0;
		int lastA=N/3;
		Tracer tracer=new Tracer();
		SortedMap<Integer,Integer> counters=new TreeMap<>();
		for (int a=1;a<=lastA;++a)	{
			int lastB=(N-a)/2;
			for (int b=a;b<=lastB;++b)	{
				int lastC=Math.min(a+b-1,N-(a+b));
				for (int c=b;c<=lastC;++c)	{
					int p1=(a+b)*(a+c);
					int p2=b*c;
					if ((p1%p2)==0)	{
						++count;
						if ((p1/p2)==3) tracer.log(a,b,c);
						EulerUtils.increaseCounter(counters,p1/p2);
					}
				}
			}
		}
		System.out.println(count);
		System.out.println(counters);
		tracer.finalTrace();
	}
}
