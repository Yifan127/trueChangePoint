package moa.classifiers.core.driftdetection;

import moa.AbstractMOAObject;


public class SEEDAngle extends AbstractMOAObject{

	private class SEEDBlock extends AbstractMOAObject
	{
		private SEEDBlock next;
		private SEEDBlock previous;
		
		private int blockSize;
		private double total;
		private double variance;
		private int itemCount;
		
		public SEEDBlock(int blockSize)
		{
		    this.next = null;
		    this.previous = null;
		    this.blockSize = blockSize;
		
		    this.total = 0;
		    this.variance = 0;
		    this.itemCount = 0;
		}
		
		public SEEDBlock(SEEDBlock block)
		{
		    this.next = block.getNext();
		    this.previous = block.getPrevious();
		    this.blockSize = block.blockSize;
		
		    this.total = block.total;
		    this.variance = block.variance;
		    this.itemCount = block.itemCount;
		}
		
		public void setNext(SEEDBlock next)
		{
		    this.next = next;
		}
		
		public SEEDBlock getNext()
		{
		    return this.next;
		}
		
		public void setPrevious(SEEDBlock previous)
		{
		    this.previous = previous;
		}
		
		public SEEDBlock getPrevious()
		{
		    return this.previous;
		}
		
		public int getBlockSize()
		{
		    return blockSize;
		}
		
		public void setBlockSize(int blockSize)
		{
		    this.blockSize = blockSize;
		}
		
		public void add(double value)
		{
		    itemCount++;
		    total += value;
		}
		
		public boolean isFull()
		{
		    if (itemCount == blockSize)
		    {
			return true;
		    } else
		    {
			return false;
		    }
		}
		
		public double getMean()
		{
		    return this.total / this.itemCount;
		}
		
		public void setTotal(double value)
		{
		    this.total = value;
		}
		
		public double getTotal()
		{
		    return this.total;
		}
		
		public void setItemCount(int value)
		{
		    this.itemCount = value;
		}
		
		public int getItemCount()
		{
		    return this.itemCount;
		}
		
		public void setVariance(double value)
		{
		    this.variance = value;
		}
		
		public double getVariance()
		{
		    return this.variance;
		}
		
		@Override
		public void getDescription(StringBuilder sb, int indent) {
			// TODO Auto-generated method stub
			
		}
	
	}
	
	
	private class SEEDWindow extends AbstractMOAObject
	{
		private SEEDBlock head;
		private SEEDBlock tail;
		
		private int blockSize;
		private int width;
		private double total;
		private double variance;
		private int blockCount;
		
		private int DECAY_MODE = 1;
		private final int LINEAR_DECAY = 1;
		private final int EXPONENTIAL_DECAY = 2;
		
		private int COMPRESSION_MODE = 1;
		private final int FIXED_TERM = 1;
		
		private int decayCompressionCount = 0;
		private int linearFixedTermSize = 50;
		private int minNumBlock = 200;
		
		private double epsilonPrime = 0.0;
		private double alpha = 0.0;
		
		public SEEDWindow(int blockSize)
		{
		    clear();
		    this.blockSize = blockSize;
		    addBlockToHead(new SEEDBlock(blockSize));
		}
		
		public SEEDWindow(int blockSize, int decayMode, int compressionMode, double epsilonPrime, double alpha, int compressionTerm)
		{
		    clear();
		    this.blockSize = blockSize;
		    this.DECAY_MODE = decayMode;
		    this.COMPRESSION_MODE = compressionMode;
		    this.epsilonPrime = epsilonPrime;
		    this.alpha = alpha;
		    setCompressionTerm(compressionTerm);
		    addBlockToHead(new SEEDBlock(blockSize));
		}
		
		public void clear()
		{
		    head = null;
		    tail = null;
		    width = 0;
		    blockCount = 0;
		    total = 0;
		    variance = 0;
		}
		
