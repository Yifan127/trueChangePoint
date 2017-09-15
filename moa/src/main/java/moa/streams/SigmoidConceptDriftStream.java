package moa.streams;

import moa.core.Example;
import moa.core.FastVector;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;


public class SigmoidConceptDriftStream extends ConceptDriftStream {
	
	//private static List<Integer> driftPoints = new ArrayList<Integer>();
	private static TreeMap<Integer,Double> driftPoints = new TreeMap<Integer,Double>();
	protected InstancesHeader streamHeader;
	
	@Override
    public String getPurposeString() {
        return "Adds Linear Concept Drift to examples in a stream.";
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
        double x = -4.0 * (double) (numberInstanceStream - this.positionOption.getValue()) / (double) this.widthOption.getValue();
        double probabilityDrift = 1.0 / (1.0 + Math.exp(x));
        if (this.random.nextDouble() > probabilityDrift) {
            return this.inputStream.nextInstance();
        } else {
        	SigmoidConceptDriftStream.driftPoints.put(numberInstanceStream, probabilityDrift);
            return this.driftStream.nextInstance();
        }
    }
    
    public TreeMap<Integer, Double> getDriftPoints() {
        return SigmoidConceptDriftStream.driftPoints;
    }
}
