package com.euler;

public class Euler277 {
	// This was conceived under the influence of https://www.youtube.com/watch?v=wXDeAfTdfgE.
	private static class Modulus	{
		private static class CollatzStep	{
			public final long result;
			public final char op;
			private CollatzStep(long result,char op)	{
				this.result=result;
				this.op=op;
			}
			public static CollatzStep apply(long num)	{
				// Conversion to int is necessary because Java is VERY stupid sometimes.
				// See also: Pair.
				// See also: string.reverse().
				// See also: string.contains(char)
				// Sequence of "see alsos" interrupted in order to not crash my hard disk.
				switch ((int)(num%3l))	{
				case 0:return new CollatzStep(num/3,'D');
				case 1:return new CollatzStep((4*num+2)/3,'U');
				case 2:return new CollatzStep((2*num-1)/3,'d');
				}
				throw new UnsupportedOperationException("Throwing here because, again, Java is very stupid.");
			}
			public static CollatzStep apply(long num,int level)	{
				for (int i=0;i<level;++i)	{
					CollatzStep result=apply(num);
					num=result.result;
				}
				return apply(num);
			}
		}
		public final long remainder;
		public final long mod;
		private Modulus(long remainder,long mod)	{
			this.remainder=remainder;
			this.mod=mod;
		}
		public static Modulus getModulusFor(char c)	{
			switch (c)	{
			case 'D':return new Modulus(0l,3l);
			case 'U':return new Modulus(1l,3l);
			case 'd':return new Modulus(2l,3l);
			}
			throw new UnsupportedOperationException("El no a you smoko");
		}
		public Modulus next(char op,int level)	{
			if (!"DUd".contains(""+op)) throw new UnsupportedOperationException("Ich kann \""+op+"\" nicht verstehen!!!!!");
			long newMod=3*mod;
			for (long candidate=remainder;candidate<=newMod;candidate+=mod)	{
				CollatzStep result=CollatzStep.apply(candidate,level);
				if (result.op==op) return new Modulus(candidate,newMod);
			}
			throw new UnsupportedOperationException("No contaba con la astusia de Collatz.");
		}
		public long firstAfter(long milestone)	{
			long q=milestone/mod;
			long res=q*mod+remainder;
			while (res<=milestone) res+=mod;
			return res;
		}
	}
	public static Modulus getModulus(String sequence)	{
		Modulus base=Modulus.getModulusFor(sequence.charAt(0));
		for (int i=1;i<sequence.length();++i) base=base.next(sequence.charAt(i),i);
		return base;
	}
	
	private final static String SEQUENCE="UDDDUdddDDUDDddDdDddDDUDDdUUDd";
	private final static long MILESTONE=1000000000000000l;
	
	public static void main(String[] args)	{
		Modulus result=getModulus(SEQUENCE);
		System.out.println(result.firstAfter(MILESTONE));
	}
}
