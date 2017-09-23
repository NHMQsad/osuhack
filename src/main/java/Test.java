import java.io.File;
import java.io.FileNotFoundException;

import lt.ekgame.beatmap_analyzer.parser.BeatmapException;
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, BeatmapException {
		// TODO Auto-generated method stub
		File folder = new File("/home/zipper/.PlayOnLinux/wineprefix/osu_on_linux/drive_c/Program Files/osu!/Songs");
		BeatmapParser parser = new BeatmapParser();
		
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				for (File beatmapFile : file.listFiles(o->o.getAbsolutePath().toLowerCase().endsWith(".osu"))) {
					System.out.println(beatmapFile.getName());
					parser.parse(beatmapFile);
				}
			}
		}
	}

}
