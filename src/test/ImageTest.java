package test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by leon on 2014/8/17.
 */
public class ImageTest {

	public static void main(String[] args) throws IOException {
		String readFormats[] = ImageIO.getReaderFormatNames();
		String writeFormats[] = ImageIO.getWriterFormatNames();
		System.out.println("Readers: " + Arrays.asList(readFormats));
		System.out.println("Writers: " + Arrays.asList(writeFormats));

		int w = 1632;

		int h = 920;

		File f = new File("E:/test.jpg");
		BufferedImage bi = ImageIO.read(f);


		// 下面的代码是可以将图片压缩并缩放到指定的大小

		BufferedImage image = new BufferedImage(w, h, BufferedImage.SCALE_SMOOTH);
		Graphics graphics = image.getGraphics();
		graphics.drawImage(bi, 0, 0, w, h, null);


		File ff = new File("E:/test2.jpg");
		ImageIO.write(image,"jpg",ff);


	}



}
