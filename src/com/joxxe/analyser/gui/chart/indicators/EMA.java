package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.paint.Color;

public class EMA extends AbstractOnGraphInstrument {

	public EMA(int timeFrame, Color color) {
		super(timeFrame, color);
	}

	/**
	 * 
	 * 
	 * 
	 * SMA: 10 period sum / 10
	 * 
	 * Multiplier: (2 / (Time periods + 1) ) = (2 / (10 + 1) ) = 0.1818 (18.18%)
	 * Price [today] x K) + (EMA [yesterday] x (1 – K)) EMA: {Close -
	 * EMA(previous day)} x multiplier + EMA(previous day).
	 * 
	 * =const*(close4-prevema)+prevema
	 */
	public void calculate(ArrayList<OHLC> stockData) {
		this.data = new double[stockData.size()];
		double sma = 0;
		// calculate sma for first entry
		for (int i = 0; i < timeFrame; i++) {
			sma += stockData.get(i).getClose();
		}
		data[timeFrame - 1] = sma / timeFrame;
		double k = (2 / ((double) timeFrame + 1));
		for (int i = timeFrame; i < stockData.size(); i++) {
			data[i] = k * stockData.get(i).getClose() + (1 - k) * data[i - 1];
		}

	}

	@Override
	public String getLabel() {
		return "EMA" + timeFrame;
	}

}
