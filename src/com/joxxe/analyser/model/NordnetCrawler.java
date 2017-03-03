package com.joxxe.analyser.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joxxe.analyser.gui.MainWindow;
import com.joxxe.analyser.model.stock.Values;
/**
 * Class with static methods that crawls nordnet.se for info.
 *  @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class NordnetCrawler {

	private static String URL = "https://www.nordnet.se/graph/[I]/[M]/[ID]?from=[S]&to=[E]&fields=open,last,high,low,volume";
	private static String SEARCH_URL = "https://www.nordnet.se/search/suggest.html";
	/**
	 * Gets data for a specified stock.
	 * @param identifier Identifier for the stock.
	 * @param marketId MarketId for the stock.
	 * @param startDate Get data from startdate until today.
	 * @return Data for the specified stock.
	 */
	public static ArrayList<Values> getStockData(String identifier, String marketId, Date startDate) {
		return getStockData(identifier, marketId, startDate,new Date());
	}
	/**
	 * Gets data for a specified stock.
	 * @param identifier Identifier for the stock.
	 * @param marketId MarketId for the stock.
	 * @param startDate Get data from startdate
	 * @param endDate Get data before edndate.
	 * @return Data for the specified stock.
	 */
	public static ArrayList<Values> getStockData(String identifier, String marketId, Date startDate,
			Date endDate) {
		ArrayList<Values> data = new ArrayList<Values>();
		String url = getUrl(identifier, marketId, startDate, endDate);
		String response = NordnetCrawler.httpGET(url);
		if (response != null) {
			JSONArray r = new JSONArray(response);
			for (Object day : r) {
				JSONObject item = (JSONObject) day;
				try {
					double high = item.getDouble("high");
					double open = item.getDouble("open");
					double low = item.getDouble("low");
					double close = item.getDouble("last");
					long time = item.getLong("time");
					Date d = new Date(time);
					boolean added = false;
					try {
						// add with volume
						double volume = item.getDouble("volume");
						data.add(new Values(d, close, high, low, open, volume));
						added = true;
					} catch (JSONException e) {
						// add some without volume
						System.err.println("Added without volume");
					}
					if (!added) {
						data.add(new Values(d, close, high, low, open));
					}

				} catch (JSONException e) {
					MainWindow.output("Error parsing row (skipping):" + item.toString());
				}

			}

		}
		return data;

	}
	/**
	 * Method that creates the url to crawl.
	 * @param identifier
	 * @param marketId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static String getUrl(String identifier, String marketId, Date startDate, Date endDate) {
		String t = NordnetCrawler.URL;
		String ss;
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		String strStartDate = dt.format(startDate);
		String strEndDate =dt.format(endDate);
		if (isInt(marketId)) {
			ss = "instrument";
		} else {
			ss = "indicator";
		}
		t = t.replace("[I]", ss);
		t = t.replace("[M]", marketId);
		t = t.replace("[ID]", identifier);
		t = t.replace("[S]", strStartDate);
		t = t.replace("[E]", strEndDate);
		return t;
	}

	/**
	 * Do a http get request to specified
	 * @param url Url to crawl
	 * @return
	 */
	private static String httpGET(String url) {
		String returnString = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			InputStream response = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
			for (String line; (line = reader.readLine()) != null;) {
				returnString += line;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(connection!=null){
				connection.disconnect();
			}
			
		}

		return returnString;
	}

	/**
	 * Method that does a http post to nordnet.
	 * Does it to SEARCH_URL.
	 * @param searchString String send with the post.
	 * @return
	 */
	private static String httpPOST(String searchString){
		HttpURLConnection urlConnection = null;
	    try {
	        URL url = new URL(SEARCH_URL);
	        urlConnection = (HttpURLConnection) url.openConnection();
	        urlConnection.setDoOutput(true);
	        urlConnection.setRequestMethod("POST");
	        urlConnection.setUseCaches(false);
	        urlConnection.setConnectTimeout(5000);
	        urlConnection.setReadTimeout(5000);
	        urlConnection.connect();
	        //You Can also Create JSONObject here 
	        OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
	        out.write("q=" + searchString);// here i sent the parameter
	        out.close();
	        int HttpResult = urlConnection.getResponseCode();
	        if (HttpResult == HttpURLConnection.HTTP_OK) {
	        	
	            BufferedReader br = new BufferedReader(new InputStreamReader(
	                    urlConnection.getInputStream(), "utf-8"));
	            String line = null;
	            Writer sb = new StringWriter();
				while ((line = br.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            br.close();
	            
	            return sb.toString();
	        } else {
	            System.err.println(urlConnection.getResponseMessage());
	        }
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (JSONException e) {
	        e.printStackTrace();
	    } finally {
	        if (urlConnection != null)
	            urlConnection.disconnect();
	    }
	    return null;
	}

	/**
	 * Is the entered string a number?
	 * @param s String
	 * @return True if number, false if not.
	 */
	private static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException er) {
			return false;
		}
	}
	
	/**
	 * Searcheds nordnet for a specified stock.
	 * @param searchString Stock name to search for
	 * @return An arraylist of possible results.
	 */
	public static ArrayList<SearchResult> search(String searchString){
		String result = NordnetCrawler.httpPOST(searchString);
		ArrayList<SearchResult> data = new ArrayList<SearchResult>();
		if(result != null){
			JSONObject r = new JSONObject(result);
			JSONArray instruments = r.getJSONArray("instruments");
			for (Object i : instruments) {
				JSONObject item = (JSONObject) i;
				String name = item.getString("name");
				String currency = item.getString("currency");
				String identifier = item.getString("identifier");
				String url = item.getString("url");
				String marketId = String.valueOf(item.getLong("market_id"));
				String marketName = item.getString("market_name");
				if(url.contains("aktiehemsidan")){ //only add aktier
					data.add(new SearchResult(name, currency, identifier, url, marketId, marketName));
				}
				
			}
		}
		return data;
	}

}
