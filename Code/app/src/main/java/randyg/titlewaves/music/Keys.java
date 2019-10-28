package randyg.titlewaves.music;

import java.util.ArrayList;

public class Keys
{
	public static int DEFAULT_INDEX = 0;
	public static ArrayList<String> keys = new ArrayList<>();

	static
	{
		keys.add("C");
		keys.add("C#");
		keys.add("D");
		keys.add("D#");
		keys.add("E");
		keys.add("F");
		keys.add("F#");
		keys.add("G");
		keys.add("G#");
		keys.add("A");
		keys.add("A#");
		keys.add("B");
	}

	public static String[] getNames()
	{
	    String[] names = new String[keys.size()];
	    for (int i=0; i<names.length; i++)
	        names[i] = keys.get(i);
	    return names;
	}

	public static int findIndex(String name)
	{
	    for (int i=0; i<keys.size(); i++)
	        if (keys.get(i).equals(name))
	            return i;
	    return -1;
	}

	public static String getName(int index)
	{
	    if (index >= 0 && index < keys.size())
	        return keys.get(index);
	    return "Unknown";
	}

	public static int getNumKeys()
	{
		return keys.size();
	}
}

