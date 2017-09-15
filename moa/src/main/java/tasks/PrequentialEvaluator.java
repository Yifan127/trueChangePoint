package tasks;


import moa.classifiers.drift.SingleClassifierDrift;
import moa.streams.InstanceStream;
import moa.tasks.EvaluatePrequential;

public class PrequentialEvaluator {	
	
	public void prequential(SingleClassifierDrift learner, InstanceStream stream, 
			int sampleFrequency, int memFrequency, int instanceLimit, String path, String destFile){
		EvaluatePrequential task = new EvaluatePrequential();
		task.evaluatorOption.resetToDefault();
		task.streamOption.setCurrentObject(stream);
		task.learnerOption.setCurrentObject(learner);
		task.sampleFrequencyOption.setValue(sampleFrequency);
		task.memCheckFrequencyOption.setValue(memFrequency);
		task.instanceLimitOption.setValue(instanceLimit);
		task.dumpFileOption.setValue(path + "\\" + destFile);
		task.prepareForUse();
		task.doTask();
	}
}
