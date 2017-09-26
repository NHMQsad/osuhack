
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lt.ekgame.beatmap_analyzer.parser.BeatmapException;
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser;

public class Test extends Application {
	public static GridPane tileButtons = new GridPane();
	public static ScrollPane root = new ScrollPane();
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
        
        tileButtons.setPadding(new Insets(20, 10, 20, 0));
        tileButtons.setHgap(10.0);
        tileButtons.setVgap(8.0);
        final TextField search = new TextField();
        search.setPromptText("Enter a beatmap: ");
        search.setOnKeyTyped(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {
						ArrayList<File> filteredFiles = new ArrayList<File>();
		    		    filteredFiles.addAll(Arrays.asList(beatmapFiles.stream()
		    		    		.filter(s -> s.getName().toLowerCase().indexOf(search.getText()) > -1)
		    		    		.toArray(File[]::new)));
		    		    Platform.runLater(new Runnable() {                          
	                        @Override
	                        public void run() {
	                            try{
	                            	destroyGrid();
	                            	tileButtons.add(search, 1, 0);
	        		    		    int count = 0;
	        		    	        for(File f : filteredFiles) {
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
	        		    	            tileButtons.add(btn,0,count);
	        		    	            count++;
	        		    	            
	        		    	        }
	        		    	        reshow(primaryStage);
	                            } finally{
	                                
	                            }
	                        }
	                    });
					}
				}.start();
			}
        });
        tileButtons.add(search, 1, 0);
        int count = 0;
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
            tileButtons.add(btn,0,count);
            count++;
        }
        reshow(primaryStage);
        
    }
	public static void destroyGrid() {
		tileButtons = new GridPane();
		tileButtons.setPadding(new Insets(20, 10, 20, 0));
        tileButtons.setHgap(10.0);
        tileButtons.setVgap(8.0);
	}
	public static void reshow(Stage primaryStage) {
		Rectangle2D mainScreen = Screen.getPrimary().getVisualBounds();
        root.setContent(tileButtons);
        Scene scene = new Scene(root);
        primaryStage.setHeight(mainScreen.getHeight());
        primaryStage.setWidth(mainScreen.getWidth());
        primaryStage.setScene(scene);
        primaryStage.show();
	}

}
