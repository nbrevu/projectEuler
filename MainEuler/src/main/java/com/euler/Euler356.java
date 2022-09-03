package com.euler;

import java.math.BigInteger;

public class Euler356 {
	private static class CubicExpressions	{
		private final BigInteger n;
		private final BigInteger twoN;
		private final BigInteger twoTwoN;
		private final BigInteger nTwoN;
		private final Instance ONE=new Instance(BigInteger.ONE,BigInteger.ZERO,BigInteger.ZERO);
		public CubicExpressions(int n)	{
			this.n=BigInteger.valueOf(n);
			twoN=BigInteger.valueOf(1l<<n);
			twoTwoN=twoN.multiply(twoN);
			nTwoN=this.n.multiply(twoN);
		}
		private class Instance	{
			// Represents x + y*a_n + z*a_n^2.
			private final BigInteger x;
			private final BigInteger y;
			private final BigInteger z;
			private Instance(BigInteger x,BigInteger y,BigInteger z)	{
				this.x=x;
				this.y=y;
				this.z=z;
			}
		}
		public Instance root()	{
			return new Instance(BigInteger.ZERO,BigInteger.ONE,BigInteger.ZERO);
		}
		public Instance product(Instance a,Instance b)	{
			/*
			 * x*x -> degree 0.
			 * x*y, y*x -> degree 1.
			 * x*z, y*y, z*x ->  degree 2.
			 * y*z, z*y -> degree 3, transformed into 2^n*x^2-n.
			 * z*z -> degree 4, transformed into 2^2n*x^2 -nx -n*2^n
			 * 
			 * Therefore the result has:
			 * degree 0 -> (x*x) -n*(y*z+z*y) -n*2^n*(z*z).
			 * degree 1 -> (x*y+y*x) -n*(z*z)
			 * degree 2 -> (x*z+y*y+z*x) +2^n*(y*z+z*y) +2^2n*(z*z)
			 */
			BigInteger xx=a.x.multiply(b.x);
			BigInteger xy=a.x.multiply(b.y);
			BigInteger xz=a.x.multiply(b.z);
			BigInteger yx=a.y.multiply(b.x);
			BigInteger yy=a.y.multiply(b.y);
			BigInteger yz=a.y.multiply(b.z);
			BigInteger zx=a.z.multiply(b.x);
			BigInteger zy=a.z.multiply(b.y);
			BigInteger zz=a.z.multiply(b.z);
			BigInteger yzzy=yz.add(zy);
			BigInteger x=xx.subtract(n.multiply(yzzy)).subtract(nTwoN.multiply(zz));
			BigInteger y=xy.add(yx).subtract(n.multiply(zz));
			BigInteger z=xz.add(yy).add(zx).add(twoN.multiply(yzzy)).add(twoTwoN.multiply(zz));
			return new Instance(x,y,z);
		}
		public Instance binaryExponentiation(Instance i1,long exp)	{
			Instance result=ONE;
			Instance power=i1;
			while (exp>0)	{
				if ((exp&1)!=0) result=product(result,power);
				power=product(power,power);
				exp>>=1;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		CubicExpressions cubic=new CubicExpressions(2);
		CubicExpressions.Instance instance=cubic.root();
		CubicExpressions.Instance pow=cubic.binaryExponentiation(instance,100l);
		System.out.println(pow.x);
		System.out.println(pow.y);
		System.out.println(pow.z);
	}
}
