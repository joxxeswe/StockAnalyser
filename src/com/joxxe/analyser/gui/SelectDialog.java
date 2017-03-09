package com.joxxe.analyser.gui;

import com.joxxe.analyser.model.Util;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
/**
 * A popup dialog that lets user make choices.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class SelectDialog extends Dialog<UserChoice> {

	/**
	 * Private class that shows colors;
	 *  @author joakim hagberg joakimhagberg87@gmail.com
	 *
	 */
	private class ColorSet {
		private Color color;
		private String name;

		ColorSet(String name, Color color) {
			this.name = name;
			this.color = color;
		}

		public Color getColor() {
			return color;
		}

		@Override
		public String toString() {
			return name;
		}

	}


	public SelectDialog(String currClass, String title, String text, String lbl1) {
		setTitle(title);
		setHeaderText(text);
		setResizable(true);

		Label label1 = new Label(lbl1);
		TextField text1 = new TextField();
		ChangeListener<String> forceNumberListener = (observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(oldValue);
		};

		text1.textProperty().addListener(forceNumberListener);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(label1, 1, 1);
		grid.add(text1, 2, 1);
		getDialogPane().setContent(grid);

		ButtonType buttonTypeOk = addOkCancelButton(text1, null);

		setResultConverter(new Callback<ButtonType, UserChoice>() {
			@Override
			public UserChoice call(ButtonType b) {

				if (b == buttonTypeOk) {
					UserChoice u = new UserChoice();
					u.setSelectedClass(currClass);
					u.addValue(Util.toInt(text1.getText()));
					return u;
				}
				return null;
			}
		});
	}

	public SelectDialog(String currClass, String title, String text, String lbl1, boolean color) {
		setTitle(title);
		setHeaderText(text);
		setResizable(true);

		Label label1 = new Label(lbl1);
		TextField text1 = new TextField();
		ChangeListener<String> forceNumberListener = (observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*"))
				((StringProperty) observable).set(oldValue);
		};

		text1.textProperty().addListener(forceNumberListener);
		Label label2 = new Label("Select color");
		ObservableList<ColorSet> options = FXCollections.observableArrayList(new ColorSet("Red", Color.INDIANRED),
				new ColorSet("Blue", Color.LIGHTSTEELBLUE), new ColorSet("Green", Color.GREENYELLOW),
				new ColorSet("Brown", Color.BURLYWOOD), new ColorSet("Grey", Color.DIMGREY));
		ComboBox<ColorSet> colors = new ComboBox<ColorSet>(options);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(label1, 1, 1);
		grid.add(text1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(colors, 2, 2);
		getDialogPane().setContent(grid);

		ButtonType buttonTypeOk = addOkCancelButton(text1, colors);

		setResultConverter(new Callback<ButtonType, UserChoice>() {
			@Override
			public UserChoice call(ButtonType b) {

				if (b == buttonTypeOk) {
					UserChoice u = new UserChoice();
					u.setSelectedClass(currClass);
					u.setColor(colors.getSelectionModel().getSelectedItem().getColor());
					u.addValue(Util.toInt(text1.getText()));
					return u;
				}
				return null;
			}
		});
	}

	/**
	 * Adds validation for input.
	 * @param text1
	 * @param colors
	 * @return A button with validation.
	 */
	private ButtonType addOkCancelButton(TextField text1, ComboBox<ColorSet> colors) {
		ButtonType buttonTypeOk = new ButtonType("Add", ButtonData.APPLY);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
		final Button okButton = (Button) getDialogPane().lookupButton(buttonTypeOk);
		okButton.addEventFilter(ActionEvent.ACTION, ae -> {
			if (text1.getText().length() < 1
					|| (colors != null && colors.getSelectionModel().getSelectedItem() == null))
				ae.consume();
		});
		return buttonTypeOk;
	}
}
