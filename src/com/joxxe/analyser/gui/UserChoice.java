package com.joxxe.analyser.gui;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class UserChoice {

	private String selectedClass;
	private ArrayList<Integer> values;
	private Color color;
	
	public UserChoice(){
		values = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "UserChoice [selectedClass=" + selectedClass + ", values=" + values + ", color=" + color + "]";
	}

	public ArrayList<Integer> getValues(){
		return values;
	}
	public void addValue(int v){
		values.add(v);
	}
	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
