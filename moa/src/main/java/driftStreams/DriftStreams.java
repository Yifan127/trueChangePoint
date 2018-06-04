package driftStreams;


import moa.streams.InstanceStream;
import moa.streams.LinearConceptDriftStream;
import moa.streams.LogConceptDriftStream;
import moa.streams.SigmoidConceptDriftStream;

public class DriftStreams {
	
	private LinearConceptDriftStream linearDrift;
	private LogConceptDriftStream logDrift;
	private SigmoidConceptDriftStream sigmoidDrift;
	
	public LinearConceptDriftStream generateLinearDriftStream(InstanceStream inputStream, 
			InstanceStream driftStream, int center, int width){
		//drift control
		linearDrift = new LinearConceptDriftStream();
		linearDrift.streamOption.setCurrentObject(inputStream);
		linearDrift.driftstreamOption.setCurrentObject(driftStream);
		linearDrift.positionOption.setValue(center);
		linearDrift.widthOption.setValue(width);
		linearDrift.prepareForUse();
		
		return linearDrift;
	}
	
	public LogConceptDriftStream generateLogDriftStream(InstanceStream inputStream, 
			InstanceStream driftStream, int center, int width){
		//drift control
		logDrift = new LogConceptDriftStream();
		logDrift.streamOption.setCurrentObject(inputStream);
		logDrift.driftstreamOption.setCurrentObject(driftStream);
		logDrift.positionOption.setValue(center);
		logDrift.widthOption.setValue(width);
		logDrift.prepareForUse();
		
		return logDrift;
	}
	

	public SigmoidConceptDriftStream generateSigmoidDriftStream(InstanceStream inputStream, 
			InstanceStream driftStream, int center, int width){
		//drift control
		sigmoidDrift = new SigmoidConceptDriftStream();
		sigmoidDrift.streamOption.setCurrentObject(inputStream);
		sigmoidDrift.driftstreamOption.setCurrentObject(driftStream);
		sigmoidDrift.positionOption.setValue(center);
		sigmoidDrift.widthOption.setValue(width);
		sigmoidDrift.prepareForUse();
		
		return sigmoidDrift;
	}
}
