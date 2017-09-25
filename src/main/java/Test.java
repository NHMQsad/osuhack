import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lt.ekgame.beatmap_analyzer.parser.BeatmapException;
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser;

public class Test extends Application {
	public static ArrayList<File> beatmapFiles;
	public static void main(String[] args) throws FileNotFoundException, BeatmapException {
		// TODO Auto-generated method stub
		File folder = new File("/home/zipper/.PlayOnLinux/wineprefix/osu_on_linux/drive_c/Program Files/osu!/Songs");
		BeatmapParser parser = new BeatmapParser();
		beatmapFiles = new ArrayList<File>();
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				for (File beatmapFile : file.listFiles(o->o.getAbsolutePath().toLowerCase().endsWith(".osu"))) {
					beatmapFiles.add(beatmapFile);
				}
			}
		}
		launch(args);
	}

	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("osuHack");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

}
