package com.euler;

public class Euler711 {
	private final static int N=4;
	
	private static int stateToIndex(int remaining,int parity)	{
		return 2*remaining+parity;
	}
	
	private static int indexToRemaining(int stateIndex)	{
		return stateIndex>>1;
	}
	
	private static int indexToParity(int stateIndex)	{
		return stateIndex&1;
	}
	
	public static void main(String[] args)	{
		// Pues está mal.
		int maxNumber=1<<N;
		int maxState=2*(1+maxNumber);
		boolean[] results=new boolean[maxState];
		int[] parityCache=new int[1+N];
		for (int i=0;i<=N;++i) parityCache[i]=Integer.bitCount(i)&1;
		// El turno actual es de Oscar. results[i] es TRUE si gana Oscar, FALSE si gana Eric.
		results[0]=false;
		results[1]=true;
		for (int i=1;i<=N;++i)	{
			// First, the even case.
			boolean evenWins=false;
			for (int j=1;j<=i;++j)	{
				int remaining=i-j;
				int parity=parityCache[j];
				int newIndex=stateToIndex(remaining,parity);
				if (!results[newIndex])	{
					evenWins=true;
					break;
				}
			}
			results[stateToIndex(i,0)]=evenWins;
			boolean oddWins=false;
			for (int j=1;j<=i;++j)	{
				int remaining=i-j;
				int parity=1-parityCache[j];
				int newIndex=stateToIndex(remaining,parity);
				if (!results[newIndex])	{
					oddWins=true;
					break;
				}
			}
			results[stateToIndex(i,1)]=oddWins;
		}
		int result=0;
		for (int i=1;i<=maxNumber;++i) if (!results[i])	{
			System.out.println(i+"...");
			result+=i;
		}
		System.out.println(result);
	}
}
