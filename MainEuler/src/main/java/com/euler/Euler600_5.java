package com.euler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler600_5 {
	private final static long GOAL=100;
	
	private static class Array	{
		private final static Comparator<long[]> ARRAY_COMPARATOR=new Comparator<>()	{
			@Override
			public int compare(long[] a,long[] b)	{
				for (int i=0;i<6;++i)	{
					int result=Long.compare(a[i],b[i]);
					if (result!=0) return result;
				}
				return 0;
			}
		};
		private final long[] values;
		public Array(long a,long b,long c,long d,long e,long f)	{
			values=new long[] {a,b,c,d,e,f};
		}
		private Array(long[] values)	{
			this.values=values;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(values);
		}
		@Override
		public boolean equals(Object other)	{
			Array aOther=(Array)other;
			return Arrays.equals(values,aOther.values);
		}
		@Override
		public String toString()	{
			return Arrays.toString(values);
		}
		public Array getCanonicalForm()	{
			NavigableSet<long[]> forms=new TreeSet<>(ARRAY_COMPARATOR);
			long[] reversed=new long[] {values[5],values[4],values[3],values[2],values[1],values[0]};
			forms.add(values);
			forms.add(reversed);
			for (int i=1;i<6;++i)	{
				long[] copy=new long[6];
				System.arraycopy(values,i,copy,0,6-i);
				System.arraycopy(values,0,copy,6-i,i);
				forms.add(copy);
				copy=new long[6];
				System.arraycopy(reversed,i,copy,0,6-i);
				System.arraycopy(reversed,0,copy,6-i,i);
				forms.add(copy);
			}
			return new Array(forms.first());
		}
	}
	
	private static enum HexagonDistribution	{
		REGULAR(1)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				return distribution.size()==1;
			}
		},
		ELONGATED(3)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=2) return false;
				Iterator<IntSet> iterator=distribution.iterator();
				IntSet set1=iterator.next();
				if (set1.size()==4) set1=iterator.next();
				if (set1.size()==2)	{
					IntCursor cursor=set1.cursor();
					cursor.moveNext();
					int val1=cursor.elem();
					cursor.moveNext();
					int val2=cursor.elem();
					return Math.abs(val1-val2)==3;
				}
				return false;
			}
		},
		SUPERMAN_REGULAR(2)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=2) return false;
				IntSet set1=distribution.iterator().next();
				if (set1.size()!=3) return false;
				int[] values=set1.toIntArray();
				Arrays.sort(values);
				return (values[0]+2==values[1])&&(values[1]+2==values[2]);
			}
		},
		PRISM(6)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=3) return false;
				for (IntSet subSet:distribution)	{
					if (subSet.size()!=2) return false;
					IntCursor cursor=subSet.cursor();
					cursor.moveNext();
					int val1=cursor.elem();
					cursor.moveNext();
					int val2=cursor.elem();
					if (Math.abs(val1-val2)!=3) return false;
				}
				return true;
			}
		},
		TRAPEZIUM(6)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=3) return false;
				boolean has2=false;
				boolean has3=false;
				for (IntSet subSet:distribution) switch (subSet.size())	{
					case 2:	{
						IntCursor cursor=subSet.cursor();
						cursor.moveNext();
						int val1=cursor.elem();
						cursor.moveNext();
						int val2=cursor.elem();
						int dist=Math.abs(val1-val2);
						if ((dist!=2)&&(dist!=4)) return false;
						has2=true;
						break;
					}	case 3:	{
						int[] array=subSet.toIntArray();
						Arrays.sort(array);
						if ((array[1]!=array[0]+1)||(array[2]!=array[1]+1)) return false;
						has3=true;
					}
				}
				return has2&&has3;
			}
		},
		SUPERMAN_IRREGULAR(6)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=4) return false;
				int size2=0;
				for (IntSet subSet:distribution) if (subSet.size()==2)	{
					IntCursor cursor=subSet.cursor();
					cursor.moveNext();
					int val1=cursor.elem();
					cursor.moveNext();
					int val2=cursor.elem();
					int dist=Math.abs(val1-val2);
					if ((dist!=2)&&(dist!=4)) return false;
					++size2;
				}
				return size2==2;
			}
		},
		FOUR_SIDES(12)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=4) return false;
				int size2=0;
				for (IntSet subSet:distribution) if (subSet.size()==2)	{
					IntCursor cursor=subSet.cursor();
					cursor.moveNext();
					int val1=cursor.elem();
					cursor.moveNext();
					int val2=cursor.elem();
					if (Math.abs(val1-val2)!=1) return false;
					++size2;
				}
				return size2==2;
			}
		},
		FIVE_SIDES(12)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				if (distribution.size()!=5) return false;
				for (IntSet subSet:distribution) if (subSet.size()==2)	{
					IntCursor cursor=subSet.cursor();
					cursor.moveNext();
					int val1=cursor.elem();
					cursor.moveNext();
					int val2=cursor.elem();
					return Math.abs(val1-val2)==1;
				}
				return false;
			}
		},
		IRREGULAR(12)	{
			@Override
			protected boolean isThisCase(Set<IntSet> distribution)	{
				return distribution.size()==6;
			}
		},
		;
		
		private final static Set<HexagonDistribution> ALL_CASES=EnumSet.allOf(HexagonDistribution.class);
		public static HexagonDistribution identify(Array canonicalForm)	{
			LongObjMap<IntSet> valueMap=HashLongObjMaps.newMutableMap();
			for (int i=0;i<6;++i) valueMap.computeIfAbsent(canonicalForm.values[i],(long unused)->HashIntSets.newMutableSet()).add(i);
			Set<IntSet> distribution=new HashSet<>(valueMap.values());
			for (HexagonDistribution distr:ALL_CASES) if (distr.isThisCase(distribution)) return distr;
			System.out.println(canonicalForm);
			throw new IllegalArgumentException("Lo que me habéis endiñao pa papear me roe las tripas.");
		}
		
		public final int multiplier;
		protected abstract boolean isThisCase(Set<IntSet> distribution);
		private HexagonDistribution(int multiplier)	{
			this.multiplier=multiplier;
		}
	}
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long halfGoal=(GOAL-2)/2;
		Multimap<Array,Array> forms=MultimapBuilder.hashKeys().arrayListValues().build();
		for (long a=1;a<halfGoal;++a)	{
			for (long b=1;b<halfGoal;++b)	{
				long maxD=Math.min(halfGoal,a+b);
				for (long d=1;d<maxD;++d)	{
					long e=a+b-d;
					long diff=e-b;
					long missing=GOAL-(a+b+d+e);
					if (diff>=0)	{
						long count=(missing-diff)/2;
						for (long f=1;f<=count;++f)	{
							Array hexagon=new Array(a,b,f+diff,d,e,f);
							forms.put(hexagon.getCanonicalForm(),hexagon);
						}
					}	else	{
						diff=-diff;
						long count=(missing-diff)/2;
						for (long c=1;c<=count;++c)	{
							Array hexagon=new Array(a,b,c,d,e,c+diff);
							forms.put(hexagon.getCanonicalForm(),hexagon);
						}
					}
				}
			}
			System.out.println(a+"...");
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(forms.keySet().size());
		// for (Map.Entry<Array,Collection<Array>> entry:forms.asMap().entrySet()) System.out.println(entry.getKey()+" => "+entry.getValue());
		ObjIntMap<HexagonDistribution> counters=HashObjIntMaps.newMutableMap();
		for (Map.Entry<Array,Collection<Array>> entry:forms.asMap().entrySet())	{
			HexagonDistribution distr=HexagonDistribution.identify(entry.getKey());
			if (entry.getValue().size()!=distr.multiplier) throw new IllegalStateException("OH NEIN! DAS TRAGEDIA DE LA MUERTE!!!!! ODER????? "+distr+" (expected="+distr.multiplier+", found="+entry.getValue().size()+").");
			counters.addValue(distr,1);
		}
		System.out.println(counters);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
