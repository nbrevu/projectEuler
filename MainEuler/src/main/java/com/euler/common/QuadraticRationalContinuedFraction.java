package com.euler.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.LongConsumer;

import com.euler.common.ConvergentInversion.QuadraticRational;
import com.euler.common.Convergents.ContinuedFraction;
import com.euler.common.Convergents.Convergent;
import com.euler.common.Convergents.InfiniteContinuedFraction;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

/*
 * FUNCIONA. CREO QUE VOY A LLORAR.
 */
public class QuadraticRationalContinuedFraction implements ContinuedFraction  {
	public static enum IterationType	{
		UNLIMITED	{
			@Override
			public OptionalInt getCycles(int cycleLength)	{
				return OptionalInt.empty();
			}
		},
		SINGLE_PASS	{
			@Override
			public OptionalInt getCycles(int cycleLength)	{
				int length=1+(cycleLength%2);
				return OptionalInt.of(length);
			}
		},
		DOUBLE_PASS	{
			@Override
			public OptionalInt getCycles(int cycleLength)	{
				int length=2*(1+(cycleLength%2));
				return OptionalInt.of(length);
			}
		};
		public abstract OptionalInt getCycles(int cycleLength);
	}
	// https://sites.millersville.edu/bikenaga/number-theory/periodic-continued-fractions/periodic-continued-fractions.html
	private static class SequenceElement	{
		public final BigInteger m;
		public final BigInteger s;
		public SequenceElement(BigInteger m,BigInteger s)	{
			this.m=m;
			this.s=s;
		}
		@Override
		public int hashCode()	{
			return Objects.hash(m,s);
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof SequenceElement)	{
				SequenceElement seOther=(SequenceElement)other;
				return (m.equals(seOther.m)&&s.equals(seOther.s));
			}	else return false;
		}
	}
	private final long[] initialValues;
	private final long[] periodicValues;
	private final OptionalInt iterations;
	private QuadraticRationalContinuedFraction(long[] initialValues,long[] periodicValues,OptionalInt iterations)	{
		this.initialValues=initialValues;
		this.periodicValues=periodicValues;
		this.iterations=iterations;
	}
	@Override
	public String toString()	{
		return String.format("Antes del periodo: %s. Después del periodo: %s.",Arrays.toString(initialValues),Arrays.toString(periodicValues));
	}
	public static QuadraticRationalContinuedFraction getFor(QuadraticRational root,IterationType iterations)	{
		return getForGenericQuadraticRational(root.a,root.b,root.c,iterations);
	}
	public static QuadraticRationalContinuedFraction getForGenericQuadraticRational(long a,long b,long c,IterationType iterations)	{
		return getForGenericQuadraticRational(BigInteger.valueOf(a),BigInteger.valueOf(b),BigInteger.valueOf(c),iterations);
	}
	public static QuadraticRationalContinuedFraction getForGenericQuadraticRational(BigInteger a,BigInteger b,BigInteger c,IterationType iterations)	{
		BigInteger m,d,s;
		if (b.subtract(a.multiply(a)).mod(c.abs()).signum()==0)	{
			m=a;
			d=b;
			s=c;
		}	else	{
			BigInteger absC=c.abs();
			m=a.multiply(absC);
			d=b.multiply(c.multiply(c));
			s=c.multiply(absC);
		}
		List<BigInteger> contFracTerms=new ArrayList<>();
		ObjIntMap<SequenceElement> sequenceElements=HashObjIntMaps.newMutableMap();
		sequenceElements.put(new SequenceElement(m,s),0);
		double sd=Math.sqrt(d.doubleValue());
		for (;;)	{
			double x=(m.doubleValue()+sd)/s.doubleValue();
			BigInteger ak=BigInteger.valueOf((long)Math.floor(x));
			contFracTerms.add(ak);
			m=ak.multiply(s).subtract(m);
			s=d.subtract(m.multiply(m)).divide(s);	// That's right, we use the value of m we just updated in the previous line.
			SequenceElement se=new SequenceElement(m,s);
			int index=sequenceElements.getOrDefault(se,-1);
			if (index>=0)	{
				List<BigInteger> prePeriod=contFracTerms.subList(0,index);
				List<BigInteger> period=contFracTerms.subList(index,contFracTerms.size());
				OptionalInt cyclesToRepeat=iterations.getCycles(period.size());
				return new QuadraticRationalContinuedFraction(toLongArray(prePeriod),toLongArray(period),cyclesToRepeat);
			}
			sequenceElements.put(se,contFracTerms.size());
		}
	}
	private static long[] toLongArray(List<BigInteger> list)	{
		return list.stream().mapToLong(BigInteger::longValue).toArray();
	}
	private static interface ArraySupplier	{
		public long[] nextArray();
	}
	private class ArraysCursor implements LongCursor	{
		private long[] array;
		private int position;
		private final ArraySupplier arraySupplier;
		public ArraysCursor(OptionalInt iterations)	{
			array=initialValues;
			position=-1;
			if (iterations.isPresent())	{
				arraySupplier=new ArraySupplier()	{
					int remaining=iterations.getAsInt();
					@Override
					public long[] nextArray()	{
						if (remaining==0) return null;
						--remaining;
						return periodicValues;
					}
				};
			}	else arraySupplier=()->periodicValues;
		}
		@Override
		public boolean moveNext() {
			++position;
			if (position>=array.length)	{
				array=arraySupplier.nextArray();
				position=0;
			}
			return array!=null;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public long elem() {
			return array[position];
		}
		@Override
		public void forEachForward(LongConsumer arg0) {
			throw new UnsupportedOperationException();
		}
	}
	@Override
	public LongCursor getTermsAsCursor() {
		return new ArraysCursor(iterations);
	}
	public static void main(String[] args)	{
		/*-
		System.out.println(QuadraticRationalContinuedFraction.getForGenericQuadraticRational(BigInteger.valueOf(2),BigInteger.valueOf(7),BigInteger.valueOf(3)));
		System.out.println(QuadraticRationalContinuedFraction.getForGenericQuadraticRational(BigInteger.valueOf(37),BigInteger.valueOf(18),BigInteger.valueOf(191)));
		*/
		/*
		ContinuedFraction fr1=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(BigInteger.valueOf(2),BigInteger.valueOf(7),BigInteger.valueOf(3));
		ContinuedFraction fr2=InfiniteContinuedFraction.getForQuadraticRational(2,7,3);
		*/
		ContinuedFraction fr1=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(BigInteger.valueOf(37),BigInteger.valueOf(18),BigInteger.valueOf(191),IterationType.UNLIMITED);
		ContinuedFraction fr2=InfiniteContinuedFraction.getForQuadraticRational(37,18,191);
		Iterator<Convergent> it1=fr1.iterator();
		Iterator<Convergent> it2=fr2.iterator();
		for (int i=0;i<50;++i)	{
			System.out.println(it1.next()+" <==> "+it2.next());
		}
	}
}
