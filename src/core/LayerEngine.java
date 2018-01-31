package core;

public class LayerEngine {

	PerlinNoiseGenerator noise;
	int layers;

	public LayerEngine(PerlinNoiseGenerator noise, int layerCount){
		this.noise=noise;
		this.layers=layerCount;
	}

	public double getNoiseAt(int[] point){
		
		double noiseCount = 0;
		for (int c = 0; c<layers; c++){
			noiseCount+=noise.getNoiseAt(point,1<<c);
		}
		return noiseCount/layers;

	}
}
