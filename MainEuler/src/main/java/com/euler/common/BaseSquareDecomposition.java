package com.euler.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.LongPair;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

// See also: EulerUtils.hermiteAlgorithm.
public class BaseSquareDecomposition	{
	private final static Comparator<LongPair> SORT_BY_Y=(LongPair a,LongPair b)->	{
		int diffY=Long.compare(a.y,b.y);
		if (diffY!=0) return diffY;
		else return Long.compare(a.x,b.x);
	};
	public final static BaseSquareDecomposition EMPTY=new BaseSquareDecomposition(Set.of());
	private final Set<LongPair> decomps;
	public BaseSquareDecomposition(long a,long b)	{
		decomps=Set.of(LongPair.sorted(a,b));
	}
	public BaseSquareDecomposition(LongPair decomp)	{
		decomps=Set.of(decomp);
	}
	public BaseSquareDecomposition(Set<LongPair> decomps)	{
		this.decomps=decomps;
	}
	private static LongPair getFrom(long a,long b,long c,long d)	{
		long n1=a*c+b*d;
		long n2=Math.abs(a*d-b*c);
		return LongPair.sorted(n1,n2);
	}
	public BaseSquareDecomposition combineWith(BaseSquareDecomposition other)	{
		Set<LongPair> result=new HashSet<>();
		for (LongPair p:decomps) for (LongPair q:other.decomps)	{
			result.add(getFrom(p.x,p.y,q.x,q.y));
			result.add(getFrom(p.y,p.x,q.x,q.y));
		}
		return new BaseSquareDecomposition(result);
	}
	public Set<LongPair> getBaseCombinations()	{
		return decomps;
	}
	public List<LongPair> getAllCombinations()	{
		List<LongPair> result=new ArrayList<>();
		for (LongPair p:decomps)	{
			boolean hasZero=(p.x==0l);
			boolean isSamePair=(p.x==p.y);
			result.add(new LongPair(p.x,p.y));
			result.add(new LongPair(p.x,-p.y));
			if (!hasZero)	{
				result.add(new LongPair(-p.x,p.y));
				result.add(new LongPair(-p.x,-p.y));
			}
			if (!isSamePair)	{
				result.add(new LongPair(p.y,p.x));
				result.add(new LongPair(-p.y,p.x));
				if (!hasZero)	{
					result.add(new LongPair(p.y,-p.x));
					result.add(new LongPair(-p.y,-p.x));
				}
			}
		}
		result.sort(SORT_BY_Y);
		return result;
	}
	public BaseSquareDecomposition scale(long factor)	{
		Set<LongPair> result=new HashSet<>();
		for (LongPair p:decomps) result.add(new LongPair(p.x*factor,p.y*factor));
		return new BaseSquareDecomposition(result);
	}
	// If this object contains decompositions for some N, applying this gets the decompositions for 2N.
	public BaseSquareDecomposition scramble()	{
		Set<LongPair> result=new HashSet<>();
		for (LongPair p:decomps) result.add(new LongPair(p.y-p.x,p.y+p.x));
		return new BaseSquareDecomposition(result);
	}
	public static class PrimePowerDecompositionFinder	{
		private static class PowerCache	{
			private final long base;
			private final IntLongMap powers;
			public PowerCache(long base)	{
				this.base=base;
				powers=HashIntLongMaps.newMutableMap();
				powers.put(0,1l);
				powers.put(1,base);
			}
			public long get(int exp)	{
				return powers.computeIfAbsent(exp,(int e)->powers.get(e-1)*base);
			}
		}
		private final static LongPair ZERO_AND_ONE=new LongPair(0l,1l);
		private final long prime;
		private final CombinatorialNumberCache combis;
		private final LongPair basePair;
		private final BaseSquareDecomposition baseDecomposition;
		private final IntObjMap<LongPair> primitiveDecompositions;
		private final PowerCache powersA;
		private final PowerCache powersB;
		public PrimePowerDecompositionFinder(long prime,CombinatorialNumberCache combis)	{
			this.prime=prime;
			this.combis=combis;
			basePair=EulerUtils.hermiteAlgorithm(prime);
			baseDecomposition=new BaseSquareDecomposition(basePair);
			primitiveDecompositions=HashIntObjMaps.newMutableMap();
			primitiveDecompositions.put(0,ZERO_AND_ONE);
			primitiveDecompositions.put(1,basePair);
			powersA=new PowerCache(basePair.x);
			powersB=new PowerCache(basePair.y);
		}
		private LongPair getPrimitiveDecomposition(int pow)	{
			long a=0;
			long b=0;
			for (int i=0;i<=pow;++i)	{
				long baseTerm=powersA.get(i)*powersB.get(pow-i);
				baseTerm*=combis.get(pow,i);
				if ((i&2)!=0) baseTerm=-baseTerm;	// Not a typo, we really want to check the second bit.
				if ((i&1)!=0) a+=baseTerm;
				else b+=baseTerm;
			}
			return LongPair.sorted(Math.abs(a),Math.abs(b));
		}
		public BaseSquareDecomposition getFor(int pow)	{
			// Assumes that either pow==1 or all the powers below or equal to (pow-1) have already been calculated.
			if (pow==1) return baseDecomposition;
			Set<LongPair> result=new HashSet<>();
			result.add(primitiveDecompositions.computeIfAbsent(pow,this::getPrimitiveDecomposition));
			long scale=prime;
			for (int x=pow-2;x>=0;x-=2)	{
				LongPair base=primitiveDecompositions.get(x);
				result.add(new LongPair(base.x*scale,base.y*scale));
				scale*=prime;
			}
			return new BaseSquareDecomposition(result);
		}
	}
}