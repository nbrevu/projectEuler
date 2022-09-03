package com.euler.common.padic;

import java.util.Arrays;

import com.euler.common.EulerUtils;

/*
 * HUGE Java shortcoming: not allowing numerical templates. In C++, the prime would be a template element and the numbers would be just something
 * like "PAdic<numType,prime> or maybe even "PAdic<numType,prime,precision>", such as "PAdic<int64_t,137,8>".
 * 
 * This is a patch instead. Operations are done using this object instead of the object themselves.
 */
public class RationalPAdicArithmetic {
	private final static long[] ZERO_ARRAY=new long[] {0};
	/*
	 * Immutable object with two guarantees:
	 * 1) Every digit is an integer between 0 and p-1, included. Longs are used for convenience in case that primes above 2^15.5 are used.
	 * 2) The first element of the array will be 0, except if the number is zero itself, in whose case there will be a single element equal to 0.
	 * Note that there can be several "zeros" with different precision. Typically we will want to use certain fixed precision P, and we will set
	 * v to P-1 in each case.
	 * 
	 * Every operation guaranteed that the precision is maintained. If two numbers with different precision operate between them, the lower one
	 * prevails. This guarantees correctness.
	 */
	public class PAdicRational	{
		public final int v;
		private final long[] values;
		/*
		 * Not super efficient, but I'd rather be correct AND keep the guarantees.
		 * The guarantee that the values are in [0..p-1] are watched by all the algorithms generating the values in the first place.
		 */
		private PAdicRational(int v,long[] values)	{
			if (values[0]>0)	{
				this.v=v;
				this.values=values;
			}	else	{
				int shift=0;
				do ++shift; while ((shift<values.length)&&(values[shift]==0));
				if (shift==values.length)	{
					this.v=v+values.length-1;
					this.values=ZERO_ARRAY;
				}	else	{
					this.v=v+shift;
					this.values=Arrays.copyOfRange(values,shift,values.length);
				}
			}
		}
		public int getV()	{
			return v;
		}
		public int getPrecision()	{
			return v+values.length;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append("...");
			for (int i=values.length-1;i>=0;--i) sb.append('[').append(values[i]).append(']');
			if (v!=0) sb.append("*p^").append(v);
			return sb.toString();
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(values)+v;
		}
		@Override
		public boolean equals(Object other)	{
			if (other==this) return true;
			else if (!(other instanceof PAdicRational)) return false;
			PAdicRational parOther=(PAdicRational)other;
			return (v==parOther.v)&&Arrays.equals(values,parOther.values);
		}
		// Useful for internal operations like multiplication and division.
		private long[] getDigits()	{
			return Arrays.copyOf(values,values.length);
		}
		public long getDigitAt(int pos)	{
			if ((pos<v)||(pos>=getPrecision())) return 0l;
			else return values[pos-v];
		}
		public boolean isZero()	{
			return values[0]==0l;
		}
	}
	
