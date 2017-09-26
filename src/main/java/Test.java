
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import lt.ekgame.beatmap_analyzer.beatmap.Beatmap;
import lt.ekgame.beatmap_analyzer.beatmap.osu.OsuBeatmap;
import lt.ekgame.beatmap_analyzer.beatmap.osu.OsuObject;
import lt.ekgame.beatmap_analyzer.parser.BeatmapException;
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser;

public class Test extends Application {
	public static GridPane tileButtons = new GridPane();
	public static ScrollPane root = new ScrollPane();
	public static ArrayList<File> beatmapFiles;
	public static Robot robot;
	public static void main(String[] args) throws FileNotFoundException, BeatmapException, AWTException {
		// TODO Auto-generated method stub
		robot = new Robot();
		File folder = new File("/home/zipper/.PlayOnLinux/wineprefix/osu_on_linux/drive_c/Program Files/osu!/Songs");
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
	        		    	                	    File winFile = null;
	        		    	                	    for(File f : filteredFiles) {
	        		    	                	    	if(f.getName().equals(clickedBtn.getText())) {
	        		    	                	    		winFile = f;
	        		    	                	    		break;
	        		    	                	    	}
	        		    	                	    }
	        		    	                	    if(winFile != null) {
	        		    	                	    	try {
															doMap(winFile);
														} catch (FileNotFoundException | BeatmapException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
	        		    	                	    }
	        		    	                	    else {
	        		    	                	    	System.out.println("Failed to find beatmap");
	        		    	                	    }
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
                	    File winFile = null;
                	    for(File f : beatmapFiles) {
                	    	if(f.getName().equals(clickedBtn.getText())) {
                	    		winFile = f;
                	    		break;
                	    	}
                	    }
                	    if(winFile != null) {
                	    	try {
								doMap(winFile);
							} catch (FileNotFoundException | BeatmapException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                	    }
                	    else {
                	    	System.out.println("Failed to find beatmap");
                	    }
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
	public static void doMap(File mapFile) throws FileNotFoundException, BeatmapException {
		BeatmapParser parser = new BeatmapParser();
		OsuBeatmap map = parser.parse(mapFile, OsuBeatmap.class);
		List<OsuObject> circles = map.getHitObjects();
		int lastTime = 0;
		for(OsuObject circle : circles) {
			int startX = (int)MouseInfo.getPointerInfo().getLocation().getX();
			int startY = (int)MouseInfo.getPointerInfo().getLocation().getY();
			int endX = (int)circle.getPosition().getX();
			int endY = (int)circle.getPosition().getY();
			int time = circle.getStartTime() - lastTime;
			int ticks = dist(startX, startY, endX, endY);
			mouseGlide(startX, startY, endX, endY, time, ticks);
			lastTime = circle.getStartTime();
		}
	}
	public static void mouseGlide(int x1, int y1, int x2, int y2, int t, int n) {
		double dx = (x2 - x1) / ((double) n);
	    double dy = (y2 - y1) / ((double) n);
	    double dt = t / ((double) n);
	    for (int step = 1; step <= n; step++) {
	        robot.delay((int)dt);
	        robot.mouseMove((int) (x1 + dx * step), (int) (y1 + dy * step));
	    }
	}
	public static int dist(int x1, int y1, int x2, int y2) {
		return (int)(Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1))));
	}

}
