package com.joxxe.analyser.gui.pane;

import com.joxxe.analyser.model.SearchResult;
import com.joxxe.analyser.model.stock.Stock;
import com.joxxe.analyser.model.stock.StockHandler;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
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
		ListView<SearchResult> list = new ListView<>(sh.getSearchResults());
		list.prefWidthProperty().bind(parent.prefWidthProperty());
		list.getStyleClass().add("results");
		box0.getChildren().addAll(list);
		this.getChildren().addAll(box0);
		
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
                ContextMenu contextMenu = new ContextMenu();
				MenuItem deleteItem = new MenuItem();
	            deleteItem.setText("Add stock");
	            deleteItem.setOnAction(event -> addStock(sh, cell.getItem()));
	            contextMenu.getItems().addAll(deleteItem);
	            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
	                if (isNowEmpty) {
	                    cell.setContextMenu(null);
	                } else {
	                    cell.setContextMenu(contextMenu);
	                }
	            });
                return cell;
            }
        });
		
	}

	private void addStock(StockHandler sh, SearchResult s) {
		if(s != null){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					sh.addStock(new Stock(s.getName(), s.getMarketId(), s.getIdentifier(), s.getUrl()));
				}
			});
			
		}
	}
}
