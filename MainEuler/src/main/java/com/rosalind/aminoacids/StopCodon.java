package com.rosalind.aminoacids;

public enum StopCodon implements TranslatedCodon {
	OCHRE,AMBER,OPAL;

	@Override
	public boolean isStop() {
		return true;
	}

	@Override
	public boolean isStart() {
		return false;
	}

	@Override
	public char getSymbol() {
		return '-';
	}

	@Override
	public String getName() {
		return name();
	}
}
