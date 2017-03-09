package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.canvas.GraphicsContext;
/**
 * Shows the volume for a instrument
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class Volume {

	private double width;
	private double height = StockChart.HEIGHT_OF_INSTRUMENT;
	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}
	
	public void draw(GraphicsContext gc,ArrayList<OHLC> data, double startY) {
		double minVol = Double.MAX_VALUE;
		double maxVol = Double.MIN_VALUE;
		boolean noVolume = false;
		for (OHLC v : data) {
			if (v.getVolume() == -1) {
				noVolume = true;
				break;
			}
			if (v.getVolume() < minVol) {
				minVol = v.getVolume();
			}
			if (v.getVolume() > maxVol) {
				maxVol = v.getVolume();
			}

		}
		gc.clearRect(0, startY, this.width, this.height);
		if (!noVolume) {
			gc.setStroke(StockChart.CHART_LINE_COLOR);
			gc.setFill(StockChart.CHART_BACKGROUND_COLOR);
			gc.fillRect(0, startY,  this.width,  this.height);
			gc.setFill(StockChart.CHART_LABEL_COLOR);
			gc.fillText("Volume", 10, startY, 100);
			gc.setFill(StockChart.VOLUME_COLOR);
			double scale = ( this.height) / (maxVol - minVol);
			for (int i = 0; i < data.size(); i++) {
				double barWidth =  this.width / (data.size());
				double barHeight = data.get(i).getVolume() * scale;
				double x = (i * barWidth);
				double y = startY +  this.height - barHeight;
				gc.fillRect(x, y, barWidth, barHeight);
			}
		}

	}


}
