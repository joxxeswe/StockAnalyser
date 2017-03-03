package com.joxxe.analyser.model.stock;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;

public class Stock implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4769334868506811919L;
	private SimpleStringProperty name;
	private SimpleStringProperty url;
	private SimpleStringProperty marketId;
	private SimpleStringProperty identifier;
	
	private ArrayList<Values> values = new ArrayList<Values>();

	public Stock(String name,String marketId,String identifier,String url) {
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



	public boolean addQouteDay(Values q, boolean checkForMultiple) {
		// check so no day is added two times.
		if(checkForMultiple){
			int i = values.lastIndexOf(q);
			if(i != -1){ //SPEED UP!
				values.get(i).setClose(q.getClose());
				values.get(i).setHigh(q.getHigh());
				values.get(i).setLow(q.getLow());
				values.get(i).setOpen(q.getOpen());
				values.get(i).setVolume(q.getVolume());
				return true;
			}
		}else{
			values.add(q);
			return true;
		}
		return false;
		
	}
	
	public void sortQuoteDays(boolean acending){
		values.sort(new Comparator<Values>() {

			@Override
			public int compare(Values o1, Values o2) {
				if(o1.getDateAsDate().equals(o2.getDateAsDate())){
					//equal
					return 0;
				}else if(o1.getDateAsDate().before(o2.getDateAsDate())){
					if(acending){
						return 1;
					}else{
						return -1;
					}
				}else{
					if(acending){
						return -1;
					}else{
						return 1;
					}
				}
			}
		});
	}

	public ArrayList<Values> getQuoteDays() {
		return values;
	}
	
	public Date getLatestUpdate(){
		if(values.size() > 0){
			return values.get(values.size()-1).getDateAsDate();
		}
		return null;
		
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
		out.writeObject(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setName(new SimpleStringProperty((String) in.readObject()));
		setIdentifier(new SimpleStringProperty((String) in.readObject()));
		setMarketId(new SimpleStringProperty((String) in.readObject()));
		setUrl(new SimpleStringProperty((String) in.readObject()));
		values = (ArrayList<Values>) in.readObject();

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
