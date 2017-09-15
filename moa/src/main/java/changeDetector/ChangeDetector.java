package changeDetector;

import moa.classifiers.core.driftdetection.ADWINChangeDetector;
import moa.classifiers.core.driftdetection.CusumDM;
import moa.classifiers.core.driftdetection.DDM;
import moa.classifiers.core.driftdetection.PageHinkleyDM;
import moa.classifiers.core.driftdetection.SEEDChangeDetector;

public class ChangeDetector {
	
	public DDM getDDM(){
		DDM detector = new DDM();
		detector.getOptions().resetToDefaults();
		detector.prepareForUse();
		
		return detector;
	}
	
	public CusumDM getCusum(){
		CusumDM detector = new CusumDM();
		detector.getOptions().resetToDefaults();
		detector.prepareForUse();
		
		return detector;
	}
	
	public PageHinkleyDM getPHT(){
		PageHinkleyDM detector = new PageHinkleyDM();
		detector.getOptions().resetToDefaults();
		detector.prepareForUse();
		
		return detector;
	}
	
	public ADWINChangeDetector getAdwin(){
		ADWINChangeDetector detector = new ADWINChangeDetector();
		detector.getOptions().resetToDefaults();
		detector.prepareForUse();
		
		return detector;
	}
	
	public SEEDChangeDetector getSeed(){
		SEEDChangeDetector detector = new SEEDChangeDetector();
		detector.getOptions().resetToDefaults();
		detector.prepareForUse();
		
		return detector;
	}

}
