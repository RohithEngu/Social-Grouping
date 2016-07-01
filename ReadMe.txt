There are Three folders:
-Pre-Processing: 
	This is Java project which has everything from per processing to the LSH implementation.
	in this Project there is a PreProcessing.java file which does the initial pre processing. you can run 
	it with dataset in input folder(provided).
	Dataset can be fetched from http://help.sentiment140.com/for-students corpus. which should be saved to input folder.
	There is a BuildInitialMatrix.java which generates all from initial matrix to the final candidate pairs.
	Parameters can be set from the main stub in BuildInitialMatrix.java.
	in the input folder i already put the TopWords file for shingling from it.
-Xke_hadoop-master:
	This is a Java project for running the hadoop job to fetch top words in decreasing order.
-Python Script:
	Though it is not used for this project, i wrote this script to fetch tweets continuosly.
