package com.androidiansoft.gaming.yahtzee.activities;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.androidiansoft.gaming.yahtzee.data.Game;
import com.androidiansoft.gaming.yahtzee.data.Roll;
import com.androidiansoft.gaming.yahtzee.data.Turn;
import com.androidiansoft.gaming.yahtzee.data.User;

import android.util.Log;

public class XMLParser {

	StringBuffer strBuf = new StringBuffer();
	static String type;
	String answer;

	// Place data in hashmap and send request to service
	static public void createGame(String url, Game newGame) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs
					.add(new BasicNameValuePair("name", newGame.getName()));
			nameValuePairs.add(new BasicNameValuePair("connected", newGame
					.getConnectedUser() + ""));
			nameValuePairs.add(new BasicNameValuePair("created", newGame
					.getCreatedUser() + ""));
			nameValuePairs.add(new BasicNameValuePair("singleplayer", newGame
					.getSinglePlayer() + ""));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Place data in hashmap and send request to service
	static public void createRoll(String url, Roll newRoll) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
			nameValuePairs.add(new BasicNameValuePair("die1", newRoll.getDie1()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("die2", newRoll.getDie2()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("die3", newRoll.getDie3()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("die4", newRoll.getDie4()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("die5", newRoll.getDie5()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("rollNum", newRoll
					.getRollNum() + ""));
			nameValuePairs.add(new BasicNameValuePair("turn", newRoll.getTurn()
					+ ""));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Place data in hashmap and send to service
	static public void createTurnAndUpdateGame(String url, Turn newTurn, Game newGame) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("game", newTurn
					.getGameId() + ""));
			nameValuePairs.add(new BasicNameValuePair("user", newTurn
					.getUserId() + ""));
			nameValuePairs.add(new BasicNameValuePair("selected", newTurn
					.getScoreSelected() + ""));
			nameValuePairs.add(new BasicNameValuePair("points", newTurn
					.getPointsForScore() + ""));
			nameValuePairs.add(new BasicNameValuePair("id", newGame.getId()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("turn", newGame.getTurn()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("totalpoints", newGame.getCrePts()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("valid", newGame.getValid()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("conBonus", newGame.getConBonus()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("creBonus", newGame.getCreBonus()
					+ ""));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Place data in hashmap and send to service
	static public void updateTurn(String url, Turn newTurn) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("game", newTurn
					.getGameId() + ""));
			nameValuePairs.add(new BasicNameValuePair("user", newTurn
					.getUserId() + ""));
			nameValuePairs.add(new BasicNameValuePair("selected", newTurn
					.getScoreSelected() + ""));
			nameValuePairs.add(new BasicNameValuePair("points", newTurn
					.getPointsForScore() + ""));
			nameValuePairs.add(new BasicNameValuePair("turn", newTurn.getId()
					+ ""));
			httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPut);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Place data in hashmap and send to service
	static public void updateGame(String url, Game newGame) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("id", newGame.getId()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("turn", newGame.getTurn()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("totalpoints", newGame.getCrePts()
					+ ""));
			httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPut);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Place data in hashmap and send to service
	static public void updateFinalGame(String url, Game newGame) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
			nameValuePairs.add(new BasicNameValuePair("id", newGame.getId()
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("crePts", newGame
					.getCrePts() + ""));
			nameValuePairs.add(new BasicNameValuePair("conPts", newGame
					.getConPts() + ""));
			nameValuePairs.add(new BasicNameValuePair("valid", newGame
					.getValid() + ""));
			nameValuePairs.add(new BasicNameValuePair("creBonus", newGame
					.getCreBonus() + ""));
			nameValuePairs.add(new BasicNameValuePair("conBonus", newGame
					.getConBonus() + ""));
			httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpClient.execute(httpPut);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Get xml from GET method on RESTful service
	public String getXmlFromUrlGet(String url) {
		String xml = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}

	// Create GET statement without returning xml
	public void urlGet(String url) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			httpClient.execute(httpGet);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Get DOM element
	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}

	// Return value from element and string tag
	public String getValue(Element item, String str, String parent) {
		answer = null;
		NodeList n = item.getElementsByTagName(str);
		if (item.getElementsByTagName(str).equals(null)) {
			answer = "0";
		} else {
			for (int i = 0; i < n.getLength(); i++) {
				if (n.item(i).getParentNode().getNodeName().equals(parent)) {
					NodeList n1 = item.getElementsByTagName(str);
					answer = this.getElementValue(n1.item(i));
				} else {

				}
			}
		}
		return answer;
	}

	// Get value for element found
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

}
