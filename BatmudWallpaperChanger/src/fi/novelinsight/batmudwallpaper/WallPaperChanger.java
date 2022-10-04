package fi.novelinsight.batmudwallpaper;
		
import com.mythicscape.batclient.interfaces.BatClientPlugin;
import com.mythicscape.batclient.interfaces.BatClientPluginTrigger;
import com.mythicscape.batclient.interfaces.BatClientPluginCommandTrigger;
import com.mythicscape.batclient.interfaces.ParsedResult;

import java.util.regex.*;
import java.io.*;
import java.util.*;

/*
 * TODO: parse location file which sets specific wallpapers for specific locations..
 */
		
public class WallPaperChanger extends BatClientPlugin 
implements BatClientPluginTrigger, BatClientPluginCommandTrigger 
{
	// the pictures file directory..
	final public File dir = 
			new File(System.getProperty("user.dir") + File.separator + "batmud-pictures" + File.separator);
	
	final public File confFile = 
			new File(System.getProperty("user.dir") + File.separator + "batmud-pictures" + File.separator + "pictures.txt");

	//final public File dir = new File("C:\\batmud-pictures\\"); // the pictures file directory..
	
	//final public File confFile = new File("C:\\batmud-pictures\\pictures.txt");
	
	
	private File [] files = null;
	
	private String location = "", coordinates = "";
	
	private String previousCoordinates = "";
	//private String previousLocation = "";
	
	private Map<String, String> coordPictures = new LinkedHashMap<>(); 
	
	private java.util.Random r = new java.util.Random();
		
	///////////////////////////////////////////////////////////////////////////////////
	
	public String getName() {
		return "NI BatMUD WallPaperChangerPlugin";
	}
	
	public void loadPlugin() {
		this.getClientGUI().printText("generic", "Loading NI WallpaperChangerPlugin...\n");
		
		if(dir.exists() == false) {
			this.getClientGUI().printText("generic", "Put batmud-pictures dir in: " + System.getProperty("user.dir") + "\n");
			this.getClientGUI().printText("generic", "Configuration file is: " + this.confFile.toString() + "\n");
		}
		
		// loads picture specifications
		try {
			Scanner scanner = new Scanner(this.confFile);
		
			while(scanner.hasNext()) {
				String line = scanner.nextLine().replaceAll("\n", "").replaceAll("\r", "");
				if(line.startsWith("#") || line.startsWith(" ")) continue; // skip comment lines
				
				String[] tokens = line.split(" ");
				
				if(tokens.length >= 2) { // line is parseable
					String coordinates = "";

					for(int i=0;i<(tokens.length-1);i++) {
						if(i == 0) coordinates = tokens[i];
						else coordinates = coordinates + " " + tokens[i];
					}
					
					String file = tokens[tokens.length-1];
				
					if(new File(file).canRead()) {	
						coordPictures.put(coordinates, file);
						this.getClientGUI().printText("generic", coordinates + " => " + file + "\n");
					}
				}
			}
			
			scanner.close();
		}
		catch(FileNotFoundException e) { }
		
		files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					
					for (Map.Entry<String, String> entry : coordPictures.entrySet()) {
						if(entry.getValue().contains(name)) 
							return false; // don't reuse pictures which have been assigned specific area  
					}
					
					return name.endsWith(".jpg") || name.endsWith(".JPG") || 
							name.endsWith(".png") || name.endsWith(".PNG") ||
							name.endsWith(".jpeg") || name.endsWith(".JPEG");
				}
			});
	}

		
	public ParsedResult trigger(ParsedResult arg0) {
		// this.getClientGUI().printText("generic", "whereami\n");
	
		if(arg0.getStrippedText().contains("Obvious exits are:") || 
			arg0.getStrippedText().contains("Obvious exit is:") || 
			arg0.getStrippedText().contains("Loc:    Arelium") // Arelium maphack
			/*arg0.getStrippedText().contains("Exits: ")*/) {
			
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
				location = matcher.group(1);
				coordinates = matcher.group(3);
				
				// Format: GLOBAL COORDINATES ARE: 7224x, 9062y
				// this.getClientGUI().printText("generic", "LOCATION IS: '" + location + "'\n");
				// this.getClientGUI().printText("generic", "GLOBAL COORDINATES ARE: " + coordinates + "\n");
				
				// now sets wallpaper randomly/not location dependent
				
				if(files.length > 0 && 
					coordinates.equalsIgnoreCase(previousCoordinates) == false /*&& 
					location.equalsIgnoreCase(previousLocation) == false*/) {
					
					// change background image everytime global coordinates/area change..
					
					String filename = files[r.nextInt(files.length)].toString(); // random
					
					for (Map.Entry<String, String> entry : coordPictures.entrySet()) {
						
						//this.getClientGUI().printText("generic", entry.getKey() + "==" + coordinates + "\n");
						
						if(entry.getKey().contains(coordinates) || 
							location.contains(entry.getKey())) {
							filename = entry.getValue();
							break;
						}
							
				    }
					
					// this.getClientGUI().printText("generic", "NEW: " + coordinates + " => " + filename + "\n");
					
					//this.getClientGUI().printText("generic", filename + "\n");
					this.getClientGUI().setWallpaper(filename);
				}
				
				previousCoordinates = coordinates;
				//previousLocation = location;
			}
			
			// removes 'whereami' return string from the input
			txt = txt.replaceAll(regexp, "");
			
			arg0 = new ParsedResult(txt);
		}
		
		return arg0;
	}
	
	// own whereami command
	public String trigger(String line) {
		
		if(line.contains("tellmelocation")) {
			if(coordinates.length() > 0) {
				String value = "Latest location: " + location + " (" + coordinates + ").\n";
				this.getClientGUI().printText("generic", value);
				return line;
			}
			else {
				String value = "No latest location.\n";
				this.getClientGUI().printText("generic", value);
				return line;
			}
		}
		
		return null;
	}
	
}

