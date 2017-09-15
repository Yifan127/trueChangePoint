package scenarios;

import java.util.TreeMap;

import changeDetector.ChangeDetector;
import classifiers.DriftClassifier;
import moa.classifiers.core.driftdetection.*;
import moa.classifiers.drift.SingleClassifierDrift;
import moa.streams.InstanceStream;
import moa.streams.LogConceptDriftStream;
import streams.SEAStreamsGenerator;
import tasks.PrequentialEvaluator;
import tasks.writeToFiles;
import utils.Constants;
import utils.CreateDirectory;
import driftStreams.DriftStreams;

public class evaluateSEALog {
	private static int center = Constants.center;
	private static int width = Constants.width;
	private static int maxInstances = Constants.maxInstances;
	private static String path = "SEALog\\" + Integer.toString(center) + "_" + Integer.toString(width) + "\\";
	private static String filename = "seaLogDrift_" + center + "_" + width + ".arff";
	
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
		LogConceptDriftStream logStream = drift.generateLogDriftStream(inputStream, driftStream, center, width);
		//write stream to ARFF file
		write.writeStreamToFile(logStream, fullPath, filename, maxInstances);	
		//write drift points to CSV file
		TreeMap<Integer, Double> driftPoints = logStream.getDriftPoints();
		write.writeDriftToFile(driftPoints, fullPath, "seaLogDriftPoints.csv");
		
		//evaluate Prequential - ddm
		DDM ddm = detector.getDDM();
		SingleClassifierDrift ddmLearner = learner.getSingleClassifierDrift(ddm);
		eval.prequential(ddmLearner, logStream, 100, 100, maxInstances, fullPath, "sea_DDM_Log.csv");
		
		//evaluate Prequential - cusum
		CusumDM cusum = detector.getCusum();
		SingleClassifierDrift cusumLearner = learner.getSingleClassifierDrift(cusum);
		eval.prequential(cusumLearner, logStream, 100, 100, maxInstances, fullPath, "sea_Cusum_Log.csv");
		
		//evaluate Prequential - pht
		PageHinkleyDM pht = detector.getPHT();
		SingleClassifierDrift phtLearner = learner.getSingleClassifierDrift(pht);
		eval.prequential(phtLearner, logStream, 100, 100, maxInstances, fullPath, "sea_PHT_Log.csv");
		/*
		//evaluate Prequential - adwin
		ADWINChangeDetector adwin = detector.getAdwin();
		SingleClassifierDrift adwinLearner = learner.getSingleClassifierDrift(adwin);
		eval.prequential(adwinLearner, logStream, 100, 100, maxInstances, fullPath, "sea_Adwin_Log.csv");
		*/
		//evaluate Prequential - seed
		SEEDChangeDetector seed = detector.getSeed();
		SingleClassifierDrift seedLearner = learner.getSingleClassifierDrift(seed);
		eval.prequential(seedLearner, logStream, 100, 100, maxInstances, fullPath, "sea_Seed_Log.csv");
	
		System.out.println("Done!");
	}

}
