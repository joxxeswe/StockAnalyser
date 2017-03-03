package com.joxxe.analyser.gui.pane;

import com.joxxe.analyser.gui.MainWindow;
import com.joxxe.analyser.gui.chart.StockChart;
import com.joxxe.analyser.model.stock.Stock;
import com.joxxe.analyser.model.stock.StockHandler;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
/**
 * The pane that shows a graph for a stock.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class StockPane extends Pane {
	private StockChart chart;

	public StockPane(StockHandler sh, Pane parent) {
		VBox box = new VBox();
		box.getStyleClass().add("background");
		Button zoomOut = new Button("Zoom out");
		zoomOut.setOnAction(Action ->{
			chart.zoomOut();
		});
		chart = new StockChart();
		chart.prefWidthProperty().bind(parent.prefWidthProperty().add(-MainWindow.LIST_WIDTH-5));
		chart.prefHeightProperty()
				.bind(parent.prefHeightProperty().add(-MainWindow.OUTPUT_HEIGHT+MainWindow.TOP_HEIGHT));
		box.getChildren().addAll(chart,zoomOut);
		this.getChildren().addAll(box);
		//

	}

	public void setStock(Stock s) {
		chart.setData(s);
	}
}
