package streams;

import moa.streams.InstanceStream;
import moa.streams.generators.SEAGenerator;

public class SEAStreamsGenerator {
	
	private SEAGenerator stream;
	private int seed = 791;
	
	public InstanceStream getStream(int inputFunction, int numInstances, 
			int noisePerc, boolean balance){
		//original stream
		stream = new SEAGenerator();
		stream.getOptions().resetToDefaults();
		stream.functionOption.setValue(inputFunction);
		stream.instanceRandomSeedOption.setValue(seed);
		stream.numInstancesConcept.setValue(numInstances);
		stream.noisePercentageOption.setValue(noisePerc);
		stream.balanceClassesOption.setValue(balance);
		stream.prepareForUse();
		
		return stream;
	}

}
