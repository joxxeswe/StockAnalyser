package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.paint.Color;
/**
 * Abstract class that all indicators must implement!.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public abstract class AbstractIndicator {

	protected double width;
	protected double height;
	protected ArrayList<OHLC> data;
	protected Color color;
	protected int timeFrame;
	
	/**
	 * Constructor
	 * @param timeFrame Sets the timeframe for the indicator.
	 * @param color The color the indicator should be drawn with.
	 */
		public AbstractIndicator(int timeFrame,Color color){
			this.timeFrame = timeFrame;
			this.color = color;
			this.height = StockChart.HEIGHT_OF_INSTRUMENT;
		}
	public double getHeight() {
		return height;
	}
	/**
	 * Returns a label for the indicator, shown on the chart.
	 * @return a label for the indicator, shown on the chart.
	 */
	public abstract String getLabel();
	public abstract double getValueAtPos(int xPos);
	public double getWidth() {
		return width;
	}
	public void setData(ArrayList<OHLC> data){
		this.data = data;
	}
	
	/**
	 * The height of the instrument
	 * @param height height of the instrument
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * The width of the instrument
	 * @param width width of the instrument
	 */
	public void setWidth(double width) {
		this.width = width;
	}
}
