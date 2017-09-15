package scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import changeDetector.ChangeDetector;
import classifiers.DriftClassifier;
import moa.classifiers.core.driftdetection.*;
import moa.classifiers.drift.SingleClassifierDrift;
import moa.streams.InstanceStream;
import moa.streams.LinearConceptDriftStream;
import streams.SEAStreamsGenerator;
import tasks.PrequentialEvaluator;
import tasks.writeToFiles;
import utils.Constants;
import utils.CreateDirectory;
import driftStreams.DriftStreams;

public class evaluateSEALinear {
	private static int center = Constants.center;
	private static int width = Constants.width;
	private static int maxInstances = Constants.maxInstances;
	private static String path = "SEALinear\\" + Integer.toString(center) + "_" + Integer.toString(width) + "\\";
	private static String filename = "seaLinearDrift_" + center + "_" + width + ".arff";
	private static List<Integer> detectedPoints = new ArrayList<Integer>();
	
	public static void main(String[] args)
	{
		DriftStreams drift = new DriftStreams();
		writeToFiles write = new writeToFiles();
		SEAStreamsGenerator sea = new SEAStreamsGenerator();
		ChangeDetector detector = new ChangeDetector();
		DriftClassifier learner = new DriftClassifier();
		PrequentialEvaluator eval = new PrequentialEvaluator();
		CreateDirectory dir = new CreateDirectory();
		dir.createDir(path);
		String fullPath = dir.getFullPath();
		
		InstanceStream inputStream = sea.getStream(1, 0, 10, false);
		InstanceStream driftStream = sea.getStream(2, 0, 10, false);
		LinearConceptDriftStream linearStream = drift.generateLinearDriftStream(inputStream, driftStream, center, width);
		//write stream to ARFF file
		write.writeStreamToFile(linearStream, fullPath, filename, maxInstances);	
		//write drift points to CSV file
		TreeMap<Integer, Double> driftPoints = linearStream.getDriftPoints();
		write.writeDriftToFile(driftPoints, fullPath, "seaLinearDriftPoints.csv");
		
		//evaluate Prequential - ddm
		DDM ddm = detector.getDDM();
		SingleClassifierDrift ddmLearner = learner.getSingleClassifierDrift(ddm);

		eval.prequential(ddmLearner, linearStream, 100, 100, maxInstances, fullPath, "sea_DDM_Linear.csv");
		if(ddm.getChange()){
			detectedPoints.add(e)
		};
		
		//evaluate Prequential - cusum
		CusumDM cusum = detector.getCusum();
		SingleClassifierDrift cusumLearner = learner.getSingleClassifierDrift(cusum);
		eval.prequential(cusumLearner, linearStream, 100, 100, maxInstances, fullPath, "sea_Cusum_Linear.csv");
		
		//evaluate Prequential - pht
		PageHinkleyDM pht = detector.getPHT();
		SingleClassifierDrift phtLearner = learner.getSingleClassifierDrift(pht);
		eval.prequential(phtLearner, linearStream, 100, 100, maxInstances, fullPath, "sea_PHT_Linear.csv");
		
		/*
		//evaluate Prequential - adwin
		ADWINChangeDetector adwin = detector.getAdwin();
		SingleClassifierDrift adwinLearner = learner.getSingleClassifierDrift(adwin);
		eval.prequential(adwinLearner, linearStream, 100, 100, maxInstances, fullPath, "sea_Adwin_Linear.csv");
		*/
		//evaluate Prequential - seed
		SEEDChangeDetector seed = detector.getSeed();
		SingleClassifierDrift seedLearner = learner.getSingleClassifierDrift(seed);
		eval.prequential(seedLearner, linearStream, 100, 100, maxInstances, fullPath, "sea_Seed_Linear.csv");
	
		System.out.println("Done!");
	}

}
