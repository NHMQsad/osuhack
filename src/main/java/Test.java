
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
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
        StackPane root = new StackPane();
        TilePane tileButtons = new TilePane(Orientation.VERTICAL);
        tileButtons.setPadding(new Insets(20, 10, 20, 0));
        tileButtons.setHgap(10.0);
        tileButtons.setVgap(8.0);
        for(File f : beatmapFiles) {
        	Button btn = new Button();
        	btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            btn.setText(f.getName());
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                	Object source = event.getSource();
                	if (source instanceof Button) { //should always be true in your example
                	    Button clickedBtn = (Button) source; // that's the button that was clicked
                	    System.out.println(clickedBtn.getText()); // prints the id of the button
                	}
                }
            });
            tileButtons.getChildren().add(btn);
        }
        root.getChildren().add(tileButtons);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

}
