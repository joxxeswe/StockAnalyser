package com.joxxe.analyser.model;
/**
 * A class that stores info about a search on nordnet.
*  @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class SearchResult {

	private String name;
	private String currency;
	private String identifier;
	private String url;
	private String marketId;
	private String marketName;
	public SearchResult(String name,String currency,String identifier,String url,String marketId,
			String marketName){
		this.name = name;
		this.currency = currency;
		this.identifier = identifier;
		this.url = url;
		this.marketId = marketId;
		this.marketName = marketName;
	}
	public String getName() {
		return name;
	}

	public String getCurrency() {
		return currency;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getMarketId() {
		return marketId;
	}
	
	public String getMarketName() {
		return marketName;
	}
	
}
