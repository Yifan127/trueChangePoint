package driftStreams;


import moa.streams.InstanceStream;
import moa.streams.LinearConceptDriftStream;
import moa.streams.LogConceptDriftStream;
import moa.streams.QuadraticConceptDriftStream;
import moa.streams.SigmoidConceptDriftStream;

public class DriftStreams {
	
	private LinearConceptDriftStream linearDrift;
	private LogConceptDriftStream logDrift;
	private QuadraticConceptDriftStream quadraticDrift;
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
	
	public QuadraticConceptDriftStream generateQuadraticDriftStream(InstanceStream inputStream, 
			InstanceStream driftStream, int center, int width){
		//drift control
		quadraticDrift = new QuadraticConceptDriftStream();
		quadraticDrift.streamOption.setCurrentObject(inputStream);
		quadraticDrift.driftstreamOption.setCurrentObject(driftStream);
		quadraticDrift.positionOption.setValue(center);
		quadraticDrift.widthOption.setValue(width);
		quadraticDrift.prepareForUse();
		
		return quadraticDrift;
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
