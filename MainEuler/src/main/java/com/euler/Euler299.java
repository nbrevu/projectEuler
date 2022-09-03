package com.euler;

import java.util.Arrays;

public class Euler299 {
	private static class Triangle299	{
		// These sides are SQUARED.
		private final long straight;
		private final long diagonal;
		private final long hypotenuse;
		public Triangle299(long straight,long diagonal,long hypothenuse)	{
			this.straight=straight;
			this.diagonal=diagonal;
			this.hypotenuse=hypothenuse;
			if (Math.sqrt(hypotenuse)>=Math.sqrt(straight)+Math.sqrt(diagonal)) throw new IllegalArgumentException("¿QUÉ LE ESTÁS HACIENDO A MI AMADO CUERPO DE ALGORITMOS?");
		}
		public boolean isSimilarSymmetric(Triangle299 other)	{
			if (straight*other.diagonal!=diagonal*other.straight) return false;
			return straight*other.hypotenuse==hypotenuse*other.straight;
		}
		public boolean isSimilarStandard(Triangle299 other)	{
			if (straight*other.straight!=diagonal*other.diagonal) return false;
			return straight*other.hypotenuse==hypotenuse*other.diagonal;
		}
		public boolean isSimilar(Triangle299 other)	{
			return isSimilarSymmetric(other)||isSimilarStandard(other);
		}
		@Override
		public String toString()	{
			return "["+straight+","+diagonal+","+hypotenuse+"]";
		}
	}
	
	public static Triangle299[] getTriangles(long a,long b,long d,long p)	{
		long ba=b-a;
		long ba2=ba*ba;
		long ap=a-p;
		long ap2=2*ap*ap;
		long h1=ba2+ap2+2*ba*ap;
		Triangle299 t1=new Triangle299(ba2,ap2,h1);
		long da=d-a;
		long da2=da*da;
		long p2=2*p*p;
		long h2=da2+p2+2*da*p;
		Triangle299 t2=new Triangle299(da2,p2,h2);
		long h3=b*b+d*d;
		Triangle299 t3=new Triangle299(h1,h2,h3);
		return new Triangle299[] {t1,t2,t3};
	}
	
	public static void main(String[] args)	{
		System.out.println("Starting!");
		long counter=0;
		for (long bd=6;bd<5000;++bd)	{
			for (long b=3;;++b)	{
				long d=bd-b;
				if (d<3) break;
				long maxA=Math.min(b,d);
				for (long a=2;a<maxA;++a)	{
					boolean hasP=false;
					for (long p=1;p<a;++p)	{
						Triangle299[] triangles=getTriangles(a,b,d,p);
						if (!triangles[0].isSimilar(triangles[2])) continue;
						boolean isSimilar=false;
						boolean isWeird=false;
						if (triangles[0].isSimilarStandard(triangles[1])) isSimilar=true;
						else if (triangles[0].isSimilarSymmetric(triangles[1]))	{
							isSimilar=true;
							isWeird=true;
							throw new IllegalArgumentException("VORSICHT! NICHT MÖGLICH!");
						}
						if (isSimilar)	{
							hasP=true;
							System.out.println("Similar "+((isWeird)?"(but weird)":"(as expected)")+" triangles found for a="+a+", b="+b+", d="+d+", p="+p+": "+Arrays.toString(triangles)+".");
							if (p*2==a) System.out.println("\tp is exactly half of a.");
							else System.out.println("\tp is not half of a!");
							if ((p*2!=a)&&b!=d) throw new IllegalArgumentException("ES IST TOTAL UNREGELMÄßIG!!!!!");
						}
					}
					if (hasP) ++counter;
				}
			}
		}
		System.out.println("Total: "+counter+" cases encountered.");
	}
}
