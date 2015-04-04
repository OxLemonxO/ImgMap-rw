package ga.nurupeaches.imgmap.natives;

import java.awt.*;

public class ColorDistBenchmark {

	private static Color[] colorSample1 = new Color[]{
			Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA
	};

	private static Color[] colorSample2 = new Color[]{
			Color.MAGENTA, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED
	};

	private static final int RUNS = 10000000;

	public static void main(String[] args){
		for(int i=0; i < 100000000; i++){
			for(int pos=0; pos < colorSample1.length; pos++){
				bukkitDist(colorSample1[pos], colorSample2[pos]);
				directTranslationDist(colorSample1[pos], colorSample2[pos]);
			}
		}

		long bukkitNow = System.currentTimeMillis();
		for(int i=0; i < RUNS; i++){
			for(int pos=0; pos < colorSample1.length; pos++){
				bukkitDist(colorSample1[pos], colorSample2[pos]);
			}
		}
		long bukkitAfter = System.currentTimeMillis();


		long directNow = System.currentTimeMillis();
		for(int i=0; i < RUNS; i++){
			for(int pos=0; pos < colorSample1.length; pos++){
				directTranslationDist(colorSample1[pos], colorSample2[pos]);
			}
		}
		long directAfter = System.currentTimeMillis();

		System.out.println("Bukkit took " + (bukkitAfter - bukkitNow));
		System.out.println("Direct took " + (directAfter- directNow));
	}

	public static double bukkitDist(Color c1, Color c2) {
		double rmean = (double)(c1.getRed() + c2.getRed()) / 2.0D;
		double r = (double)(c1.getRed() - c2.getRed());
		double g = (double)(c1.getGreen() - c2.getGreen());
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2.0D + rmean / 256.0D;
		double weightG = 4.0D;
		double weightB = 2.0D + (255.0D - rmean) / 256.0D;
		return weightR * r * r + weightG * g * g + weightB * (double)b * (double)b;
	}

	public static double directTranslationDist(Color c1, Color c2){
		double rMean = (double)(c1.getRed() + c2.getRed()) / 2.0D;
		double r = (double)(c1.getRed() - c2.getRed());
		double g = (double)(c1.getGreen() - c2.getGreen());
		double b = (double)(c1.getBlue() - c2.getBlue());
		return (2 + (rMean/256.0D)) * (r * r) + 4 * (g * g) + (2 + (255.0D-rMean)/256.0D) * (b * b);
	}

}