package com.adm.LSH;

import java.util.BitSet;

public class BitMatrix {
	public BitMatrix (int numRows, int numColumns) {
		rows = new BitSet[numRows];
		for (int i = 0; i < numRows; i++)
			rows[i] = new  BitSet(numColumns);
	}
	public BitSet [] rows;
	public void clear (int i, int j) { rows[i].clear(j); }
	public boolean get (int i, int j) { return rows[i].get(j); }
	public void set (int i, int j) { rows[i].set(j); }
}