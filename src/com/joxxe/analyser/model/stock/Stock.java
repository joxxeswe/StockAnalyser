package com.joxxe.analyser.model.stock;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a stock storing OHLC and volume for each day.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class Stock implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4769334868506811919L;
	private SimpleStringProperty name;
	private SimpleStringProperty url;
	private SimpleStringProperty marketId;
	private SimpleStringProperty identifier;
	private Date latestAddedValue;
	private Map<Integer,OHLC> data;


	public Stock(String name,String marketId,String identifier,String url) {
		this.latestAddedValue = null;
		this.data = new HashMap<>();
		setName(new SimpleStringProperty(name));
		setMarketId(new SimpleStringProperty(marketId));
		setIdentifier(new SimpleStringProperty(identifier));
		setUrl(new SimpleStringProperty(url));
	}
	
	public Stock(){
		
	}

	public String getName() {
		return name.get();
	}

	private void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public String getUrl() {
		return url.get();
	}

	private void setUrl(SimpleStringProperty url) {
		this.url = url;
	}

	public String getMarketId() {
		return marketId.get();
	}

	private void setMarketId(SimpleStringProperty marketId) {
		this.marketId = marketId;
	}

	public String getIdentifier() {
		return identifier.get();
	}

	public void setIdentifier(SimpleStringProperty identifier) {
		this.identifier = identifier;
	}

	public void addOHLC(OHLC o) {
		
		// check so no day is added two times.
		try{
		int key = o.getDateAsNumber();
		OHLC foundOHLC = data.get(key);
		if(foundOHLC!=null){
			foundOHLC.setClose(o.getClose());
			foundOHLC.setHigh(o.getHigh());
			foundOHLC.setLow(o.getLow());
			foundOHLC.setOpen(o.getOpen());
			foundOHLC.setVolume(o.getVolume());
		}else{
			data.put(o.getDateAsNumber(), o);
		}
		if(latestAddedValue == null ||
				o.getDateAsDate().compareTo(latestAddedValue) >= 0){
			latestAddedValue = o.getDateAsDate();
		}
		}catch(NumberFormatException e){
			//do not add, invalid date...
		}
		
	}
	
	public ArrayList<OHLC> getQuoteDays() {
		ArrayList<OHLC> list = new ArrayList<OHLC>(data.values());
		list.sort(new Comparator<OHLC>() {

			@Override
			public int compare(OHLC o1, OHLC o2) {
				if(o1.getDateAsDate().equals(o2.getDateAsDate())){
					return 0;
				}else if(o1.getDateAsDate().before(o2.getDateAsDate())){
					return -1;
				}else{
					return 1;
				}
			}
		});
		return list;
	}
	/**
	 * Returns a copy of the latest time the stock is updated.
	 * @return a copy of the latest time the stock is updated or null if no update.
	 */ 
	public Date getLatestUpdate(){
		if(latestAddedValue==null){
			return null;
		}
		return new Date(latestAddedValue.getTime());
		
	}

	@Override
	public String toString() {
		return "<-- " + name.getValue() + "-->\n" + data.toString() + "\n\n";
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getName());
		out.writeObject(getIdentifier());
		out.writeObject(getMarketId());
		out.writeObject(getUrl());
		out.writeObject(latestAddedValue);
		out.writeObject(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setName(new SimpleStringProperty((String) in.readObject()));
		setIdentifier(new SimpleStringProperty((String) in.readObject()));
		setMarketId(new SimpleStringProperty((String) in.readObject()));
		setUrl(new SimpleStringProperty((String) in.readObject()));
		latestAddedValue = (Date) in.readObject();
		data = (HashMap<Integer,OHLC>) in.readObject();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stock other = (Stock) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.getValue().equals(other.identifier.getValue()))
			return false;
		return true;
	}


}
