
// This program implimets FMNN testing algorithm
import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;

public class FmnnTest{


	private  static double data[][];
	private static boolean dataLoaded = false;

	private static int numData;
	private static int vecLen;

	public	static double [][] memmbershipValVector;
	public FmnnTest(){


	}



	public static void main(String[] args) throws IOException{

		if(args.length != 2){
			System.out.println("Incorrect arguments. Proivide Proper Arguments" + "\n"+
								"1. HboxDataFileNAme 2. inputDimension");
			System.exit(0);

		}
		String fileName = args[0];
		int inputDimension = Integer.parseInt(args[1]);

		
		double [] [] hBoxData = loadDataFromTextFile(fileName);
		//double currentPoint[] = {0.1666666667,0.4166666667,0.0677966102,0.0416666667};
		 double currentPoint[] = {0.202898551,0.08};
		FmnnTestPoint fobj = new FmnnTestPoint();

		long beforeTime = System.currentTimeMillis();
        memmbershipValVector =	fobj.testPoint(currentPoint, hBoxData, inputDimension, numData ,vecLen); // function 
        int index = getBestResult(memmbershipValVector);

        double timeInSeconds = ((double) (System.currentTimeMillis() - beforeTime)) / 1000;

		
		System.out.println(" Max membershipValue- " + memmbershipValVector[index][0] + " class = " + memmbershipValVector[index][1]);
		System.out.println("*************Time Taken =  ********** - " + timeInSeconds);
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



	public static int getBestResult (double [][] membershipValVector){
		
		double maxMembershipVal=0; int index=-1;
		for(int i =0 ; i< membershipValVector.length; i++){
			if (membershipValVector[i][0] > maxMembershipVal){
				maxMembershipVal = membershipValVector[i][0];			
				index =i;
			}
		}
		
		return index;
	}

}// end of class FmnnTest

class FmnnTestPoint{
	public FmnnTestPoint(){

	}

	public double[][] testPoint(double[] currentPoint, double [][] hBoxData, int inputDimension, int totalhBoxes, int vecLen){

		double [][] memmbershipValVector = new double[totalhBoxes][2]; // to store mvalue and class number 
		double membershipValue;
		double[] currentPointsInDimension = new double[inputDimension]; 
		int classNum;

		for (int i = 0; i < totalhBoxes ; i++ ){

			/*************************loading hBox data into cmputauional variables****************************  */
			double [] minPointDimofhBox  = new double[inputDimension]; double [] maxPointDimofhBox  = new  double[inputDimension];
			
			for (int ii =0; ii< inputDimension; ii++)
				minPointDimofhBox[ii] = hBoxData[i][ii];
			
			for (int jj=0; jj < inputDimension; jj++)
				maxPointDimofhBox[jj]= hBoxData[i][inputDimension+jj]; // hBoxinformation is stored in single vector. min points, max points and class num
			
			classNum = (int)hBoxData[i][2*inputDimension]; // last field is classNumber
			

			/******************************finding memebership value for each hBoxes************************************/

			
			for (int kk =0; kk < inputDimension ; kk++ ) {
				currentPointsInDimension[kk] = currentPoint[kk];

			}

			int gama = 4; double result =0;    
			// System.out.println("In checkMembership");  
		
			
					result =0;
				

					//*******************membership function**********************************************//

					for(int j=0 ; j < inputDimension; j++) //this represents the Summation of membership formula
					{ 
						double fa = 1- maxValue(0, gama*minValue(1, (currentPointsInDimension[j]- maxPointDimofhBox[j])));
						double fb = 1 - maxValue (0, gama*minValue(1, (minPointDimofhBox[j] - currentPointsInDimension[j])));

						result = result + (maxValue(0, fa) + maxValue( 0, fb) );              


					}
					result = result / (2*inputDimension);

					memmbershipValVector[i][0] = result;
					memmbershipValVector[i][1] = classNum;

		}// end of for main loop
		return memmbershipValVector;
	} // end of the method

	

	public static double maxValue(double a, double b){
		if(a > b )
			return a;
		else
			return b;
	}

	public static double minValue(double a, double b){
		if(a < b )
			return a;
		else
			return b;
	}
} // end of FmnnTestPoint
