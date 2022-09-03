package com.euler;

import java.util.BitSet;
import java.util.Locale;

import com.euler.common.Rational;

public class Euler469 {
	private static class Simulator	{
		private final int n;
		private long cases;
		private long counter;
		private BitSet bits;
		public Simulator(int n)	{
			this.n=n;
			cases=0;
			counter=0;
			bits=new BitSet(n);
			bits.set(0,n);
		}
		public void simulate()	{
			simulateRecursive(n);
		}
		private void simulateRecursive(int emptyChairs)	{
			if (bits.isEmpty())	{
				++cases;
				counter+=emptyChairs;
			}	else for (int p=bits.nextSetBit(0);p>=0;p=bits.nextSetBit(1+p))	{
				int leftPos=(p==0)?(n-1):(p-1);
				int rightPos=(p==n-1)?0:(p+1);
				boolean left=bits.get(leftPos);
				boolean right=bits.get(rightPos);
				bits.clear(leftPos);
				bits.clear(p);
				bits.clear(rightPos);
				simulateRecursive(emptyChairs-1);
				bits.set(leftPos,left);
				bits.set(p);
				bits.set(rightPos,right);
			}
		}
		public Rational getExpectedValue()	{
			return new Rational(counter,cases*n);
		}
	}
	
	/*
	E(1)=0=0.0000000000 (cases=1, cases/n=1, counter=0, counter/n=0); f=0 (diff=0).
	E(2)=1/2=0.5000000000 (cases=2, cases/n=1, counter=2, counter/n=1); f=1 (diff=1).
	E(3)=2/3=0.6666666667 (cases=3, cases/n=1, counter=6, counter/n=2); f=2 (diff=1).
	E(4)=1/2=0.5000000000 (cases=4, cases/n=1, counter=8, counter/n=2); f=2 (diff=0).
	E(5)=3/5=0.6000000000 (cases=10, cases/n=2, counter=30, counter/n=6); f=3 (diff=1).
	E(6)=5/9=0.5555555556 (cases=18, cases/n=3, counter=60, counter/n=10); f=10/3 (diff=1/3).
	E(7)=4/7=0.5714285714 (cases=42, cases/n=6, counter=168, counter/n=24); f=4 (diff=2/3).
	E(8)=9/16=0.5625000000 (cases=96, cases/n=12, counter=432, counter/n=54); f=9/2 (diff=1/2).
	E(9)=22/39=0.5641025641 (cases=234, cases/n=26, counter=1188, counter/n=132); f=66/13 (diff=15/26).
	E(10)=14/25=0.5600000000 (cases=600, cases/n=60, counter=3360, counter/n=336); f=28/5 (diff=34/65).
	E(11)=37/66=0.5606060606 (cases=1584, cases/n=144, counter=9768, counter/n=888); f=37/6 (diff=17/30).
	E(12)=34/61=0.5573770492 (cases=4392, cases/n=366, counter=29376, counter/n=2448); f=408/61 (diff=191/366).
	E(13)=29/52=0.5576923077 (cases=12480, cases/n=960, counter=90480, counter/n=6960); f=29/4 (diff=137/244).
	E(14)=171/308=0.5551948052 (cases=36960, cases/n=2640, counter=287280, counter/n=20520); f=171/22 (diff=23/44).
	E(15)=518/933=0.5551982851 (cases=111960, cases/n=7464, counter=932400, counter/n=62160); f=2590/311 (diff=3799/6842).
	E(16)=135/244=0.5532786885 (cases=351360, cases/n=21960, counter=3110400, counter/n=194400); f=540/61 (diff=9950/18971).
	E(17)=865/1564=0.5530690537 (cases=1126080, cases/n=66240, counter=10587600, counter/n=622800); f=865/92 (diff=3085/5612).
	E(18)=2851/5169=0.5515573612 (cases=3721680, cases/n=206760, counter=36948960, counter/n=2052720); f=17106/1723 (diff=83357/158516).
	E(19)=1372/2489=0.5512253917 (cases=12544560, cases/n=660240, counter=131382720, counter/n=6914880); f=1372/131 (diff=123070/225713).
	E(20)=11/20=0.5500000000 (cases=43444800, cases/n=2172240, counter=477892800, counter/n=23894640); f=11 (diff=69/131).
	E(21)=16714/30411=0.5496037618 (cases=153271440, cases/n=7298640, counter=1769009760, counter/n=84238560); f=116998/10137 (diff=5491/10137).
	E(22)=7537/13739=0.5485843220 (cases=553956480, cases/n=25179840, counter=6685620480, counter/n=303891840); f=15074/1249 (diff=6674636/12661113).
	E(23)=27699/50531=0.5481585561 (cases=2037409920, cases/n=88583040, counter=25686944640, counter/n=1116823680); f=27699/2197 (diff=1478473/2744053).
	E(24)=103952/189939=0.5472914988 (cases=7658340480, cases/n=319097520, counter=100592271360, counter/n=4191344640); f=831616/63313 (diff=73353565/139098661).
	E(25)=22052/40325=0.5468567886 (cases=29266272000, cases/n=1170650880, counter=400111488000, counter/n=16004459520); f=22052/1613 (diff=54781668/102123869).
	 */
	public static void main(String[] args)	{
		Rational prev=Rational.ZERO;
		for (int i=1;i<=30;++i)	{
			Simulator s=new Simulator(i);
			s.simulate();
			Rational e=s.getExpectedValue();
			Rational f=e.multiply(i);
			Rational diff=f.subtract(prev);
			prev=f;
			System.out.println(String.format(Locale.UK,"E(%d)=%s=%.10f (cases=%d, cases/n=%d, counter=%d, counter/n=%d); f=%s (diff=%s).",i,e.toString(),e.toDouble(),s.cases,s.cases/i,s.counter,s.counter/i,f.toString(),diff.toString()));
		}
	}
}
