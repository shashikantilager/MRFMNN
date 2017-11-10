
// This program is FMNNN training algorithm

/*
 * @author shashi
 */

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Point;
import java.io.FileReader;
import java.io.File;
import java.io.*;


public class  FMNN{

	public List<DataSet> dataSet;

	public FMNN() {
		dataSet = new ArrayList<DataSet>();
	}

	public static void main(String[] args) throws IOException{

		if(args.length != 3){
			System.out.println("Incorrect command line arguments. Provide proper arguments");
			System.out.println("1. dimension 2. InputFileName  3. OutPutFilehBoxfileName");
			System.exit(0);

		}

		int n= Integer.parseInt(args[0]);
		String fileName = args[1];
		String file_prefix = args[2];
		
		double beginTime = System.currentTimeMillis();

		DataReader reader = new DataReader();
		FMNN fmnnObj = new FMNN();

		fmnnObj.setdataSet(reader.LoadDataSetfromFile(fileName));
		

		int totalpoints = 0;
		for ( DataSet i : fmnnObj.dataSet){
			//System.out.println(i);
			//System.out.println("x "+ i.pt.getX() +"y " + i.pt.getY() + "class= " + i.classNum);
			totalpoints += 1;
		}
		System.out.println(" total input points read = " + totalpoints);

		HiddenLayer hiddenLayerObj = new HiddenLayer(n);

		List <HyperBox> hBox =  hiddenLayerObj.classifyData(fmnnObj.dataSet,n);

		//Testing with output console
		
	

		OverlapTest oobj = new OverlapTest();

		hBox = oobj.checkOverlap(hBox, n );

		/***************************************************Creating a output dynamic partiton file for test************************************************/

		String path = "/root/shashi/FMMNN_2d/"+ "hBoxFIles/";
        
		 System.out.println(path);

		 //String file_suffix = getSuffix (String fileName);
		 String fullpath = path+ file_prefix;

	  		File file = new File(fullpath);
		
		PrintWriter printWriter = null;

		try
		{
		   
		    printWriter = new PrintWriter(file);
		      for(int i=0; i< hBox.size(); i++){

		      	    String outPut = "";
		 		 						
 		 	    for (int j =0; j < n; j++)
 		 	    {
 		 	    	outPut= outPut+ hBox.get(i).minPoint[j] + ",";
 		 	    }
 		 	    for (int j = 0; j < n; j++)
 		 	    {
 		 	    	outPut= outPut+ hBox.get(i).maxPoint[j] + ",";
 		 	    }
 		 	    outPut = outPut + hBox.get(i).classNum;
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


        	double endTime = System.currentTimeMillis();
        	//System.out.println("Number of points in hBox" + i+  " = " + hBox.get(i).hBoxPoints.size() + "classNum - " + hBox.get(i).classNum);
        	System.out.println("***********************************************************************************************"+"\n");
			System.out.println("Number of hyperboxex created = " + hBox.size());
			System.out.println("------------------------------------------------------");
			System.out.println("FMNN Job Took "
					+ (endTime - beginTime) / 1000 + " seconds.");
			System.out.println("------------------------------------------------------");
		

	}   //end of main

	public void setdataSet(List<DataSet> dataSet){
		this.dataSet = dataSet;
	}



}// enf of class FMNN



/***************************************Class DataReader to handle the  data set files****************************************************************/
class DataReader{   
	public List<DataSet> LoadDataSetfromFile(String fileName) throws IOException{
		
		String filePath = "dataset/" ;
		filePath = filePath + fileName;
		
		Scanner dataReaderObj = new Scanner(new FileReader(filePath));
		List<DataSet> dataSetObj = new ArrayList<DataSet>();
		int classNum, dimension;

		String meta_input = dataReaderObj.nextLine();
		int numData = Integer.parseInt(meta_input); // number of dataset points
		meta_input = dataReaderObj.nextLine(); 
		int numAttributes = Integer.parseInt(meta_input);  // number of field in each line
		dimension = numAttributes -1 ; // last attribute is to mention class number
		double [] dataPoint = new double[dimension]; 

		System.out.println("numData= " + numData + " numAttributes= " + numAttributes + " dimension= " + dimension);
		while(dataReaderObj.hasNextLine()){


			String input = dataReaderObj.nextLine();
			String[] attr = null;
			if (input != null ) {
				attr = input.split(",");
			}
			if (attr != null && attr.length == numAttributes ) {

				// One less than toal fields, last one is class numbner in data set
				for (int i = 0; i < dimension ; i++){
					dataPoint[i]= Double.parseDouble(attr[i]); 
				}
				classNum = Integer.parseInt(attr[numAttributes-1]); // last location location is class number(starts with 0 , hence -1)

				DataSet sample = new DataSet(dataPoint, classNum, dimension );
				dataSetObj.add(sample);
			}
		}
		return dataSetObj;
	}

}


/********************************************************************************************************************************************************/


class HiddenLayer
{
	public List<HyperBox> hBox;
	private  boolean hBoxStatusOfClass;
	private int inputDimension;
	HiddenLayer(int n){
		inputDimension = n;
	}

	public List <HyperBox> classifyData(List<DataSet> dataSet,int n)
	{ 
		hBox = new ArrayList<HyperBox>();
		boolean isMember;
		hBoxStatusOfClass = false;
		
		inputDimension = n;


		for(DataSet i : dataSet)
		{    
			isMember = false;
			if (hBox.size() < 1){ // If no single hyperbox, create one
				i.membershipValue =1;
				HyperBox newHBox1 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);
				hBox.add(newHBox1);
				hBoxStatusOfClass = true; //atleast one hyperbox created

			}

			/*if(i.classNum ==1 && !hBoxStatusOfClass1) //If there is no Single Hyperbox created till in HiddenLayer for this class
			{
				//System.out.println("In no hyperbox status");
				i.membershipValue =1;
				HyperBox newHBox1 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);
				hBox.add(newHBox1);
				hBoxStatusOfClass1 = true; //atleast one hyperbox created
			}
			else if(i.classNum == 2 && !hBoxStatusOfClass2 ){
				i.membershipValue =1;
				HyperBox newHBox1 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);
				hBox.add(newHBox1);
				hBoxStatusOfClass2 = true; //atleast one hyperbox created

			}
			else if(i.classNum == 3 && !hBoxStatusOfClass3 ){
				i.membershipValue =1; 
				HyperBox newHBox1 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);
				hBox.add(newHBox1);
				hBoxStatusOfClass3 = true; //atleast one hyperbox created


			}
			else if(i.classNum == 4 && !hBoxStatusOfClass4 ){
				i.membershipValue =1; 
				HyperBox newHBox1 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);
				hBox.add(newHBox1);
				hBoxStatusOfClass4 = true; //atleast one hyperbox created

			}	*/
			else
			{
				isMember = checkMembership(hBox, i, inputDimension);
				if(!isMember)
				{
					HyperBox newHBox2 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);// constructor will set min and max point of new 						hyperbox
					i.membershipValue =1; // for point hyperbox, it will have full membership value
					newHBox2.addDataSet(i);
					hBox.add(newHBox2);

				}
			}

		} 
		return hBox;
	}

