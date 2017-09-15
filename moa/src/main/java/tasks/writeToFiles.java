package tasks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import moa.streams.InstanceStream;
import moa.tasks.WriteStreamToARFFFile;

public class writeToFiles {
	
	public void writeStreamToFile(InstanceStream stream, String path, String destFile, int maxInstances){	
		WriteStreamToARFFFile task = new WriteStreamToARFFFile();
		task.getOptions().resetToDefaults();
		task.streamOption.setCurrentObject(stream);
		task.arffFileOption.setValue(path + "\\" + destFile);
		task.suppressHeaderOption.unset();
		task.maxInstancesOption.setValue(maxInstances);
		task.prepareForUse();
		task.doTask();
	}
	
	public Object writeDriftToFile(TreeMap<Integer, Double> driftPoints, String path, String destFile){
    	if (destFile != null){	
			try{
				Writer w = new BufferedWriter(new FileWriter(path + "\\" + destFile));
				Iterator<Entry<Integer, Double>> i = driftPoints.entrySet().iterator();
			    while (i.hasNext()) {
			        Map.Entry<Integer, Double> pair = (Map.Entry<Integer, Double>)i.next();
			        w.write(pair.getKey().toString() + " , " + pair.getValue().toString());
			        w.write("\n");
			        i.remove(); // avoids a ConcurrentModificationException
			    }
                w.close();             
			} catch (Exception ex) {
                throw new RuntimeException(
                        "Failed writing to file " + path + destFile, ex);
            }
            return "Drift points written to CSV file " + path + destFile;
		}
		throw new IllegalArgumentException("No destination file to write to.");
    }

}
