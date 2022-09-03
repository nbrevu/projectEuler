package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.math.LongMath;

public class Euler442_2 {
	private final static int DIGITS=18;
	private final static long ORDINAL=LongMath.pow(10l,DIGITS);
	private final static int MAX_POWER=DIGITS-1;
	private final static int MANUAL_SEGMENT=1000;
	
	private static class DigitPattern	{
		private final int[] digits;
		public final static DigitPattern NO_PATTERN=new DigitPattern(new int[0]);
		private DigitPattern(int[] digits)	{
			this.digits=digits;
		}
		public static DigitPattern fromNumber(long n)	{
			int numDigits=LongMath.log10(n+1,RoundingMode.UP);
			int[] result=new int[numDigits];
			for (int i=result.length-1;i>=0;--i)	{
				result[i]=(int)(n%10);
				n/=10;
			}
			return new DigitPattern(result);
		}
		public boolean contains(DigitPattern other)	{
			for (int i=0;i<=digits.length-other.digits.length;++i)	{
				boolean isValid=true;
				for (int j=0;j<other.digits.length;++j) if (digits[i+j]!=other.digits[j])	{
					isValid=false;
					break;
				}
				if (isValid) return true;
			}
			return false;
		}
		public int[] getOverlappingPoints(DigitPattern other)	{
			/*
			 * Returns an array with all the "overlapping lengths", if any, between the end of this pattern and the start of the one passed as an
			 * argument. Thera can be more than one overlapping point; for example, [665315] -> [531578] would have two "lengths":
			 * 1 (66531_5_31578) and 4 (66_5315_78).
			 * 
			 * In this problem we see one such case, but it's too big to actually matter:
			 * Overlapping points from [4, 5, 9, 4, 9, 7, 2, 9, 8, 6, 3, 5, 7, 2, 1, 6, 1] to [1, 6, 1, 0, 5, 1]: [1, 3]
			 * A pattern might overlap with itself! For example, 1331 + 1331 = 133_1_331. 
			 */
			IntStream.Builder result=IntStream.builder();
			int lookingFor=other.digits[0];
			for (int i=1;i<digits.length;++i) if (digits[i]==lookingFor)	{
				int possibleLength=digits.length-i;
				boolean isValid=true;
				for (int j=1;j<possibleLength;++j) if (digits[i+j]!=other.digits[j])	{
					isValid=false;
					break;
				}
				if (isValid) result.accept(possibleLength);
			}
			return result.build().toArray();
		}
		public int length()	{
			return digits.length;
		}
		@Override
		public String toString()	{
			return Arrays.toString(digits);
		}
	}
	
	private static List<DigitPattern> generatePatterns(int maxPower)	{
		List<DigitPattern> result=new ArrayList<>();
		long n=1;
		for (int i=1;i<=maxPower;++i)	{
			n*=11;
			DigitPattern pattern=DigitPattern.fromNumber(n);
			boolean isIncluded=false;
			for (DigitPattern p:result) if (pattern.contains(p))	{
				isIncluded=true;
				break;
			}
			if (!isIncluded) result.add(pattern);
		}
		return result;
	}
	
	private static class ElevenCounter	{
		private static class OverlappedPattern	{
			public final DigitPattern pattern;
			public final int[] positions;
			public OverlappedPattern(DigitPattern pattern,int[] positions)	{
				this.pattern=pattern;
				this.positions=positions;
			}
			@Override
			public String toString()	{
				return String.format("Overlap: %s (%s)",pattern.toString(),positions.toString());
			}
		}
		private final List<DigitPattern> patterns;
		private final Map<DigitPattern,List<OverlappedPattern>> overlappedPatterns;
		public ElevenCounter(int maxPower)	{
			patterns=generatePatterns(maxPower);
			overlappedPatterns=new HashMap<>();
			for (DigitPattern p:patterns) overlappedPatterns.put(p,getOverlaps(p));
		}
		private List<OverlappedPattern> getOverlaps(DigitPattern p)	{
			List<OverlappedPattern> result=new ArrayList<>();
			for (DigitPattern q:patterns)	{
				int[] overlap=p.getOverlappingPoints(q);
				if (overlap.length>=1) result.add(new OverlappedPattern(q,overlap));
			}
			return result;
		}
		private long countElevenFreeNumbers(DigitPattern prefix,int additionalDigits)	{
			overlappedPatterns.computeIfAbsent(prefix,this::getOverlaps);
			return countElevenFreeNumbersRecursive(prefix,additionalDigits,0,true);
		}
		private long countElevenFreeNumbersRecursive(DigitPattern lastElement,int remainingDigits,int extraDigits,boolean isAdd)	{
			long result=LongMath.pow(10l,remainingDigits+extraDigits);
			if (!isAdd) result=-result;
			for (OverlappedPattern overlap:overlappedPatterns.get(lastElement))	{
				DigitPattern addingPattern=overlap.pattern;
				int patternLength=addingPattern.length();
				for (int pos:overlap.positions)	{
					int addedLength=patternLength-pos;
					if (addedLength<=remainingDigits) result+=countElevenFreeNumbersRecursive(addingPattern,remainingDigits-addedLength,extraDigits,!isAdd);
				}
			}
			for (int i=0;i<remainingDigits;++i)	{
				int remainingAfter=remainingDigits-i;
				int extraAfter=extraDigits+i;
				for (DigitPattern p:patterns) if (p.length()<=remainingAfter) result+=countElevenFreeNumbersRecursive(p,remainingAfter-p.length(),extraAfter,!isAdd);
			}
			return result;
		}
		private boolean isElevenFree(DigitPattern pattern)	{
			for (DigitPattern p:patterns) if (pattern.contains(p)) return false;
			return true;
		}
		private long getNthElevenFreeNumberManual(long ordinal,long currentValue)	{
			for (;;)	{
				DigitPattern number=DigitPattern.fromNumber(currentValue);
				if (isElevenFree(number))	{
					--ordinal;
					if (ordinal==0) return currentValue;
				}
				++currentValue;
			}
		}
		private long getNthElevenFreeNumberRecursive(long ordinal,long manualRun,long prefix,int initialDigits)	{
			for (;;)	{
				if (ordinal<=manualRun)	{
					long actualNumber=prefix*LongMath.pow(10l,initialDigits);
					return getNthElevenFreeNumberManual(ordinal,actualNumber);
				}
				DigitPattern prefixPattern=DigitPattern.fromNumber(prefix);
				if (isElevenFree(prefixPattern))	{
					long count=countElevenFreeNumbers(prefixPattern,initialDigits);
					if (count>=ordinal) return getNthElevenFreeNumberRecursive(ordinal,manualRun,10l*prefix,initialDigits-1);
					else ordinal-=count;
				}
				++prefix;
			}
		}
		public long getNthElevenFreeNumber(long ordinal,int initialDigits,long manualRun)	{
			long baseCount=countElevenFreeNumbers(DigitPattern.NO_PATTERN,initialDigits);
			if (baseCount>=ordinal) throw new IllegalArgumentException("The initial set is too large. Use fewer initial digits.");
			return getNthElevenFreeNumberRecursive(ordinal-baseCount,manualRun,1,initialDigits);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ElevenCounter counter=new ElevenCounter(MAX_POWER);
		// This algorithm counts the 0, but that shouldn't count. Therefore, ORDINAL+1.
		long result=counter.getNthElevenFreeNumber(ORDINAL+1,DIGITS,MANUAL_SEGMENT);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
