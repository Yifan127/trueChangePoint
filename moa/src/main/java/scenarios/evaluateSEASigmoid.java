package scenarios;

import java.util.TreeMap;

import changeDetector.ChangeDetector;
import classifiers.DriftClassifier;
import driftStreams.DriftStreams;
import moa.classifiers.core.driftdetection.*;
import moa.classifiers.drift.SingleClassifierDrift;
import moa.streams.InstanceStream;
import moa.streams.SigmoidConceptDriftStream;
import streams.SEAStreamsGenerator;
import tasks.PrequentialEvaluator;
import tasks.writeToFiles;
import utils.Constants;
import utils.CreateDirectory;

public class evaluateSEASigmoid {
	private static int center = Constants.center;
	private static int width = Constants.width;
	private static int maxInstances = Constants.maxInstances;
	private static String path = "SEASigmoid\\" + Integer.toString(center) + "_" + Integer.toString(width) + "\\";
	private static String filename = "seaSigmoidDrift_" + center + "_" + width + ".arff";
	
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
		SigmoidConceptDriftStream sigmoidStream = drift.generateSigmoidDriftStream(inputStream, driftStream, center, width);
		//write stream to ARFF file
		write.writeStreamToFile(sigmoidStream, fullPath, filename, maxInstances);	
		//write drift points to CSV file
		TreeMap<Integer, Double> driftPoints = sigmoidStream.getDriftPoints();
		write.writeDriftToFile(driftPoints, fullPath, "seaSigmoidDriftPoints.csv");
		
		//evaluate Prequential - ddm
		DDM ddm = detector.getDDM();
		SingleClassifierDrift ddmLearner = learner.getSingleClassifierDrift(ddm);
		eval.prequential(ddmLearner, sigmoidStream, 100, 100, maxInstances, fullPath, "sea_DDM_Sigmoid.csv");
		
		//evaluate Prequential - cusum
		CusumDM cusum = detector.getCusum();
		SingleClassifierDrift cusumLearner = learner.getSingleClassifierDrift(cusum);
		eval.prequential(cusumLearner, sigmoidStream, 100, 100, maxInstances, fullPath, "sea_Cusum_Sigmoid.csv");
		
		//evaluate Prequential - pht
		PageHinkleyDM pht = detector.getPHT();
		SingleClassifierDrift phtLearner = learner.getSingleClassifierDrift(pht);
		eval.prequential(phtLearner, sigmoidStream, 100, 100, maxInstances, fullPath, "sea_PHT_Sigmoid.csv");
		/*
		//evaluate Prequential - adwin
		ADWINChangeDetector adwin = detector.getAdwin();
		SingleClassifierDrift adwinLearner = learner.getSingleClassifierDrift(adwin);
		eval.prequential(adwinLearner, sigmoidStream, 100, 100, maxInstances, fullPath, "sea_Adwin_Sigmoid.csv");
		*/
		//evaluate Prequential - seed
		SEEDChangeDetector seed = detector.getSeed();
		SingleClassifierDrift seedLearner = learner.getSingleClassifierDrift(seed);
		eval.prequential(seedLearner, sigmoidStream, 100, 100, maxInstances, fullPath, "sea_Seed_Sigmoid.csv");
	
		System.out.println("Done!");
	}

}
