package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.canvas.GraphicsContext;
/**
 * Abstratct class that you should extend if youre developing an instrument
 * to show below the stock chart.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public abstract class AbstractIntrument {

	public static double instrumentGraphHeight	= 80;
	protected double width;
	protected double height;
	protected ArrayList<OHLC> data;
	
	public double getWidth() {
		return width;
	}
	public void setData(ArrayList<OHLC> data){
		this.data = data;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}

	public AbstractIntrument(){
		this.height = instrumentGraphHeight;
	}
	public abstract void draw(GraphicsContext gc, double startY,int startIndex,int endIndex);
	public abstract double getValueAtPos(int xPos);
	public abstract String getLabel();

	
}
