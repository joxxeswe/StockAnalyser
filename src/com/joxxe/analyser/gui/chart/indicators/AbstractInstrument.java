package com.joxxe.analyser.gui.chart.indicators;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
/**
 * Abstratct class that you should extend if youre developing an instrument
 * to show below the stock chart.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public abstract class AbstractInstrument extends AbstractIndicator {



	public AbstractInstrument(int timeFrame, Color color) {
		super(timeFrame, color);
	}
	public abstract void draw(GraphicsContext gc, double startY,int startIndex,int endIndex);
	public abstract double getValueAtPos(int xPos);
	public abstract String getLabel();

	
}
