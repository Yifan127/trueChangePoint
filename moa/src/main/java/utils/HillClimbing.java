package utils;

import java.util.Random;

public class HillClimbing {
	double[] data;
    
    public HillClimbing()
    {
	
    }
    
    public HillClimbing(double[] data)
    {
    	this.data = data;
    }
    
    public double climb(int initialPoint, double acceleration, double epsilon)
    {
    	double currentPoint = initialPoint;
    	double previousPoint = initialPoint;
		int stepSize = 1;
		
		double acc = acceleration;
		//double[] candidates = new double[]{0.0, acc, 2*acc, 3*acc};
		double[] candidates = new double[]{-3*acc, -2*acc, -acc, 0.0, acc, 2*acc, 3*acc};
		
		while(true)
		{
		    double before = eval(currentPoint);
		    int best = -1;
		    double bestScore = 0;
		    for(int j = 0; j < candidates.length; j++)
		    {
				double temp = eval(currentPoint + stepSize * candidates[j]);
				//System.out.println(temp);
				if(temp > bestScore)
				{
				    bestScore = temp;
				    best = j;
				}
		    }
	        
		    System.out.println("best: " + best);
		    
		    if(best == -1){
		    	return currentPoint;	    	
		    }
		    
		    if(candidates[best] == 0.0)
		    {
		    	stepSize /= 2;
		    }
		    else
		    {    
		    	previousPoint = currentPoint;
				currentPoint = currentPoint + stepSize * candidates[best];
				System.out.println("currentPoint:" + currentPoint);
				
				//slope
				double slope = (bestScore - before)/(currentPoint - previousPoint);
				System.out.println("slope:" + slope);
				
				stepSize *= candidates[best];				
		    }
		    
		    System.out.println("step size:" + stepSize);
		    double tt = eval(currentPoint) - before;
		    System.out.println(before + "," + eval(currentPoint) + "," + tt);
		    
		    if(tt < epsilon && tt >= 0)
		    {
		    	//System.out.println("current point:" + currentPoint);
				return currentPoint;
		    }
		}
	
    }
    
    public double eval(double x)
    {
		double y = 0.0;
		if((int)Math.round(x) >= data.length)
		{
		    y = data[(int)Math.round(data.length-1)];
		}
		else if((int)Math.round(x) < 0)
		{
		    y = data[(int)Math.round(0)];
		}
		else
		{
		    y = data[(int)Math.round(x)];
		}
		
		return y;
    }
    
    public double evalfunc(double x)
    {
		Random rng = new Random();
		double noise = rng.nextGaussian() * 0.002;
		double y = 0.0;
		
		if(x < 0)
		{
		    y = 1.0;
		}
		else if(x > 100)
		{
		    y = 1.0;
		}
		else
		{
		    y = Math.sin((x/100.0 * Math.PI)+Math.PI) + 1.0 + noise;
		}
		
		return y;
	 }
    
    public double randomClimb(int init, double acceleration, double epsilon, 
    		int truePoint, int detected, int sample){
	    double prediction = 0;  
	    double best = detected - truePoint;
	    for(int round=0; round<30; round++){
	    	Random rnd = new Random();
	    	int initialPoint = init + rnd.nextInt(1000) / sample;
	    	System.out.println("initial point: " + initialPoint);
	    	double result = climb(initialPoint, acceleration, epsilon);
	    	System.out.println("result: " + result);
	    	
	    	/*
	    	//climb again
	    	int secondRun = (int)Math.round(result);
	    	System.out.println("secondRun point: " + secondRun);
	    	result = climb(secondRun, acceleration, epsilon);
	    	System.out.println("second result: " + result);
	    	*/
	    	double point = detected - result * sample;    	
	    	double gap = Math.abs(point - truePoint);
	    	System.out.println("gap: " + gap);
	    	if(gap < best){
	    		best = gap;
	    		prediction = point;
	    	}
	    }
	    
	    if(prediction == 0){
    		prediction = detected;
    	}
	    System.out.println("best gap: " + best);
		return prediction;
    }

}
