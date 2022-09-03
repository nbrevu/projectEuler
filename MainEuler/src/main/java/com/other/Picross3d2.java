package com.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class Picross3d2 {
	private enum BlockStatus	{
		BLUE(true,'B',false)	{
			@Override
			public boolean canBeReplacedBy(BlockStatus colour)	{
				return false;
			}
			@Override
			public BlockStatus combineWith(BlockStatus other) {
				if ((other==BLUE)||(other==POSSIBLE_BLUE)) return other;
				else if (other==GAP) return POSSIBLE_BLUE;
				else return UNKNOWN;
			}
		},ORANGE(true,'O',false)	{
			public boolean canBeReplacedBy(BlockStatus colour)	{
				return false;
			}
			@Override
			public BlockStatus combineWith(BlockStatus other) {
				if ((other==ORANGE)||(other==POSSIBLE_ORANGE)) return other;
				else if (other==GAP) return POSSIBLE_ORANGE;
				else return UNKNOWN;
			}
		},POSSIBLE_BLUE(false,'b',true)	{
			public boolean canBeReplacedBy(BlockStatus colour)	{
				return (colour==BLUE)||(colour==GAP);
			}
			@Override
			public BlockStatus combineWith(BlockStatus other) {
				if ((other==BLUE)||(other==POSSIBLE_BLUE)||(other==GAP)) return this;
				else return UNKNOWN;
			}
		},POSSIBLE_ORANGE(false,'o',true)	{
			public boolean canBeReplacedBy(BlockStatus colour)	{
				return (colour==ORANGE)||(colour==GAP);
			}
			@Override
			public BlockStatus combineWith(BlockStatus other) {
				if ((other==ORANGE)||(other==POSSIBLE_ORANGE)||(other==GAP)) return this;
				else return UNKNOWN;
			}
		},UNKNOWN(false,'U',true)	{
			public boolean canBeReplacedBy(BlockStatus colour)	{
				return true;
			}
			@Override
			public BlockStatus combineWith(BlockStatus other) {
				return UNKNOWN;
			}
		},GAP(false,'G',false)	{
			public boolean canBeReplacedBy(BlockStatus colour)	{
				return false;
			}
			@Override
			public BlockStatus combineWith(BlockStatus other) {
				if (other==GAP) return this;
				else if ((other==BLUE)||(other==POSSIBLE_BLUE)) return POSSIBLE_BLUE;
				else if ((other==ORANGE)||(other==POSSIBLE_ORANGE)) return POSSIBLE_ORANGE;
				else return UNKNOWN;
			}
		};
		public final boolean isAColour;
		public final char identifier;
		public final boolean isUnknown;
		private BlockStatus(boolean isAColour,char identifier,boolean isUnknown)	{
			this.isAColour=isAColour;
			this.identifier=identifier;
			this.isUnknown=isUnknown;
		}
		public abstract boolean canBeReplacedBy(BlockStatus colour);
		public abstract BlockStatus combineWith(BlockStatus other);
		public static BlockStatus fromChar(char id)	{
			for (BlockStatus status:values()) if (status.identifier==id) return status;
			throw new IllegalArgumentException("DAS KENNE ICH NICHT!!!!!");
		}
		public static Collection<BlockStatus> REPLACEABLE_BLOCKS=EnumSet.of(POSSIBLE_BLUE,POSSIBLE_ORANGE,UNKNOWN);
	}
	private enum SeriesType	{
		ZERO	{
			@Override
			public boolean accept(int amountOfSeries)	{
				return amountOfSeries==0;
			}
		},
		CONTINUOUS	{
			@Override
			public boolean accept(int amountOfSeries)	{
				return amountOfSeries==1;
			}
		},CIRCLE	{
			public boolean accept(int amountOfSeries)	{
				return amountOfSeries==2;
			}
		},SQUARE	{
			public boolean accept(int amountOfSeries)	{
				return amountOfSeries>2;
			}
		};
		public abstract boolean accept(int amountOfSeries);
	}
	private static class ColourSummary	{
		public final SeriesType type;
		public final int totalAmount;
		public ColourSummary(SeriesType type,int totalAmount)	{
			this.type=type;
			this.totalAmount=totalAmount;
		}
	}
	private static class BlockConfiguration	{
		private final List<BlockStatus> blocks;
		public BlockConfiguration(List<BlockStatus> blocks)	{
			this.blocks=blocks;
		}
		public static BlockConfiguration fromString(String ids)	{
			List<BlockStatus> blocks=new ArrayList<>();
			for (char id:ids.toCharArray()) blocks.add(BlockStatus.fromChar(id));
			return new BlockConfiguration(blocks);
		}
		public int checkColour(BlockStatus colour,ColourSummary goal)	{
			// Positive result, equalling N: we still need to add N blocks of this colour.
			// Result equal to 0: great! Both the amount of blocks and the amount of series match.
			// Result equal to -1: Oops, this is not valid (too many blocks or incorrect amount of series).
			if (!colour.isAColour) throw new IllegalArgumentException(""+colour.name()+" ist keine Farbe!!!!!");
			boolean previousIsSameColour=false;
			int totalBlocks=0;
			int totalSeries=0;
			for (BlockStatus block:blocks) if (block==colour)	{
				++totalBlocks;
				if (!previousIsSameColour) ++totalSeries;
				previousIsSameColour=true;
			}	else previousIsSameColour=false;
			int missing=goal.totalAmount-totalBlocks;
			if (missing>0) return missing;
			else if (missing==0) return (goal.type.accept(totalSeries))?0:-1;
			else return -1;
		}
		public List<BlockConfiguration> getPossibleConfigurations(ColourSummary blueGoal,ColourSummary orangeGoal)	{
			int blueStatus=checkColour(BlockStatus.BLUE,blueGoal);
			if (blueStatus<0) return Collections.emptyList();
			int orangeStatus=checkColour(BlockStatus.ORANGE,orangeGoal);
			if (orangeStatus<0) return Collections.emptyList();
			int position=getReplaceableBlockPosition();
			if (position==-1) return ((blueStatus==0)&&(orangeStatus==0))?Arrays.asList(this):Collections.emptyList();
			List<BlockConfiguration> result=new ArrayList<>();
			replaceAndKeepChecking(position,BlockStatus.GAP,result,blueGoal,orangeGoal);
			if (blueStatus>0) replaceAndKeepChecking(position,BlockStatus.BLUE,result,blueGoal,orangeGoal);
			if (orangeStatus>0) replaceAndKeepChecking(position,BlockStatus.ORANGE,result,blueGoal,orangeGoal);
			return result;
		}
		private int getReplaceableBlockPosition()	{
			for (int i=0;i<blocks.size();++i) if (BlockStatus.REPLACEABLE_BLOCKS.contains(blocks.get(i))) return i;
			return -1;
		}
		private void replaceAndKeepChecking(int position,BlockStatus newStatus,List<BlockConfiguration> result,ColourSummary blueGoal,ColourSummary orangeGoal)	{
			if (!blocks.get(position).canBeReplacedBy(newStatus)) return;
			List<BlockStatus> newList=new ArrayList<>(blocks);
			newList.set(position,newStatus);
			BlockConfiguration replaced=new BlockConfiguration(newList);
			result.addAll(replaced.getPossibleConfigurations(blueGoal,orangeGoal));
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			for (BlockStatus b:blocks) result.append(b.identifier);
			return result.toString();
		}
		public static BlockConfiguration fixBlocks(List<BlockConfiguration> confs)	{
			if (confs.isEmpty()) throw new IllegalArgumentException("Das kann ich nicht machen!!!!!!");
			BlockConfiguration baseConf=confs.get(0);
			if (confs.size()==1) return baseConf;
			int size=baseConf.blocks.size();
			for (int i=1;i<confs.size();++i) if (confs.get(i).blocks.size()!=size) throw new IllegalArgumentException("Das kann ich nicht machen!!!!!");
			List<BlockStatus> result=new ArrayList<>();
			for (int j=0;j<size;++j)	{
				BlockStatus candidate=baseConf.blocks.get(j);
				for (int i=1;i<confs.size();++i)	{
					candidate=candidate.combineWith(confs.get(i).blocks.get(j));
					if (candidate==BlockStatus.UNKNOWN) break;
				}
				result.add(candidate);
			}
			return new BlockConfiguration(result);
		}
	}
	public static void main(String[] args)	{
		ColourSummary blue=new ColourSummary(SeriesType.ZERO,0);
		ColourSummary orange=new ColourSummary(SeriesType.CIRCLE,6);
		BlockConfiguration startStatus=BlockConfiguration.fromString("ooOoooOO");
		List<BlockConfiguration> possibleConfigurations=startStatus.getPossibleConfigurations(blue,orange);
		System.out.println("ATIENDE QUÉ GAÑANAZO: Ich habe "+possibleConfigurations.size()+" Dinge gefunden!");
		for (BlockConfiguration conf:possibleConfigurations) System.out.println(conf.toString());
		System.out.println("ENDLICH HABE ICH DAS GEFUNDEN!!!!!:");
		System.out.println(BlockConfiguration.fixBlocks(possibleConfigurations).toString());
	}
}
