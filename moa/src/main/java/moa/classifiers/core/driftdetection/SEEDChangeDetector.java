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

public class SEEDChangeDetector extends AbstractChangeDetector
{
    protected SEED seed;
    
    public FloatOption deltaSEEDOption = new FloatOption("deltaSEED", 'd', "Delta value of SEED Detector", 0.05, 0.0, 1.0);
    public IntOption blockSizeSEEDOption = new IntOption("blockSizeSEED", 'b', "BlockSize value of SEED Detector", 32, 32, 256);
    public FloatOption epsilonPrimeSEEDOption = new FloatOption("epsilonPrimeSEED", 'e', "EpsilonPrime value of SEED Detector", 0.01, 0.0025, 0.01);
    public FloatOption alphaSEEDOption = new FloatOption("alphaSEED", 'a', "Alpha value of SEED Detector", 0.8, 0.2, 0.8);
    public IntOption compressTermSEEDOption = new IntOption("compressTermSEED", 'c', "CompressTerm value of SEED Detector", 75, 50, 100);

   
    public void input(double inputValue)
    {
		if(this.seed == null)
		{
		    resetLearning();
		}
		this.isChangeDetected = seed.setInput(inputValue);
		this.isWarningZone = false;
		this.delay = 0.0;
		this.estimation = 0.0;
    }
    
    @Override
    public void resetLearning()
    {
		seed = new SEED((double)this.deltaSEEDOption.getValue(), 
			this.blockSizeSEEDOption.getValue(), 
			(double) this.epsilonPrimeSEEDOption.getValue(), 
			(double)this.alphaSEEDOption.getValue(), 
			this.compressTermSEEDOption.getValue());
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
