package com.euler;

public class Euler94 {
	/*
try2(){
    int m[MAXI] = { -1, 1 };
    int a = 1, b;
    int sum = 0;
    int i;
    printf(" i          m         a         b       sum\n");
    for( i=2 ; i<MAXI ; i++ ){
        m[i] = (i%2==0)?(m[i-1]+m[i-2]):(2*m[i-1]+m[i-2]);
        a += 4*m[i]*m[i-1];
        b = (i%2==0) ? (a+1) : (a-1);
        if( a+a+b > MAX ) break;
        sum += a+a+b;
        printf("%2d: %9d %9d %9d %9d\n", i, m[i], a, b, sum);
    }
}	 */
	private final static long LIMIT=1000000000;
	
	public static void main(String[] args)	{
		long mPrev2=0;
		long mPrev=1;
		long a=1;
		long sum=0;
		for (boolean evenCase=true;;evenCase=!evenCase)	{
			long m=mPrev2+((evenCase?1:2)*mPrev);
			a+=4*m*mPrev;
			long b=a+(evenCase?1:-1);
			long perimeter=a+a+b;
			if (perimeter>LIMIT) break;
			sum+=perimeter;
			mPrev2=mPrev;
			mPrev=m;
		}
		System.out.println(sum);
	}
}
