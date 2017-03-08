package com.joxxe.analyser.gui.chart;

import java.awt.Button;

import javafx.scene.layout.Pane;

public class RemoveIndicatorButton extends Pane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6222926442866253673L;
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
