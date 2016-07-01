package com.adm.shingling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class ShingleTopWords {

	public String commonWordsDocument;
	HashSet<String> uniqueShingles = new HashSet<String>();

	public void shingleTopWords(String filename,int count){
		BufferedReader br;
		ArrayList<String> words = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(filename));
			String line="";
			commonWordsDocument = "";
			int i = 0;
			while(((line = br.readLine())!=null) && i < count){
				String[] parts = line.split(" ");
				words.add(parts[0]);
				i++;
			}
			Collections.sort(words);
			for(String word:words){
				commonWordsDocument += word+" ";
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public HashSet<String> shingleIntoShingles(int k) {
		for (int i = 0; i < commonWordsDocument.length() - k + 1; i++) {
			// extract an n-shingle
			String shingle = commonWordsDocument.substring(i, i + k);
			uniqueShingles.add(shingle);
		}
		return uniqueShingles;
	}

	public static void main(String[] args) {
		ShingleTopWords s = new ShingleTopWords();
		//TopWords Obtained from MAP Reduce Job
		s.shingleTopWords("input/TopWords", 2500);
		s.shingleIntoShingles(4);
		
	}

}
