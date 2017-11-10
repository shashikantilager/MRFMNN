
package cgl.imr.samples.FMNN;

import java.io.IOException;

import org.safehaus.uuid.UUIDGenerator;

import cgl.imr.base.TwisterModel;
import cgl.imr.base.TwisterMonitor;
import cgl.imr.base.impl.JobConf;
import cgl.imr.client.TwisterDriver;
import cgl.imr.types.DoubleVectorData;
import cgl.imr.types.IntValue;


public class FMNNMapReduce {

	

	/**
	 * Main program to run K-means clustering.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			String errorReport = "Arguments Incorrect Exception\n" + 
					     "Please Provide Proper Commmand Line Arguments\n"
					    ;
			System.out.println(errorReport);
			System.exit(0);
		}
		String partitionFile = args[0];
		int numMapTasks = Integer.parseInt(args[1]);
			int dimension = Integer.parseInt(args[2]);
			IntValue intObj = new IntValue(dimension);

		FMNNMapReduce client;
		try {
			client = new FMNNMapReduce();
			double beginTime = System.currentTimeMillis();

			client.driveMapReduce(partitionFile, numMapTasks, intObj);

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

	public void driveMapReduce(String partitionFile, int numMapTasks,
			IntValue intObj) throws Exception {

		long beforeTime = System.currentTimeMillis();
		int numReducers = 0; // Initially considering no reducer

		// JobConfigurations
		JobConf jobConf = new JobConf("FMNN-map-reduce"
				+ uuidGen.generateRandomBasedUUID());
		jobConf.setMapperClass(FMNNMapTask.class);
 		
		//jobConf.setReducerClass(KMeansReduceTask.class);
		//jobConf.setCombinerClass(KMeansCombiner.class);
		jobConf.setNumMapTasks(numMapTasks);
		jobConf.setNumReduceTasks(0);
		//jobConf.setNumReduceTasks(numReducers);

		TwisterModel driver = new TwisterDriver(jobConf);
		driver.configureMaps(partitionFile);

		

		//double totalError = 0;
		int loopCount = 0;
		TwisterMonitor monitor = null;

		@SuppressWarnings("unused")
		// Use this with the while loop.
		boolean complete = false;
		
			monitor = driver.runMapReduceBCast(intObj);
			monitor.monitorTillCompletion();

		
		double timeInSeconds = ((double) (System.currentTimeMillis() - beforeTime)) / 1000;
		
		System.out.println("@ Main Driver Code- > Total Time for FMNN Classifier : " + timeInSeconds);
		System.out.println("@ Main Driver Code- > Total loop count : " + (loopCount));
		// Close the TwisterDriver. This will close the broker connections and
		driver.close();
	}
}
