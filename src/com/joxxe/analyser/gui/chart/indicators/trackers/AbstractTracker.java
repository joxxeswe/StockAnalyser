package com.joxxe.analyser.gui.chart.indicators.trackers;

import java.util.ArrayList;
import java.util.Arrays;

import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
/**
 * 
 * @author joakim
 *
 */
public abstract class AbstractTracker {

	protected int timeFrame;
	protected double[] data;
	protected double[] zoomData;
	protected Color color;

	public AbstractTracker(int timeFrame,Color color){
		this.timeFrame = timeFrame;
		this.color = color;
	}
	
	public int getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(int timeFrame) {
		this.timeFrame = timeFrame;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double[] getData() {
		return this.data;
	}
	public void drawGraphInstrument(GraphicsContext gc,int startIndex,int endIndex,double w,double h,double min,double max) {
		zoomData = Arrays.copyOfRange(this.data, startIndex, endIndex+1);
		//draw it
		gc.setStroke(this.color);
		gc.setLineWidth(1);
		double xScale = w / (zoomData.length);
		double yScale = h / (max - min);
		for (int i = 0; i < zoomData.length - 1; i++) {
			double x1 = (i * xScale);
			double y1 = ((max - zoomData[i]) * yScale);
			double x2 = ((i + 1) * xScale);
			double y2 = ((max - zoomData[i+1]) * yScale);
			if(zoomData[i] != -1){
				gc.strokeLine(x1, y1, x2, y2);
			}
			if(i==zoomData.length-2){//last loop
				gc.strokeLine(x1+xScale, y1, x2+xScale, y2);
			}
		}
		
	}
	/**
	 * Calculates data, remember to set data, minData and maxData.
	 * @param stockData Data to calculate from.
	 */
	public abstract void calculate(ArrayList<OHLC> stockData);

	public abstract String getLabel();

	public double getValueAtPos(int xPos) {
		return zoomData[xPos];
	}
}
