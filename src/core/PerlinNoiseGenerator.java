package core;

import java.util.Random;

public class PerlinNoiseGenerator {

	int dims;
	short[] seed;
	double[][] gradients;

	/***
	 * Perlin noise in n dimensions
	 * @param dimensions the number of dimensions. dimensions<32 due to integer bit size limit
	 * @param complexity the number of possible gradient vectors (only affects a small amount of memory, does not affect computation time)
	 */
	public PerlinNoiseGenerator(int dimensions, short complexity){

		dims=dimensions;
		seed = new short[complexity];
		gradients = new double[complexity][dims];

		//fill seed up to complexity
		for (int c = 0; c<complexity; c++){
			seed[c] = (short) (c+1);
		}

		//shuffle seed
		Random r = new Random();
		for (int c = 0; c<complexity; c++){
			short save = seed[c];
			int roll = r.nextInt(complexity);
			seed[c]=seed[roll];
			seed[roll]=save;
		}

		double min=1,max=0;

		//calculate gradient vectors using seed
		for (int c = 0; c<complexity; c++){
			double mag = 0;
			r = new Random(seed[c]<<5 + seed[c]*11);
			r = new Random(seed[seed[c]-1]^r.nextLong());
			for (int i = 0; i<dims; i++){
				gradients[c][i]=r.nextDouble()-.5;
				mag+=gradients[c][i]*gradients[c][i];
			}

			mag=Math.sqrt(mag);

			for (int i = 0; i<dims; i++){
				gradients[c][i]/=mag;
				min=Math.min(gradients[c][i], min);
				max=Math.max(gradients[c][i], max);
			}
		}
	}

	/***
	 * get the perlin noise at a location
	 * @param realPoint array of the coordinates of a point
	 * @return the noise at that point
	 */
	public double getNoiseAt(int[] point,double zoom){

		double[] realPoint = new double[point.length];
		
		for (int c = 0; c<realPoint.length; c++){
			realPoint[c]=(point[c]+.5)/zoom;
		}

		double[] sx = new double[dims];//interpolation weights
		for (int c = 0; c<dims; c++){
			sx[c] = smoothcurve(realPoint[c]-hardFloor(realPoint[c]));
			/*if (sx[c] == 1){
				sx[c] = 0.5;
			}
			sx[c] = 0.5;*/
		}

		double[] heap = new double[dims];//work array
		boolean[] contains = new boolean[dims];//other work array
		int rank = 0;
		int count = 0;
		double val = 0;

		while (rank<dims){

			if (rank == 0){
				val = getDotAt(realPoint,count);
				count++;
			}

			if (contains[rank]){
				val=lerp(heap[rank],val,sx[rank]);
				contains[rank]=false;
				rank++;
			}else{
				heap[rank]=val;
				contains[rank]=true;
				rank = 0;
			}
		}

		//val = 0 here

		return val;

	}

	/***
	 * get dot product of gradient vector and distance vector at a location
	 * @param point location of point
	 * @param count number of n-dim square's corner
	 * @return the dot product of gradient and distance vectors
	 */
	private double getDotAt(double[] point, int count){
		int hash = 0;

		int[] corner = new int[dims];
		double sum = 0;

		for (int c = 0; c<dims; c++){
			corner[c] = ((count>>c)&1)+hardFloor(point[c]);
			hash = seed[(hash+corner[c])&(seed.length-1)]-1;
		}

		for (int c = 0; c<dims; c++){
			double dx = point[c]-corner[c];
			sum += gradients[hash][c]*dx;
		}

		return sum;
	}

	/***
	 * java's Math.floor returns x if x is an integer; i need it to decrement if its an integer
	 * @param x var to floor
	 * @return hard floor of x
	 */
	private int hardFloor(double x){
		if (x==(int)x){
			return (int)Math.floor(x)-1;
		}
		return (int)Math.floor(x);
	}

	/***
	 * linear interpolation
	 * @param v1 first value
	 * @param v2 second value
	 * @param w interpolation weight
	 * @return interpolation from v1 to v2 using w as weight
	 */
	private double lerp(double v1, double v2, double w){
		return (v2-v1)*w+v1;
	}

	/***
	 * places x on an S-curve
	 * @param x any number -1<=x<=1
	 * @return
	 */
	private double smoothcurve(double x){
		x=Math.abs(x);
		return x*x*x*(x*(x*6-15)+10);
	}
}
