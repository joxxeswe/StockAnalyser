package com.joxxe.analyser.gui.pane;

import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.model.stock.Stock;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
}
