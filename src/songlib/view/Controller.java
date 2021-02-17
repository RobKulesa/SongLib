/**
 * Rutgers CS213 Sp21 SongLib (Asst0) 
 * @author Rob Kulesa
 * @author Aaron Kan
 */
package songlib.view;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import songlib.app.models.SongModel;

public class Controller {
	private static final boolean DEBUG = false;
	private static final String testFile = "empty.txt";
	private static boolean allowSelect = true;
	Stage mainStage;

	@FXML
    private TextField nameBox;

    @FXML
    private TextField artistBox;

    @FXML
    private TextField albumBox;

    @FXML
    private TextField yearBox;

    @FXML
    private ListView<SongModel> songList;

    @FXML
    private Button addBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button delBtn;

	public void setMainStage(Stage stage) {
		mainStage = stage;
		ArrayList<SongModel> songs = readTextFile(testFile);
		for(SongModel song : songs) {
			if(DEBUG) System.out.println(song);
			this.songList.getItems().add(song);
		}
		this.songList.getItems().sort(new SongModelComparator());
		this.songList.getSelectionModel().select(0);
		if(!this.songList.getItems().isEmpty()) {
			this.clickListItem(null);
		} else {
			this.editBtn.setVisible(false);
			this.editBtn.setDisable(true);
			this.delBtn.setVisible(false);
			this.delBtn.setDisable(true);
		}
		this.songList.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(!allowSelect) event.consume();
			}
		});

		this.mainStage.setOnCloseRequest(event -> {
			clickQuitBtn(null);
		});
	}
	
    @FXML
    void clickAddBtn(MouseEvent event) {
			/*
			* 	1. User clicks add button
				2. List locks
				3. Change button layout
				4. Clear textfields
				5. Instantiate new SongModel
				6. Wait for user to fill in data
				7. Check for valid input; error and force user to repeat if invalid
				8. If Valid, add item to listview
				9. Resort the listview -- this.songList.getItems().sort(new SongModelComparator());
				10. Add item to textfile
				11. Change buttons back
				12. auto select the item we just added in
				13. unlock the listview
			*/
			if(this.addBtn.getText().equals("Add")){
				this.addBtn.setText("Save Add");
				this.delBtn.setText("Cancel Add");
				this.delBtn.setVisible(true);
				this.delBtn.setDisable(false);
				this.editBtn.setDisable(true);
				this.editBtn.setVisible(false);
				this.songList.getSelectionModel().clearSelection();
				allowSelect = false;
				this.artistBox.setEditable(true);
				this.nameBox.setEditable(true);
				this.albumBox.setEditable(true);
				this.yearBox.setEditable(true);

				this.artistBox.setText("");
				this.nameBox.setText("");
				this.albumBox.setText("");
				this.yearBox.setText("");

				return;
			}
			
			if(this.addBtn.getText().equals("Save Add")){
				SongModel newAddition = new SongModel();
				//Error Checking
				try{
					newAddition.setName(this.nameBox.getText());
					newAddition.setArtist(this.artistBox.getText());
					if(this.albumBox.getText() != null && !this.albumBox.getText().isBlank()) newAddition.setAlbum(this.albumBox.getText());
					if(this.yearBox.getText() != null && onlyDigits(this.yearBox.getText().trim())) {
						if(Integer.parseInt(this.yearBox.getText().trim()) > 0) {
							newAddition.setYear(Integer.parseInt(this.yearBox.getText()));
						} else {
							this.yearBox.setText("");
							newAddition.setYear(0);
						}
					}	
				} 
				catch(IllegalArgumentException e){
					errorDialog("Song info contains invalid input.");
					return;
				}

				if(this.nameBox.getText().isBlank() || this.artistBox.getText().isBlank()){
					errorDialog("Song info contains invalid input.");
					return;
				}

				for(SongModel song : this.songList.getItems()) {
					if(newAddition.compareTo(song) == 0) {
						errorDialog("Songs must have a unique name / artist combination. Click OK to resume adding.");
						songList.getItems().sort(new SongModelComparator());
						clickListItem(null);
						return;
					}
				}
	
				this.songList.getItems().add(newAddition);
				this.songList.getSelectionModel().select(newAddition);
				this.songList.getItems().sort(new SongModelComparator());
				this.delBtn.setVisible(true);
				this.delBtn.setDisable(false);
				this.clickDelBtn(null); // really cancel edit button
				return;
			}
    }

    @FXML
    void clickDelBtn(MouseEvent event) {
		
		if(this.delBtn.getText().equals("Cancel Add")) {
			this.artistBox.setText("");
			this.nameBox.setText("");
			this.albumBox.setText("");
			this.yearBox.setText("");
			this.editBtn.setVisible(true);
			this.editBtn.setDisable(false);
			this.nameBox.setEditable(false);
			this.artistBox.setEditable(false);
			this.albumBox.setEditable(false);
			this.yearBox.setEditable(false);
			this.addBtn.setText("Add");
			this.delBtn.setText("Delete");
			allowSelect = true;
			if(this.songList.getItems().size() > 0) {
				this.songList.getSelectionModel().select(0);
				clickListItem(null); 
			}
			return;
		}
		
		if(this.delBtn.getText().equals("Cancel Edit")) {
			this.addBtn.setVisible(true);
			this.addBtn.setDisable(false);
			this.nameBox.setEditable(false);
			this.artistBox.setEditable(false);
			this.albumBox.setEditable(false);
			this.yearBox.setEditable(false);
			this.editBtn.setText("Edit");
			this.delBtn.setText("Delete");
			allowSelect = true;
			clickListItem(null);
			return;
		}
		int selected = this.songList.getSelectionModel().getSelectedIndex();
		if(selected < 0) {
			if(this.songList.getItems().size() < 1) {
				errorDialog("No items left to remove!");
				return;
			} else {
				errorDialog("Please select an item to remove first.");
				return;
			}
		} else {
			if(selected < this.songList.getItems().size() - 1) { //not last item, select next
				this.songList.getItems().remove(selected);
				this.songList.getSelectionModel().select(selected);
			} else if(this.songList.getItems().size() == 1) { //only one item, select none
				this.songList.getItems().remove(selected);
			} else { //last item, more than one item, select prev
				this.songList.getItems().remove(selected);
				this.songList.getSelectionModel().select(--selected);
			}
			if(!this.songList.getItems().isEmpty()) {
				this.clickListItem(null);
			} else {
				this.editBtn.setVisible(false);
				this.editBtn.setDisable(true);
				this.delBtn.setVisible(false);
				this.delBtn.setDisable(true);
				clearTextFields();
			}
		}
    }

    @FXML
    void clickEditBtn(MouseEvent event) throws IllegalArgumentException {
		if(this.editBtn.getText().equals("Save Edit")) {
			SongModel edited = new SongModel();

			if(this.nameBox.getText().isBlank() || this.artistBox.getText().isBlank()){
				errorDialog("Song info contains invalid input.");
				return;
			}

			try {
				edited.setName(this.nameBox.getText());
				edited.setArtist(this.artistBox.getText());
				if(this.albumBox.getText() != null && !this.albumBox.getText().isBlank()) edited.setAlbum(this.albumBox.getText());
				if(this.yearBox.getText() != null && onlyDigits(this.yearBox.getText().trim())) {
					if(Integer.parseInt(this.yearBox.getText().trim()) > 0) {
						edited.setYear(Integer.parseInt(this.yearBox.getText()));
					} else {
						this.yearBox.setText("");
						edited.setYear(0);
					}
				}
			} catch(IllegalArgumentException e) {
				errorDialog("Song info contains invalid input.");
				return;
			}

			SongModel removed = this.songList.getSelectionModel().getSelectedItem();
			this.songList.getItems().remove(this.songList.getSelectionModel().getSelectedIndex());
			for(SongModel song : this.songList.getItems()) {
				if(edited.compareTo(song) == 0) {
					errorDialog("Songs must have a unique name / artist combination. Click OK to resume editing.");
					songList.getItems().add(removed);
					songList.getItems().sort(new SongModelComparator());
					songList.getSelectionModel().select(removed);
					clickListItem(null);
					return;
				}
			}
			this.songList.getItems().add(edited);
			this.songList.getSelectionModel().select(edited);
			this.songList.getItems().sort(new SongModelComparator());
			this.clickDelBtn(null); // really cancel edit button
			return;
		}
		
		this.editBtn.setText("Save Edit");
		this.delBtn.setText("Cancel Edit");
		this.addBtn.setVisible(false);
		this.addBtn.setDisable(true);
		this.nameBox.setEditable(true);
		this.artistBox.setEditable(true);
		this.albumBox.setEditable(true);
		this.yearBox.setEditable(true);
		allowSelect = false;
    }

    @FXML
    void clickListItem(MouseEvent event) {
		if(!this.songList.getItems().isEmpty() && allowSelect) {
			this.nameBox.setText(this.songList.getSelectionModel().getSelectedItem().getName());
			this.artistBox.setText(this.songList.getSelectionModel().getSelectedItem().getArtist());
			this.albumBox.setText(this.songList.getSelectionModel().getSelectedItem().getAlbum());
			if(this.songList.getSelectionModel().getSelectedItem().getYear() > 0) {
				this.yearBox.setText(String.valueOf(this.songList.getSelectionModel().getSelectedItem().getYear()));
			} else {
				this.yearBox.setText("");
			}
		}
    }

    @FXML
    void clickQuitBtn(ActionEvent event) {
		songList.getItems().sort(new SongModelComparator());
		if(!addBtn.getText().equals("Add") || !editBtn.getText().equals("Edit") || !delBtn.getText().equals("Delete")) {
			errorDialog("Please finish the pending changes before quitting.");
			return;
		} else {
			try {
				writeTextFile(testFile);
			} catch(IOException e) {
				errorDialog("An unexpected error occured. Please try again");
			}
			mainStage.close();
		}
    }

	public void writeTextFile(String fileName) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		for(SongModel song : songList.getItems()) {
			bw.write(song.getName() + "|" + song.getArtist() + "|" + song.getAlbum() + "|" + song.getYear() + "\n");
		}
		bw.close();
	}

	public ArrayList<SongModel> readTextFile(String fileName) {
		ArrayList<SongModel> songs = new ArrayList<SongModel>();

		try { 
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String data;
			while((data = br.readLine()) != null) {
				String[] songInfo = data.split("\\|");
				SongModel song;
				if(songInfo.length == 2){
					song = new SongModel(songInfo[0], songInfo[1]);
				} else {
					song = new SongModel(songInfo[0], songInfo[1], songInfo[2], Integer.parseInt(songInfo[3].trim()));
				}
				songs.add(song);
			}
			br.close();

		} catch(IOException e1) {
			try {
				File songsFile = new File(fileName);
				songsFile.createNewFile();
			} catch(IOException e2) {
				System.out.println("An error occurred trying to create file: " + fileName);
      			e2.printStackTrace();
			}
		}
		return songs;
	}

	public static boolean onlyDigits(String str) {
		return Pattern.compile("[0-9]+").matcher(str).matches();
	}

	public void clearTextFields() {
		this.nameBox.setText("");
		this.artistBox.setText("");
		this.albumBox.setText("");
		this.yearBox.setText("");
		return;
	}

	public void errorDialog(String errorMsg) {
		Dialog<String> dialog = new Dialog<String>();
		dialog.setTitle("Error! =(");
		ButtonType type = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.setContentText(errorMsg);
		dialog.getDialogPane().getButtonTypes().add(type);
		dialog.setResizable(true);
		dialog.showAndWait();
	}

	
}

class SongModelComparator implements Comparator<SongModel> {
	@Override
	public int compare(SongModel o1, SongModel o2) {
		return o1.compareTo(o2);
	}
}
