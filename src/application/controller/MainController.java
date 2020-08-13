package application.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import application.Main;
import application.RollTask;
import application.model.MainList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
	private Stage parent;
	
	@FXML
	HBox answer_container;
	
	@FXML
	Label answer;
	
	@FXML
	Label percent;
	
	@FXML
	Button roll;
	
	@FXML
	TextField rollTimes;
	
	@FXML
	TextField add_text;
	
	@FXML
	Button add;
	
	@FXML
	Button clear;
	
	@FXML
	VBox items;
	
	@FXML
	Button save;
	
	@FXML
	Button load;
	
	@FXML
	ProgressIndicator progress; //?
	
	public void handleAdd() {
		if(!add_text.getText().isBlank()) {
			try {
				FXMLLoader itemLoader = new FXMLLoader();
				itemLoader.setLocation(Main.class.getResource("view/ItemView.fxml"));
				BorderPane item = (BorderPane) itemLoader.load();
				ItemController controller = itemLoader.getController();
				controller.init(this,item);
				controller.setText(add_text.getText());
				items.getChildren().add(item);
				MainList.getInstance().add(item);
				add_text.clear();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(!items.getChildren().isEmpty()) {
				for(Node item : items.getChildren()) {
					item.getStyleClass().remove("win");
					BorderPane itm = (BorderPane) item;
					HBox i = (HBox) itm.getCenter();
					Label l = (Label) i.getChildren().get(0);
					l.setText("");
					l.setVisible(false);
				}
			}
			
			answer_container.setVisible(false);
		}
	}
	
	public void handleClear() {
		answer_container.setVisible(false);
		MainList.getInstance().getList().clear();
		items.getChildren().clear();
	}
	
	public void handleRoll() {
		if(!MainList.getInstance().getList().isEmpty()) {
			progress.setVisible(true);
			progress.setPrefWidth(20);
			
			for(Node item : items.getChildren())
				if(item.getStyleClass().contains("win"))
					item.getStyleClass().remove("win");
			
			int rolls = 1;
			if(!rollTimes.getText().isBlank()) {
				try {
					rolls = Integer.parseInt(rollTimes.getText());
				} catch(NumberFormatException e) {
					rolls = Integer.MAX_VALUE;
					rollTimes.setText(Integer.toString(Integer.MAX_VALUE));
				}
			}
			
			runRolls(rolls);
		}
	}
	
	//i -> it -> itm -> item (sorry)
	private void runRolls(int rolls) {
		RollTask rolling = new RollTask();
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<double[]> future = executor.submit(rolling.roll(rolls,
				MainList.getInstance().size()));
		try {
			progress.setVisible(false);
			progress.setPrefWidth(0);
			double[] response = future.get();
			
			int win = (int)response[response.length-1];
			MainList.getInstance().get(win).getStyleClass().add("win");
			BorderPane p = (BorderPane) MainList.getInstance().get(win);
			HBox it = (HBox) p.getCenter();
			Label item = (Label) it.getChildren().get(1);
			String a = item.getText();
			
			for(int i=0; i<items.getChildren().size(); i++) {
				BorderPane b = (BorderPane) items.getChildren().get(i);
				HBox itm = (HBox) b.getCenter();
				Label l = (Label) itm.getChildren().get(0);
				l.setVisible(true);
				l.setText(String.format("%.2f %%", response[i]));
			}
			answer_container.setVisible(true);
			answer_container.getStyleClass().add("win");
			answer.setText(a);
			double even = (((double)rolls/MainList.getInstance().getList().size())/rolls)*100;
			percent.setText(String.format("%.2f%% (%.2f%%)",
					response[win], even));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}
	
	public void handleSave() {
		FileChooser choose = new FileChooser();
		FileChooser.ExtensionFilter extensions = new FileChooser.ExtensionFilter("txt", "*.txt");
		choose.getExtensionFilters().add(extensions);
		choose.setTitle("Save List");
		
		File file = choose.showSaveDialog(parent);
		if(file != null) {
			try(BufferedWriter out = Files.newBufferedWriter(file.toPath(),
					StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)){
				out.write(MainList.getInstance().toString());
			} catch(FileAlreadyExistsException e) {
				File f = new File(file.toString());
				file.delete();
				try(BufferedWriter out = Files.newBufferedWriter(f.toPath(),
						StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)){
					out.write(MainList.getInstance().getList().toString());
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void handleLoad() {
		MainList.getInstance().getList().clear();
		answer_container.getChildren().clear();
		items.getChildren().clear();
		
		FileChooser choose = new FileChooser();
		choose.setTitle("Open the Choices");
		FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("txt", "*.txt");
		choose.getExtensionFilters().add(extension);
		
		File file = choose.showOpenDialog(parent);
		try {
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("view/ItemView.fxml"));
				try{
					//lazy :) <- but ??
					BorderPane item = (BorderPane) loader.load();
					ItemController controller = loader.getController();
					controller.init(this, item);
					HBox label = (HBox) item.getCenter();
					Label text = (Label) label.getChildren().get(1);
					text.setText(scanner.nextLine());
					items.getChildren().add(item);
					MainList.getInstance().getList().add(item);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Da fk happened? TXTs only!");
				}
			}
			scanner.close();
		} catch(FileNotFoundException fe) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/WarningView.fxml"));
			try{
				BorderPane item = (BorderPane) loader.load();
				Stage warn = new Stage();
				warn.setScene(new Scene(item));
				warn.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Da fk happened? TXTs only!");
			}
		}
	}
	
	public void keyboardFunction() {
		add_text.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if(e.getCode().equals(KeyCode.ENTER)) {
					handleAdd();
				}
			}
		});
		
		rollTimes.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				if(e.getCode().equals(KeyCode.ENTER)) {
					handleRoll();
				}
			}
		});
	}
	
// set option for default random roll amount
	public void genStartRoll(int roof) {
		RollTask get = new RollTask();
		rollTimes.setText(Integer.toString(get.init(roof)));
	}

	public void init(Stage stage, int defaultRoll) {
		MainList.getInstance();
		parent = stage;
		keyboardFunction();
		genStartRoll(defaultRoll);
	}
}
