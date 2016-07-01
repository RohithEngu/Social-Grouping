package com.adm.LSH;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Scanner;

import com.adm.shingling.ShingleTopWords;
public class BuildInitialMatrix {


	public int[][] matrixRows;
	//Constructor which accepts
	public void GenerateInitialMatrix(int numberOfUsers,int shingleLength,int noOfTopWords){
		ShingleTopWords s = new ShingleTopWords();
		s.shingleTopWords("input/TopWords", noOfTopWords);
		HashSet<String> uniqueShingles = s.shingleIntoShingles(shingleLength);
		matrixRows = new int[uniqueShingles.size()][];
		for (int i = 0; i < uniqueShingles.size(); i++)
			matrixRows[i] = new int[numberOfUsers];
		compareShinglesWithUsers(uniqueShingles,numberOfUsers);
	}


	private void compareShinglesWithUsers(HashSet<String> uniqueShingles,int noOfUsers) {
		int k=0;
		File t=null;
		Scanner scan =null;
		for(int i =0;i<noOfUsers;i++){
			String text;
			k=0;
			try {
				t = new File("userdocs/"+i+".txt");
				scan = new Scanner(t);
				text = scan.useDelimiter("\\A").next();
				for(String str:uniqueShingles){				
					if(text.toLowerCase().contains(str.toLowerCase())){
						matrixRows[k][i] = 1;
					}
					else
						matrixRows[k][i] = 0;
					k++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				scan.close();
			}
		}
		try {
			writeCSV(matrixRows,"initialMatrix.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	//Save Initial Matrix To CSV
	public static void writeCSV(int[][] matrix,String filename) throws Exception {
		BufferedWriter br = new BufferedWriter(new FileWriter("output/Matrix/"+filename));
		StringBuilder sb = new StringBuilder();
		System.out.println(matrix.length +" column:"+matrix[0].length);
		for(int i=0;i<matrix.length;i++){
			sb=new StringBuilder();
			for (int t:matrix[i]) {
				sb.append(t);
				sb.append(",");
			}
			//String tmp = sb.substring(0, sb.length()-1);
			br.write(sb.substring(0, sb.length()-1));
			br.write("\n");
		}
		br.close();
	}

	public static void main(String[] args){
		BuildInitialMatrix b = new BuildInitialMatrix();
		int noOfBands = 200;
		int noOfHashFns = 1600;
		int numberOfUsers = 500;
		int shingleLength = 5;
		int noOfTopWords = 1000;
		b.GenerateInitialMatrix(numberOfUsers,shingleLength,noOfTopWords);
		LSH m = new LSH(numberOfUsers,noOfHashFns,noOfBands);
		try {
			m.readInitialData();
			m.applyLSH();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
