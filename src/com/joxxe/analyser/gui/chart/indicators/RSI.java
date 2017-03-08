package com.joxxe.analyser.gui.chart.indicators;

import java.util.Arrays;
import com.joxxe.analyser.gui.chart.StockChart;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RSI extends AbstractIntrument {

	private int time;
	private double[] rsi;

	public RSI(int time) {
		this.time = time;
	}

	private double[] calculateRSI() {
		double gain[] = new double[data.size()];
		double loss[] = new double[data.size()];
		for (int i = 1;i<data.size();i++) {
			double diff = data.get(i).getClose() - data.get(i - 1).getClose();
			gain[i] = (diff > 0) ? diff : 0;
			loss[i] = (diff < 0) ? -diff : 0;
		}
		// calculate avg gain & avg loss
		double avGain;
		double avLoss;
		double rs;
		this.rsi = new double[data.size()];
		for (int i = time; i < gain.length; i++) {
			if (i==time) { //first loop
				double aG = 0;
				double aL = 0;
				for (int j = 1; j < time+1; j++) { 
					aG += gain[j];
					aL += loss[j];
				}
				avGain = aG / time;
				avLoss = aL / time;
			} else {
				avGain = ((gain[i-1] * (time-1)) + gain[i]) / time;
				avLoss = ((loss[i-1] * (time-1)) + loss[i]) / time;
				
			}
			gain[i] = avGain;
			loss[i] = avLoss;
			rs = avGain / avLoss;
			rsi[i] = (avLoss == 0) ? 100 : (100 - (100 / (1 + rs)));	
		}
		return rsi;
	}

	@Override
	public void draw(GraphicsContext gc, double startY, int startIndex, int endIndex) {
		double minVal = 0;
		double maxVal = 100;
		double[] temp = calculateRSI();
		rsi = Arrays.copyOfRange(temp, startIndex, endIndex+1);
		gc.clearRect(0, startY, width, height);
		gc.setFill(StockChart.graphBackgroundColor);
		gc.fillRect(0, startY, width, height);
		gc.setFill(StockChart.labelColor);
		gc.fillText("RSI", 10, startY + 20, 100);
		double yScale = height / (maxVal - minVal);
		double xScale = width / (endIndex-startIndex);
		gc.setLineWidth(1);
		//30 70 bar
		drawHorizontalBars(gc, startY, maxVal, yScale);
		gc.setStroke(StockChart.graphColor);
		gc.setLineWidth(1);
		for (int i = 0; i < rsi.length-1; i++) {
			double x1 = (i * xScale);
			double y1 = ((maxVal - rsi[i]) * yScale) + startY;
			double x2 = ((i + 1) * xScale);
			double y2 = ((maxVal - rsi[i+1]) * yScale) + startY;
			// double y3 = startY + 20;
			gc.strokeLine(x1, y1, x2, y2);
		}
	}

	private void drawHorizontalBars(GraphicsContext gc, double startY, double maxVal, double yScale) {
		double y1 = ((maxVal - 30) * yScale) + startY;
		double y2 = ((maxVal - 70) * yScale) + startY;
		double x1 = 0;
		double x2 = width;
		gc.setStroke(Color.RED);
		gc.strokeLine(x1,y1,x2,y1);
		gc.strokeLine(x1,y2,x2,y2);
	}

	@Override
	public double getValueAtPos(int pos) {
		return rsi[pos];
	}

	@Override
	public String getLabel() {
		return "rsi " + time + ":";
	}

}
