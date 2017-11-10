
// This program partitions the input datasets based on the space divison algoirthm proposed in the paper
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Point;
import java.io.FileReader;
import java.io.File;
import java.io.*;
import java.awt.geom.Point2D;


///////////////////////////////Main Class. Main Function./////////////////////////////////////
public class DataPreprocessing{

	private  static double data[][];
	private static boolean dataLoaded = false;

	private static int numData;
	private static int vecLen;


	public DataPreprocessing(){


	}



	public static void main(String[] args) throws IOException{

		if(args.length != 5){
			System.out.println("Incorrect arguments. Proivide Proper Arguments" + "\n"+
								"1. DataFileNAme  2. Output fileDirectory 3. filePrefix, 4.  inputDimension 5. vectorLength" );
			System.exit(0);

		} //hBoxFilesWilt
		String fileName = args[0];
		String outDirectory = args[1];
		String file_Prefix = args[2];
		int inputDimension = Integer.parseInt(args[3]);
		int vectorLength = Integer.parseInt(args[4]);
		
		double [] [] hBoxData = loadDataFromTextFile(fileName);
			double beginTime = System.currentTimeMillis();
		CreateMapperFiles cmobj = new CreateMapperFiles();
		System.out.println("inputDimension- " + inputDimension + " numData - " + numData + " vecLen- "+ vecLen);
		cmobj.mapperFile(hBoxData, inputDimension, numData, outDirectory , file_Prefix, vectorLength );
        	double endTime = System.currentTimeMillis();
		System.out.println("*************************************************************************************");
		System.out.println("Time "	+ (endTime - beginTime) / 1000 + " seconds.");
		
	}

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

		data = new double[numData][vecLen];
		// ZBJ: Do gc in order to load larger file
		Runtime.getRuntime().gc();
		
		System.out.println("At load dataset vecLen- " + vecLen + " numData- "+ numData);
		String[] vectorValues = null;
		int numRecords = 0;
		int j=2;
		while ((inputLine = reader.readLine()) != null) {
			vectorValues = inputLine.split(",");
			if (vecLen != vectorValues.length) {
				throw new IOException("Vector length did not match at line "
						+ numRecords);
			}
			j++;
			for (int i = 0; i < vecLen; i++) {
				//System.out.println("Processing line no - " + j);
				data[numRecords][i] = Double.valueOf(vectorValues[i]);
			}
			numRecords++;
		}

		// ZBJ: Do close
		reader.close();
		
		// ZBJ: Do gc in order to load largeer file
		Runtime.getRuntime().gc();
		
		return data;
	} // end od load data
}//end of class


/////////////////////////////////////Create Input Files for Different Mappers///////////////////////////////
class CreateMapperFiles{

	public CreateMapperFiles(){

	} 
	public void mapperFile(double [][] hBoxData, int inputDimension, int numData, String outDirectory, String file_Prefix, int vectorLength) throws IOException{

		String path = "/root/shashi/data_processing/" +  "output/"+  outDirectory +"/";

		
		file_Prefix = file_Prefix+ inputDimension + "D_"; // this seperates dataset is which dimension
		 int classNum;
	    
			double[] medianVector = {0.5245061120379945,0.5257386954528109,0.5247292690407334,0.525317460648903};//dataset3

			int[] file_id = new int[vectorLength]; //vectorlength is equal to the n^vectorlen			
		
			for (int k =0; k< vectorLength; k++ ) {
					
			System.out.println("median vector - "+medianVector[k] + " LENGTH- " + medianVector.length + " dimension " + inputDimension);
			
			}
			for (int i = 0; i< numData ; i ++){
			
			
			String fullpath = path+ file_Prefix;
		      	for (int j =0 ; j < vectorLength;j++){
		      	//if (hBoxData[i][j] < 0.5 || hBoxData[i][j] < 0.5 )
		      	if (hBoxData[i][j] < medianVector[j] || hBoxData[i][j] == medianVector[j] ) // checks for median in this direction
		      		file_id[j] =0;
		      	else
		      		file_id[j] =1;
		      	}
		       String file_suffixId="" ;
		       //creating a file id for each point based on its each dimension
		       for (int ii =0 ; ii< vectorLength ; ii ++)
		       		file_suffixId = file_suffixId + file_id[ii];

		       fullpath = fullpath + file_suffixId;
		     //  System.out.println(fullpath);
 
 

		      	//File file = new File(fullpath);
		        PrintWriter printWriter = null;
		        try
		        {
		        	FileWriter fw = new FileWriter(fullpath, true); //second flag indicates file is opened for append
    				BufferedWriter bw = new BufferedWriter(fw);

		            printWriter = new PrintWriter(bw);
	            	String outPut = "";
	              	for(int k=0; k< inputDimension; k++){
	              	outPut = outPut+ hBoxData[i][k] + ",";
	              	 		 	    
	     		   	}
	     		   	 classNum = (int)hBoxData[i][inputDimension]; // last entry in eachline is classnum
	            	outPut = outPut + classNum;

	            	printWriter.println(outPut);
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
		



		}// end of for

	} // end of method


}// end of class
