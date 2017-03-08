package com.joxxe.analyser.gui;

import java.util.ArrayList;
import java.util.Date;

import com.joxxe.analyser.gui.pane.ResultPane;
import com.joxxe.analyser.gui.pane.StockPane;
import com.joxxe.analyser.model.NordnetCrawler;
import com.joxxe.analyser.model.SearchResult;
import com.joxxe.analyser.model.stock.Stock;
import com.joxxe.analyser.model.stock.StockHandler;
import com.joxxe.analyser.threadExecuter.NotifyingThread;
import com.joxxe.analyser.threadExecuter.ThreadExecuter;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * Main GUI window.
 * 
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class MainWindow extends Application {

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		exit();
		super.stop();
	}

	public static final double LIST_WIDTH = 150;
	public static final double TOP_HEIGHT = 20;
	public static final double OUTPUT_HEIGHT = 50;
	protected static final int MAX_SIZE = 50;
	private BorderPane mainPane;
	private ThreadExecuter th;
	private StockHandler stockHandler;
	private ResultPane resultPane;
	private StockPane stockPane;
	private Pane content;
	private static TextArea output;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// set up the gui
		Pane rootPane = new Pane();
		mainPane = new BorderPane();
		Scene scene = new Scene(rootPane, 800, 640);
		scene.getStylesheets().add(getClass().getResource("/styles/Analyser.css").toExternalForm());
		// gui

		VBox mainBox = new VBox();
		TextField searchField = new TextField();
		searchField.setPrefHeight(TOP_HEIGHT);
		output = new TextArea();
		output.prefWidthProperty().bind(mainPane.prefWidthProperty());
		output.setPrefHeight(OUTPUT_HEIGHT);
		output.getStyleClass().add("output");
		searchField.setPromptText("Search...");
		searchField.setPrefWidth(mainPane.getWidth());
		//
		stockHandler = new StockHandler();
		th = new ThreadExecuter();
		MainMenu menu = new MainMenu(this);
		//
		ListView<Stock> list = new ListView<Stock>(stockHandler.getStocks());
		//
		content = new Pane();
		content.getStyleClass().add("background");
		resultPane = new ResultPane(stockHandler,content);
		stockPane = new StockPane(content);
		mainPane.setTop(searchField);
		mainPane.setCenter(content);
		mainPane.setBottom(output);
		rootPane.getChildren().addAll(menu.getMenuBar(), mainPane);


		searchField.setOnAction(e -> {
			NotifyingThread t = new NotifyingThread() {
				@Override
				public void doRun() {
					String str = searchField.getText();
					ArrayList<SearchResult> result = NordnetCrawler.search(str);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stockHandler.clearSearchResults();
							MainWindow.output("Searching for: " + str);
							for (SearchResult s : result) {
								stockHandler.addSearchResult(s);
							}
							setPane(0);
						}
					});

				}
			};
			this.executeRunnable(t);
		});

		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stock>() {

			@Override
			public void changed(ObservableValue<? extends Stock> observable, Stock oldValue, Stock newValue) {
				// user clicked on a stock in the listview.
				stockPane.setStock(newValue);
				setPane(1);
			}
		});
		list.setCellFactory(new Callback<ListView<Stock>, ListCell<Stock>>() {

			@Override
			public ListCell<Stock> call(ListView<Stock> param) {
				ListCell<Stock> cell = new ListCell<Stock>() {
					@Override
					protected void updateItem(Stock t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getName());
						} else {
							setText(null);
						}
					}
				};
				// menu when right clickin on a stock
				ContextMenu contextMenu = new ContextMenu();
				MenuItem deleteItem = new MenuItem();
				deleteItem.setText("Delete");
				deleteItem.setOnAction(event -> stockHandler.removeStock(cell.getItem()));
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

		// redirectOutput();

		//
		primaryStage.setScene(scene);
		primaryStage.show();
		content.prefWidthProperty().bind(rootPane.widthProperty());
		content.prefHeightProperty().bind(rootPane.heightProperty().add(-(TOP_HEIGHT+OUTPUT_HEIGHT)));
		//
		Pane menuLeft = createLeftMenu(200, list);
		list.prefHeightProperty().bind(rootPane.heightProperty().add(-300));
		rootPane.getChildren().add(menuLeft);
		
		
		//
		double y= (rootPane.getHeight()/2)-(list.getPrefHeight()/2);
		if(y < 0 ){
			y = 0;
		}
		menuLeft.setLayoutY(y);
		menuLeft.setTranslateX(-200);
		mainPane.requestFocus();

	}

	private Pane createLeftMenu(double width,ListView<Stock> list) {
		Pane p = new Pane();
		p.getStyleClass().add("left-menu");
		Button btn = new Button(">");
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER_LEFT);
		VBox menuLeft = new VBox();
		menuLeft.setMaxWidth(width);
		menuLeft.setMinWidth(width);
		list.setMaxWidth(width);
		list.setMinWidth(width);
		menuLeft.setFillWidth(true);
		menuLeft.getChildren().add(list);
		TranslateTransition openNav=new TranslateTransition(new Duration(350), p);
        TranslateTransition closeNav=new TranslateTransition(new Duration(350), p);
		btn.setOnAction(Event -> {
			System.out.println("X:" + p.getTranslateX());
			if(p.getTranslateX()<0){
				btn.setText("<");
				openNav.setToX(0);
                openNav.play();
            }else{
            	btn.setText(">");
                closeNav.setToX(-width);
                closeNav.play();
            }
			

		});
		hbox.getChildren().addAll(menuLeft,btn);
		p.getChildren().add(hbox);
		
		return p;
	}

	/**
	 * Sets witch pane to be shown on the right side in the gui.
	 * 
	 * @param id
	 *            0-1, where 0 is the search result, 1 is the stocktab.
	 */
	private void setPane(int id) {
		content.getChildren().clear();
		switch (id) {
		case 0:
			content.getChildren().addAll(resultPane);
			break;
		case 1:
			content.getChildren().addAll(stockPane);
			break;

		default:
			break;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Method to execute another thread
	 * 
	 * @param t
	 *            Thread to execute.
	 */
	public void executeRunnable(NotifyingThread t) {
		th.execute(t);
	}

	/**
	 * Method for printing text in output field.
	 * 
	 * @param text
	 *            Text to print.
	 */
	public static void output(String text) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// output.insertText(0, text + "\n");
				String txt = output.getText();
				String[] lines = txt.split("\n");
				String str = text + "\n";
				for (int i = 0; i < MAX_SIZE - 1; i++) {
					if (i < lines.length) {
						str = str + lines[i] + "\n";
					} else {
						break;
					}

				}
				output.setText(str);
			}
		});
	}

	/**
	 * Method that updates all stocks.
	 */
	public void updateAllStocks() {
		NotifyingThread t = new NotifyingThread() {
			@Override
			public void doRun() {
				stockHandler.updateStocks(new Date());
			}
		};
		this.executeRunnable(t);

	}

	public void exit() {
		stockHandler.save();
		th.exit();
		Platform.exit();
		System.exit(1);

	}
}
