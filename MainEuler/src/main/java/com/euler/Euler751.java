package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.math.LongMath;

public class Euler751 {
	private final static int N=24;
	
	private static class ObjectWithCarry<T>	{
		public final T object;
		public final long carry;
		public ObjectWithCarry(T object,long carry)	{
			this.object=object;
			this.carry=carry;
		}
	}
	
	private static long[] createPowersOfTenCache()	{
		long[] result=new long[19];
		result[0]=1l;
		for (int i=1;i<result.length;++i) result[i]=10l*result[i-1];
		return result;
	}
	private final static long[] POWERS_OF_TEN=createPowersOfTenCache();
	
	private static class Section	{
		public final long value;
		public final int numDigits;
		public Section(long value)	{
			this.value=value;
			numDigits=LongMath.log10(value+1,RoundingMode.UP);	// And THIS was the stupid error. That "10" between 6 and 19 was wreaking havoc!
		}
		private Section(long value,int numDigits)	{
			this.value=value;
			this.numDigits=numDigits;
		}
		public ObjectWithCarry<Section> multiply(long factor,long carry)	{
			long product=value*factor+carry;
			long power10=POWERS_OF_TEN[numDigits];
			long rem=product%power10;
			long newCarry=product/power10;
			Section result=new Section(rem,numDigits);
			return new ObjectWithCarry<>(result,newCarry);
		}
	}
	
	private static class SectionedNumber	{
		// Assumes that the tip is 1.
		public final Section[] sections;
		public SectionedNumber(Section[] sections)	{
			this.sections=sections;
		}
		public static SectionedNumber initial()	{
			Section s2=new Section(2l);
			return new SectionedNumber(new Section[] {s2});
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append('.');
			for (Section s:sections)	{
				String format=String.format("%%0%dd",s.numDigits);
				sb.append(String.format(format,s.value));
			}
			return sb.toString();
		}
		public int getNumDigits()	{
			int result=0;
			for (Section s:sections) result+=s.numDigits;
			return result;
		}
		public ObjectWithCarry<SectionedNumber> multiply(long factor)	{
			long carry=0l;
			Section[] result=new Section[sections.length];
			for (int i=sections.length-1;i>=0;--i)	{
				ObjectWithCarry<Section> newSection=sections[i].multiply(factor,carry);
				result[i]=newSection.object;
				carry=newSection.carry;
			}
			return new ObjectWithCarry<>(new SectionedNumber(result),carry);
		}
		public boolean check()	{
			long currentValue=2l;
			SectionedNumber currentNumber=this;
			for (int i=0;i<sections.length;++i)	{
				ObjectWithCarry<SectionedNumber> nextNumber=currentNumber.multiply(currentValue);
				long nextValue=currentValue+nextNumber.carry;
				if (nextValue!=sections[i].value) return false;
				currentValue=nextValue;
				currentNumber=nextNumber.object;
			}
			return true;
		}
		private SectionedNumber concat(Section s)	{
			Section[] result=new Section[sections.length+1];
			System.arraycopy(sections,0,result,0,sections.length);
			result[sections.length]=s;
			return new SectionedNumber(result);
		}
		public Iterable<SectionedNumber> getChildren()	{
			long initialValue=sections[sections.length-1].value;
			long finalValue=2*initialValue;
			return ()->new Iterator<SectionedNumber>()	{
				private long currentValue=initialValue;
				@Override
				public boolean hasNext() {
					return currentValue<finalValue;
				}
				@Override
				public SectionedNumber next() {
					SectionedNumber result=concat(new Section(currentValue));
					++currentValue;
					return result;
				}
			};
		}
		private static int countCommonDigits(List<SectionedNumber> ns)	{
			SectionedNumber base=ns.get(0);
			if (ns.size()==1) return base.getNumDigits();
			else throw new IllegalArgumentException("Oh, it turns out that this does happen.");
		}
	}
	
	private static SectionedNumber getResult()	{
		List<SectionedNumber> currentGeneration=Arrays.asList(SectionedNumber.initial());
		for (;;)	{
			if (currentGeneration.isEmpty()) throw new IllegalStateException("Fission mailed.");
			List<SectionedNumber> nextGeneration=new ArrayList<>();
			for (SectionedNumber s:currentGeneration)	{
				boolean alreadyPassed=false;
				for (SectionedNumber child:s.getChildren())	{
					boolean pass=child.check();
					if (pass)	{
						nextGeneration.add(child);
						alreadyPassed=true;
					}	else if (alreadyPassed) break;
				}
				if (SectionedNumber.countCommonDigits(nextGeneration)>=N) return nextGeneration.get(0);
				else currentGeneration=nextGeneration;
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SectionedNumber number=getResult();
		StringBuilder result=new StringBuilder();
		result.append('2');
		result.append(number.toString().substring(0,1+N));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result.toString());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
