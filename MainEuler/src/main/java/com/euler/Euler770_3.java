package com.euler;

import java.math.BigInteger;

import com.euler.common.BigRational;

public class Euler770_3 {
	/*
	 * data[i][j] shows the best increment (in ratio) when the remaining turns are i "gives" and j "takes".
	 * data[i][j] contains RATIOS, not ABSOLUTE QUANTITIES. So it works by multiplying.
	 * A "take" decreases the value, so I should bet zero when only "takes" remain. Therefore data[0][x]=1 for all x.
	 * A "give" increases the value, so I should bet everything when only "gives" remain. Therefore data[x][0]=2^x for all x.
	 * Now let's suppose that we want to calculate data[i][j] for some nonzero i, j.
	 * The value depends only on data[i-1][j] and data[i][j-1].
	 * 	I can bet any amount between 0 and 1. Let's call this amount "a".
	 * 	If B chooses to GIVE, then my result is (1+a)*data[i-1][j]; let's call data[i-1][j] "G".
	 * 	If B chooses to TAKE, then my result is (1-a)*data[i][j-1]; let's call data[i][j-1] "T".
	 *  The equilibrium happens when both amounts are equal. If I bet more, B will "punish" me by choosing to TAKE. If I bet less, I'm not
	 *   maximising!
	 *  So, I need to find some "a" such that (1+a)*G == (1-a)*T -> G+aG==T-aT -> a(G+T)=(T-G), and naturally a=(T-G)/(T+G).
	 *  Since T and G are always positive, it's clear that this amount is between 0 and 1.
	 *  (1+a) = (T+G+T-G)/(T+G)=2T/(T+G) -> f=2GT/(T+G).
	 *  (1-a) = (T+G-T+G)/(T+G)=2G/(T+G) -> f=2GT/(T+G).
	 *  So: data[i][j]=2GT/(T+G) where G=data[i-1][j] and T=data[i][j-1];
	 * To calculate g(X), we inspect the values for data[i][i]. The first time we find data[i][i]>=X, we can say that g(X)=i.
	 * 
	 * Case study: N=1. We expect data[0][1]=1, data[1][0]=2, so data[1][1]=2*2*1/(2+1)=4/3.
	 *  Let's say we bet "a".
	 *  If B GIVES, then I have 1+a. In the next turn I bet 0, because B is going to take. Final result: 1+a.
	 *  If B TAKES, then I have 1-a. In the next turn I bet 1-a, because B is going to give. Final result: 2-2a.
	 * 	My guarantee is the minimum between(1+a,2-2a). If I bet, say, 0.5, my final result is min(1.5,1)=1.
	 * 	So I need to bet some a such that 1+a=2-2a -> a=1/3, and the value is 4/3 :).
	 */
	private final static BigRational ONE=new BigRational(BigInteger.ONE);
	private final static BigRational TWO=new BigRational(BigInteger.TWO);
	
	private static BigRational[][] calculate(int maxN)	{
		BigRational[][] data=new BigRational[1+maxN][1+maxN];
		for (int j=0;j<=maxN;++j) data[0][j]=ONE;
		for (int i=1;i<=maxN;++i)	{
			data[i][0]=TWO.multiply(data[i-1][0]);
			for (int j=1;j<=maxN;++j)	{
				BigRational g=data[i-1][j];
				BigRational t=data[i][j-1];
				data[i][j]=TWO.multiply(g).multiply(t).divide(g.sum(t));
			}
		}
		return data;
	}
	
	/*
	Best result for N=0: 1/1.
	Best result for N=1: 4/3.
	Best result for N=2: 16/11.
	Best result for N=3: 32/21.
	Best result for N=4: 256/163.
	Best result for N=5: 512/319.
	Best result for N=6: 2048/1255.
	Best result for N=7: 4096/2477.
	Best result for N=8: 65536/39203.
	Best result for N=9: 131072/77691.
	Best result for N=10: 524288/308333.
	Best result for N=11: 1048576/612467.
	Best result for N=12: 8388608/4870343.
	Best result for N=13: 16777216/9688683.
	Best result for N=14: 67108864/38569007.
	Best result for N=15: 134217728/76803709.
	
	Naturally, the numerators are powers of two. What about the denominators?
	In order to make this series more clear, let's make it so that the numerator is always 4^N.
	Then the denominators are:
	0->1
	1->3
	2->11
	3->42
	4->163
	5->638
	6->2510
	WHICH IS IN OEIS! https://oeis.org/A032443 to be clear.
	This seems to be related to the Catalan numbers.
	It seems that d_(n+1)=4*d_n-a_n for some succession a_n.
	a_n=4*d_n-d_(n+1):
		a_0=4*1-3=1
		a_1=4*3-11=1
		a_2=4*11-42=2
		a_3=4*42-163=5
		a_4=4*163-638=14
		a_5=4*638-2510=2552-2510=42.
		Definitely the Catalan numbers :D:D:D:D.
		If these numbers are C(N), we can use a very simple recursion for denominators:
		d_0=1.
		d_n=4*d_(n-1)-C(n-1).
		For numerators we don't even need a recursion, we can just make n_n=4^n for n>=0.
	 */
	public static void main(String[] args)	{
		BigRational[][] results=calculate(100);
		for (int i=0;i<=100;++i)	{
			System.out.println("Best result for N="+i+": "+results[i][i]+".");
		}
	}
}