	public class PAdicRationalSquareMatrix	{
		private final PAdicRational[][] elements;
		public PAdicRationalSquareMatrix(int size)	{
			elements=new PAdicRational[size][size];
			for (int i=0;i<size;++i) for (int j=0;j<size;++j) elements[i][j]=ZERO;
		}
		private PAdicRationalSquareMatrix(PAdicRational[][] elements)	{
			this.elements=elements;
		}
		public void set(int i,int j,PAdicRational n)	{
			elements[i][j]=n;
		}
		public PAdicRational get(int i,int j)	{
			return elements[i][j];
		}
		public PAdicRationalSquareMatrix copy()	{
			PAdicRational[][] result=new PAdicRational[elements.length][];
			for (int i=0;i<result.length;++i) result[i]=Arrays.copyOf(elements[i],elements[i].length);
			return new PAdicRationalSquareMatrix(result);
		}
		public PAdicRationalSquareMatrix destructiveInverse()	{
			PAdicRationalSquareMatrix result=new PAdicRationalSquareMatrix(elements.length);
			for (int i=0;i<elements.length;++i) result.elements[i][i]=ONE;
			int n=elements.length;
			for (int i=0;i<n;++i)	{
				PAdicRational toDivide=elements[i][i];
				if (toDivide.isZero())	{
					int row=getRowToExchange(elements,i);
					swapRows(elements,i,row);
					swapRows(result.elements,i,row);
					toDivide=elements[i][i];
				}
				elements[i][i]=ONE;
				for (int k=i+1;k<n;++k) elements[i][k]=divide(elements[i][k],toDivide);
				for (int k=0;k<n;++k) result.elements[i][k]=divide(result.elements[i][k],toDivide);
				for (int j=i+1;j<n;++j)	{
					PAdicRational toMultiply=elements[j][i];
					if (!toMultiply.isZero())	{
						elements[j][i]=ZERO;
						for (int k=i+1;k<n;++k) elements[j][k]=subtract(elements[j][k],multiply(elements[i][k],toMultiply));
						for (int k=0;k<n;++k) result.elements[j][k]=subtract(result.elements[j][k],multiply(result.elements[i][k],toMultiply));
					}
				}
			}
			for (int i=n-1;i>0;--i) for (int j=i-1;j>=0;--j)	{
				PAdicRational toMultiply=elements[j][i];
				if (!toMultiply.isZero()) for (int k=0;k<n;++k) result.elements[j][k]=subtract(result.elements[j][k],multiply(result.elements[i][k],toMultiply));
			}
			return result;
		}
		public void destructiveSolve(PAdicRational[] vector)	{
			int n=elements.length;
			for (int i=0;i<n;++i)	{
				PAdicRational toDivide=elements[i][i];
				if (toDivide.isZero())	{
					int row=getRowToExchange(elements,i);
					swapRows(elements,i,row);
					PAdicRational swap=vector[i];
					vector[i]=vector[row];
					vector[row]=swap;
					toDivide=elements[i][i];
				}
				elements[i][i]=ONE;
				for (int k=i+1;k<n;++k) elements[i][k]=divide(elements[i][k],toDivide);
				vector[i]=divide(vector[i],toDivide);
				for (int j=i+1;j<n;++j)	{
					PAdicRational toMultiply=elements[j][i];
					if (!toMultiply.isZero())	{
						elements[j][i]=ZERO;
						for (int k=i+1;k<n;++k) elements[j][k]=subtract(elements[j][k],multiply(elements[i][k],toMultiply));
						vector[j]=subtract(vector[j],multiply(vector[i],toMultiply));
					}
				}
			}
			for (int i=n-1;i>0;--i) for (int j=i-1;j>=0;--j)	{
				PAdicRational toMultiply=elements[j][i];
				if (!toMultiply.isZero()) vector[j]=subtract(vector[j],multiply(vector[i],toMultiply));
			}
		}
		private int getRowToExchange(PAdicRational[][] matrix,int diag)	{
			for (int i=diag+1;i<matrix.length;++i) if (!matrix[i][diag].isZero()) return i;
			throw new ArithmeticException("Singular matrix.");
		}
		private void swapRows(PAdicRational[][] matrix,int i,int j)	{
			PAdicRational[] swap=matrix[i];
			matrix[i]=matrix[j];
			matrix[j]=swap;
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			for (int i=0;i<elements.length;++i)	{
				if (i!=0) result.append(System.lineSeparator());
				result.append("[{").append(elements[i][0].toString());
				for (int j=1;j<elements.length;++j) result.append("} {").append(elements[i][j]);
				result.append("}]");
			}
			return result.toString();
		}
	}
	
	public final long p;
	public final int defaultPrecision;
	private final long[] inverses;
	private final PAdicRational ZERO;
	private final PAdicRational ONE;
	
	public RationalPAdicArithmetic(long p,int defaultPrecision)	{
		this.p=p;
		this.defaultPrecision=defaultPrecision;
		inverses=EulerUtils.calculateModularInverses2((int)p,p);
		ZERO=new PAdicRational(defaultPrecision-1,ZERO_ARRAY);
		long[] oneDigits=new long[defaultPrecision];
		oneDigits[0]=1l;
		ONE=new PAdicRational(0,oneDigits);
	}
	
	private long safeMod(long n)	{
		long result=n%p;
		return (result<0)?(result+p):result;
	}
	private PAdicRational getZero(int precision)	{
		return new PAdicRational(precision-1,ZERO_ARRAY);
	}
	
