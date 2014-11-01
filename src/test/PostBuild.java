package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import test.utils.LoadTest;
import test.utils.PostPhotoTest;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by leon on 2014/8/19.
 */
public class PostBuild {

//	private static final Logger logger = LogManager.getLogger(PostBuild.class);

	public static void main(String[] args) throws IOException, InterruptedException {

//		for (int i = 0; i < 20;i++)
		new Thread(new PostPhotoTest()).start();
	}
}
