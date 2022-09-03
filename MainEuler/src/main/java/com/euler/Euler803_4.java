package com.euler;

import java.math.BigInteger;

import com.google.common.math.LongMath;

public class Euler803_4 {
	private final static BigInteger F=BigInteger.valueOf(25214903917l);
	private final static BigInteger K=BigInteger.valueOf(11);
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(2l,48));
	
	// private final static BigInteger A0=BigInteger.valueOf(123456);
	private final static BigInteger A0=BigInteger.valueOf(78580612777175l);
	
	// Not working :(. Review.
	private static class Automata	{
		private static interface State	{
			public int nextState(char c);
		}
		private static enum S0 implements State	{
			INSTANCE;
			@Override
			public int nextState(char c)	{
				switch (c)	{
					case 'P':return 1;
					default: return 0;
				}
			}
		}
		private static enum Sp implements State	{
			INSTANCE;
			@Override
			public int nextState(char c)	{
				switch (c)	{
					case 'P':return 1;
					case 'u':return 2;
					default: return 0;
				}
			}
		}
		private static enum Su implements State	{
			INSTANCE;
			@Override
			public int nextState(char c)	{
				switch (c)	{
					case 'P':return 1;
					case 'z':return 3;
					default: return 0;
				}
			}
		}
		private static enum Sz1 implements State	{
			INSTANCE;
			@Override
			public int nextState(char c)	{
				switch (c)	{
					case 'P':return 1;
					case 'z':return 4;
					default: return 0;
				}
			}
		}
		private static enum Sz2 implements State	{
			INSTANCE;
			@Override
			public int nextState(char c)	{
				switch (c)	{
					case 'P':return 1;
					case 'l':return 5;
					default: return 0;
				}
			}
		}
		private static enum Sl implements State	{
			INSTANCE;
			@Override
			public int nextState(char c)	{
				switch (c)	{
					case 'P':return 1;
					case 'e':return -1;
					default: return 0;
				}
			}
		}
		private final static State[] STATES=new State[] {S0.INSTANCE,Sp.INSTANCE,Su.INSTANCE,Sz1.INSTANCE,Sz2.INSTANCE,Sl.INSTANCE};
		private State state;
		public Automata()	{
			state=S0.INSTANCE;
		}
		public boolean run(char c)	{
			int index=state.nextState(c);
			if (index<0)	{
				state=STATES[0];
				return true;
			}
			if (index>4) throw new RuntimeException("Vaaale.");
			state=STATES[index];
			return false;
		}
	}
	
	public static void main(String[] args)	{
		// char[] toCheck="RxqLBfWzv".toCharArray();
		Automata automata=new Automata();
		BigInteger a=A0;
		for (long i=0;;++i)	{
			long b=(a.longValueExact()>>16)%52;
			char c=(char)((b<=25)?(b+'a'):(b-26+'A'));
			if (automata.run(c))	{
				System.out.println("Iterations: "+i+".");
				System.out.println("Last an: "+a.toString()+". Last an BINARY: "+a.toString(2)+".");
			}
			a=a.multiply(F).add(K).mod(MOD);
		}
		/*-
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds");
		*/
	}
}
