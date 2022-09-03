package com.euler;

public class Euler770_2 {
	/*
	 * OK, I CAN bet 0. No need to use "PseudoDouble", I can use normal doubles. Or maybe BigDecimal to avoid infinites when I get to 308.
	 */
	
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
	private static double[][] calculate(int maxN)	{
		double[][] data=new double[1+maxN][1+maxN];
		for (int j=0;j<=maxN;++j) data[0][j]=1d;
		for (int i=1;i<=maxN;++i)	{
			data[i][0]=2*data[i-1][0];
			for (int j=1;j<=maxN;++j)	{
				double g=data[i-1][j];
				double t=data[i][j-1];
				data[i][j]=2*g*t/(g+t);
			}
		}
		return data;
	}
	
	public static void main(String[] args)	{
		double[][] results=calculate(100);
		for (int i=0;i<=100;++i)	{
			System.out.println("Best result for N="+i+": "+results[i][i]+".");
		}
	}
}
