package com.joxxe.analyser.gui.pane;

import java.lang.reflect.InvocationTargetException;

import com.joxxe.analyser.gui.UserChoice;
import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.gui.chart.indicators.AbstractInstrument;
import com.joxxe.analyser.gui.chart.indicators.AbstractOnGraphInstrument;
import com.joxxe.analyser.model.stock.Stock;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * The pane that shows a graph for a stock.
 * 
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class StockPane extends Pane {
	private StockChart chart;

	public StockPane(Pane parent) {
		VBox box = new VBox();
		box.getStyleClass().add("stockchart");
		chart = new StockChart();
		chart.prefWidthProperty().bind(parent.prefWidthProperty());
		chart.prefHeightProperty().bind(parent.prefHeightProperty());
		box.getChildren().addAll(chart);
		this.getChildren().addAll(box);
		//

	}

	public void setStock(Stock s) {
		chart.setData(s);
	}
	
	public void addIndicator(UserChoice c){
		String name = c.getSelectedClass();
		Object obj;
		try {
			obj = Class.forName(name).getConstructor(Integer.TYPE,Color.class).newInstance(c.getValues().get(0),c.getColor());
			if(obj instanceof AbstractInstrument){
				chart.addIndicator((AbstractInstrument)obj);
			}else if(obj instanceof AbstractOnGraphInstrument){
				chart.addIndicator((AbstractOnGraphInstrument)obj);
			}
			chart.redraw();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//chart.addIndicator(c);
	}
}
