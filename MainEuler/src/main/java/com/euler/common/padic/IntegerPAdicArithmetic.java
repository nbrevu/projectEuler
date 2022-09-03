package com.euler.common.padic;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.koloboke.function.IntIntToIntFunction;

/*
 * HUGE Java shortcoming: not allowing numerical templates. In C++, the prime would be a template element and the numbers would be just something
 * like "PAdic<numType,prime> or maybe even "PAdic<numType,prime,precision>", such as "PAdic<int64_t,137,8>".
 * 
 * This is a patch instead. Operations are done using this object instead of the object themselves.
 * 
 * Also, this is obsolete. Use RationalPAdicArithmetic always.
 */
public class IntegerPAdicArithmetic {
	// The array is mutable despite being final! What's fixed is the length.
	public class PAdicInteger	{
		public final long[] values;
		private PAdicInteger(int size)	{
			values=new long[size];
		}
		private PAdicInteger(long[] values)	{
			this.values=values;
		}
		public int v()	{
			for (int i=0;i<values.length;++i) if (values[i]!=0) return i;
			return values.length;
		}
		private PAdicInteger reduce(int digits)	{
			return new PAdicInteger(Arrays.copyOfRange(values,digits,values.length));
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append("...");
			for (int i=values.length-1;i>=0;--i) sb.append('[').append(values[i]).append(']');
			return sb.toString();
		}
		@Override
		public int hashCode()	{
			// ACHTUNG! This is dangerous since these numbers are mutable. It's not recommended to use these numbers as keys in sets or maps.
			return Arrays.hashCode(values);
		}
		@Override
		public boolean equals(Object other)	{
			if (other==this) return true;
			else if (!(other instanceof PAdicInteger)) return false;
			PAdicInteger paiOther=(PAdicInteger)other;
			return Arrays.equals(values,paiOther.values);
		}
		public PAdicInteger copy()	{
			return new PAdicInteger(Arrays.copyOf(values,values.length));
		}
	}
	
	public final long p;
	private final long[] inverses;
	
	public IntegerPAdicArithmetic(long p)	{
		this.p=p;
		inverses=EulerUtils.calculateModularInverses2((int)p,p);
	}
	
	public PAdicInteger newNumber(int size)	{
		return new PAdicInteger(size);
	}
	
	public PAdicInteger add(PAdicInteger a,PAdicInteger b)	{
		int size=Math.min(a.values.length,b.values.length);
		PAdicInteger result=newNumber(size);
		long carry=0;
		for (int i=0;i<size;++i)	{
			long digit=a.values[i]+b.values[i]+carry;
			result.values[i]=digit%p;
			carry=digit/p;
		}
		return result;
	}
	private void multiply(PAdicInteger a,PAdicInteger b,PAdicInteger result)	{
		for (int i=0;i<a.values.length;++i) for (int j=0;j<b.values.length;++j)	{
			int s=i+j;
			if (s>=result.values.length) break;	// Next i.
			result.values[s]+=a.values[i]*b.values[j];
		}
		long carry=0;
		for (int i=0;i<result.values.length;++i)	{
			long digit=result.values[i]+carry;
			result.values[i]=digit%p;
			carry=digit/p;
		}
	}
	public PAdicInteger multiplySafe(PAdicInteger a,PAdicInteger b)	{
		int size=Math.min(a.values.length,b.values.length);
		PAdicInteger result=newNumber(size);
		multiply(a,b,result);
		return result;
	}
	public PAdicInteger multiplyUnsafe(PAdicInteger a,PAdicInteger b)	{
		int size=Math.max(a.values.length,b.values.length);
		PAdicInteger result=newNumber(size);
		multiply(a,b,result);
		return result;
	}
	private void divideDestructive(PAdicInteger dividend,PAdicInteger divisor,PAdicInteger result)	{
		long inverse=inverses[(int)divisor.values[0]];
		for (int i=0;i<dividend.values.length;++i)	{
			if (dividend.values[i]==0)	{
				result.values[i]=0;
				continue;
			}
			long quotDigit=(inverse*dividend.values[i])%p;
			result.values[i]=quotDigit;
			long carry=0;
			for (int j=0;(i+j)<dividend.values.length;++j)	{
				long toSubtract=quotDigit*((j>=divisor.values.length)?0:divisor.values[j])-carry;
				long fullRemainder=dividend.values[i+j]-toSubtract;
				long digit=fullRemainder%p;
				if (digit<0) digit+=p;
				carry=(fullRemainder-digit)/p;
				dividend.values[i+j]=digit;
			}
		}
	}
	public PAdicInteger divideNonDestructive(PAdicInteger dividend,PAdicInteger divisor,IntIntToIntFunction determinePrecision)	{
		int v1=dividend.v();
		int v2=divisor.v();
		if (v2>v1) throw new ArithmeticException("This division doesn't result in a P-adic integer.");
		if (v2>0)	{
			dividend=dividend.reduce(v2);
			divisor=divisor.reduce(v2);
		}	else	{
			// We need this because the division is destructive. The divisor is unaffected, no need to copy.
			dividend=dividend.copy();
		}
		int precision=determinePrecision.applyAsInt(dividend.values.length,divisor.values.length);
		PAdicInteger result=new PAdicInteger(precision);
		divideDestructive(dividend,divisor,result);
		return result;
	}
	public PAdicInteger divideSafe(PAdicInteger dividend,PAdicInteger divisor)	{
		// Uses the minimum precision guaranteed to be correct.
		return divideNonDestructive(dividend,divisor,Math::min);
	}
	public PAdicInteger divideUnsafe(PAdicInteger dividend,PAdicInteger divisor)	{
		// Uses the precision of the dividend no matter what.
		return divideNonDestructive(dividend,divisor,(int prec1,int prec2)->prec1);
	}
	
	// Calculate the inverse of a number (modulo the prime p). Assumes that the number is not a multiple of p.
	public PAdicInteger inverse(long number,int precision)	{
		PAdicInteger result=newNumber(precision);
		long remainder=1;
		long baseMod=number%p;
		if (baseMod<0) baseMod+=p;
		long inverse=inverses[(int)baseMod];
		for (int i=0;i<result.values.length;++i)	{
			long digit=(inverse*remainder)%p;
			if (digit<0) digit+=p;
			result.values[i]=digit;
			remainder=(remainder-number*digit)/p;
		}
		return result;
	}
	
	public PAdicInteger getInteger(long n,int precision)	{
		PAdicInteger result=newNumber(precision);
		for (int i=0;i<result.values.length;++i)	{
			long digit=n%p;
			if (digit<0) digit+=p;
			n=(n-digit)/p;
			result.values[i]=digit;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		IntegerPAdicArithmetic arithmetic=new IntegerPAdicArithmetic(5);
		for (int i=1;i<=100;++i) if ((i%5)!=0)	{
			PAdicInteger n=arithmetic.getInteger(i,8);
			PAdicInteger inv=arithmetic.inverse(i,8);
			PAdicInteger one=arithmetic.multiplySafe(n,inv);
			System.out.println(String.format("%d (inverso): {%s}*{%s}={%s}.",i,n.toString(),inv.toString(),one.toString()));
		}
		PAdicInteger one=arithmetic.getInteger(1,8);
		for (int i=1;i<=100;++i) if ((i%5)!=0)	{
			PAdicInteger n=arithmetic.getInteger(i,8);
			PAdicInteger inv=arithmetic.divideUnsafe(one,n);
			System.out.println(String.format("%d (división): {%s}*{%s}={%s}.",i,n.toString(),inv.toString(),arithmetic.multiplyUnsafe(n,inv)));
		}
	}
}
