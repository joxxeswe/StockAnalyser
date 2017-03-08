package com.joxxe.analyser.model.stock;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
	private HashMap<String,OHLC> values;


	public Stock(String name,String marketId,String identifier,String url) {
		this.latestAddedValue = null;
		this.values = new HashMap<>();
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



	public void addQouteDay(OHLC q) {
		// check so no day is added two times.
		if(values.containsKey(q.getDate())){
			//already contains, update it!
			OHLC v = values.get(q.getDate());
			v.setClose(q.getClose());
			v.setHigh(q.getHigh());
			v.setLow(q.getLow());
			v.setOpen(q.getOpen());
			v.setVolume(q.getVolume());
		}else{
			//add it
			values.put(q.getDate(), q);
		}
		if(latestAddedValue == null ||
				q.getDateAsDate().compareTo(latestAddedValue) >= 0){
			latestAddedValue = q.getDateAsDate();
		}
	}
	
	public ArrayList<OHLC> getQuoteDays() {
		ArrayList<OHLC> list = new ArrayList<OHLC>(values.values());
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
	
	public Date getLatestUpdate(){
		return latestAddedValue;
		
	}

	@Override
	public String toString() {
		return "<-- " + name.getValue() + "-->\n" + values.toString() + "\n\n";
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getName());
		out.writeObject(getIdentifier());
		out.writeObject(getMarketId());
		out.writeObject(getUrl());
		out.writeObject(latestAddedValue);
		out.writeObject(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setName(new SimpleStringProperty((String) in.readObject()));
		setIdentifier(new SimpleStringProperty((String) in.readObject()));
		setMarketId(new SimpleStringProperty((String) in.readObject()));
		setUrl(new SimpleStringProperty((String) in.readObject()));
		latestAddedValue = (Date) in.readObject();
		values = (HashMap<String,OHLC>) in.readObject();
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
