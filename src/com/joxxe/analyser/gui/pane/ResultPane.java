package com.joxxe.analyser.gui.pane;

import com.joxxe.analyser.model.SearchResult;
import com.joxxe.analyser.model.stock.Stock;
import com.joxxe.analyser.model.stock.StockHandler;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
/**
 * Pane that shows results from searching nordnet.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class ResultPane extends Pane {

	public ResultPane(StockHandler sh, Pane parent){
		VBox box0 = new VBox();
		box0.getStyleClass().add("background");
		box0.prefWidthProperty().bind(parent.prefWidthProperty());
		box0.prefHeightProperty().bind(parent.prefHeightProperty());
		Button addButton = new Button("Add selected");
		ListView<SearchResult> list = new ListView<>(sh.getSearchResults());
		list.prefWidthProperty().bind(parent.prefWidthProperty());
		list.getStyleClass().add("results");
		box0.getChildren().addAll(list,addButton);
		this.getChildren().addAll(box0);
		//add selected stock to list of stocks.
		addButton.setOnAction(event ->{
			SearchResult s = list.getSelectionModel().getSelectedItem();
			if(s != null){
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						sh.addStock(new Stock(s.getName(), s.getMarketId(), s.getIdentifier(), s.getUrl()));
					}
				});
				
			}
            
		});
		
		list.setCellFactory(new Callback<ListView<SearchResult>, ListCell<SearchResult>>(){

            @Override
            public ListCell<SearchResult> call(ListView<SearchResult> p) {
                
                ListCell<SearchResult> cell = new ListCell<SearchResult>(){

                    @Override
                    protected void updateItem(SearchResult t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName() + " (" + t.getMarketName() + ")");
                        }else{
                        	setText(null);
                        }
                    }

                };
                
                return cell;
            }
        });
		
	}
}
