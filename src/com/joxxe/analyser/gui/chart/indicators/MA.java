package com.joxxe.analyser.gui.chart.indicators;

import java.util.ArrayList;

import com.joxxe.analyser.model.stock.OHLC;

import javafx.scene.paint.Color;

public class MA extends AbstractOnGraphInstrument {

	public MA(int timeFrame,Color color) {
		super(timeFrame,color);
	}

	public void calculate(ArrayList<OHLC> stockData) {
		this.data = new double[stockData.size()];
		double ma;
		for (int i = 0; i < stockData.size(); i++) {
			ma = -1;
			// do we have prev data?
			if (i - timeFrame >= 0) {
				//i = 199
				ma = 0;
				for (int j = 0; j < timeFrame; j++) {
					ma += stockData.get(i-j).getClose();
				}
								
			}
			data[i] = (ma / timeFrame);
			if (data[i] < 0) {
				data[i] = -1;
			}
		}
	}

	@Override
	public String getLabel() {
		return "MA" +timeFrame;
	}

}
