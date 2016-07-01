package com.example.trendfinder;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class ReverseComparator extends WritableComparator{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ReverseComparator() {
		super((Class<? extends WritableComparable>) ReverseComparator.class);
		// TODO Auto-generated constructor stub
	}

   
}
