
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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
import lt.ekgame.beatmap_analyzer.beatmap.osu.OsuCircle;
import lt.ekgame.beatmap_analyzer.beatmap.osu.OsuObject;
import lt.ekgame.beatmap_analyzer.beatmap.osu.OsuSlider;
import lt.ekgame.beatmap_analyzer.parser.BeatmapException;
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser;
import lt.ekgame.beatmap_analyzer.utils.Vec2;

public class Test extends Application implements NativeKeyListener {
	public static GridPane tileButtons = new GridPane();
	public static ScrollPane root = new ScrollPane();
	public static ArrayList<File> beatmapFiles;
	public static Robot robot;
	public static boolean running = false;
	public static boolean inOsu = false;
	public static List<OsuObject> circles;
	public static void main(String[] args) throws FileNotFoundException, BeatmapException, AWTException {
		// TODO Auto-generated method stub
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);
		GlobalScreen.addNativeKeyListener(new Test());
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
		circles = map.getHitObjects();
		running = true;
	}
	public void doOsu() {
		inOsu = true;
		int td1 = 0;
		int td2 = 0;
		long secTime = System.nanoTime();
		int firstNote = circles.get(0).getStartTime();
		long base = System.nanoTime();
		int lastTime = circles.get(0).getStartTime()-1;
		for(int i = 0; (i < circles.size()); i++) {
			if(circles.get(i) instanceof OsuCircle) {
				OsuCircle circle = (OsuCircle) circles.get(i);
				long startTime = System.nanoTime();
				int startX = (int)MouseInfo.getPointerInfo().getLocation().getX();
				int startY = (int)MouseInfo.getPointerInfo().getLocation().getY();
				int endX = scaleX((int)circle.getPosition().getX());
				int endY = scaleY((int)circle.getPosition().getY());
				int time = circles.get(i).getStartTime() - lastTime;
				int ticks = dist(startX, startY, endX, endY);
				robot.keyRelease(java.awt.event.KeyEvent.VK_Z);
				robot.mouseMove(endX, endY);
	//			td1 = (int)((System.nanoTime()-startTime)/1000000);
	//			int test = (time-(td1+td2));
	//			System.out.println(test);
	//			int delay = (test) > 0 ? (test) : 0;
	//			System.out.println(time-(td1+td2));
	//			System.out.println(td1);
	//			System.out.println(td2);
				int test = (int)((circle.getStartTime()-firstNote) - ((System.nanoTime()-secTime)/1000000.0));
				int delay = (test-(i/100)) > 0 ? (test-(i/100)) : 0;
	//			mouseGlide(startX, startY, endX, endY, delay, 100);
				robot.delay(delay);
				robot.keyPress(java.awt.event.KeyEvent.VK_Z);
				secTime = System.nanoTime();
				firstNote = circle.getStartTime();
				startTime = System.nanoTime();
				//mouseGlide(startX, startY, endX, endY, time, ticks);
				lastTime = circle.getStartTime();
				if(!running)  {
					break;
				}
			}
			else {
				OsuSlider slider = (OsuSlider) circles.get(i);
				long startTime = System.nanoTime();
				int startX = (int)MouseInfo.getPointerInfo().getLocation().getX();
				int startY = (int)MouseInfo.getPointerInfo().getLocation().getY();
				int endX = scaleX((int)slider.getPosition().getX());
				int endY = scaleY((int)slider.getPosition().getY());
				int time = slider.getStartTime() - lastTime;
				int ticks = dist(startX, startY, endX, endY);
				robot.keyRelease(java.awt.event.KeyEvent.VK_Z);
				robot.mouseMove(endX, endY);
	//			td1 = (int)((System.nanoTime()-startTime)/1000000);
	//			int test = (time-(td1+td2));
	//			System.out.println(test);
	//			int delay = (test) > 0 ? (test) : 0;
	//			System.out.println(time-(td1+td2));
	//			System.out.println(td1);
	//			System.out.println(td2);
				int test = (int)((circles.get(i).getStartTime()-firstNote) - ((System.nanoTime()-secTime)/1000000.0));
				int delay = (test-(i/100)) > 0 ? (test-(i/100)) : 0;
	//			mouseGlide(startX, startY, endX, endY, delay, 100);
				robot.delay(delay);
				robot.keyPress(java.awt.event.KeyEvent.VK_Z);
				secTime = System.nanoTime();
				firstNote = circles.get(i).getStartTime();
				startTime = System.nanoTime();
				//mouseGlide(startX, startY, endX, endY, time, ticks);
				lastTime = slider.getStartTime();
				List<Vec2> sliderPoints = slider.getSliderPoints();
				int totTime = slider.getEndTime()-slider.getStartTime();
				for(Vec2 point : sliderPoints) {
					startX = (int)MouseInfo.getPointerInfo().getLocation().getX();
					startY = (int)MouseInfo.getPointerInfo().getLocation().getY();
					endX = scaleX((int)point.getX());
					endY = scaleY((int)point.getY());
					mouseGlide(startX,startY,endX,endY,(totTime/sliderPoints.size()),50);
				}
				if(!running)  {
					break;
				}
			}
//			td2 = (int)((System.nanoTime()-startTime)/1000000);
		}
		robot.keyRelease(java.awt.event.KeyEvent.VK_Z);
		running = false;
		inOsu = false;
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
	
	public static int scaleX(int smolX) {
		double factor = (MouseInfo.getPointerInfo().getDevice().getDisplayMode().getWidth()-550)/512.0;
		return (int)((smolX*factor)+275);
	}
	public static int scaleY(int smolY) {
		double factor = (MouseInfo.getPointerInfo().getDevice().getDisplayMode().getHeight()-150)/384.0;
		return (int)((smolY*factor)+75);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
			if(running && !inOsu && arg0.getKeyCode() == NativeKeyEvent.VC_S) {
				new Thread() {
					public void run() {
						doOsu();
					}
				}.start();
				System.out.println("startOsu");
				
			} else if(running && inOsu && arg0.getKeyCode() == NativeKeyEvent.VC_S) {
				System.out.println("endOsu");
				running = false;
				inOsu = false;
			}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
