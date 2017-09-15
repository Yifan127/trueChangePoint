package moa.streams;

import moa.core.Example;
import moa.core.FastVector;

import java.util.Random;
import java.util.TreeMap;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;


public class QuadraticConceptDriftStream extends ConceptDriftStream {
    
	//private static List<Integer> driftPoints = new ArrayList<Integer>();
	private static TreeMap<Integer,Double> driftPoints = new TreeMap<Integer,Double>();
	protected InstancesHeader streamHeader;
		
	@Override
    public String getPurposeString() {
        return "Adds Quadratic Concept Drift to examples in a stream.";
    }
	
	private static final long serialVersionUID = 1L;

    @Override
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {

        this.inputStream = (ExampleStream) getPreparedClassOption(this.streamOption);
        this.driftStream = (ExampleStream) getPreparedClassOption(this.driftstreamOption);
        this.random = new Random(this.randomSeedOption.getValue());
        numberInstanceStream = 0;
        
        // generate header
        Instances first = this.inputStream.getHeader();
        FastVector newAttributes = new FastVector();
        for (int i = 0; i < first.numAttributes() - 1; i++) {
            newAttributes.addElement(first.attribute(i));
        }

        Attribute classLabels = first.classAttribute();
        newAttributes.addElement(classLabels);

        this.streamHeader = new InstancesHeader(new Instances(
                getCLICreationString(InstanceStream.class), newAttributes, 0));
        this.streamHeader.setClassIndex(this.streamHeader.numAttributes() - 1);
        restart();
    }
    
    @Override
    public InstancesHeader getHeader() {
        return this.streamHeader;
    }
    
    @Override
    public Example nextInstance() {
        numberInstanceStream++;
        double k = 1.0 / (double) this.widthOption.getValue();
        double start = Math.ceil(this.positionOption.getValue() - this.widthOption.getValue() / 2);
        double end = Math.ceil(this.positionOption.getValue() + this.widthOption.getValue() / 2);
        double probabilityDrift = Math.pow((numberInstanceStream - start), 2) / Math.pow(this.widthOption.getValue(), 2);
        
        if(numberInstanceStream <= start){
        	return this.inputStream.nextInstance();
        }else if(numberInstanceStream >= end){
        	return this.driftStream.nextInstance();
        }else {
        	if (this.random.nextDouble() > probabilityDrift) {
                return this.inputStream.nextInstance();
            } else {
            	QuadraticConceptDriftStream.driftPoints.put(numberInstanceStream, probabilityDrift);
                return this.driftStream.nextInstance();
            }
        }
    }
    
    public TreeMap<Integer, Double> getDriftPoints() {
        return QuadraticConceptDriftStream.driftPoints;
    }

    public Integer getTrueChangePoint() {
    	return QuadraticConceptDriftStream.driftPoints.firstKey();
    }
}
