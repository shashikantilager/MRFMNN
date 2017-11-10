


package cgl.imr.samples.FMNN_Testing;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cgl.imr.base.Combiner;
import cgl.imr.base.Key;
import cgl.imr.base.SerializationException;
import cgl.imr.base.TwisterException;
import cgl.imr.base.Value;
import cgl.imr.base.impl.JobConf;
import cgl.imr.types.BytesValue;
import cgl.imr.types.DoubleVectorData;
import cgl.imr.types.DoubleArray;
import cgl.imr.types.StringKey;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Convert the set of bytes representing the Value object into a ByteValue.
 * 
 * @author Jaliya Ekanayake (jaliyae@gmail.com)
 * 
 */
public class FMNN_TestingCombiner implements Combiner {

	//DoubleArray results;
	//DoubleVectorData edgeList;
    DoubleVectorData results;
	public FMNN_TestingCombiner() {
		//results = new DoubleArray();
        results   = new DoubleVectorData();
		//edgeList = new DoubleVectorData();
	}

	public void close() throws TwisterException {

	}

	
	 // Combines the reduce outputs to a single value.
	public void combine(Map<Key, Value> keyValues) throws TwisterException {
	

		String dummyKey = "1";
		
		int temp = -1;
		System.out.println("$$$$$$$$$$$$$$$In combiner $$$$$$ ");
		Key k1  = new StringKey("1");
		//DoubleVectorData tempNodes = new DoubleVectorData();
		//DoubleVectorData tempEdgeList = new DoubleVectorData();
		DoubleVectorData tempData = new DoubleVectorData();
		Set<Key> keySet = keyValues.keySet();
		
		for (Key key : keySet) {
		
			try {

				byte[] bytes = key.getBytes();

				String s = new String(bytes);

		
				if( s.equals(dummyKey)) {
					System.out.println(" mapper is over *** ");
					//continue;
				}
				
				BytesValue val = (BytesValue) keyValues.get(key);

				tempData.fromBytes(val.getBytes());

            	this.results = tempData;//reducerData;

			} catch (SerializationException e) {
				throw new TwisterException(e);
			}
		}
	}

	public void configure(JobConf jobConf) throws TwisterException {
		// TODO Auto-generated method stub

	}

	public DoubleVectorData getResults() {
		return results;
	}
}
