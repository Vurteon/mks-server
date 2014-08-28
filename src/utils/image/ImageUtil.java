package utils.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * author: 康乐
 * time: 2014/8/28
 * function: 压缩图片，输入一组带有图片的输入流，返回一个压缩后存放图片的byte数组
 */
public class ImageUtil {

	/**
	 * 压缩图片并返回压缩后的byte数组
	 * @param inputStream 输入流，只要是inputStream均可
	 * @return 返回一个带有压缩后图片内容的byte数组
	 * @throws IOException
	 */
	public static byte[] getCompressedImage(InputStream inputStream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(inputStream);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}


}
