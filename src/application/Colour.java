package application;

import java.util.HashMap;
import java.util.Random;

public class Colour {

	public static HashMap<ColourName, String> colourCode = new HashMap<>();
	enum ColourName {
		RED,
		GREEN,
		BLUE,
		YELLOW
	}
	static {
		colourCode.put(ColourName.RED,"#D60808");
		colourCode.put(ColourName.GREEN,"#3DA414");
		colourCode.put(ColourName.BLUE,"#080FD6");
		colourCode.put(ColourName.YELLOW,"#D3D608");
	}
	
	private ColourName name;
	private String code;
	void setColour(ColourName colourName){
		name = colourName;
		code = colourCode.get(colourName);
	}
	
	public boolean equals(Colour c) {
		return (name.equals(c.name));
	}
	
	public Colour(ColourName colourName){
		setColour(colourName);
	}
	public Colour() {
		Random r = new Random();
		int x = r.nextInt(4);
		switch (x) {
			case 0:
				setColour(ColourName.RED);
				break;
			case 1:
				setColour(ColourName.GREEN);
				break;
			case 2:
				setColour(ColourName.BLUE);
				break;
			case 3:
				setColour(ColourName.YELLOW);
				break;
		}
	}
	public Colour(String name) {
		switch (name) {
		case "Red":
			setColour(ColourName.RED);
			break;
		case "Green":
			setColour(ColourName.GREEN);
			break;
		case "Blue":
			setColour(ColourName.BLUE);
			break;
		case "Yellow":
			setColour(ColourName.YELLOW);
			break;
		default:
			setColour(ColourName.BLUE);
		}
	}
	String getCode() {return code;}
	public static boolean validColourName(String name) {
		switch (name) {
		case "Red":
		case "Green":
		case "Blue":
		case "Yellow":
			return true;
		default:
			return false;
		}
	}
}
