package test;

import test.utils.LoadTest;
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


	public static void main(String[] args) throws IOException, InterruptedException {


		new Thread(new LoadTest()).start();


	}
}
