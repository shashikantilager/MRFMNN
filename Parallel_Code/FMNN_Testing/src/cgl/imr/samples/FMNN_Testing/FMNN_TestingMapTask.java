/*
 @author- Shashikant Ilager
 */

package cgl.imr.samples.FMNN_Testing;

import cgl.imr.base.Key;
import cgl.imr.base.MapOutputCollector;
import cgl.imr.base.MapTask;
import cgl.imr.base.SerializationException;
import cgl.imr.base.TwisterException;
import cgl.imr.base.Value;
import cgl.imr.base.impl.JobConf;
import cgl.imr.base.impl.MapperConf;
import cgl.imr.data.file.FileData;
import cgl.imr.types.BytesValue;
import cgl.imr.data.file.FileData;
import cgl.imr.types.BytesValue;
import cgl.imr.types.DoubleVectorData;
import cgl.imr.types.StringKey;
import cgl.imr.types.DoubleArray;
import java.io.FileReader;
import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import cgl.imr.message.MapperRequest;

public class FMNN_TestingMapTask implements MapTask {

	//private TwisterConfigurations configs ;
	private FileData fileData;
	private DoubleVectorData vectorData;

	private  MapperRequest mapperReqObj;
	int MapId;

	
  	private static int inputDimension;

	public void close() throws TwisterException {
		// TODO Auto-generated method stub
	}

	/**
	 * Loads the vector data from a file. Since the map tasks are cached
	 * across iterations, we only need to load this data  once for all
	 * the iterations.
	 */
	public void configure(JobConf jobConf, MapperConf mapConf) throws TwisterException {
		
		fileData = (FileData) mapConf.getDataPartition(); // Gets respective file for each mapper using partion file
		 mapperReqObj = new MapperRequest(jobConf, mapConf, 1);
	
		 MapId = mapperReqObj.getMapTaskNo();
		 this.vectorData = new DoubleVectorData();                     
		 fileData = (FileData) mapConf.getDataPartition(); 
		try{                
		      vectorData.loadDataFromTextFile(fileData.getFileName());        
		 } 
		 catch (Exception e) {
		      throw new TwisterException(e);
		}
	} // end of configure method

	

	/****************************************Map function for the FMNN_Test algorithm****************************************************/
	
	public void map(MapOutputCollector collector, Key key, Value val) //TODO , 
			throws TwisterException {

		
		//inputDimension = 2; // dimension //TODO . Get it from driver broadcast and test point too
		double  [][] membershipValVector; // each line has 2 calues -> 0- mvalue , 1- classNum
		double [][] hBoxData = vectorData.getData() ;   // loading hBox Data into the local variable
		double [][] tempmembershipValVector = new double[vectorData.getNumData()][2]; // for every point it returns set of m,c wrt all hboxes
		//double currentPoint[] = {0.1666666667,0.4166666667,0.0677966102,0.0416666667}; // test point- 4d dataset
		// double currentPoint[] = {0.202898551,0.08}; // 2d dataset
		DoubleVectorData testingData = new DoubleVectorData();
		try{
			testingData.fromBytes(val.getBytes());
			int testVecLen = testingData.getVecLen();
			 inputDimension = testVecLen -1 ; // last entry is class info test data, 
			double[][] testPointData = testingData.getData();
			double [] currentPoint = new double[inputDimension];
			
			int testnumData = testingData.getNumData();
			//System.out.println("Test data NumData- " + testnumData + " inputDimension -" +inputDimension + " vecdata no - "
								 //+ vectorData.getNumData() );
			FMNN_TestingMapTask fmnnTestObj = new FMNN_TestingMapTask();

			membershipValVector = new double[testnumData][2];
	        for (int i =0 ; i< testnumData; i ++){
	        	 
	        	 for (int ii = 0; ii < inputDimension ; ii++) // not copying last attrbt, which is class number
	        	 	currentPoint[ii] = testPointData[i][ii];

	        	 tempmembershipValVector =	fmnnTestObj.testPoint(currentPoint, hBoxData, inputDimension, vectorData.getNumData(), vectorData.getVecLen()); // this return set of results
	        	 int index = localBestResult(tempmembershipValVector);
	        	 membershipValVector[i][0] = tempmembershipValVector[index][0]; // copying mvalue
	        	 membershipValVector[i][1] = tempmembershipValVector[index][1]; // copying corresponding class
	        }
	        int index = localBestResult(membershipValVector);

	        

			/*for (int j =0; j <  testnumData ; j ++){
				System.out.println(" @ Mapper point " + j + " has  membershipValue " + membershipValVector[j][0] + " class - " + membershipValVector[j][1]);
			
			}*/

			
			int dummyKey = 1;                     //Since we are using only one reducer , only one unique willl be there for all mappers 
			String keyStr = Integer.toString(dummyKey); 
			//DoubleArray doubleArrayObj = new DoubleArray(localbestData, 2); 
			DoubleVectorData mapResult  = new DoubleVectorData(membershipValVector, testnumData , 2 );
			collector.collect(new StringKey(keyStr), new BytesValue(mapResult.getBytes()));


		}

		 catch (SerializationException  e) {
			throw new TwisterException(e);
		}

	
	} //end of map method


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


	public static int localBestResult (double [][] membershipValVector){
		
		double maxMembershipVal=0; int index=-1;
		for(int i =0 ; i< membershipValVector.length; i++){
			if (membershipValVector[i][0] > maxMembershipVal){
				maxMembershipVal = membershipValVector[i][0];			
				index =i;
			}
		}
		
		return index;
	}

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


} // end of class




