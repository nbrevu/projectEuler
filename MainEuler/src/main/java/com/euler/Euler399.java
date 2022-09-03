package com.euler;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.List;

import com.euler.common.Primes;

public class Euler399 {
	private static class Matrix	{
		private final BigInteger[][] coeffs;
		private Matrix(BigInteger a00,BigInteger a01,BigInteger a10,BigInteger a11)	{
			coeffs=new BigInteger[2][2];
			coeffs[0][0]=a00;
			coeffs[0][1]=a01;
			coeffs[1][0]=a10;
			coeffs[1][1]=a11;
		}
		public static Matrix IDENTITY=new Matrix(ONE,ZERO,ZERO,ONE);
		public static Matrix FIBO_BASE=new Matrix(ONE,ONE,ONE,ZERO);
		public Matrix multiply(Matrix other,BigInteger mod)	{
			BigInteger a00=this.coeffs[0][0];
			BigInteger a01=this.coeffs[0][1];
			BigInteger a10=this.coeffs[1][0];
			BigInteger a11=this.coeffs[1][1];
			BigInteger b00=other.coeffs[0][0];
			BigInteger b01=other.coeffs[0][1];
			BigInteger b10=other.coeffs[1][0];
			BigInteger b11=other.coeffs[1][1];
			BigInteger c00=(a00.multiply(b00)).add(a01.multiply(b10)).mod(mod);
			BigInteger c01=(a00.multiply(b01)).add(a01.multiply(b11)).mod(mod);
			BigInteger c10=(a10.multiply(b00)).add(a11.multiply(b10)).mod(mod);
			BigInteger c11=(a10.multiply(b01)).add(a11.multiply(b11)).mod(mod);
			return new Matrix(c00,c01,c10,c11);
		}
		public long getHead()	{
			return coeffs[0][0].longValue();
		}
		@Override
		public String toString()	{
			return "[["+coeffs[0][0]+","+coeffs[0][1]+"],["+coeffs[1][0]+","+coeffs[1][1]+"]]";
		}
	}
	
	private static Matrix getPower(Matrix in,long power,BigInteger mod)	{
		Matrix result=Matrix.IDENTITY;
		Matrix tmpProduct=in;
		while (power>0)	{
			if ((power%2)==1l) result=result.multiply(tmpProduct,mod);
			tmpProduct=tmpProduct.multiply(tmpProduct,mod);
			power/=2;
		}
		return result;
	}
	
	private final static List<Integer> primesUnder2000=Primes.listIntPrimes(2000);
	
	private static int firstMultipleIndex(int mod)	{
		int prev2=1;
		int prev=1;
		for (int index=3;index<=(mod*mod);++index)	{
			int cur=(prev2+prev)%mod;
			if (cur==0) return index;
			prev2=prev;
			prev=cur;
		}
		return -1;
	}
	
	private static int getNthFibonacci(int index,int mod)	{
		Matrix mat=getPower(Matrix.FIBO_BASE,index-1,BigInteger.valueOf(mod));
		return (int)(mat.getHead());
	}
	
	public static void main(String[] args)	{
		for (int p:primesUnder2000)	{
			int index=firstMultipleIndex(p);
			if (index==-1) System.out.println("No encuentro múltiplos de "+p+" :(.");
			else	{
				int rem=getNthFibonacci(index*p,p*p);
				String msg;
				if (rem==0) msg="Múltiplo de "+p+"^2 encontrado correctamente.";
				else msg="No se ha encontrado el múltiplo de "+p+"^2.";
				System.out.println(""+p+": encontrado en "+index+". "+msg);
				if ((p!=5))	{
					int q=p%5;
					boolean isPlusMinus1=(q==1)||(q==4);
					int divisor;
					int addend;
					if (isPlusMinus1)	{
						divisor=p-1;
						addend=1;
					}	else	{
						divisor=p+1;
						addend=2;
					}
					boolean conditionHolds=(divisor%index)==0;
					System.out.println("\t"+p+" es <múltiplo de 5>+-"+addend+". Se espera que el índice sea divisor de "+divisor+": "+(conditionHolds?"CORRECTO":"INCORRECTO")+".");
				}
			}
		}
	}
}
