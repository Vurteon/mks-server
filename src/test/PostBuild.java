package test;

import test.utils.LoadTest;

import java.io.*;

/**
 * Created by leon on 2014/8/19.
 */
public class PostBuild {


	public static void main(String[] args) throws IOException, InterruptedException {


		new Thread(new LoadTest()).start();


	}
}
