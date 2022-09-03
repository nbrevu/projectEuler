package com.euler.experiments;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

/*
 * ACHTUNG!!!!! This is a very simple model and it doesn't reflect the full possibilities of a set of quantum variables.
 */
public class Entanglement {
	private static enum Sign	{
		PLUS,MINUS;
		private static Sign product(Sign a,Sign b)	{
			switch (a)	{
				case PLUS:return b;
				case MINUS:return (a==PLUS)?MINUS:PLUS;
				default:throw new IllegalStateException();
			}
		}
	}
	private static class SingleVarState	{
		public final int var;
		public final boolean isOn;
		public SingleVarState(int var,boolean isOn)	{
			this.var=var;
			this.isOn=isOn;
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof SingleVarState)	{
				SingleVarState sOther=(SingleVarState)other;
				return (var==sOther.var)&&(isOn==sOther.isOn);
			}	else return false;
		}
		@Override
		public int hashCode()	{
			return Objects.hash(var,isOn);
		}
	}
	private static class PureState	{
		public final Set<SingleVarState> states;
		private final String stringCache;
		public PureState(Set<SingleVarState> states)	{
			this.states=states;
			stringCache=getRepresentation();
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof PureState)	{
				PureState pOther=(PureState)other;
				return states.equals(pOther.states);
			}	else return false;
		}
		@Override
		public int hashCode()	{
			return states.hashCode();
		}
		public final static PureState product(PureState a,PureState b)	{
			Set<SingleVarState> result=new HashSet<>();
			result.addAll(a.states);
			result.addAll(b.states);
			return new PureState(ImmutableSet.copyOf(result));
		}
		public Optional<PureState> withMeasure(int var,boolean isOn)	{
			Set<SingleVarState> result=new HashSet<>();
			for (SingleVarState state:states) if (state.var==var)	{
				if (state.isOn!=isOn) return Optional.empty();
			}	else result.add(state);
			return Optional.of(new PureState(ImmutableSet.copyOf(result)));
		}
		@Override
		public String toString()	{
			return stringCache;
		}
		private String getRepresentation()	{
			SortedMap<Integer,Boolean> tmpMap=new TreeMap<>();
			for (SingleVarState state:states) tmpMap.put(state.var,state.isOn);
			StringBuilder inner=new StringBuilder();
			StringBuilder ids=new StringBuilder();
			for (Map.Entry<Integer,Boolean> entry:tmpMap.entrySet())	{
				inner.append(entry.getValue().booleanValue()?'1':'0');
				ids.append(entry.getKey());
			}
			return String.format("|%s>_(%s)",inner.toString(),ids.toString());
		}
	}
	private static class FullState	{
		public final Set<Pair<Sign,PureState>> addends;
		private final String stringCache;
		public FullState(Set<Pair<Sign,PureState>> addends)	{
			this.addends=addends;
			stringCache=getRepresentation();
		}
		@Override
		public boolean equals(Object other)	{
			if (other instanceof FullState)	{
				FullState sOther=(FullState)other;
				return addends.equals(sOther.addends);
			}	else return false;
		}
		@Override
		public int hashCode()	{
			return addends.hashCode();
		}
		public static FullState product(FullState a,FullState b)	{
			Set<Pair<Sign,PureState>> result=new HashSet<>();
			for (Pair<Sign,PureState> f1:a.addends) for (Pair<Sign,PureState> f2:b.addends)	{
				Sign sign=Sign.product(f1.getLeft(),f2.getLeft());
				PureState state=PureState.product(f1.getRight(), f2.getRight());
				result.add(Pair.of(sign,state));
			}
			return new FullState(ImmutableSet.copyOf(result));
		}
		public FullState withMeasure(int var,boolean isOn)	{
			Set<Pair<Sign,PureState>> result=new HashSet<>();
			for (Pair<Sign,PureState> state:addends)	{
				Optional<PureState> afterMeasure=state.getRight().withMeasure(var,isOn);
				if (afterMeasure.isPresent()) result.add(Pair.of(state.getLeft(),afterMeasure.get()));
			}
			return new FullState(ImmutableSet.copyOf(result));
		}
		@Override
		public String toString()	{
			return stringCache;
		}
		private String getRepresentation()	{
			StringBuilder result=new StringBuilder();
			for (Pair<Sign,PureState> state:addends)	{
				result.append((state.getLeft()==Sign.PLUS)?'+':'-');
				result.append(state.getRight().toString());
			}
			return result.toString();
		}
	}
	private enum BellState	{
		PSI_PLUS	{
			@Override
			public FullState getFor(int a,int b)	{
				Pair<Sign,PureState> plus00=Pair.of(Sign.PLUS,get00(a,b));
				Pair<Sign,PureState> plus11=Pair.of(Sign.PLUS,get11(a,b));
				return new FullState(ImmutableSet.of(plus00,plus11));
			}
		},
		PSI_MINUS	{
			@Override
			public FullState getFor(int a,int b)	{
				Pair<Sign,PureState> plus00=Pair.of(Sign.PLUS,get00(a,b));
				Pair<Sign,PureState> minus11=Pair.of(Sign.MINUS,get11(a,b));
				return new FullState(ImmutableSet.of(plus00,minus11));
			}
		},
		PHI_PLUS	{
			@Override
			public FullState getFor(int a,int b)	{
				Pair<Sign,PureState> plus01=Pair.of(Sign.PLUS,get01(a,b));
				Pair<Sign,PureState> plus10=Pair.of(Sign.PLUS,get10(a,b));
				return new FullState(ImmutableSet.of(plus01,plus10));
			}
		},
		PHI_MINUS	{
			@Override
			public FullState getFor(int a,int b)	{
				Pair<Sign,PureState> plus01=Pair.of(Sign.PLUS,get01(a,b));
				Pair<Sign,PureState> minus10=Pair.of(Sign.MINUS,get10(a,b));
				return new FullState(ImmutableSet.of(plus01,minus10));
			}
		};
		public abstract FullState getFor(int a,int b);
		private static PureState getState(int a,boolean isAOn,int b,boolean isBOn)	{
			return new PureState(ImmutableSet.of(new SingleVarState(a,isAOn),new SingleVarState(b,isBOn)));
		}
		private static PureState get00(int a,int b)	{
			return getState(a,false,b,false);
		}
		private static PureState get01(int a,int b)	{
			return getState(a,false,b,true);
		}
		private static PureState get10(int a,int b)	{
			return getState(a,true,b,false);
		}
		private static PureState get11(int a,int b)	{
			return getState(a,true,b,true);
		}
	}
	private static BellState tryToIdentify(FullState s,int a,int b)	{
		for (BellState state:BellState.values()) if (state.getFor(a,b).equals(s)) return state;
		return null;
	}
	
	public static void main(String[] args)	{
		for (BellState c12:BellState.values())	{
			FullState state12=c12.getFor(1,2);
			for (BellState c34:BellState.values())	{
				FullState state34=c34.getFor(3,4);
				FullState state1234=FullState.product(state12,state34);
				for (BellState c23:BellState.values())	{
					FullState state23=c23.getFor(2,3);
					// ZUTUN! Pues tengo dudas de cómo se haría :(.
				}
				/*
				// NICHT MÖGLICH!!!!! Tengo que "desentrelazar" los estados de Bell de 2 y 3, no los booleanos por separado. ZUTUN! TODO! TEHDÄ!
				for (boolean m2:boolStates)	{
					FullState state134=state1234.withMeasure(2,m2);
					for (boolean m3:boolStates)	{
						FullState state14=state134.withMeasure(3,m3);
						BellState c14=tryToIdentify(state14,1,4);
						String message=String.format("%s(1,2)*%s(3,4) => (2=%s, 3=%s) => %s(1,4)",c12.name(),c34.name(),m2?"+":"-",m3?"+":"-",(c14==null)?"<Unknown>":c14.name());
						System.out.println(message);
					}
				}
				*/
			}
		}
	}
}
