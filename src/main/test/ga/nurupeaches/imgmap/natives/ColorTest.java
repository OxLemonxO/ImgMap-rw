package ga.nurupeaches.imgmap.natives;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ColorTest {

	public static void main(String[] args){
        BufferedImage image = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_RGB);
        for(int x=0; x < 4096; x++){
            for(int y=0; y < 4096; y++){
                image.setRGB(x, y, -(x + 4096 * y));
            }
        }

        JFrame frame = new JFrame();
        frame.setSize(1920, 1080);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.show();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}