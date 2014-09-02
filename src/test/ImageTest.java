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



		File f = new File("E:/previous-24.png");
		BufferedImage bi = ImageIO.read(f);


		System.out.println(bi.getHeight());

		// 下面的代码是可以将图片压缩并缩放到指定的大小


//		float ii = bi.getWidth()/10.0f;

//		System.out.println(ii);

//		float asd = (bi.getHeight() / ii);

//		System.out.println(asd);


		// 下面三行代码属于CPU密集型
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.SCALE_SMOOTH);
		Graphics graphics = image.getGraphics();
		graphics.drawImage(bi, 0, 0, 10, 10, null);


		// 下面两行代码属于IO密集型
		File ff = new File("E:/test2.jpg");
		ImageIO.write(image,"jpg",ff);


	}



}
