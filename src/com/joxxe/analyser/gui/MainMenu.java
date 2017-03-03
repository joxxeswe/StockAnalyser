package com.joxxe.analyser.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
/**
 * The menu for the gui.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class MainMenu {

	private MenuBar menuBar;

	public MainMenu(MainWindow mw){
		menuBar = new MenuBar();
		String os = System.getProperty("os.name");
		//if on mac, show it in system menu bar.
		if (os != null && os.startsWith("Mac")) {
			menuBar.useSystemMenuBarProperty().set(true);
		}
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
		menuFile.getItems().addAll(update,new SeparatorMenuItem(),exit);
		menuBar.getMenus().addAll(menuFile);
		
	}
	
	/**
	 * Method that returns the menu
	 * @return Menu returned
	 */
	public MenuBar getMenuBar() {
		return menuBar;
	}
	
}
