package com.euler;

import java.math.BigInteger;
import java.util.Locale;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.BigRational;

public class Euler469_3 {
	private final static int N=10000;
	
	/*
	 * I see. This is wrong because we don't just need to count cases. They aren't equiprobable, because at each time the probability of choosing
	 * a given seat is not always the same :O. This explains why I'm getting a decreasing function where E(10000) ~= 0.50345, but the answer is
	 * actually something like 0.56.
	 */
	/*
	E(4)=1/2=0.5000000000.
	E(5)=3/5=0.6000000000.
	E(6)=5/9=0.5555555556.
	E(7)=4/7=0.5714285714.
	E(8)=9/16=0.5625000000.
	E(9)=22/39=0.5641025641.
	E(10)=14/25=0.5600000000.
	E(11)=37/66=0.5606060606.
	E(12)=34/61=0.5573770492.
	E(13)=29/52=0.5576923077.
	E(14)=171/308=0.5551948052.
	E(15)=518/933=0.5551982851.
	E(16)=135/244=0.5532786885.
	E(17)=865/1564=0.5530690537.
	E(18)=2851/5169=0.5515573612.
	E(19)=1372/2489=0.5512253917.
	E(20)=11/20=0.5500000000.
	E(21)=16714/30411=0.5496037618.
	E(22)=7537/13739=0.5485843220.
	E(23)=27699/50531=0.5481585561.
	E(24)=103952/189939=0.5472914988.
	E(25)=22052/40325=0.5468567886.
	E(26)=171677/314366=0.5461054949.
	E(27)=679202/1244703=0.5456739479.
	E(28)=30391/55762=0.5450127327.
	E(29)=1118455/2053751=0.5445913356.
	E(30)=4649405/8546673=0.5440017420.
	...
	E(9988)=0.5034520615.
	E(9989)=0.5034518929.
	E(9990)=0.5034517243.
	E(9991)=0.5034515558.
	E(9992)=0.5034513872.
	E(9993)=0.5034512187.
	E(9994)=0.5034510502.
	E(9995)=0.5034508818.
	E(9996)=0.5034507133.
	E(9997)=0.5034505449.
	E(9998)=0.5034503765.
	E(9999)=0.5034502082.
	E(10000)=0.5034500398.
	 */
	public static void main(String[] args)	{
		BigCombinatorialNumberCache combis=new BigCombinatorialNumberCache(N/2);
		BigInteger[] factorials=new BigInteger[N];
		factorials[0]=BigInteger.ONE;
		for (int i=1;i<factorials.length;++i) factorials[i]=factorials[i-1].multiply(BigInteger.valueOf(i));
		for (int i=4;i<=N;++i)	{
			BigInteger num=BigInteger.ZERO;
			BigInteger denom=BigInteger.ZERO;
			int n2,n3;
			if ((i%2)==0)	{
				n2=i/2;
				n3=0;
			}	else	{
				n2=(i-3)/2;
				n3=1;
			}
			while (n2>=0)	{
				int points=n2+n3;
				int free=i-points;
				BigInteger cases1=combis.get(points,n2);
				BigInteger cases2=factorials[points-1];
				BigInteger cases=cases1.multiply(cases2);
				num=num.add(cases.multiply(BigInteger.valueOf(free)));
				denom=denom.add(cases);
				n2-=3;
				n3+=2;
			}
			denom=denom.multiply(BigInteger.valueOf(i));
			BigRational r=new BigRational(num,denom);
			// System.out.println(String.format(Locale.UK,"E(%d)=%s=%.10f.",i,r.toString(),r.toDouble(25)));
			System.out.println(String.format(Locale.UK,"E(%d)=%.10f.",i,r.toDouble(25)));
		}
	}
}
