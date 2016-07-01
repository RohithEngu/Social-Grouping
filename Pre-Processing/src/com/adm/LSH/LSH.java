package com.adm.LSH;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.opencsv.CSVReader;

public class LSH {

	int[][] hashValues;
	int noOfShingles;
	int[][] sigMatrix; //Stores Signature Matrix
	int[][] sigMatrixTranspose; //stores Transpose of the Signature Matrix for easier use
	/*static int numberOfUsers = 500;
	static int numberOfHashFns = 1000;
	static int numberOfBands = 200;
	static int numberOfRowsPerBand = numberOfHashFns/numberOfBands;
	static double similarityThreshold = (Math.pow((double)1/numberOfBands,(double)1/numberOfRowsPerBand));*/
	int numberOfUsers;
	int numberOfHashFns;
	int numberOfBands;
	int numberOfRowsPerBand;
	double similarityThreshold;
	/*Initializing with 
	 * number of documents(users), 
	 * number of hash functions for generating signature Matrix,
	 * number of Bands*/ 
	public LSH(int noOfUsers,int noOfHashFns,int noOfBands){
		numberOfUsers = noOfUsers;
		numberOfHashFns = noOfHashFns;
		numberOfBands = noOfBands;
		numberOfRowsPerBand = numberOfHashFns/numberOfBands;
		similarityThreshold = (Math.pow((double)1/numberOfBands,(double)1/numberOfRowsPerBand));
	}
	static double max_Sim = 0.000000;
	HashMap<Integer,Set<Integer>> finalPairs = new HashMap<Integer,Set<Integer>>();
	//Read Initial Data
	public void readInitialData() throws Exception{
		CSVReader csvReader = new CSVReader(new FileReader(new File("output/Matrix/initialMatrix.csv")));
		List<String[]> list = csvReader.readAll();
		csvReader.close();
		String[][] dataArr = new String[list.size()][];
		dataArr = list.toArray(dataArr);
		noOfShingles = list.size();
		System.out.println(noOfShingles);
		hash();
		sigMatrix = new int[numberOfHashFns][numberOfUsers];
		sigMatrixTranspose = new int[numberOfUsers][numberOfHashFns];
		for(int i=0;i<numberOfHashFns;i++)
			Arrays.fill(sigMatrix[i],99999);
		for(int j=0;j<numberOfUsers;j++){
			for(int i=0;i<noOfShingles;i++){
				if(Integer.parseInt(dataArr[i][j])==1)
				{
					for(int hash=0;hash<numberOfHashFns;hash++)
					{
						sigMatrix[hash][j]=Math.min(sigMatrix[hash][j],hashValues[hash][i] );
					}
				}
			}
		}
		
		for(int i=0;i<numberOfUsers;i++)
		for(int j=0;j<numberOfHashFns; j++)
			sigMatrixTranspose[i][j]=sigMatrix[j][i]; 
	}
	//Applying LSH to the signature Matrix
	public void applyLSH() throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter("output/CandidatePairs.txt"));
		StringBuilder sb = new StringBuilder();
		for(int bandsIndex=0;bandsIndex<numberOfBands;bandsIndex++){
			HashMap<Integer,Set<Integer>> result = fetchCandidatePairs(bandsIndex);
			addToFinalPairs(result);
		}
		sb.append(printCandidatePairsInBand(finalPairs));
		br.write(sb.toString());
		System.out.println("MAX Simi:"+max_Sim);
		br.close();
	}
	//Storing All the unique Candidate Pairs
	private void addToFinalPairs(HashMap<Integer, Set<Integer>> result) {
		for(Map.Entry<Integer, Set<Integer>> entry:result.entrySet()){
			if(!finalPairs.containsKey(entry.getKey())){
				Set<Integer> set = new HashSet<Integer>();
				set.addAll(entry.getValue());
				finalPairs.put(entry.getKey(), set);
			}
			else{
				finalPairs.get(entry.getKey()).addAll(entry.getValue());
			}		
		}
		
	}
	private String printCandidatePairsInBand(HashMap<Integer, Set<Integer>> result) {
		StringBuilder sb1 = new StringBuilder();
		//System.out.println("Band "+bandsIndex+" CandidateSet:\n");
		for (Map.Entry<Integer, Set<Integer>> entry : result.entrySet())
		{		   
		   for(Integer t:entry.getValue()){
			   //System.out.println("["+entry.getKey()+","+t+"]\n");
			   sb1.append("["+entry.getKey()+","+t+"]\n");
		   }
		}		
		return sb1.toString();
	}
	private HashMap<Integer,Set<Integer>>  fetchCandidatePairs(int bandsIndex) {
		Integer[][] temp = new Integer[numberOfUsers][numberOfRowsPerBand];
		HashMap<Integer,Set<Integer>> result = new HashMap<Integer,Set<Integer>>();
		for(int i=0;i<numberOfUsers;i++){
			for(int j=0;j<numberOfRowsPerBand;j++){
				temp[i][j] = sigMatrixTranspose[i][j+(numberOfRowsPerBand*bandsIndex)];
			}
		}		
		for(int i = 0;i<numberOfUsers;i++){
			Integer[] i1 = temp[i];
	        HashSet<Integer> set1 = new HashSet<Integer>(Arrays.asList(i1));
	        int c1 = set1.size();
			for(int j= i+1;j<numberOfUsers;j++){
				Integer[] i2 = temp[j];
				HashSet<Integer> set2 = new HashSet<>(Arrays.asList(i2));
		        int c2 = set2.size();
		        set1.retainAll(set2);
		        double tmpSimilarity = (double)set1.size()/((c1+c2)-set1.size());
		        if(tmpSimilarity >= similarityThreshold){
		        	if(tmpSimilarity>max_Sim) max_Sim=tmpSimilarity;
		        	addToFinalCandidatePair(result,i,j);
		        }
			}
		}
		return result;
	}
	
	private void addToFinalCandidatePair(HashMap<Integer,Set<Integer>> result,int i, int j) {
		if(!result.containsKey(i)){
			Set<Integer> set = new HashSet<Integer>();
			set.add(j);
			result.put(i, set);
		}
		else{
			result.get(i).add(j);
		}		
	}
	public void hash()
	{
		hashValues = new int[numberOfHashFns][noOfShingles];
		for(int i=0;i<100;i++){
			hashValues[i] = getHashedValues(i);
		}
	}
	private int[] getHashedValues(int i) {
		int[] hashes = new int[noOfShingles];
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(numberOfHashFns);
		for(int j = 0;j<noOfShingles;j++){
			hashes[j] = Math.abs(2*j+i+randomInt)%noOfShingles;
		}
		return hashes;
	}
	/*public static void main(String[] args){
		LSH m = new LSH(500,1000,200);
		try {
			m.readInitialData();
			m.applyLSH();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}