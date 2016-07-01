package com.adm.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class PreProcessing {

	Map<String,String> UserTweets = new HashMap<String, String>();
	Map<Integer,TreeSet<String>> UniqueWords = new HashMap<Integer,TreeSet<String>>();

	public void readFileAndParse(String filename){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line="";
			while((line = br.readLine())!=null){
				String[] parts = line.split(",");
				AddToMap(parts[4],removeStopWords(parts[5]));
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
		AddToFile();
	}

	private void AddToFile() {
		BufferedWriter bw1,bw2;
		try {

			bw2 =  new BufferedWriter(new FileWriter("output/UserMapping.Properties",true));
			int i =0 ;
			for(Map.Entry<String, String> user: UserTweets.entrySet()){
				bw1 =  new BufferedWriter(new FileWriter("userdocs/"+Integer.toString(i)+".txt",true));
				bw1.write(PreProcessString(user.getValue().trim().toLowerCase())+"\n");
				bw2.write(PreProcessString(user.getKey().toLowerCase())+"="+i+"\n");
				bw1.close();
				i++;
				if(i == 80000)
					break;
				//AddToUniqueWords(user.getValue());
			}
			bw2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	private void AddToUniqueWords(String value) {
		TreeSet<String> set;
		if(!UniqueWords.containsKey(1)){
			set = new TreeSet<String>();
			UniqueWords.put(1, set);
		}
		else{
			String[] temp = value.split(" ");
			TreeSet<String> tmpSet = UniqueWords.get(1);
			tmpSet.addAll(Arrays.asList(temp));
			UniqueWords.put(1, tmpSet);
		}
	}

	private String PreProcessString(String str){
		StringBuilder builder = new StringBuilder();
		for (char ch : str.toCharArray()) 
			if (Character.isAlphabetic(ch)||Character.isDigit(ch)||Character.isSpaceChar(ch)) 
				builder.append(ch);
		return builder.toString();
	}
	public static String removeStopWords(String text) throws Exception {
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		Analyzer analyzer = new StandardAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
		tokenStream = new StopFilter(tokenStream, stopWords);
		StringBuilder sb = new StringBuilder();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			String term = charTermAttribute.toString();
			sb.append(term + " ");
		}
		analyzer.close();
		tokenStream.close();
		return sb.toString();
	}

	private void AddToMap(String username, String tweetText) {
		if(!UserTweets.containsKey(username)){
			UserTweets.put(username, tweetText);
		}
		else
			UserTweets.put(username, UserTweets.get(username)+" "+tweetText);

	}
	public static void main(String[] args) {
		PreProcessing p = new PreProcessing();
		p.readFileAndParse("input/Dataset.csv");
		//p.readFileAndParse("input/test.csv");
	}

}