		public void addTransaction(double value)
		{
		    if (tail.isFull())
		    {
				if (COMPRESSION_MODE == FIXED_TERM)
				{
				    if (tail.getPrevious() != null && decayCompressionCount > linearFixedTermSize && blockCount > minNumBlock)
				    {
						decayCompressionCount = 0;
						SEEDBlock cursor = tail;
			
						double epsilon = 0.0;
			
						int i = 0; 
			
						while (cursor != null && cursor.getPrevious() != null)
						{
						    double n0 = cursor.getItemCount();
						    double n1 = cursor.getPrevious().getItemCount();
						    double u0 = cursor.getTotal();
						    double u1 = cursor.getPrevious().getTotal();
			
						    double diff = Math.abs(u1 / n1 - (u0 / n0));
			
						    if (DECAY_MODE == LINEAR_DECAY)
						    {
							epsilon += epsilonPrime * alpha;
						    } 
						    else if (DECAY_MODE == EXPONENTIAL_DECAY)
						    {
							epsilon = epsilonPrime * Math.pow(1 + alpha, i);
						    }
			
						    if (diff < epsilon)
						    {
							compressBlock(cursor);
						    }
						    cursor = cursor.getPrevious();
						    i++; 
						}
				    }
				} 
		
		
				addBlockToTail(new SEEDBlock(this.blockSize));
					decayCompressionCount++;
			    }
			    tail.add(value);
			    total += value;
		
			    width++;
			    if (width >= 2)
			    {
					double incVariance = (width - 1) * (value - total / (width - 1)) * (value - total / (width - 1)) / width;
					variance += incVariance;
					tail.setVariance(tail.getVariance() + incVariance);
			    }
		
			}
		
			public void compressBlock(SEEDBlock cursor)
			{
			    cursor.getPrevious().setTotal(cursor.getTotal() + cursor.getPrevious().getTotal());
			    cursor.getPrevious().setItemCount(cursor.getItemCount() + cursor.getPrevious().getItemCount());
			    cursor.getPrevious().setVariance(cursor.getVariance() + cursor.getPrevious().getVariance());
			    cursor.getPrevious().setBlockSize(cursor.getBlockSize() + cursor.getPrevious().getBlockSize());
		
			    if (cursor.getNext() != null)
			    {
					cursor.getPrevious().setNext(cursor.getNext());
					cursor.getNext().setPrevious(cursor.getPrevious());
			    } 
			    else
			    {
					cursor.getPrevious().setNext(null);
					tail = cursor.getPrevious();
			    }
		
			    blockCount--;
			}
		
			//		public boolean checkHomogeneity(SEEDBlock block)
			//		{
			//			double diff = Math.abs(block.getMean() - block.getPrevious().getMean());
			//			double epsilonPrime = getADWINBound(block.getItemCount(), block.getPrevious().getItemCount());
			//			// double epsilonPrime = 0.01;
			//			if (diff < epsilonPrime)
			//			{
			//				return true;
			//			} 
			//			else
			//			{
			//				return false;
			//			}
			//		}
			//
			//		private double getADWINBound(double n0, double n1)
			//		{
			//			double n = n0 + n1;
			//			double dd = Math.log(2 * Math.log(n) / 0.99);
			//			double v = variance / width;
			//			double m = (1 / (n0)) + (1 / (n1));
			//			double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;
			//
			//			return epsilon;
			//		}
		
			public void addBlockToHead(SEEDBlock block)
			{
			    if (head == null)
			    {
					head = block;
					tail = block;
			    } 
			    else
			    {
					block.setNext(head);
					head.setPrevious(block);
					head = block;
			    }
			    blockCount++;
			}
		
			public void removeBlock(SEEDBlock block)
			{
			    width -= block.getItemCount();
			    total -= block.getTotal();
			    variance -= block.getVariance();
			    blockCount--;
		
			    if (block.getPrevious() != null && block.getNext() != null)
			    {
					block.getPrevious().setNext(block.getNext());
					block.getNext().setPrevious(block.getPrevious());
					block.setNext(null);
					block.setPrevious(null);
			    } 
			    else if (block.getPrevious() == null && block.getNext() != null)
			    {
					block.getNext().setPrevious(null);
					head = block.getNext();
					block.setNext(null);
			    } 
			    else if (block.getPrevious() != null && block.getNext() == null)
			    {
					block.getPrevious().setNext(null);
					tail = block.getPrevious();
					block.setPrevious(null);
			    } 
			    else if (block.getPrevious() == null && block.getNext() == null)
			    {
					head = null;
					tail = null;
			    }
			}
		
