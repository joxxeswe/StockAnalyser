package com.joxxe.analyser.model.stock;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
/**
 * Represents daily values for a stock (open,high,low,close,volume,date).
*  @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class OHLC implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1194336592470949744L;
	private SimpleObjectProperty<Date> date;
	private SimpleDoubleProperty volume;
	private SimpleDoubleProperty open;
	private SimpleDoubleProperty close;
	private SimpleDoubleProperty low;
	private SimpleDoubleProperty high;
	

	
	public OHLC(){
		
	}

   
	
	public OHLC(Date date, double close, double high, double low, double open,
			double volume) {
		setDate(date);
		setClose(close);
		setHigh(high);
		setLow(low);
		setOpen(open);
		setVolume(volume);
	}

	public OHLC(Date date, double close, double high, double low, double open) {
		setDate(date);
		setClose(close);
		setHigh(high);
		setLow(low);
		setOpen(open);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OHLC) {
			return ((OHLC)obj).getDate().equalsIgnoreCase(this.getDate());
		}
		return false;
	}

	public double getClose() {
		return close.get();
	}

	public Date getDateAsDate() {
		return date.get();
	}
	
	public String getDate(){
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		return f.format(date.get());
	}

	/**
	 * Returns an integer yyyyMMdd.
	 * @return
	 */
	public int getDateAsNumber(){
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(f.format(date.get()));
	}
	
	public double getHigh() {
		return high.get();
	}

	public double getLow() {
		return low.get();
	}

	public double getOpen() {
		return open.get();
	}

	public double getVolume() {
		if(volume == null){
			return -1;
		}else{
			return volume.get();
		}
		
	}

	public void setClose(double close) {
		this.close = new SimpleDoubleProperty(close);
	}

	private void setDate(Date date) {
		this.date = new SimpleObjectProperty<Date>(date);
	}

	public void setHigh(double high) {
		this.high = new SimpleDoubleProperty(high);
	}

	public void setLow(double low) {
		this.low = new SimpleDoubleProperty(low);
	}

	public void setOpen(double open) {
		this.open = new SimpleDoubleProperty(open);
	}

	public void setVolume(double volume) {
		this.volume = new SimpleDoubleProperty(volume);
	}
	


	@Override
	public String toString() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		return "--> date=" + f.format(date.get()) + ", volume=" + volume + ", open=" + open + ", close=" + close + ", low=" + low + ", high=" + high + " \n";
	}




	 @Override
	    public void writeExternal(ObjectOutput out) throws IOException {
	        out.writeObject(getDateAsDate());
	        
	        out.writeDouble(getOpen());
	        out.writeDouble(getClose());
	        out.writeDouble(getLow());
	        out.writeDouble(getHigh());
	        out.writeDouble(getVolume());
	    }

	    @Override
	    public void readExternal(ObjectInput in) throws IOException,
	            ClassNotFoundException {
	        setDate((Date)in.readObject());
	        
	        setOpen(in.readDouble());
	        setClose(in.readDouble());
	        setLow(in.readDouble());
	        setHigh(in.readDouble());
	        setVolume(in.readDouble()); //TODO opptional
	    }
}