	public PAdicRational add(PAdicRational a,PAdicRational b)	{
		int v=Math.min(a.v,b.v);
		int precision=Math.min(a.getPrecision(),b.getPrecision());
		int size=precision-v;
		long[] values=new long[size];
		long carry=0;
		for (int i=v;i<precision;++i)	{
			long value=a.getDigitAt(i)+b.getDigitAt(i)+carry;
			values[i-v]=value%p;
			carry=value/p;
		}
		return new PAdicRational(v,values);
	}
	public PAdicRational subtract(PAdicRational minuend,PAdicRational subtrahend)	{
		int v=Math.min(minuend.v,subtrahend.v);
		int precision=Math.min(minuend.getPrecision(),subtrahend.getPrecision());
		int size=precision-v;
		long[] values=new long[size];
		long carry=0;
		for (int i=v;i<precision;++i)	{
			long value=minuend.getDigitAt(i)-(subtrahend.getDigitAt(i)+carry);
			long digit=safeMod(value);
			values[i-v]=digit;
			carry=(digit-value)/p;
		}
		return new PAdicRational(v,values);
	}
	public PAdicRational multiply(PAdicRational a,PAdicRational b)	{
		if (a.isZero()||b.isZero()) return getZero(a.getPrecision()+b.getPrecision());
		int aV=a.v;
		int bV=b.v;
		int aPrec=a.getPrecision();
		int bPrec=b.getPrecision();
		int v=aV+bV;
		/*
		int precision=Math.min(aPrec,b.getPrecision());
		int requiredDigits=precision-v;
		*/
		int requiredDigits=Math.min(aPrec-aV,bPrec-bV);
		int precision=v+requiredDigits;
		if (requiredDigits<=0) return (precision==defaultPrecision)?ZERO:getZero(precision);
		long[] digits=new long[requiredDigits];
		for (int i=aV;i<aPrec;++i)	{
			int maxJ=Math.min(bPrec,precision-i);
			for (int j=bV;j<maxJ;++j)	{
				int shift=i+j-v;
				digits[shift]+=a.getDigitAt(i)*b.getDigitAt(j);
			}
		}
		long carry=0;
		for (int i=0;i<digits.length;++i)	{
			long digit=digits[i]+carry;
			digits[i]=digit%p;
			carry=digit/p;
		}
		return new PAdicRational(v,digits);
	}
	public PAdicRational multiplyTimesPrimePower(PAdicRational n,int addedPower)	{
		return new PAdicRational(n.v+addedPower,n.values);
	}
	public PAdicRational pow(PAdicRational base,int exp)	{
		PAdicRational current=base;
		PAdicRational result=ONE;
		while (exp>0)	{
			if ((exp%2)==1) result=multiply(result,current);
			current=multiply(current,current);
			exp/=2;
		}
		return result;
	}
	public PAdicRational divide(PAdicRational dividend,PAdicRational divisor)	{
		if (divisor.values[0]==0) throw new ArithmeticException("Can't divide by zero.");
		int aV=dividend.v;
		int bV=divisor.v;
		int aPrec=dividend.getPrecision();
		int bPrec=divisor.getPrecision();
		int v=aV-bV;
		/*-
		int maxPrecision=Math.min(aPrec,bPrec);
		int maxRequiredDigits=maxPrecision-v;
		*/
		int maxRequiredDigits=Math.min(aPrec-aV,bPrec-bV);
		int maxPrecision=v+maxRequiredDigits;
		if (maxRequiredDigits<=0) return (maxPrecision==defaultPrecision)?ZERO:getZero(maxPrecision);
		int requiredDigits=Math.min(dividend.values.length,maxRequiredDigits);
		long[] digits=new long[requiredDigits];
		long[] dividendCopy=dividend.getDigits();
		long inverse=inverses[(int)divisor.values[0]];
		for (int i=0;i<digits.length;++i)	{
			if (dividendCopy[i]==0)	{
				// digits[i]=0;	// Already the default value, no need to spend cycles.
				continue;
			}
			long quotDigit=(inverse*dividendCopy[i])%p;
			digits[i]=quotDigit;
			long carry=0;
			for (int j=0;(i+j)<dividendCopy.length;++j)	{
				long toSubtract=quotDigit*((j>=divisor.values.length)?0:divisor.values[j])-carry;
				long fullRemainder=dividendCopy[i+j]-toSubtract;
				long digit=safeMod(fullRemainder);
				carry=(fullRemainder-digit)/p;
				dividendCopy[i+j]=digit;
			}
		}
		return new PAdicRational(v,digits);
	}
	public PAdicRational inverse(long number)	{
		int v=0;
		while ((number%p)==0)	{
			number/=p;
			--v;
		}
		int requiredDigits=defaultPrecision-v;
		if (requiredDigits<=0) return ZERO;
		long[] digits=new long[requiredDigits];
		long inverse=inverses[(int)safeMod(number)];
		long remainder=1;
		for (int i=0;i<requiredDigits;++i)	{
			long digit=safeMod(inverse*remainder);
			remainder=(remainder-number*digit)/p;
			digits[i]=digit;
		}
		return new PAdicRational(v,digits);
	}
	public PAdicRational getInteger(long number)	{
		if (number==0) return ZERO;
		int v=0;
		while ((number%p)==0)	{
			number/=p;
			++v;
		}
		int requiredDigits=defaultPrecision-v;
		if (requiredDigits<=0) return ZERO;
		long[] digits=new long[requiredDigits];
		for (int i=0;i<requiredDigits;++i)	{
			long digit=safeMod(number);
			number=(number-digit)/p;
			digits[i]=digit;
		}
		return new PAdicRational(v,digits);
	}
	public PAdicRationalSquareMatrix createMatrix(int size)	{
		return new PAdicRationalSquareMatrix(size);
	}
	public PAdicRationalSquareMatrix inverse(PAdicRationalSquareMatrix original)	{
		PAdicRationalSquareMatrix copy=original.copy();
		return copy.destructiveInverse();
	}
	// Assumes same size. Bad things may happen if there is a mismatch.
	public PAdicRationalSquareMatrix multiply(PAdicRationalSquareMatrix a,PAdicRationalSquareMatrix b)	{
		int n=a.elements.length;
		PAdicRational[][] matrix=new PAdicRational[n][n];
		PAdicRationalSquareMatrix result=new PAdicRationalSquareMatrix(matrix);
		for (int i=0;i<n;++i) for (int j=0;j<n;++j)	{
			PAdicRational value=ZERO;
			for (int k=0;k<n;++k) value=add(value,multiply(a.elements[i][k],b.elements[k][j]));
			result.elements[i][j]=value;
		}
		return result;
	}
	// Also assumes that the sizes are OK.
	public PAdicRational[] multiply(PAdicRationalSquareMatrix matrix,PAdicRational[] vector)	{
		int n=vector.length;
		PAdicRational[] result=new PAdicRational[vector.length];
		for (int i=0;i<n;++i)	{
			PAdicRational elem=ZERO;
			for (int j=0;j<n;++j) elem=add(elem,multiply(matrix.elements[i][j],vector[j]));
			result[i]=elem;
		}
		return result;
	}
	public PAdicRational[] solve(PAdicRationalSquareMatrix matrix,PAdicRational[] vector)	{
		PAdicRationalSquareMatrix tmp=matrix.copy();
		PAdicRational[] result=Arrays.copyOf(vector,vector.length);
		tmp.destructiveSolve(result);
		return result;
	}
	