			public void addBlockToTail(SEEDBlock block)
			{
			    if (tail == null)
			    {
					tail = block;
					head = block;
			    } 
			    else
			    {
					block.setPrevious(tail);
					tail.setNext(block);
					tail = block;
			    }
			    blockCount++;
			}
		
			public int getBlockCount()
			{
			    return this.blockCount;
			}
		
			public void setBlockCount(int value)
			{
			    this.blockCount = value;
			}
		
			public int getWidth()
			{
			    return this.width;
			}
		
			public void setWidth(int value)
			{
			    this.width = value;
			}
		
			public void setHead(SEEDBlock head)
			{
			    this.head = head;
			}
		
			public void setTail(SEEDBlock tail)
			{
			    this.tail = tail;
			}
		
			public SEEDBlock getHead()
			{
			    return this.head;
			}
		
			public SEEDBlock getTail()
			{
			    return this.tail;
			}
		
			public double getTotal()
			{
			    return this.total;
			}
		
			public void setTotal(double value)
			{
			    this.total = value;
			}
		
			public double getVariance()
			{
			    return this.variance;
			}
		
			public void setVariance(double value)
			{
			    this.variance = value;
			}
		
			public void setBlockSize(int value)
			{
			    if (value > 32)
			    {
				this.blockSize = value;
			    } 
			    else
			    {
				this.blockSize = 32;
			    }
			}
		
			public int getBlockSize()
			{
			    return this.blockSize;
			}
		
			public double getEpsilonPrime()
			{
			    return epsilonPrime;
			}
		
			public void setEpsilonPrime(double epsilonPrime)
			{
			    this.epsilonPrime = epsilonPrime;
			}
		
			public void setAlpha(double alpha)
			{
			    this.alpha = alpha;
			}
		
			public void setCompressionTerm(int value)
			{
			    this.linearFixedTermSize = value;
			}
			
			@Override
			public void getDescription(StringBuilder sb, int indent) {
				// TODO Auto-generated method stub
				
			}
	}
		
	public SEEDWindow window;
	private double DELTA;
	private int blockSize;
	private int elementCount;
	private int driftLocation;
	private double driftAngle;
	
	/**
	 * Constructor for all required parameters.
	 * @param delta 
	 * @param blockSize Recommended value: 32
	 * @param epsilonPrime Recommended values between [0.0025, 0.01]
	 * @param alpha Growth parameter - Recommended values between [0.2, 0.8]
	 * @param compressionterm Recommended values between [50, 100]
	 */
	public SEEDAngle(double delta, int blockSize, double epsilonPrime, double alpha, int term)
	{
	    this.DELTA = delta;
	    this.blockSize = blockSize;
	    this.window = new SEEDWindow(blockSize, 1, 1, epsilonPrime, alpha, term);
	    this.driftLocation = 0;
	}   
	
