
package cgl.imr.samples.FMNN_Testing;

import java.io.IOException;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import org.safehaus.uuid.UUIDGenerator;

import cgl.imr.base.TwisterModel;
import cgl.imr.base.TwisterMonitor;
import cgl.imr.base.impl.JobConf;
import cgl.imr.client.TwisterDriver;
import cgl.imr.types.DoubleVectorData;
import cgl.imr.types.DoubleArray;


public class FMNN_TestingMapReduce {



	/**
	 * Main program to run K-means clustering.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static String outfilePrefix;

	DoubleVectorData finalData;
	public static void main(String[] args) throws Exception {

		if (args.length != 6) {
			String errorReport = "Arguments Incorrect Exception\n" + 
					     "Please Provide Proper Commmand Line Arguments\n"
					    ;
			System.out.println(errorReport);
			System.exit(0);
		}
		String partitionFile = args[0];
		int dimension = Integer.parseInt(args[1]);
		
		String testingDataFile = args[2];
		int numMapTasks = Integer.parseInt(args[3]);
		int numReduceTasks = Integer.parseInt(args[4]);
		outfilePrefix = args[5];

		FMNN_TestingMapReduce client;
		
		try {
			client = new FMNN_TestingMapReduce();
			double beginTime = System.currentTimeMillis();
			client.driveMapReduce(partitionFile,testingDataFile, numMapTasks,numReduceTasks);
			double endTime = System.currentTimeMillis();
			System.out.println("------------------------------------------------------");
			System.out.println("FMNN MapReduce Job Took "
					+ (endTime - beginTime) / 1000 + " seconds.");
			System.out.println("------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private UUIDGenerator uuidGen = UUIDGenerator.getInstance();

	public void driveMapReduce(String partitionFile, String testingDataFile, int numMapTasks, int numReduceTasks) throws Exception {

		long beforeTime = System.currentTimeMillis();
		String inputpath  = "input/";
		inputpath= inputpath +testingDataFile; 
		DoubleVectorData testingData = new DoubleVectorData();
		try {

                      	testingData.loadDataFromTextFile(inputpath);
                        
		   } 
		 catch (IOException e){
		              e.printStackTrace();                          
		 }
		// JobConfigurations
		JobConf jobConf = new JobConf("FMNN_Test-map-reduce"
				+ uuidGen.generateRandomBasedUUID());
		jobConf.setMapperClass(FMNN_TestingMapTask.class);
 		
		jobConf.setReducerClass(FMNN_TestingReduceTask.class);
		jobConf.setCombinerClass(FMNN_TestingCombiner.class);
		jobConf.setNumMapTasks(numMapTasks);
		jobConf.setNumReduceTasks(numReduceTasks);

		TwisterModel driver = new TwisterDriver(jobConf);
		driver.configureMaps(partitionFile);

		

	
		int loopCount = 0;
		TwisterMonitor monitor = null;

		@SuppressWarnings("unused")
	
		boolean complete = false;
		
			monitor = driver.runMapReduceBCast(testingData);
			monitor.monitorTillCompletion();

		
			finalData = new DoubleVectorData();
			 finalData = ((FMNN_TestingCombiner) driver.getCurrentCombiner()).getResults();
			 int totalTestpoints = finalData.getNumData();
			 double [][] data = finalData.getData();
			System.out.println("*******************Execution Completed - ************ ");
			//for (int i = 0; i < totalTestpoints; i ++){
			///	System.out.println("Point " + i + " has membershipValue - " + data[i][0] + " classNum- " + data[i][1]);
			//}

			double timeInSeconds = ((double) (System.currentTimeMillis() - beforeTime)) / 1000;
			
			System.out.println("@ Main Driver Code- > Total Time for FMNN Classifier testing : " + timeInSeconds);
			
			String path = "/root/t/twister/samples/FMNN_Testing/bin/output/";
			String outFileFullpath = path +  outfilePrefix;

			File file = new File(outFileFullpath);
			
			PrintWriter printWriter = null;
			try
			{
		   
		    	printWriter = new PrintWriter(file);
		    	printWriter.println(totalTestpoints); // first line in file is totaltestpoints
		    	printWriter.println(3); //veclen
		        for (int j =0; j <  totalTestpoints ; j ++) {
					String outPut = "";
		 	 					
 		 	    	outPut = outPut+ j + "," + data[j][0]+","+ data[j][1];
 		 	   
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
			System.out.println("******************Writing to the file Completed - ************ ");
		

		// Close the TwisterDriver. This will close the broker connections and
		driver.close();
	}
}