	public static boolean checkMembership(List<HyperBox> hBox, DataSet currentData, int inputDimension){

		double membershipValue;
		double[] currentPointsInDimension = new double[inputDimension];
		boolean classified = false; 

		for (int kk =0; kk < inputDimension ; kk++ ) {
			currentPointsInDimension[kk] = currentData.pt[kk];

		}

		int gama = 4; int hBoxToBeExpanded = -1; double result =0; double maxResult =0;

		//int[] connectedToOutLayer = new int [outNeurons]; // Represents matrix U       

		//debug
		// System.out.println("In checkMembership");  
		for(int i = 0; i < hBox.size(); i++)
		{
			result =0;
			if(hBox.get(i).classNum == currentData.classNum){ //If current data set and hyperbox belogns to same class
				HyperBox hb = hBox.get(i);

				double[] maxPointDimofhBox = new double[inputDimension]; double[] minPointDimofhBox = new double[inputDimension];
				for (int ii = 0 ; ii < inputDimension; ii++){
					maxPointDimofhBox[ii] = hb.maxPoint[ii];
					minPointDimofhBox [ii] = hb.minPoint[ii];
				} 



				//*******************membership function**********************************************//

				for(int j=0 ; j < inputDimension; j++) //this represents the Summation of membership formula
				{ 
					double fa = 1- maxValue(0, gama*minValue(1, (currentPointsInDimension[j]- maxPointDimofhBox[j])));
					double fb = 1 - maxValue (0, gama*minValue(1, (minPointDimofhBox[j] - currentPointsInDimension[j])));

					result = result + (maxValue(0, fa) + maxValue( 0, fb) );              


				}
				result = result / (2*inputDimension);
				//ToDO.. Before going for expansion, we can check for max membership value hBox, presently scanning for all hBoxes of similar class
				if(result == 1){
					classified = true;
					currentData.membershipValue = result;
					hBox.get(i).addDataSet(currentData);
					//return classified;

					break;
					// If point is fully contained in a box , no need to further check for other Hyperboxes
				}

				//*****************Expansion*************************************************//     
				else  //  If not full membership, check for best hyperbox which has max MValue and can be expanded
				{   

					boolean isExpand = false;                   
					// System.out.println("In Expansion");

					double theta = 0.3;
					double threshHold=0.0;
					for( int k = 0; k < inputDimension; k++){
						threshHold = threshHold + (maxValue(maxPointDimofhBox[k], currentPointsInDimension[k] ) -
						 minValue(minPointDimofhBox[k],currentPointsInDimension[k] ));
					}

					//Updating new min and maxpoint
					if(inputDimension*theta >= threshHold){
						isExpand = true;

						if((result > maxResult) &&  isExpand){ // selecting best hBoxexs for Expansion
							maxResult = result;
							hBoxToBeExpanded = i;
						} 
					} 
				} // end of else 
			} // end of main if (class comparison)
		} // end of main for loop

		// Setting up new min and max point of Expanding Hyperbox
		if ((hBoxToBeExpanded != -1) && (result != 1)){

			HyperBox hb1 = hBox.get(hBoxToBeExpanded);

			double[] maxPointDimofhBoxExp = new double[inputDimension]; double[] minPointDimofhBoxExp = new double[inputDimension];
			for (int ii = 0 ; ii < inputDimension; ii++){
				maxPointDimofhBoxExp[ii] = hb1.maxPoint[ii];
				minPointDimofhBoxExp [ii] = hb1.minPoint[ii];
			} 

			double [] newMin = new double [inputDimension];// temporary  min pont  //*************
			for (int ii = 0 ; ii < inputDimension ; ii++){
				newMin [ii] = minValue(minPointDimofhBoxExp[ii] , currentPointsInDimension[ii]);
			}

			hBox.get(hBoxToBeExpanded).setMinPoint(newMin, inputDimension); // setting up new minpoint for hyperbox

			double[] newMax = new double [inputDimension];
			for (int jj = 0 ; jj < inputDimension ; jj++){
				newMax [jj] = maxValue(maxPointDimofhBoxExp[jj],currentPointsInDimension[jj]);
			}

			hBox.get(hBoxToBeExpanded).setMaxPoint(newMax, inputDimension);
			classified = true;
			currentData.membershipValue = maxResult;
			hBox.get(hBoxToBeExpanded).addDataSet(currentData);
			//return classified;

		}   


		return classified;

		//hbox.membershipValue = result
} //end of membership function 


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
}//end of HiddenLayer Class
/*************************************************************************************************************************************************/


