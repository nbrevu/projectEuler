package com.euler.common.alpertron;

import java.math.BigInteger;

public class DiophantineSolutionRecursion	{
	public final BigInteger p;
	public final BigInteger q;
	public final BigInteger r;
	public final BigInteger s;
	public final BigInteger k;
	public final BigInteger l;
	public DiophantineSolutionRecursion(BigInteger p,BigInteger q,BigInteger r,BigInteger s,BigInteger k,BigInteger l)	{
		this.p=p;
		this.q=q;
		this.r=r;
		this.s=s;
		this.k=k;
		this.l=l;
	}
	public FixedSolution apply(FixedSolution base)	{
		BigInteger x=p.multiply(base.x).add(q.multiply(base.y)).add(k);
		BigInteger y=r.multiply(base.x).add(s.multiply(base.y)).add(l);
		return new FixedSolution(x,y);
	}
}