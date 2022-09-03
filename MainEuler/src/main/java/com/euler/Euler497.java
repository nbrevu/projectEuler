package com.euler;

public class Euler497 {
	private final static long MOD=1_000_000_000l;
	private final static int N=10000;
	
	private static class MovementCount	{
		public final long ab;
		public final long ac;
		public final long ba;
		public final long bc;
		public final long ca;
		public final long cb;
		public MovementCount(long ab,long ac,long ba,long bc,long ca,long cb)	{
			this.ab=ab;
			this.ac=ac;
			this.ba=ba;
			this.bc=bc;
			this.ca=ca;
			this.cb=cb;
		}
		public static MovementCount getInitialCase()	{
			return new MovementCount(0l,1l,0l,0l,0l,0l);
		}
		public MovementCount getNextLevel(long mod)	{
			/*
			 * The current object contains a sequence of movements to move a pile from A to C using B as the "swap space".
			 * The next level is built like this:
			 * - Move the pile from A to B using C as the swap space. At the end of this, the agent is at B.
			 * 	- This is equivalent to exchanging b and c in the current object.
			 * 	- ab<-ac; ac<-ab; ba<-ca; bc<-cb; ca<-ba; cb<-bc.
			 * - Move from B to A to get the remaining disk.
			 * 	- ++ba.
			 * - Move from A to C to put the disk in place.
			 * 	- ++ac.
			 * - Move from C to B so that your starting position for the next "recursive call" is the position of the pile.
			 * 	- ++cb.
			 * - Move the pile from B to C using A as the swap space. At the end of this, the agent is at C.
			 * 	- This is equivalent to exchanging a and b.
			 * 	- ab<-ba; ac<-bc; ba<-ab; bc<-ac; ca<-cb; cb<-ca.
			 */
			return new MovementCount((ac+ba)%mod,(1+ab+bc)%mod,(1+ca+ab)%mod,(cb+ac)%mod,(ba+cb)%mod,(1+bc+ca)%mod);
		}
	}
	private static long countExpectedMoves(long n,long a,long b,long c,long mod,MovementCount counters)	{
		long aLeft=a-1;
		long aRight=n-a;
		long bLeft=b-1;
		long bRight=n-b;
		long cLeft=c-1;
		long cRight=n-c;
		long al2=(aLeft*aLeft)%mod;
		long ar2=(aRight*aRight)%mod;
		long bl2=(bLeft*bLeft)%mod;
		long br2=(bRight*bRight)%mod;
		long cl2=(cLeft*cLeft)%mod;
		long cr2=(cRight*cRight)%mod;
		long ab=(mod+bl2-al2)%mod;
		long ac=(mod+cl2-al2)%mod;
		long ba=(mod+ar2-br2)%mod;
		long bc=(mod+cl2-bl2)%mod;
		long ca=(mod+ar2-cr2)%mod;
		long cb=(mod+br2-cr2)%mod;
		/*
		 * Note that there is an additional travel from b to a before the "recursive" count from "counters". This is added here because it must
		 * not be part of the recursive operations, so it won't be included in the MovementCount objects.
		 */
		return ((ab*counters.ab)+(ac*counters.ac)+(ba*(counters.ba+1))+(bc*counters.bc)+(ca*counters.ca)+(cb*counters.cb))%mod;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		MovementCount ck=MovementCount.getInitialCase();
		long n=10l;
		long a=3l;
		long b=6l;
		long c=9l;
		for (int i=1;i<=N;++i)	{
			result+=countExpectedMoves(n,a,b,c,MOD,ck);
			ck=ck.getNextLevel(MOD);
			n=(n*10)%MOD;
			a=(a*3)%MOD;
			b=(b*6)%MOD;
			c=(c*9)%MOD;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
