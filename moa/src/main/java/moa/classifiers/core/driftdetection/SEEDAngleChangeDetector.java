/*
 * SEEDChangeDetector.java
 * author: David T.J. Huang - The University of Auckland
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 * 
 */

package moa.classifiers.core.driftdetection;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;

import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;


/**
 * 
 * Drift detection method as published in:
 * </p>
 * David Tse Jung Huang, Yun Sing Koh, Gillian Dobbie, and Russel Pears: Detecting Volatility Shift in Data Streams. ICDM 2014: 863-868
 * </p>
 * Usage: {@link #setInput(double)}
 * </p>
 * 
 * @author David T.J. Huang - The University of Auckland
 * @version 1.0
 */

public class SEEDAngleChangeDetector extends AbstractChangeDetector
{
    protected SEEDAngle seed;
    
    //private boolean isChangeDetected;
    //private boolean isWarningZone;
    //private double delay;
    //private double estimation;
    private int driftPosition;
    private double driftAngle;

    private double confidence;

    private int c = 0;

    public int getc()
    {
    	return c;
    }

    public int getDriftPosition()
    {
    	return this.driftPosition;
    }
    
    public double getDriftAngle()
    {
    	return this.driftAngle;
    }
    
    public boolean getIsDrift()
    {
    	return this.isChangeDetected;
    }

    public void input(double inputValue)
    {
		c++;
		if(this.seed == null)
		{
		    resetLearning();
		}
		this.isChangeDetected = seed.setInput(inputValue);
		this.isWarningZone = false;
		this.delay = 0.0;
		this.estimation = 0.0;
		this.driftPosition = seed.getDriftLocation();
		this.driftAngle = seed.getDriftAngle();
		//return isChangeDetected;
    }

    
    public SEEDAngleChangeDetector()
    {
    	resetLearning();
    }
    
    public SEEDAngleChangeDetector(double confidence)
    {
		this.confidence = confidence;
		resetLearning();
    }

    public void resetLearning()
    {
    	seed = new SEEDAngle(0.05, 32, 0.01, 0.8, 75);
    }

    
    
    @Override
    public void getDescription(StringBuilder sb, int indent)
    {
	// TODO Auto-generated method stub	
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository)
    {
	// TODO Auto-generated method stub
    }

}

