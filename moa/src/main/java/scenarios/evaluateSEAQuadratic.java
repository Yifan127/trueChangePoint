package scenarios;

import java.util.TreeMap;

import changeDetector.ChangeDetector;
import classifiers.DriftClassifier;
import moa.classifiers.core.driftdetection.*;
import moa.classifiers.drift.SingleClassifierDrift;
import moa.streams.InstanceStream;
import moa.streams.QuadraticConceptDriftStream;
import streams.SEAStreamsGenerator;
import tasks.PrequentialEvaluator;
import tasks.writeToFiles;
import utils.Constants;
import utils.CreateDirectory;
import driftStreams.DriftStreams;

public class evaluateSEAQuadratic {
	private static int center = Constants.center;
	private static int width = Constants.width;
	private static int maxInstances = Constants.maxInstances;
	private static String path = "SEAQuadratic\\" + Integer.toString(center) + "_" + Integer.toString(width) + "\\";
	private static String filename = "seaQuadraticDrift_" + center + "_" + width + ".arff";
	
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
		QuadraticConceptDriftStream quadraticStream = drift.generateQuadraticDriftStream(inputStream, driftStream, center, width);
		//write stream to ARFF file
		write.writeStreamToFile(quadraticStream, fullPath, filename, maxInstances);	
		//write drift points to CSV file
		TreeMap<Integer, Double> driftPoints = quadraticStream.getDriftPoints();
		write.writeDriftToFile(driftPoints, fullPath, "seaQuadraticDriftPoints.csv");
		
		//evaluate Prequential - ddm
		DDM ddm = detector.getDDM();
		SingleClassifierDrift ddmLearner = learner.getSingleClassifierDrift(ddm);
		eval.prequential(ddmLearner, quadraticStream, 100, 100, maxInstances, fullPath, "sea_DDM_Quadratic.csv");
		
		//evaluate Prequential - cusum
		CusumDM cusum = detector.getCusum();
		SingleClassifierDrift cusumLearner = learner.getSingleClassifierDrift(cusum);
		eval.prequential(cusumLearner, quadraticStream, 100, 100, maxInstances, fullPath, "sea_Cusum_Quadratic.csv");
		
		//evaluate Prequential - pht
		PageHinkleyDM pht = detector.getPHT();
		SingleClassifierDrift phtLearner = learner.getSingleClassifierDrift(pht);
		eval.prequential(phtLearner, quadraticStream, 100, 100, maxInstances, fullPath, "sea_PHT_Quadratic.csv");
		/*
		//evaluate Prequential - adwin
		ADWINChangeDetector adwin = detector.getAdwin();
		SingleClassifierDrift adwinLearner = learner.getSingleClassifierDrift(adwin);
		eval.prequential(adwinLearner, quadraticStream, 100, 100, maxInstances, fullPath, "sea_Adwin_Quadratic.csv");
		*/
		//evaluate Prequential - seed
		SEEDChangeDetector seed = detector.getSeed();
		SingleClassifierDrift seedLearner = learner.getSingleClassifierDrift(seed);
		eval.prequential(seedLearner, quadraticStream, 100, 100, maxInstances, fullPath, "sea_Seed_Quadratic.csv");
	
		System.out.println("Done!");
	}

}
