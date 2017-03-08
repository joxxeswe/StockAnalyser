package com.joxxe.analyser.gui;

import java.util.Optional;

import com.joxxe.analyser.gui.chart.indicators.EMA;
import com.joxxe.analyser.gui.chart.indicators.MA;
import com.joxxe.analyser.gui.chart.indicators.RSI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * The menu for the gui.
 * 
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class MainMenu {

	private MenuBar menuBar;

	public MainMenu(MainWindow mw) {
		menuBar = new MenuBar();
		String os = System.getProperty("os.name");
		// if on mac, show it in system menu bar.
		if (os != null && os.startsWith("Mac")) {
			menuBar.useSystemMenuBarProperty().set(true);
		}
		Menu menuFile = fileMenu(mw);
		Menu menuAdd = addMenu(mw);
		menuBar.getMenus().addAll(menuFile, menuAdd);

	}


	private Menu addMenu(MainWindow mw) {
		Menu menuAdd = new Menu("Add");
		MenuItem ma = new MenuItem("MA");
		ma.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				SelectDialog d = new SelectDialog(MA.class.getName(),"Moving average","Choose number of days and color for the instrument.","Number of days",true);
				Optional<UserChoice> result = d.showAndWait();
				if (result.isPresent()) {
					mw.addIndicator(result.get());
				}

			}
		});
		MenuItem ema = new MenuItem("EMA");
		ema.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				SelectDialog d = new SelectDialog(EMA.class.getName(),"Exponential Moving average","Choose number of days and color for the instrument.","Number of days",true);
				Optional<UserChoice> result = d.showAndWait();
				if (result.isPresent()) {
					mw.addIndicator(result.get());
				}

			}
		});
		MenuItem rsi = new MenuItem("RSI");
		rsi.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				SelectDialog d = new SelectDialog(RSI.class.getName(),"Relative strength index","Choose number of days and color for the instrument.","Number of days");
				Optional<UserChoice> result = d.showAndWait();
				if (result.isPresent()) {
					mw.addIndicator(result.get());
				}

			}
		});
		menuAdd.getItems().addAll(ma,ema, rsi);
		return menuAdd;
	}

	private Menu fileMenu(MainWindow mw) {
		Menu menuFile = new Menu("File");
		MenuItem update = new MenuItem("Update stocks");
		update.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				mw.updateAllStocks();
			}
		});
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				mw.exit();
			}
		});
		menuFile.getItems().addAll(update, new SeparatorMenuItem(), exit);
		return menuFile;
	}

	/**
	 * Method that returns the menu
	 * 
	 * @return Menu returned
	 */
	public MenuBar getMenuBar() {
		return menuBar;
	}

}
