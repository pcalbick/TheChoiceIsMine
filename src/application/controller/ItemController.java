package application.controller;

import application.model.MainList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ItemController{
	private BorderPane item;
	private MainController parent;
	
	@FXML
	Button remove;
	
	@FXML
	Label text;

	public void handleRemove() {
		MainList.getInstance().removeObject(item);
		parent.items.getChildren().remove(item);
	}
	
	public void setText(String s) {
		text.setText(s);
	}
	
	public void init(MainController parent, BorderPane item) {
		this.item = item;
		this.parent	= parent;
	}
}