class DataSet
{
	public int dimension;
	public double [] pt;
	public int classNum;
	double membershipValue;
	DataSet(double [] dataPoint, int classNum, int dimension)
	{
		pt = new double[dimension];

		for (int i = 0; i < dimension; i ++){
			pt[i] = dataPoint [i];
		}
		this.classNum = classNum;
		this.dimension = dimension;

	}

	@Override
		public String toString(){
			return  pt +","+classNum;

		}


}

/***************************************************************************************************************************************************/

class HyperBox{
	public int dimension;
	public  double [] maxPoint;
	public  double [] minPoint;
	//	public double hBoxPoints[][dimension]
	public List<DataSet> hBoxPoints;
	public int classNum; // To define to which output  class it is connected
	public double membershipValue;


	HyperBox(){

	}
	HyperBox(double [] min, double[] max,  int classNum, int dimension){
		maxPoint = new double[dimension];
		minPoint = new double[dimension];

		// initially for point hypewrbox minpoint and maxpoint are as equal to a point coordiantes
		for (int i= 0; i<  dimension ; i++){
			this.minPoint[i] = min [i];
			this.maxPoint[i] = max[i];
		}

		this.classNum = classNum;
		hBoxPoints = new ArrayList <DataSet>();
	}

	public void setMaxPoint(double [] max, int dimension){
		for (int i= 0; i <  dimension ; i++){
			this.maxPoint[i] = max [i];

		}
	}

	public void setMinPoint(double [] min, int dimension){
		for (int i = 0; i <  dimension ; i++){
			this.minPoint[i] = min [i];

		}
	}
	/* public void addDataSet(double data[], int classNum, double membershipValue){ // the points which are belongs to hyperbozx are be added to it.
	   int length = hBoxPoints.length;s

	// Doubt, Please check
	for (int i =0; i< dimension; i++){

	hBoxPoints[length+1][i]= data[i];
	}
	}*/
	public void addDataSet(DataSet tempDataSet){ // the points which are belongs to hyperbozx are be added to it.
		hBoxPoints.add(tempDataSet);

	}