	/**
	 * Main method for passing in input values and performing drift detection
	 * 
	 * @param inputValue instance from the stream
	 * @return boolean value true or false signaling whether a drift has occurred
	 */
	public boolean setInput(double inputValue)
	{
	    SEEDBlock cursor;

	    addElement(inputValue);

	    if (elementCount % blockSize == 0 && window.getBlockCount() >= 2) // Drift Point Check
	    {
		boolean blnReduceWidth = true;

		while (blnReduceWidth)
		{
		    blnReduceWidth = false;
		    int n1 = 0;
		    int n0 = window.getWidth();
		    double u1 = 0;
		    double u0 = window.getTotal();

		    double[] blockMeans = new double[window.getBlockCount()];
		    //double[] blockVars = new double[window.getBlockCount()];
		    double[] blocksizes = new double[window.getBlockCount()];
		    int cursorIndex = window.getBlockCount() - 1;
		    int driftLocationIndex = cursorIndex;

		    //System.out.println("----------------------------------------");

		    cursor = window.getTail();
		    while (cursor.getPrevious() != null)
		    {
			blockMeans[cursorIndex] = cursor.getMean();
			//	blockVars[cursorIndex] = cursor.getVariance();
			blocksizes[cursorIndex--] = cursor.getItemCount();
			n0 -= cursor.getItemCount();
			n1 += cursor.getItemCount();
			u0 -= cursor.getTotal();
			u1 += cursor.getTotal();
			double diff = Math.abs(u1 / n1 - (u0 / n0));
			//System.out.println(getc() + ": " + diff + "," + n0 + "," + n1);

			if (diff > getADWINBound(n0, n1))
			{
			    //System.out.println("drift " + c);
			    driftLocationIndex = cursorIndex;
//			    double[] blocksizes = new double[cursorIndex + 1];
			    blnReduceWidth = true;
			    window.setHead(cursor);

			    while (cursor.getPrevious() != null)
			    {
				cursor = cursor.getPrevious();
				blockMeans[cursorIndex] = cursor.getMean();
				//blockVars[cursorIndex] = cursor.getVariance();
				blocksizes[cursorIndex] = cursor.getItemCount();
				n1 += cursor.getItemCount();
				cursorIndex--;
				window.setWidth(window.getWidth() - cursor.getItemCount());
				window.setTotal(window.getTotal() - cursor.getTotal());
				window.setVariance(window.getVariance() - cursor.getVariance());
				window.setBlockCount(window.getBlockCount() - 1);
			    }
			    window.getHead().setPrevious(null);

			    // Starting point
			    double ix = 0.0;
			    double iy = blockMeans[0];
			    // Mid point
			    double jx = 0.0;
			    double jy = 0.0;
			    // End point
			    double kx = n1; //n0;
			    double ky = blockMeans[driftLocationIndex];//blockMeans[blockMeans.length - 1];
			    
			    jy = iy;
			    double minAngle = 360;
			    //jx = kx;

			    //for(int i = 0; i < blockMeans.length - 2 - driftLocationIndex; i++)
			    //{
			    //	jx = jx - blocksizes[driftLocationIndex];
			    //}

			    Buffer angles = new Buffer(2);
			    double[] angles_hist = new double[blockMeans.length - 1];

			    for(int j = 1; j < driftLocationIndex; j++)
			    {	
				jx = jx + blocksizes[j-1];
				//jy = jy * jx + blockMeans[j] * blocksizes[j];
				//jy /= jx + blocksizes[j];
				jy = blockMeans[j];
				
				//double angle = getAngle(ix/n1, iy, jx/n1, jy, kx/n1, ky);
				double angle = getAngle(ix, iy, jx, jy, kx, ky);
				angles.add(angle);
				angles_hist[j] = angle;

				if(angles.getMean() < minAngle)
				{
				    minAngle = angle;
				    this.driftLocation = (int)(kx - jx);
				    this.driftAngle = minAngle;
				}
				//System.out.printf("%.0f %.4f %.0f %.4f %.0f %.4f\n", ix, iy, jx, jy, kx, ky);
			    }
			    
//			    System.out.print("t");
			    return true;
			}
			cursor = cursor.getPrevious();
		    }
		}
    }
    return false;
	}
	
	// Calculates the angle (in degrees) formed given 3 points (x,y) where i is connected to j and j is connected to k.
	private double getAngle(double ix, double iy, double jx, double jy, double kx, double ky)
	{
	    double ux = jx - ix;
	    double uy = jy - iy;
	    double vx = jx - kx;
	    double vy = jx - ky;
	
	    double u_mag = Math.sqrt(ux * ux + uy * uy);
	    double v_mag = Math.sqrt(vx * vx + vy * vy);
	
	    double uDotv = ux * vx + uy * vy;
	
	    double cosAngle = uDotv / (u_mag * v_mag);
	    double angle = Math.acos(cosAngle);
	
	    return angle * (180 / Math.PI);
	}
	
	private double getADWINBound(double n0, double n1)
	{
	    double n = n0 + n1;
	    double dd = Math.log(2 * Math.log(n) / DELTA);
	    double v = window.getVariance() / window.getWidth();
	    double m = (1 / (n0)) + (1 / (n1));
	    double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;
	
	    return epsilon;
	}
	
	public void addElement(double value)
	{
	    window.addTransaction(value);
	    elementCount++;
	}
	
	public int getDriftLocation()
	{
	    return this.driftLocation;
	}
	
	public double getDriftAngle()
	{
	    return this.driftAngle;		    
	}
	
	@Override
	public void getDescription(StringBuilder sb, int indent) {
		// TODO Auto-generated method stub
		
	}

}
