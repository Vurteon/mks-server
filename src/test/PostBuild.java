package test;

import test.utils.PostTest;
import utils.CreateJson;
import utils.PartFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 2014/8/19.
 */
public class PostBuild {

	static String testUrl = "http://localhost:8080/UploadPhoto";

	static String BOUNDARY = "---------------------------7de8c1a80910";

	public static void main(String[] args) throws IOException, InterruptedException {

		for (int i = 0; i < 10; i++) {
			new Thread(new PostTest()).start();
		}

		TimeUnit.SECONDS.sleep(10);

		System.out.println(PostTest.asds.size());

	}
}
