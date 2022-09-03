package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.common.math.LongMath;

public class Euler366 {
	// Methods alwaysLoses and canWin gracefully lifted from 692's initial experiments.
	private static boolean alwaysLoses(long in,long take)	{
		if (take>=in) return false;
		long t2=2*take;
		in-=take;
		for (long i=1;i<=t2;++i) if (canWin(in,i)) return true;
		return false;
	}
	
	private static boolean canWin(long in,long take)	{
		if (take>=in) return true;
		long t2=2*take;
		in-=take;
		for (long i=1;i<=t2;++i) if (!alwaysLoses(in,i)) return false;
		return true;
	}
	
	private static long simulate(long in)	{
		for (long i=LongMath.divide(in,2,RoundingMode.DOWN);i>=1;--i) if (canWin(in,i)) return i;
		return 0;
	}
	
	private static class TmpZeckendorf	{
		private final static long[] FIBONACCIS=new long[] {1,2,3,5,8,13,21,34,55,89};
		private final static TmpZeckendorf END=new TmpZeckendorf(0,0);
		private final long remaining;
		private final long currentFibo;
		public TmpZeckendorf(long remaining,long toAdd)	{
			this.remaining=remaining;
			this.currentFibo=toAdd;
		}
		public static TmpZeckendorf start(long in)	{
			return new TmpZeckendorf(in,0l);
		}
		public boolean canContinue()	{
			return (remaining>0)||(currentFibo>0);
		}
		public TmpZeckendorf next()	{
			if (remaining==0) return END;
			int position=Arrays.binarySearch(FIBONACCIS,remaining);
			if (position>=0) return new TmpZeckendorf(0,remaining);
			long limitFibo=FIBONACCIS[-position-2];
			return new TmpZeckendorf(remaining-limitFibo,limitFibo);
		}
		public long getFibo()	{
			return currentFibo;
		}
	}
	
	private static long[] getZeckendorfRepresentation(long in)	{
		return Stream.iterate(TmpZeckendorf.start(in),TmpZeckendorf::canContinue,TmpZeckendorf::next).skip(1).mapToLong(TmpZeckendorf::getFibo).toArray();
	}
	
	/*-
H(1)=0; 1=[1].
H(2)=0; 2=[2].
H(3)=0; 3=[3].
H(4)=1; 4=[3, 1].
H(5)=0; 5=[5].
H(6)=1; 6=[5, 1].
H(7)=2; 7=[5, 2].
H(8)=0; 8=[8].
H(9)=1; 9=[8, 1].
H(10)=2; 10=[8, 2].
H(11)=3; 11=[8, 3].
H(12)=1; 12=[8, 3, 1].
H(13)=0; 13=[13].
H(14)=1; 14=[13, 1].
H(15)=2; 15=[13, 2].
H(16)=3; 16=[13, 3].
H(17)=4; 17=[13, 3, 1].
H(18)=5; 18=[13, 5].
H(19)=6; 19=[13, 5, 1].
H(20)=2; 20=[13, 5, 2].
H(21)=0; 21=[21].
H(22)=1; 22=[21, 1].
H(23)=2; 23=[21, 2].
H(24)=3; 24=[21, 3].
H(25)=4; 25=[21, 3, 1].
H(26)=5; 26=[21, 5].
H(27)=6; 27=[21, 5, 1].
H(28)=7; 28=[21, 5, 2].
H(29)=8; 29=[21, 8].
H(30)=9; 30=[21, 8, 1].
H(31)=10; 31=[21, 8, 2].
H(32)=3; 32=[21, 8, 3].
H(33)=1; 33=[21, 8, 3, 1].
H(34)=0; 34=[34].
H(35)=1; 35=[34, 1].
H(36)=2; 36=[34, 2].
H(37)=3; 37=[34, 3].
H(38)=4; 38=[34, 3, 1].
H(39)=5; 39=[34, 5].
H(40)=6; 40=[34, 5, 1].
H(41)=7; 41=[34, 5, 2].
H(42)=8; 42=[34, 8].
H(43)=9; 43=[34, 8, 1].
H(44)=10; 44=[34, 8, 2].
H(45)=11; 45=[34, 8, 3].
H(46)=12; 46=[34, 8, 3, 1].
H(47)=13; 47=[34, 13].
H(48)=14; 48=[34, 13, 1].
H(49)=15; 49=[34, 13, 2].
H(50)=16; 50=[34, 13, 3].
H(51)=4; 51=[34, 13, 3, 1].
H(52)=5; 52=[34, 13, 5].
H(53)=6; 53=[34, 13, 5, 1].
H(54)=2; 54=[34, 13, 5, 2].
H(55)=0; 55=[55].
H(56)=1; 56=[55, 1].
H(57)=2; 57=[55, 2].
H(58)=3; 58=[55, 3].
H(59)=4; 59=[55, 3, 1].
H(60)=5; 60=[55, 5].
H(61)=6; 61=[55, 5, 1].
H(62)=7; 62=[55, 5, 2].
H(63)=8; 63=[55, 8].
H(64)=9; 64=[55, 8, 1].
H(65)=10; 65=[55, 8, 2].
H(66)=11; 66=[55, 8, 3].
H(67)=12; 67=[55, 8, 3, 1].
H(68)=13; 68=[55, 13].
H(69)=14; 69=[55, 13, 1].
H(70)=15; 70=[55, 13, 2].
H(71)=16; 71=[55, 13, 3].
H(72)=17; 72=[55, 13, 3, 1].
H(73)=18; 73=[55, 13, 5].
H(74)=19; 74=[55, 13, 5, 1].
H(75)=20; 75=[55, 13, 5, 2].
H(76)=21; 76=[55, 21].
H(77)=22; 77=[55, 21, 1].
H(78)=23; 78=[55, 21, 2].
H(79)=24; 79=[55, 21, 3].
H(80)=25; 80=[55, 21, 3, 1].
H(81)=26; 81=[55, 21, 5].
H(82)=27; 82=[55, 21, 5, 1].
	 */
	
	public static void main(String[] args)	{
		// H(1)=1, H(4)=1, H(17)=1, H(8)=8 and H(18)=5
		/*
		long[] toTry=new long[] {1,4,17,8,18};
		for (long i:toTry) System.out.println("H("+i+")="+simulate(i)+".");
		*/
		long sum=0l;
		for (long i=1;i<=89;++i)	{
			long h=simulate(i);
			System.out.println("H("+i+")="+h+"; "+i+"="+Arrays.toString(getZeckendorfRepresentation(i))+".");
			sum+=h;
		}
		System.out.println(sum);
	}
}
