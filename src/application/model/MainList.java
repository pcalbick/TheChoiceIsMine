package application.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainList {
	private static MainList MASTER = null;
	private List<BorderPane> choiceList;

	public static MainList getInstance() {
		if(MASTER == null) {
			synchronized(MainList.class) {
				MASTER = new MainList();
			}
		}
		return MASTER;
	}
	
	public MainList() {
		choiceList = new ArrayList<>();
	}
	
	
	public void add(BorderPane item) {
		choiceList.add(item);
	}
	
	public void remove(int i) {
		choiceList.remove(i);
	}
	
	public int size() {
		return choiceList.size();
	}
	
	public void removeObject(Object o) {
		choiceList.remove(o);
	}
	
	public List<BorderPane> getList() {
		return choiceList;
	}
	
	public BorderPane get(int i) {
		return choiceList.get(i);
	}
	
	//???
	public String toString() {
		String list = "";
		for(int i=0; i<choiceList.size(); i++) {
			HBox c = (HBox) choiceList.get(i).getCenter();
			Label choice = (Label) c.getChildren().get(1);
			String s = choice.getText();
			list += s + "\n";
		}
		return list;
	}
}
