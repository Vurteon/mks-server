package test;

import utils.json.JSONObject;

/**
 * Created by leon on 2014/7/15.
 */
public class JSON_TEST {
	public static void main(String[] args) {

		String json = "{'name':{'first-name':'Leon','last-name':'kl'},'age':20}";


		JSONObject jsonObject = new JSONObject(json);


		System.out.println(jsonObject.getJSONObject("name").getString("last-name"));

		System.out.println(jsonObject.toString());

	}
}
