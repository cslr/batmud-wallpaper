package fi.novelinsight.batmudwallpaper;
		
import com.mythicscape.batclient.interfaces.BatClientPlugin;
import com.mythicscape.batclient.interfaces.BatClientPluginTrigger;
// import com.mythicscape.batclient.interfaces.BatClientPluginCommandTrigger;
import com.mythicscape.batclient.interfaces.ParsedResult;

import java.util.regex.*;
import java.io.*;

/*
 * TODO: parse location file which sets specific wallpapers for specific locations..
 */
		
public class WallPaperChanger extends BatClientPlugin implements BatClientPluginTrigger {
	
	String previousCoordinates = "";
	
	final public File dir = new File("C:\\batmud-pictures\\");
	
	final public File [] files = dir.listFiles(new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG");
		}
	});
	
	public String getName() {
		return "NI BatMUD WallPaperChangerPlugin";
	}
	
	public void loadPlugin() {
		this.getClientGUI().printText("generic", "Loading NI WallpaperChangerPlugin...\n");
	}

		
	public ParsedResult trigger(ParsedResult arg0) {
		// this.getClientGUI().printText("generic", "whereami\n");
	
		if(arg0.getStrippedText().contains("Obvious exits are:") || 
			arg0.getStrippedText().contains("Obvious exit is:")) {
			this.getClientGUI().doCommand("whereami");
		}
		
		// removes coordinates results from the incoming text
		
		String txt = arg0.getStrippedText();
		
		// You are in 'Inner churchyard of the Cathedral of Calythien' in Calythien on the continent of Desolathya. (Coordinates: 285x, 274y; Global: 7266x, 9285y)

		final String regexp = "You are in (.*?) \\(Coordinates: (.*?) Global: (.*?)\\)\n"; // to match location line which we remove and later parse

		
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(txt);
		
		
		if(matcher.find()) {
			if(matcher.groupCount() == 3) {
				String coordinates = matcher.group(3);
				
				//this.getClientGUI().printText("generic", 
				//	"GLOBAL COORDINATES ARE: " + coordinates + "\n");
				
				// now sets wallpaper randomly/not location dependent
				
				if(files.length > 0 && 
					coordinates.equalsIgnoreCase(previousCoordinates) == false) {
					
					// change background image everytime global coordinates/area change..
					
					java.util.Random r = new java.util.Random();
					String filename = files[r.nextInt(files.length)].toString();
					// this.getClientGUI().printText("generic", filename + "\n");
					this.getClientGUI().setWallpaper(filename);
				}
				
				previousCoordinates = coordinates; 
			}
			
			// removes 'whereami' return string from the input
			txt = txt.replaceAll(regexp, "");
			
			arg0 = new ParsedResult(txt);
		}
		
		return arg0;
	}
		
}

