package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.paint.Color;
/**
 * Calculate EMA for a instrument.
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class EMA extends AbstractOnGraphInstrument {

	public EMA(int timeFrame, Color color) {
		super(timeFrame, color);
	}

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
