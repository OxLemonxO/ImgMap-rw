package ga.nurupeaches.imgmap.natives;

import java.awt.Color;

public class ColorTest {

	static int[] colors = {
			 -7968970,
	};

	public static void main(String[] args){
		for(int color : colors){
			Color c = new Color(color);
			System.out.println(c.getRed());
			System.out.println(c.getGreen());
			System.out.println(c.getBlue());
		}
	}

}