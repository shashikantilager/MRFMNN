


package cgl.imr.samples.FMNN_Testing;
import java.util.List;

import cgl.imr.base.Key;
import cgl.imr.base.ReduceOutputCollector;
import cgl.imr.base.ReduceTask;
import cgl.imr.base.SerializationException;
import cgl.imr.base.TwisterException;
import cgl.imr.base.Value;
import cgl.imr.base.impl.JobConf;
import cgl.imr.base.impl.ReducerConf;
import cgl.imr.types.BytesValue;
import cgl.imr.types.DoubleVectorData;
import cgl.imr.types.DoubleArray;
import cgl.imr.types.IntValue;
import java.util.*;

public class FMNN_TestingReduceTask implements ReduceTask {

	public void close() throws TwisterException {

	}

	public void configure(JobConf jobConf, ReducerConf reducerConf)
		throws TwisterException {
		}

	public void reduce(ReduceOutputCollector collector, Key key, 
			List<Value> values) throws TwisterException {

	
		int          numData ;
        int count = 0;
		String       dummyKey = "1";
		String       keyStr     = "";
		DoubleVectorData  tempData = new DoubleVectorData();		
        //ArrayList <> = new List<>
  		List<MapClassData> mapDataObj = new ArrayList<MapClassData>();
        double[][] mapOutput;
        double[][] reduceOutPut; 

		if (values.size() <= 0) {
			throw new TwisterException("Reduce input error no values.");
		}

		/* convert key to string to compare it with -1 */
		try {

			byte [] kbytes = key.getBytes();
			keyStr = new String(kbytes);

		}catch(SerializationException e) {

			System.out.println("Exception @ Reducer- SerializationException :");
		} 

		System.out.println("@ Reducer-  Key -" + keyStr);
		try { 

			int numMapTasks = values.size();
			 int totalTestPoints =0;
	
			
			if ( keyStr.equals(dummyKey) ) {

                    //System.out.println("num of map taskes are "+ numMapTasks);

				System.out.println("@Reduceer *******"+ " numMapTasks - " + numMapTasks);

				for (int  i = 0; i < numMapTasks; i++) {


					BytesValue val = (BytesValue) values.get(i);

                	tempData.fromBytes(val.getBytes());
                	totalTestPoints = tempData.getNumData();
					mapOutput = tempData.getData(); // 2D doubleArray
					//for (int j =0; j< totalTestPoints; j++)
					//System.out.println("Inside data collector- " + "Mapper" + i + "memvalue -" + mapOutput[j][0] + " classNum= " + mapOutput[j][1]);
					MapClassData newMapData = new MapClassData(mapOutput, totalTestPoints);
	
					mapDataObj.add(newMapData);
					//System.out.println(" @ Reducer -Inside for - "+ "totalTestPoints- " + totalTestPoints + " Mapper " + i );

				
				} // end for
				reduceOutPut = new double[totalTestPoints][2];
				System.out.println("@ Reducer " + " numMapTasks- " + numMapTasks + "  ===  mapData size- " + mapDataObj.size());
				
				for (int j =0; j< totalTestPoints; j++){
					double memvalue = -1;
					int classNum = -1;
					for (int k = 0; k< mapDataObj.size(); k++){
						//System.out.println("Inside data copy reducer- total class objects " + mapDataObj.size());

						if (mapDataObj.get(k).mapData[j][0] > memvalue) // this will give memvalue for kth hyperbox anfd jth test point 
						{
								System.out.println("Inside data copy reducer- " + "Printing object memvalue- " + mapDataObj.get(k).mapData[j][0]  +" old value- " +memvalue);

							memvalue = mapDataObj.get(k).mapData[j][0]; // update map value
							classNum = (int)mapDataObj.get(k).mapData[j][1]; // update classvalue
						}
					}
					reduceOutPut[j][0] = memvalue;
					reduceOutPut[j][1] = classNum;

				}
				//System.out.println("**********************************output at Reducer *************************");
				for (int i =0 ;i < totalTestPoints; i++)
				///System.out.println("\n"+ "@ REduceer *******- "+ " MValue - " + reduceOutPut[i][0] + 
						//" ClassNum- "+ reduceOutPut[i][1]);
		
				 
				 tempData = new DoubleVectorData(reduceOutPut, totalTestPoints,2); // sending final (m,c value to combiner)
					//System.out.println("@Reducer - reducer output " + "  size- " + reduceOutPut.length);
				 collector.collect(key, new BytesValue(tempData.getBytes()));

			}

			

		} catch (SerializationException e) {
			throw new TwisterException(e);
		}
	}// end of reduce


	public static int reducerBestResult (double [][] membershipValVector){
		
		double maxMembershipVal=0; int index=-1;
		for(int i =0 ; i< membershipValVector.length; i++){
			if (membershipValVector[i][0] > maxMembershipVal){
				maxMembershipVal = membershipValVector[i][0];			
				index = i;
			}
		}
		
		return index;
	}
} // end of class


class MapClassData{
	int numData;
	double [][] mapData;
	public MapClassData( double[][] mapData,int numData){
		this.numData = numData;
		this.mapData = new double[numData][2];
		this.mapData = mapData;
	}

	public double[][] getMapData(){
		return this.mapData;
	}
	public int getNumData(){
		return this.numData;
	}
}