	public static void main(String[] args)	{
		RationalPAdicArithmetic arithmetic=new RationalPAdicArithmetic(5,12);
		for (int i=1;i<=100;++i)	{
			PAdicRational n=arithmetic.getInteger(i);
			PAdicRational inv=arithmetic.inverse(i);
			PAdicRational one=arithmetic.multiply(n,inv);
			System.out.println(String.format("%d (inverso): {%s}*{%s}={%s}.",i,n.toString(),inv.toString(),one.toString()));
		}
		PAdicRational one=arithmetic.getInteger(1);
		for (int i=1;i<=100;++i)	{
			PAdicRational n=arithmetic.getInteger(i);
			PAdicRational inv=arithmetic.divide(one,n);
			System.out.println(String.format("%d (división): {%s}*{%s}={%s}.",i,n.toString(),inv.toString(),arithmetic.multiply(n,inv)));
		}
		PAdicRationalSquareMatrix m=arithmetic.createMatrix(4);
		m.set(0,0,arithmetic.getInteger(5));
		m.set(0,1,arithmetic.getInteger(4));
		m.set(0,2,arithmetic.getInteger(3));
		m.set(0,3,arithmetic.getInteger(2));
		m.set(1,0,arithmetic.getInteger(0));
		m.set(1,1,arithmetic.getInteger(1));
		m.set(1,2,arithmetic.getInteger(7));
		m.set(1,3,arithmetic.getInteger(8));
		m.set(2,0,arithmetic.getInteger(9));
		m.set(2,1,arithmetic.getInteger(12));
		m.set(2,2,arithmetic.getInteger(0));
		m.set(2,3,arithmetic.getInteger(3));
		m.set(3,0,arithmetic.getInteger(2));
		m.set(3,1,arithmetic.getInteger(2));
		m.set(3,2,arithmetic.getInteger(2));
		m.set(3,3,arithmetic.getInteger(6));
		System.out.println("Original matrix:");
		System.out.println(m.toString());
		PAdicRationalSquareMatrix inv=arithmetic.inverse(m);
		System.out.println("Inverse matrix:");
		System.out.println(inv.toString());
		PAdicRationalSquareMatrix eye=arithmetic.multiply(inv,m);
		System.out.println("Expecting the identity matrix (1):");
		System.out.println(eye.toString());
		PAdicRationalSquareMatrix eye2=arithmetic.multiply(m,inv);
		System.out.println("Expecting the identity matrix (2):");
		System.out.println(eye2.toString());
		PAdicRational[] vector=new PAdicRational[4];
		vector[0]=arithmetic.getInteger(1);
		vector[1]=arithmetic.getInteger(2);
		vector[2]=arithmetic.getInteger(3);
		vector[3]=arithmetic.getInteger(4);
		PAdicRational[] vecInverse=arithmetic.solve(m,vector);
		System.out.println("Vector inverse: "+Arrays.toString(vecInverse)+".");
		PAdicRational[] recreated=arithmetic.multiply(m,vecInverse);
		System.out.println("Is this [1,2,3,4]? "+Arrays.toString(recreated)+".");
	}
}
