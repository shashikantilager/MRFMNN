import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;

public class AccuracyFinder{


	private  static double data[][];
	private static boolean dataLoaded = false;

	private static int numData;
	private static int vecLen;

	public	 static double [][] membershipValVector;
	public 	 static double [][] tempmembershipValVector;
	public AccuracyFinder(){


	}



	public static void main(String[] args) throws IOException{

		if(args.length != 4){
			System.out.println("Incorrect arguments. Proivide Proper Arguments" + "\n"+
								"1. testInputfileName . 2. ResultOutputfileName 3. testVeclen 4. ResultVecLen");
			System.exit(0);

		}
		String testPointsfileName = args[0];

		String resultfileName = args[1];
		int testVeclen = Integer.parseInt(args[2]);
		int resultVecLen = Integer.parseInt(args[3]);
		long beforeTime = System.currentTimeMillis();
		
		double [] [] testPointData = loadDataFromTextFile(testPointsfileName);
		double [] [] resultPointData = loadDataFromTextFile(resultfileName);
		if ( testPointData.length != resultPointData.length){
			System.out.println("ERROR--------->Input points mismatch between test and result data ");
			System.exit(0);
		}
		if (testPointData.length != numData) {
			System.out.println("3RR0R -----> Input points mismatch");
			System.exit(0);
		}

		FindValue fobj = new FindValue();

		fobj.calculate_value(testPointData,resultPointData,testVeclen,resultVecLen,numData); // this return set of results
        
        double timeInSeconds = ((double) (System.currentTimeMillis() - beforeTime)) / 1000;

			System.out.println("*************Time Taken =  ********** - " + timeInSeconds);
	}

	public static double[][] loadDataFromTextFile(String fileName) throws IOException {

		String filePath = "input/" ;
		 filePath = filePath + fileName;
		
		File file = new File(filePath);
		
		// ZBJ: could limit the buffer size, but slow 
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String inputLine = reader.readLine();
		if (inputLine != null) {
			numData = Integer.parseInt(inputLine);
		} else {
			new IOException("First line = number of rows is null");
		}

		inputLine = reader.readLine();
		if (inputLine != null) {
			vecLen = Integer.parseInt(inputLine);
		} else {
			new IOException("Second line = size of the vector is null");
		}

		data = new double[numData][vecLen]; 
		// ZBJ: Do gc in order to load larger file
		Runtime.getRuntime().gc();
		
		
		String[] vectorValues = null;
		int numRecords = 0;
		while ((inputLine = reader.readLine()) != null) {
			vectorValues = inputLine.split(",");
			if (vecLen != vectorValues.length) {
				throw new IOException("Vector length did not match at line "
						+ numRecords);
			}
			for (int i = 0; i < vecLen; i++) {
				data[numRecords][i] = Double.valueOf(vectorValues[i]);
			}
			numRecords++;
		}

		// ZBJ: Do close
		reader.close();
		
		// ZBJ: Do gc in order to load largeer file
		Runtime.getRuntime().gc();
		
		return data;
	}

}// end of class 



class FindValue{
	public FindValue(){

	}

	public void calculate_value(double [][] testPointData, double[][] resultPointData,int testVeclen, int resultVecLen, int numData){

		double total_false = 0;
		System.out.println("testing0- testing class  " + testPointData[0][testVeclen -1] + " resultPointData class- " + resultPointData[0][resultVecLen -1]);
		for (int i =0 ;i< numData ; i++){
			if(testPointData[i][testVeclen -1] != resultPointData[i][resultVecLen -1] )
				total_false = total_false +1;
		}
		double false_rate = ( total_false/ numData) * 100.0 ;
		System.out.println("Total input test points - " + numData + "  total_false - " + total_false);
		System.out.println("Accuracy rate - " + (100.0 - false_rate) + " %");
	}
} // end of FindValue
