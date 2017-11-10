// This program finds the median for the number of initial dimensions we pass
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Point;
import java.io.FileReader;
import java.io.File;
import java.io.*;

public class MedianFinder{

	private  static double data[][];
	private static boolean dataLoaded = false;

	private static int numData;
	private static int vecLen;


	public MedianFinder(){


	}



	public static void main(String[] args) throws IOException{

		if(args.length != 2){
			System.out.println("Incorrect arguments. Proivide Proper Arguments" + "\n"+
								"1. HboxDataFileNAme  2. medianVectorLen");
			System.exit(0);

		} //hBoxFilesWilt
		String fileName = args[0];
		int medianVectorLen =  Integer.parseInt(args[1]);
		double [] [] data = loadDataFromTextFile(fileName);


		System.out.println("Before normalze -Writing the data" + " numdata- " + numData + " veclen- " +vecLen);
		
		System.out.println("medianVectorLen- " + medianVectorLen );

		 double [] medianVector = median_calculator(data, numData, vecLen, medianVectorLen);
		//writeToFile(data, numData, vecLen, outputFileName);
		System.out.println("**************************************Median Vector*****************************************");
		//System.out.println(" After normalization Writing the data");
		for (int i=0;i< medianVectorLen; i++){
			System.out.print(medianVector[i] + "," );

		}
		System.out.println("");

	}

	// access data column wise , and median_calculator the each dimension
	public static double[] median_calculator(double[][] data, int numData, int vecLen, int medianVectorLen){
			
			double [] medianVector = new double[medianVectorLen];
		for (int i=0; i< medianVectorLen ; i++){ // leaving last column, as it is class label
			//double[] minMax = findMinMax(data)
			double min= data[0][i];
			
			System.out.println("At main loop- " + "data" + "[" + i + "]"  +"["+ "0"+"]" + data[0][i] );
			double max= min;
			//find min and max in a ith column;
			//System.out.println("Inside median_calculator- data sequence");
			double median =0;
			for (int j=0;j < numData ; j++){

				//System.out.println(data[j][i]);
				median = median+ data[j][i];
				
			}
			//System.out.println("Min=" +min + " max= " + max);
			//update the each element , to median_calculator the data
			median = median/numData;
			medianVector[i] = median;
		} // end of main for loop
		 return medianVector;
	}
/***************************************write median_calculatord data to the file**********************************************************/
public static void writeToFile( double[][] data , int numData, int vecLen, String fileName) throws IOException{


		String path = "/media/shashi/58CC0D50CC0D29B8/Paper_work/nncode/generic/data_processing/median_calculator/"+ "output/";
        
		 System.out.println(path);

		 //String file_suffix = getSuffix (String fileName);
		 String fullpath = path+ fileName;

	  	File file = new File(fullpath);
		
		PrintWriter printWriter = null;

		try
		{
		    // line each line to the file
		    printWriter = new PrintWriter(file);
		    printWriter.println(numData);
		    printWriter.println(vecLen);
		      for(int i=0; i< numData; i++){

		      	    String outPut = "";
		 		 						
 		 	    for (int j =0; j < vecLen -1 ; j++)
 		 	    {
 		 	    	outPut= outPut+ data[i][j] + ",";
 		 	    }
 				 outPut = outPut + data[i][vecLen -1]; // to avoid , for last column, adding seperatly
	    		printWriter.println(outPut);
	     		   
	   		 }
	   	}
		catch (FileNotFoundException e)
		{
		    e.printStackTrace();
		}
		finally
		{
		    if ( printWriter != null ) 
		    {
		        printWriter.close();
		    }
		}

}// end of function

/************************************************************************************************************************************/

//stores the data into column measure order. first column data in file is stored in first row of the double vector data
	public static double[][] loadDataFromTextFile(String fileName) throws IOException {

		File file = new File(fileName);
		
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

		//data = new double[numData][vecLen];

		data = new double[numData][vecLen];
		// ZBJ: Do gc in order to load larger file
		Runtime.getRuntime().gc();
		
		System.out.println("At load dataset vecLen- " + vecLen + " numData- "+ numData);
		String[] vectorValues = null;
		int numRecords = 0;
		int j=0;
		while ((inputLine = reader.readLine()) != null) {
			vectorValues = inputLine.split(",");
			
			if (vecLen != vectorValues.length) {
				throw new IOException("Vector length did not match at line "
						+ numRecords);
			}
				//System.out.println("Processing line no - " + j);
			
			for (int i = 0; i < vecLen; i++) {
			
				data[j][i] = Double.valueOf(vectorValues[i]);
				//System.out.print(data[j][i] + "\t");
			}
			//System.out.println("");
			j++;
			//numRecords++;
		}

		// ZBJ: Do close
		reader.close();
		
		// ZBJ: Do gc in order to load largeer file
		Runtime.getRuntime().gc();
		System.out.println("*******Returning from load dataset*********");
		return data;
	} // end od load data
}//end of class
