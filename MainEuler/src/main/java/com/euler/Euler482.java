package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler482 {
	private final static long N=1000;
	
	public static void main(String[] args)	{
		// Oh, some of the conditions must be wrong :(.
		long result=0;
		for (long a=1;a<=N;++a)	{
			long aa=a*a;
			for (long b=a;b<=N;++b)	{
				long bb=b*b;
				for (long c=b;c<=N;++c)	{
					long cc=c*c;
					for (long d=a;d<=N;++d)	{
						if (d>a+b) break;
						long dd=d*d;
						// First we solve the equation on f.
						long fA=d;
						long fB=bb-aa-dd;
						long fC=d*(aa-cc);
						long fDelta2=fB*fB-4*fA*fC;	// fA>0 y fC<0, por tanto fDelta2>0 y fDelta>fB.
						long fDelta=LongMath.sqrt(fDelta2,RoundingMode.DOWN);
						if (fDelta*fDelta!=fDelta2) continue;
						long fNum=fDelta-fB;
						long den=2*d;
						if ((fNum%den)!=0) continue;
						long f=fNum/den;
						if (f>a+c) continue;
						long ff=f*f;
						// Now we try to solve the equation on e.
						long eA=d;
						long eB=aa-bb-dd;
						long eC=d*(bb-cc);
						long eDelta2=eB*eB-4*eA*eC;
						long eDelta=LongMath.sqrt(eDelta2,RoundingMode.DOWN);
						if (eDelta*eDelta!=eDelta2) continue;
						long eNum=eDelta-eB;
						if ((eNum%den)!=0) continue;
						long e=eNum/den;
						if (e>b+c) continue;
						if (d+e+f>N) continue;
						long ee=e*e;
						if (f*(cc+ee-bb)==e*(ff+cc-aa))	{
							long numA=aa+dd-bb;
							long denA=2*a*d;
							{
								long gcd=EulerUtils.gcd(numA,denA);
								numA/=gcd;
								denA/=gcd;
							}
							long numB=bb+dd-aa;
							long denB=2*b*d;
							{
								long gcd=EulerUtils.gcd(numB,denB);
								numB/=gcd;
								denB/=gcd;
							}
							long numG=cc+ee-bb;
							long denG=2*c*e;
							{
								long gcd=EulerUtils.gcd(numG,denG);
								numG/=gcd;
								denG/=gcd;
							}
							long deltaA=denA*denA-numA*numA;
							long deltaB=denB*denB-numB*numB;
							long deltaG=denG*denG-numG*numG;
							long deltaAB=deltaA*deltaB;
							long deltaAG=deltaA*deltaG;
							long deltaBG=deltaB*deltaG;
							long sqAB=LongMath.sqrt(deltaAB,RoundingMode.DOWN);
							if (sqAB*sqAB!=deltaAB) continue;
							long sqAG=LongMath.sqrt(deltaAG,RoundingMode.DOWN);
							if (sqAG*sqAG!=deltaAG) continue;
							long sqBG=LongMath.sqrt(deltaBG,RoundingMode.DOWN);
							if (sqBG*sqBG!=deltaBG) continue;
							if (numA*numB*numG==sqAB*numG+sqAG*numB+sqBG*numG)	{
								System.out.println(String.format("Triángulo encontrado: a=%d, b=%d, c=%d, d=%d, e=%d, f=%d.",a,b,c,d,e,f));
								result+=a+b+c+d+e+f;
							}
						}
					}
				}
			}
		}
		System.out.println(result);
	}
}
