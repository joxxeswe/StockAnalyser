package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.paint.Color;

public abstract class AbstractIndicator {

	protected double width;
	protected double height;
	protected ArrayList<OHLC> data;
	protected Color color;
	protected int timeFrame;
	
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getWidth() {
		return width;
	}
	public void setData(ArrayList<OHLC> data){
		this.data = data;
	}
	public AbstractIndicator(int timeFrame,Color color){
		this.timeFrame = timeFrame;
		this.color = color;
		this.height = StockChart.instrumentGraphHeight;
	}
	
	public abstract String getLabel();

	public abstract double getValueAtPos(int xPos);
}
