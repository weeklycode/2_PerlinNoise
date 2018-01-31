package run;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import core.LayerEngine;
import core.PerlinNoiseGenerator;

public class TestRunner {

	public static void main(String[] args){		
		JFrame jf = new JFrame("PERLIN NOISE");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(1000, 750);
		BufferedImage bi = new BufferedImage(jf.getWidth(), jf.getHeight(),BufferedImage.TYPE_INT_RGB);
		PerlinNoiseGenerator png = new PerlinNoiseGenerator(2,(short)512);
		LayerEngine le = new LayerEngine(png,6);

		for (int x = 0; x<bi.getWidth(); x++){
			for (int y = 0; y<bi.getHeight(); y++){

				double
				noise = le.getNoiseAt(new int[]{x,y}),//between -1 and 1
				intens = (noise+1)*255/2.0;//between 0 and 255

				int val = (int)intens;

				bi.setRGB(x, y, val<<16 | val<<8 | val);
			}
		}

		jf.setVisible(true);
		jf.getGraphics().drawImage(bi, 0, 0, null);
	}
}
