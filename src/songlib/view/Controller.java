package songlib.view;

import java.io.IOException;
import java.io.BufferedReader;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import songlib.app.models.SongModel;

public class Controller {
	private static final boolean DEBUG = false;
	private static final String testFile = "songs.txt";
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
	}
	
    @FXML
    void clickAddBtn(MouseEvent event) {

    }

    @FXML
    void clickDelBtn(MouseEvent event) {
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
			//error dialog, no item selected to delete
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
			try {
				edited.setName(this.nameBox.getText());
				edited.setArtist(this.artistBox.getText());
				if(this.albumBox.getText() != null && !this.albumBox.getText().isBlank()) edited.setAlbum(this.albumBox.getText());
				if(this.yearBox.getText() != null && onlyDigits(this.yearBox.getText().trim())) {
					if(Integer.parseInt(this.yearBox.getText().trim()) > 0) {
						this.yearBox.setText(String.valueOf(this.songList.getSelectionModel().getSelectedItem().getYear()));
						edited.setYear(Integer.parseInt(this.yearBox.getText()));
					} else {
						this.yearBox.setText("");
						edited.setYear(0);
					}
				}
			} catch(IllegalArgumentException e) {
				//error dialog
				e.printStackTrace();
			}
			this.songList.getItems().remove(this.songList.getSelectionModel().getSelectedItem());
			for(SongModel song : this.songList.getItems()) {
				if(edited.compareTo(song) == 0) {
					throw new IllegalArgumentException("Songs must have a unique name / artist combination.");
					//error dialog
				}
			}
			this.songList.getItems().add(edited);
			this.songList.getSelectionModel().select(edited);
			this.songList.getItems().sort(new SongModelComparator());
			this.clickDelBtn(null);
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

    }

	public ArrayList<SongModel> readTextFile(String fileName) {
		ArrayList<SongModel> songs = new ArrayList<SongModel>();

		try { 
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String data;
			while((data = br.readLine()) != null) {
				String[] songInfo = data.split("\\|");
				SongModel song;
				//TODO: handle <2, 3, >4
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
}

class SongModelComparator implements Comparator<SongModel> {
	@Override
	public int compare(SongModel o1, SongModel o2) {
		return o1.compareTo(o2);
	}
}
