package com.joxxe.analyser.gui.chart;

import javafx.scene.layout.Pane;
/**
 * A "button" that is drawn on the stock chart so the user can remove indicators.
 *  @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class RemoveIndicatorButton extends Pane {

	/**
	 * 
	 */
	private static final double size = 15;
	private int index;
	
	public RemoveIndicatorButton(int index){
		this.index = index;
		getStyleClass().add("close-button");
		setLayoutX(70);
		setMaxHeight(size);
		setMaxWidth(size);
		setPrefWidth(size);
		setPrefHeight(size);
		setLayoutY(calculateYPos());
		
	}
	
	private double calculateYPos() {
		return 50 + (index*size)-15;
	}

	public int getIndex(){
		return index;
	}
	
	public void subIndex(){
		index--;
		setLayoutY(calculateYPos());
	}
	 
}
