package com.euler;

public class Euler803_5 {
	private final static long F=25214903917l;
	private final static long K=11l;
	
	// private final static long A0=123456l;
	private final static long A0=78580612777175l;
	
	private static class HackishBuffer	{
		private final static int LEN=16;
		private final char[] buffer;
		private int position;
		public long actualPosition;
		public HackishBuffer()	{
			buffer=new char[LEN];
			position=0;
		}
		public void put(char c)	{
			++actualPosition;
			buffer[position]=c;
			position=(position+1)&15;
		}
		public boolean checkAgainst(char[] vals)	{
			int curPos=(position-vals.length)&15;
			for (char c:vals)	{
				if (buffer[curPos]!=c) return false;
				curPos=(curPos+1)&15;
			}
			return true;
		}
	}
	
	private static class Calculator48	{
		private final static long MASK=(1l<<32)-1;
		private final static long SMALL_MASK=(1l<<16)-1;
		private final long lowF;
		private final long highF;
		private final long k;
		public Calculator48(long f,long k)	{
			lowF=f&MASK;
			highF=f>>>32;
			this.k=k;
		}
		public long next(long n)	{
			long lowN=n&MASK;
			long highN=n>>>32;
			long base=(lowN*lowF)+k;
			long carry=base>>>32;
			long lowResult=base&MASK;
			long highResult=lowN*highF+lowF*highN+carry;
			highResult&=SMALL_MASK;
			return (highResult<<32)+lowResult;
		}
	}
	
	// "P" -> 152 (!!)
	// "Pu" -> 153 :D
	// "Puz" -> 70004 :(
	// "Puzz" -> 2206134
	// "Puzzl" -> 192097862, 18 seconds.
	// "Puzzle" -> 3169968677, 339 seconds.
	// ¿Echarle un ojete a https://www.violentlymild.com/posts/reverse-engineering-linear-congruential-generators/?
	// See also https://www.pnas.org/doi/10.1073/pnas.61.1.25.
	
	/*-
	Iterations: 13169968676
	Last an: 258337257566816.
	Elapsed 322.6295453 seconds 
	 */
	public static void main(String[] args)	{
		// char[] toCheck="RxqLBfWzv".toCharArray();
		char[] toCheck="Puzzle".toCharArray();
		char lastChar=toCheck[toCheck.length-1];
		HackishBuffer buffer=new HackishBuffer();
		long a=A0;
		Calculator48 calc=new Calculator48(F,K);
		for (;;)	{
			long b=(a>>16)%52;
			char c=(char)((b<=25)?(b+'a'):(b-26+'A'));
			buffer.put(c);
			if ((lastChar==c)&&(buffer.checkAgainst(toCheck)))	{
				System.out.println("Iterations: "+(buffer.actualPosition-6));
				System.out.println("Last an: "+a+". Last an BINARY: "+Long.toString(a,2)+".");
				// break;
			}
			a=calc.next(a);
		}
		/*-
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds");
		*/
	}
}