	public double[] getMaxPoint()
	{
		return this.maxPoint;
	}

	public double[] getMinPoint(){
		return this.minPoint;
	}

	public List<DataSet> gethBoxPoints()
	{
		return hBoxPoints;
	}



} // end of HYperBox Class

class OverlapTest{


	OverlapTest(){

	}
	public  List<HyperBox> checkOverlap(List<HyperBox> hBox, int inputDimension){
			//currentPointsInDimension[1] = currentData.pt.getY();
		int totalOverlap = 0; 
		for(int j = 0; j < hBox.size(); j ++)
		{
			double[] maxPointH1_Dim = new double[inputDimension]; double[] minPointH1_Dim = new double[inputDimension];

			// double[] maxPointDimofhBox = new double[inputDimension]; double[] minPointDimofhBox = new double[inputDimension];
			for (int ii = 0 ; ii < inputDimension; ii++){
				maxPointH1_Dim[ii] = hBox.get(j).maxPoint[ii];
				minPointH1_Dim[ii] = hBox.get(j).minPoint[ii];
			} 

			for(int k = 0 ; k < hBox.size(); k++)
			{
		

				if(hBox.get(j).classNum != hBox.get(k).classNum)
				{   
					double[] maxPointH2_Dim = new double[inputDimension]; double[] minPointH2_Dim = new double[inputDimension];
					double delta_old = 1; double delta_new = 0; // double [] overlapIndex = new double[inputDimension];
					
					//Arrays.fill(overlapIndex, -1);



					for (int jj = 0 ; jj < inputDimension; jj++){
						maxPointH2_Dim[jj] = hBox.get(k).maxPoint[jj];
						minPointH2_Dim[jj] = hBox.get(k).minPoint[jj];
					} 

					//  /****************************************checking overlap testcases***********************************************  //      
					int minOverlapIndex = -1;
					int overlapCase = -1;
					int contractionCase  = -1;
					boolean isOverlapInAllDim = false;		
					//System.out.println("above alldim");
				allDim:	for(int i = 0 ; i < inputDimension; i++){
							isOverlapInAllDim = false;		

						//case1
						if( minPointH1_Dim[i] < maxPointH2_Dim[i] && maxPointH2_Dim[i]  < maxPointH1_Dim[i] && maxPointH1_Dim[i] < maxPointH2_Dim[i])
						{
							delta_new = minValue(maxPointH1_Dim[i] - minPointH2_Dim[i], delta_old);
							overlapCase =1;
							isOverlapInAllDim = true;
							//System.out.println(" In  case1-> Overlap between hbox" + j + " hBox" + k + 
								//	" between class - " +hBox.get(j).classNum  + " and class - " + hBox.get(k).classNum +
								//	" In dimension "  + i);
						} 

						//case 2
						else if (minPointH2_Dim[i] < minPointH1_Dim[i] && minPointH1_Dim[i] < maxPointH2_Dim[i] && maxPointH2_Dim[i] < maxPointH1_Dim[i])
						{
								delta_new = minValue(maxPointH2_Dim[i] - minPointH1_Dim[i], delta_old );
								overlapCase =2;
								isOverlapInAllDim = true;
							//System.out.println(" In case 2-> Overlap between hbox" +j+ " hBox"+ k +
								//	" between class - " +hBox.get(j).classNum  + " and class - " + hBox.get(k).classNum +
								//	" In dimension "  + i);
						}

						//case 3
						else if(minPointH1_Dim[i] < minPointH2_Dim[i] && minPointH2_Dim[i] < maxPointH2_Dim[i] && maxPointH2_Dim[i] < maxPointH1_Dim[i])
						{
								delta_new = minValue(minValue(maxPointH2_Dim[i] - minPointH1_Dim[i], maxPointH1_Dim[i]- minPointH2_Dim[i]), delta_old);
								overlapCase =3;
								isOverlapInAllDim = true;
							//System.out.println(" In case 3->Overlap between hbox" +j+ " hBox"+ k +
									///" between class - " +hBox.get(j).classNum  + " and class - " + hBox.get(k).classNum+
									//" In dimension "  + i);
						}

						//case 4
						else if(minPointH2_Dim[i] < minPointH1_Dim[i] && minPointH1_Dim[i] < maxPointH1_Dim[i] && maxPointH1_Dim[i] < maxPointH2_Dim[i])
						{
								delta_new = minValue(minValue(maxPointH1_Dim[i] - minPointH2_Dim[i], maxPointH2_Dim[i] - minPointH1_Dim[i]) , delta_old);
								overlapCase =4;
								isOverlapInAllDim = true;
							//System.out.println(" In case 4-> Overlap between hbox" +j+ " hBox"+ k +
									//" between class - " +hBox.get(j).classNum  + " and class - " + hBox.get(k).classNum+
									//" In dimension "  + i + "delta = " + delta_new);

						}

						if ((delta_old - delta_new) > 0){ // if overlap not exist in one particualr dimension, stop the process
							delta_old = delta_new;
							//overlapIndex[i] =1; // this has the minimum overlap among all, need to do contraction in this dimension
							minOverlapIndex= i;
							contractionCase = overlapCase; // save particular minoverlap and case number for cnotraction purpose
							

						}
						//TODO. Confusion about how to break, 
						 
						if(!isOverlapInAllDim){
								//System.out.println("breaking loop");
								break allDim; // If atleast one dimension don't have overlap - stop for further check

						}	
					

						}// end of inner forloop
						
						if (isOverlapInAllDim) // if all dimension has overlap, go for contraction
						{  totalOverlap++;
							hBox = contraction (hBox, j, k, minPointH1_Dim, maxPointH1_Dim, minPointH2_Dim, maxPointH2_Dim, 
												contractionCase, minOverlapIndex); // 
						}
				} // end of if loop
			}// end of 2nd for loop

		} //end of  main for loop
		System.out.println("Total Overlap Created ************************** " + totalOverlap);
		return hBox;
	}// end of check overlap method * /

