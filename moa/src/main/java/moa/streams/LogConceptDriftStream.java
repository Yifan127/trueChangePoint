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

/**
 * Stream generator that adds log concept drift to examples in a stream.
 *<br/><br/>
 * Example:
 *<br/><br/>
 * <code>ConceptDriftStream -s (generators.AgrawalGenerator -f 7) <br/>
 *    -d (generators.AgrawalGenerator -f 2) -w 1000000 -p 900000</code>
 *<br/><br/>
 * s : Stream <br/>
 * d : Concept drift Stream<br/>
 * p : Central position of concept drift change<br/>
 * w : Width of concept drift change<br/>
 *
 * @author Yifan Zhang
 * @version $Revision: 1 $
 */

public class LogConceptDriftStream extends ConceptDriftStream {
    
	protected InstancesHeader streamHeader;
		
	@Override
    public String getPurposeString() {
        return "Adds Logarithmic Concept Drift to examples in a stream.";
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
        double start = Math.ceil(this.positionOption.getValue() - this.widthOption.getValue() / 2);
        double end = Math.ceil(this.positionOption.getValue() + this.widthOption.getValue() / 2);
        double probabilityDrift = Math.log(numberInstanceStream - start) / Math.log(this.widthOption.getValue());
        
        if(numberInstanceStream <= start){
        	return this.inputStream.nextInstance();
        }else if(numberInstanceStream >= end){
        	return this.driftStream.nextInstance();
        }else {
        	if (this.random.nextDouble() > probabilityDrift) {
                return this.inputStream.nextInstance();
            } else {
            	LogConceptDriftStream.driftPoints.put(numberInstanceStream, probabilityDrift);
            	if(!this.isChanged){
            		this.trueChangePoint = numberInstanceStream;
            		this.isChanged = true;
            	}
                return this.driftStream.nextInstance();
            }
        }
    }
    
    @Override
    public TreeMap<Integer, Double> getDriftPoints() {
        return LogConceptDriftStream.driftPoints;
    }

}
