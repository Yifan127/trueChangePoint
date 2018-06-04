/*
 *    SingleClassifierDrift.java
 *    Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *    @author Manuel Baena (mbaena@lcc.uma.es)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package moa.classifiers.drift;

import java.util.ArrayList;
import java.util.List;

import moa.classifiers.Classifier;
import moa.classifiers.meta.WEKAClassifier;
import moa.core.Utils;
import moa.streams.ConceptDriftStream;

import com.yahoo.labs.samoa.instances.Instance;

import driftStreams.DriftStreams;


/**
 * Class for handling concept drift datasets with a wrapper on a
 * classifier.<p>
 *
 * Valid options are:<p>
 *
 * -l classname <br>
 * Specify the full class name of a classifier as the basis for
 * the concept drift classifier.<p>
 * -d Drift detection method to use<br>
 *
 * @author Manuel Baena (mbaena@lcc.uma.es)
 * @version 1.1
 */
public class SingleClassifierDrift extends DriftDetectionMethodClassifier{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    protected boolean isDetected = false;
    protected long trueChangePoint;
    protected int changeDetected = 0;
    
    public void setTrueChangePoint(long truePoint){
    	this.trueChangePoint = truePoint;
    }
    
    public void resetNumberInstances(){
    	this.numberInstances = 0;
    }
    
    public void resetChangeDetected(){
    	this.changeDetected = 0;
    }
    
    public void trainOnInstanceImpl(Instance inst) {
        this.numberInstances++;
        int trueClass = (int) inst.classValue();
        boolean prediction;
        if (Utils.maxIndex(this.classifier.getVotesForInstance(inst)) == trueClass) {
            prediction = true;
        } else {
            prediction = false;
        }
        //this.ddmLevel = this.driftDetectionMethod.computeNextVal(prediction);
        this.driftDetectionMethod.input(prediction ? 0.0 : 1.0);
        this.ddmLevel = DDM_INCONTROL_LEVEL;
        
        /*
        if (this.driftDetectionMethod.getChange()) {
        	this.ddmLevel =  DDM_OUTCONTROL_LEVEL;
	         //save detected change points to list
	         detectedChangePoints.add(numberInstances);
        }
        */
        if (this.driftDetectionMethod.getWarningZone()) {
           this.ddmLevel =  DDM_WARNING_LEVEL;
        }
        
        
    	if(!this.isDetected){
    		if (this.driftDetectionMethod.getChange()) {
    			//System.out.println("detected change:" + this.numberInstances);         	
            	if(this.numberInstances >= this.trueChangePoint){
            		this.ddmLevel =  DDM_OUTCONTROL_LEVEL;
            		this.isDetected = true;
            	}
            }
    	}
    	
        
           	
        switch (this.ddmLevel) {
        	
            case DDM_WARNING_LEVEL:
                //System.out.println("1 0 W");
            	//System.out.println("DDM_WARNING_LEVEL");
                if (newClassifierReset == true) {
                    this.warningDetected++;
                    this.newclassifier.resetLearning();
                    newClassifierReset = false;
                }
                this.newclassifier.trainOnInstance(inst);
                break;
            

            case DDM_OUTCONTROL_LEVEL:
                //System.out.println("0 1 O");
            	//System.out.println("DDM_OUTCONTROL_LEVEL");
                this.changeDetected++;
                this.classifier = null;
                this.classifier = this.newclassifier;
                if (this.classifier instanceof WEKAClassifier) {
                    ((WEKAClassifier) this.classifier).buildClassifier();
                }
                this.newclassifier = ((Classifier) getPreparedClassOption(this.baseLearnerOption)).copy();
                this.newclassifier.resetLearning();
                break;

            case DDM_INCONTROL_LEVEL:
                //System.out.println("0 0 I");
            	//System.out.println("DDM_INCONTROL_LEVEL");
                newClassifierReset = true;
                break;
            default:
            //System.out.println("ERROR!");

        }

        this.classifier.trainOnInstance(inst);
    }


}
