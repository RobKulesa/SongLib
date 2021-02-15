package songlib.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import songlib.app.models.SongModel;

public class Controller {
	private static final boolean DEBUG = false;

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
    private ListView<String> songList;

    @FXML
    private Button addBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button delBtn;

	public void setMainStage(Stage stage) {
		mainStage = stage;
		ArrayList<SongModel> songs = readTextFile("songs.txt");
		for(SongModel song : songs) {
			if(DEBUG) System.out.println(song);
			songList.getItems().add(song.getName() + ", " + song.getArtist());

			//TODO: Alphabetical sorting by song name, then artist name
		}
	}
	
    @FXML
    void clickAddBtn(MouseEvent event) {

    }

    @FXML
    void clickDelBtn(MouseEvent event) {

    }

    @FXML
    void clickEditBtn(MouseEvent event) {

    }

    @FXML
    void clickListItem(MouseEvent event) {

    }

    @FXML
    void clickQuitBtn(ActionEvent event) {

    }

	public ArrayList<SongModel> readTextFile(String fileName) {
		ArrayList<SongModel> songs = new ArrayList<SongModel>();

		String data = ""; 
	    try {
			data = new String(Files.readAllBytes(Paths.get(fileName)));
			String[] dataArr = data.split("\n");
			for(int i = 0; i < dataArr.length; i++) {
				String[] songInfo = dataArr[i].split("\\|");
				SongModel song;
				if(songInfo.length == 2){
					song = new SongModel(songInfo[0], songInfo[1]);
				} else {
					song = new SongModel(songInfo[0], songInfo[1], songInfo[2], Integer.parseInt(songInfo[3].trim()));
				}
				songs.add(song);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return songs;
	}

}