	/************************************************Contraction Method to eliminate overlap***********************************************/

	public static List <HyperBox> contraction (List<HyperBox> hBox, int j, int k, double [] minPointH1_Dim, double [] maxPointH1_Dim, double []minPointH2_Dim, double [] 
	                                     maxPointH2_Dim, int contractionCase, int minOverlapIndex){

			double newMaxofH1, newMinofH1, newMaxofH2, newMinofH2; // in selected dimension


			System.out.println("In contraction - " + " updation done to " + j + "  "+ k  + " hBoxes " + "on dimension" + minOverlapIndex);
			switch(contractionCase){
			    case 1 :
			       		newMaxofH1 = (maxPointH1_Dim[minOverlapIndex] + minPointH2_Dim[minOverlapIndex])/2;
						newMinofH2 = newMaxofH1;
						hBox.get(j).maxPoint[minOverlapIndex] = newMaxofH1;
						hBox.get(k).minPoint[minOverlapIndex] = newMinofH2;
							System.out.println("newMaxofh1 = newMinh2 - " + newMaxofH1 + "old value- " +
											 maxPointH1_Dim[minOverlapIndex] + "  " +
										 	minPointH2_Dim[minOverlapIndex]);

			       		break; 
			    case 2 :
			  			newMaxofH2 = (maxPointH2_Dim[minOverlapIndex] + minPointH1_Dim[minOverlapIndex])/2;
			  			newMinofH1 = newMaxofH2;
			  			hBox.get(j).minPoint[minOverlapIndex] = newMinofH1;
						hBox.get(k).maxPoint[minOverlapIndex] = newMaxofH2;

			      		 break; 
			   	case 3 :
			       		if((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim [minOverlapIndex]) < maxPointH1_Dim[minOverlapIndex] - 							minPointH2_Dim[minOverlapIndex]){
			       			newMinofH1 = maxPointH2_Dim[minOverlapIndex];
			       			hBox.get(j).minPoint[minOverlapIndex] = newMinofH1;
			       		}
			       		if ((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim[minOverlapIndex]) > (maxPointH1_Dim[minOverlapIndex] - 							minPointH2_Dim[minOverlapIndex])){
			       			newMaxofH1 = minPointH2_Dim[minOverlapIndex];
			       			hBox.get(j).maxPoint[minOverlapIndex] = newMaxofH1;
			       		}
			       		break; 
			    case 4 :
			       		if ((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim[minOverlapIndex]) < (maxPointH1_Dim[minOverlapIndex] -
						 minPointH2_Dim[minOverlapIndex])){
							newMaxofH2 = minPointH1_Dim[minOverlapIndex];
							hBox.get(k).maxPoint[minOverlapIndex] = newMaxofH2;			       			
			       		}
			       		if((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim[minOverlapIndex]) > (maxPointH1_Dim[minOverlapIndex] - 
						minPointH2_Dim[minOverlapIndex])){
			       				newMinofH2 = maxPointH1_Dim[minOverlapIndex];
			       				hBox.get(k).minPoint[minOverlapIndex] = newMinofH2;
			       		}
			       break; 
			    default : //Optional
			      
			}
		
			return hBox;
	}

	public static double minValue(double a, double b){
		if(a < b )
			return a;
		else
			return b;
	}
}  
