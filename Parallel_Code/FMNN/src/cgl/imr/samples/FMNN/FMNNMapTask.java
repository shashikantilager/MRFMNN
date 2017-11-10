/*
 @author- Shashikant Ilager
 */

package cgl.imr.samples.FMNN;

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
import cgl.imr.types.DataSet;
import cgl.imr.types.HyperBox;
import java.io.FileReader;
import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;
import cgl.imr.message.MapperRequest;
import cgl.imr.types.IntValue;


public class FMNNMapTask implements MapTask {

	private  FileData fileData;
	private  List<DataSet> dataSet;
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
		try {
			DataSet dataobj = new DataSet();
			dataSet =  new ArrayList<DataSet>();
			System.out.println ("In configure method Before Data Load");
			
			dataSet = dataobj.loadDataSetfromFile(fileData.getFileName());

			//System.out.println("After Data load Total points read= " + dataSet.size());
			int totalpoints = 0;
			/*System.out.println("In configure. After Data load");
		      for ( DataSet i : this.dataSet){
					//System.out.print(" dimension = " + i.dimension + " Points are = >  " );
			      	for (int ii =0 ; ii < i.dimension ; ii++  )
			      	{
			      	System.out.print(i.pt[ii] + " ");	
			      	}
			      	System.out.println( " " + i.classNum +"\n");
					
					totalpoints += 1;
		      }
		      System.out.println(" total points  = " + totalpoints);*/


		} catch (Exception e) {
			throw new TwisterException(e);
		}
	}

	

	/****************************************Map function for the FMNN algorithm****************************************************/
	
	public void map(MapOutputCollector collector, Key key, Value val) //TODO , 
			throws TwisterException {

		
		 // dimension
			int dimension;
			FMNNMapTask fmnnObj = new FMNNMapTask(); 
			try{
					IntValue intObj = new IntValue();
					intObj.fromBytes(val.getBytes());
					 dimension = intObj.getVal();
					System.out.println("At Mapper- dimension -" + dimension);
			}
			catch (SerializationException  e) {
					throw new TwisterException(e);
			}

			 List<HyperBox> hBox =  fmnnObj.classifyData(fmnnObj,dataSet,dimension); // function call to classify the data
			
			 System.out.println("Im Map Method- MapID- " + MapId + " Number of hyperboxex created = " + hBox.size() + " with partition file  = " 
			 				    + fileData.getFileName());
			 
		
				
			//try 

			{      
			     
			     /* for(int i=0; i< hBox.size(); i++){
				      //System.out.println("classNum = " + hBox.get(i).classNum+"  hBOx" + i +" maxpoint=  " +hBox.get(i).getMaxPoint().toString() + "minpoint= "+ hBox.get(i).getMinPoint().toString());
			      	
				      	System.out.println(" hBox" +i + " of class - " + hBox.get(i).classNum +  " of MapId - " + MapId + " Total Points =  " + hBox.get(i).hBoxPoints.size() );
				      	System.out.println(" MinPoint - " + hBox.get(i).minPoint[0] + "," + hBox.get(i).minPoint[1] );
				      	System.out.println(" MaxPoint - " + hBox.get(i).maxPoint[0] + "," + hBox.get(i).maxPoint[1] );
				     
				      	//for(DataSet dataPoint : hBox.get(i).hBoxPoints){
						//	System.out.println("x= " + dataPoint.pt[0] + " y= " +dataPoint.pt[1] + "\t"+" Membershipvalue = " 
												+ dataPoint.membershipValue+ "\t" + " Class= " +dataPoint.classNum );
			      	     //}
			      	    // System.out.println("total points in hbox " + i + "  are " + totalpointsinhbox);
			   
			      	}*/
   					
      		}//end of main for loop
     		 hBox = fmnnObj.checkOverlap(hBox, dimension , MapId);

     		 /*****************************************Creating a dynamic partiton file for testing purpose************************/
     		 	
                     String path=System.getenv("TWISTER_HOME");
                     path += "/samples/" + "FMNN/" + "bin/" +"dynamicFiles/" + "Ctype54d_HBox_";
                     System.out.println(path);
                     String file_prefix = "Map" + MapId;
                     String fullpath = path+ file_prefix;

                  	File file = new File(fullpath);

			        PrintWriter printWriter = null;

			        try
			        {

						  printWriter = new PrintWriter(file);
				          for(int i=0; i< hBox.size(); i++){

				          		String outPut = "";
				 		 						
				 		 	    for (int j =0; j < dimension; j++)
				 		 	    {
				 		 	    	outPut= outPut+ hBox.get(i).minPoint[j] + ",";
				 		 	    }
				 		 	    for (int j = 0; j < dimension; j++)
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
				
			  				
	 //catch (SerializationException e) {
			//throw new TwisterException(e);
		//}
	 // end of map method
}

/////
/*****************************************Classsify Data Method******************************************************************************/
		private  List <HyperBox> classifyData(FMNNMapTask fmnnObj,List<DataSet> dataSet,int n)
		{	 
			List<HyperBox> hBox;
			boolean hBoxStatusOfClass;
  			int inputDimension;

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
				else
				{
					isMember = checkMembership(hBox, i, inputDimension);
					if(!isMember)
					{
						HyperBox newHBox2 = new HyperBox(i.pt , i.pt , i.classNum , inputDimension);// constructor will set min and max point of new hyperbox
						i.membershipValue =1; // for point hyperbox, it will have full membership value
						newHBox2.addDataSet(i);
						hBox.add(newHBox2);
						//System.out.println("*************On fail with checkMembership*************************************");

					}

				}

		      } 
		       return hBox;
    	}

/*************************************Check Membership function**************************************************************************/

public boolean checkMembership(List<HyperBox> hBox, DataSet currentData, int inputDimension)
 {
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
				//if(result != 0 && result != 1) //check for Exapansion,(result ==1)=> if point allready contained within hyperbox, no need to expand
				else  //  If not full membership, check for best hyperbox which has max MValue and can be expanded
				{   

					boolean isExpand = false;                   
					// System.out.println("In Expansion");

					double theta = 0.3;
					double threshHold=0.0;
					for( int k = 0; k < inputDimension; k++){
						threshHold = threshHold + (maxValue(maxPointDimofhBox[k], currentPointsInDimension[k] ) - minValue(minPointDimofhBox[k],currentPointsInDimension[k] ));
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


/**************************************************Overlap Testing function*****************************************************************/

    public  List<HyperBox> checkOverlap(List<HyperBox> hBox, int inputDimension, int mapNo){
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

  /************************************************************Contraction*****************************************************************************/

  public static List <HyperBox> contraction (List<HyperBox> hBox, int j, int k, double [] minPointH1_Dim, double [] maxPointH1_Dim, double []minPointH2_Dim, double [] 
	                                     maxPointH2_Dim, int contractionCase, int minOverlapIndex){

			double newMaxofH1, newMinofH1, newMaxofH2, newMinofH2; // in selected dimension

			//TO.Do selecting a particular dimension for contraction
			//Presently updating in 0 dimension
			//System.out.println("In contraction - " + " updation done to " + j + "  "+ k  + " hBoxes " + "on dimension" + minOverlapIndex);
			switch(contractionCase){
			    case 1 :
			       		newMaxofH1 = (maxPointH1_Dim[minOverlapIndex] + minPointH2_Dim[minOverlapIndex])/2;
						newMinofH2 = newMaxofH1;
						hBox.get(j).maxPoint[minOverlapIndex] = newMaxofH1;
						hBox.get(k).minPoint[minOverlapIndex] = newMinofH2;
							//System.out.println("newMaxofh1 = newMinh2 - " + newMaxofH1 + "old value- " + maxPointH1_Dim[minOverlapIndex] + "  " +
											 	//minPointH2_Dim[minOverlapIndex]);


			       		break; 
			    case 2 :
			  			newMaxofH2 = (maxPointH2_Dim[minOverlapIndex] + minPointH1_Dim[minOverlapIndex])/2;
			  			newMinofH1 = newMaxofH2;
			  			hBox.get(j).minPoint[minOverlapIndex] = newMinofH1;
						hBox.get(k).maxPoint[minOverlapIndex] = newMaxofH2;

			      		 break; 
			   	case 3 :
			       		if((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim [minOverlapIndex]) < maxPointH1_Dim[minOverlapIndex] - minPointH2_Dim[minOverlapIndex]){
			       			newMinofH1 = maxPointH2_Dim[minOverlapIndex];
			       			hBox.get(j).minPoint[minOverlapIndex] = newMinofH1;
			       		}
			       		if ((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim[minOverlapIndex]) > (maxPointH1_Dim[minOverlapIndex] - minPointH2_Dim[minOverlapIndex])){
			       			newMaxofH1 = minPointH2_Dim[minOverlapIndex];
			       			hBox.get(j).maxPoint[minOverlapIndex] = newMaxofH1;
			       		}
			       		break; 
			    case 4 :
			       		if ((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim[minOverlapIndex]) < (maxPointH1_Dim[minOverlapIndex] - minPointH2_Dim[minOverlapIndex])){
							newMaxofH2 = minPointH1_Dim[minOverlapIndex];
							hBox.get(k).maxPoint[minOverlapIndex] = newMaxofH2;			       			
			       		}
			       		if((maxPointH2_Dim[minOverlapIndex] - minPointH1_Dim[minOverlapIndex]) > (maxPointH1_Dim[minOverlapIndex] - minPointH2_Dim[minOverlapIndex])){
			       				newMinofH2 = maxPointH1_Dim[minOverlapIndex];
			       				hBox.get(k).minPoint[minOverlapIndex] = newMinofH2;
			       		}
			       break; 
			    //You can have any number of case statements.
			    default : //Optional
			       //Statements
			}
		
			return hBox;
	}// end of contraction process



  	private static double maxValue(double a, double b){
		if(a > b )
		  return a;
		else
		  return b;
    	}

    	private static double minValue(double a, double b){
	      if(a < b )
		  return a;
	      else
		return b;
    	}

} // end of class




