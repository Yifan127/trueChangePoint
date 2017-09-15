package classifiers;

import moa.classifiers.core.driftdetection.AbstractChangeDetector;
import moa.classifiers.drift.SingleClassifierDrift;
import moa.classifiers.trees.HoeffdingTree;

public class DriftClassifier {
	
	public SingleClassifierDrift getSingleClassifierDrift(AbstractChangeDetector detector){
		SingleClassifierDrift learner = new SingleClassifierDrift();
		HoeffdingTree baseLearner = this.getHoeffdingTree();
		learner.baseLearnerOption.setCurrentObject(baseLearner);
		learner.driftDetectionMethodOption.setCurrentObject(detector);
		learner.prepareForUse();
		
		return learner;
	}
	
	public HoeffdingTree getHoeffdingTree(){
		HoeffdingTree learner = new HoeffdingTree();
		learner.getOptions().resetToDefaults();
		learner.prepareForUse();
		
		return learner;
	}

}
