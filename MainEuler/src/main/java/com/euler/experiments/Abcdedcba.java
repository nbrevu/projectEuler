package com.euler.experiments;

public class Abcdedcba {
	public static void main(String[] args)	{
		/*
		 * Oooooh. There are too many degrees of freedom. This doesn't work. It does work for 11111, though. But not for 41 or 271.
		 */
		int verified=0;
		int total=0;
		for (int a=1;a<10;++a)	{
			int a0000000a=a*100000001;
			for (int b=0;b<10;++b)	{
				int ab00000ba=a0000000a+10000010*b;
				for (int c=0;c<10;++c)	{
					int abc000cba=ab00000ba+1000100*c;
					for (int d=0;d<10;++d)	{
						int abcd0dcba=abc000cba+101000*d;
						for (int e=0;e<10;++e)	{
							int abcdedcba=abcd0dcba+10000*e;
							boolean cond1a=((b+c)==e);
							boolean cond1b=((a+d)==e);
							boolean cond1=(cond1a&&cond1b);
							boolean cond2=((abcdedcba%11111)==0);
							++total;
							if (cond1&&cond2) ++verified;
							else if (cond1) System.out.println("ACHTUNG! Contraejemplo, A & !B, "+abcdedcba+".");
							else if (cond2) System.out.println("ACHTUNG! Contraejemplo, B & !A, "+abcdedcba+".");
						}
					}
				}
			}
		}
		System.out.println("Encontrados "+total+" casos, de los cuales "+verified+" verifican las condiciones.");
	}
}
