package randyg.titlewaves.music;

import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public class Instruments
{
	public static final int DEFAULT_ID = 10; // Music Box

	public static LinkedHashMap<Integer, String> instruments = null;

	static
	{
		instruments = new LinkedHashMap<Integer, String>();

		// Piano
		instruments.put(0, "Acoustic Grand Piano");
		instruments.put(1, "Bright Acoustic Piano");
		instruments.put(2, "Electric Grand Piano");
		instruments.put(3, "Honky-tonk Piano");
		instruments.put(4, "Electric Piano 1");
		instruments.put(5, "Electric Piano 2");
		//instruments.put(6, "Harpsichord"); // distorted sound
		//instruments.put(7, "Clavinet"); // missing samples

		// Chromatic Percussion
		instruments.put(8, "Celesta");
		instruments.put(9, "Glockenspiel");
		instruments.put(10, "Music Box");
		instruments.put(11, "Vibraphone");
		instruments.put(12, "Marimba");
		instruments.put(13, "Xylophone");
		//instruments.put(14, "Tubular Bells"); // missing samples
		instruments.put(15, "Dulcimer");

		// Organ
		instruments.put(16, "Drawbar Organ");
		instruments.put(17, "Percussive Organ");
		instruments.put(18, "Rock Organ");
		instruments.put(19, "Church Organ");
		instruments.put(20, "Reed Organ");
		instruments.put(21, "Accordion");
		//instruments.put(22, "Harmonica"); // missing samples
		instruments.put(23, "Tango Accordion");

		// Guitar
		instruments.put(24, "Acoustic Guitar (nylon)");
		instruments.put(25, "Acoustic Guitar (steel)");
		instruments.put(26, "Electric Guitar (jazz)");
		instruments.put(27, "Electric Guitar (clean)");
		instruments.put(28, "Electric Guitar (muted)");
		//instruments.put(29, "Overdriven Guitar"); // same as distortion guitar
		instruments.put(30, "Distortion Guitar");
		instruments.put(31, "Guitar Harmonics");

		// Bass
		instruments.put(32, "Acoustic Bass");
		instruments.put(33, "Electric Bass (finger)");
		instruments.put(34, "Electric Bass (pick)");
		instruments.put(35, "Fretless Bass");
		instruments.put(36, "Slap Bass 1");
		instruments.put(37, "Slap Bass 2");
		instruments.put(38, "Synth Bass 1");
		instruments.put(39, "Synth Bass 2");

		// Strings
		instruments.put(40, "Violin");
		instruments.put(41, "Viola");
		//instruments.put(42, "Cello"); // not working
		//instruments.put(43, "Contrabass"); // not working
		instruments.put(44, "Tremolo Strings");
		instruments.put(45, "Pizzicato Strings");
		instruments.put(46, "Orchestral Harp");
		//instruments.put(47, "Timpani"); // not working

		// Ensemble
		instruments.put(48, "String Ensemble 1");
		instruments.put(49, "String Ensemble 2");
		instruments.put(50, "Synth Strings 1");
		instruments.put(51, "Synth Strings 2");
		instruments.put(52, "Choir Aahs");
		instruments.put(53, "Voice Oohs");
		instruments.put(54, "Synth Choir");
		instruments.put(55, "Orchestra Hit");

		// Brass
		instruments.put(56, "Trumpet");
		instruments.put(57, "Trombone");
		//instruments.put(58, "Tuba"); // not working
		instruments.put(59, "Muted Trumpet");
		instruments.put(60, "French Horn");
		//instruments.put(61, "Brass Section"); // not working
		//instruments.put(62, "Synth Brass 1"); // not working
		//instruments.put(63, "Synth Brass 2"); // not working

		// Reed
		instruments.put(64, "Soprano Sax");
		instruments.put(65, "Alto Sax");
		instruments.put(66, "Tenor Sax");
		instruments.put(67, "Baritone Sax"); // missing samples
		instruments.put(68, "Oboe");
		instruments.put(69, "English Horn");
		//instruments.put(70, "Bassoon"); // not working
		instruments.put(71, "Clarinet");

		// Pipe
		instruments.put(72, "Piccolo");
		instruments.put(73, "Flute");
		//instruments.put(74, "Recorder");
		instruments.put(75, "Pan Flute");
		instruments.put(76, "Blown bottle");
		instruments.put(77, "Shakuhachi");
		instruments.put(78, "Whistle");
		//instruments.put(79, "Ocarina");

		// Synth Lead
		instruments.put(80, "Lead (square)");
		//instruments.put(81, "Lead 2 (sawtooth)"); // not working
		//instruments.put(82, "Lead 3 (calliope)"); // worthless
		instruments.put(83, "Lead (chiff)");
		instruments.put(84, "Lead (charang)");
		instruments.put(85, "Lead (voice)");
		instruments.put(86, "Lead (fifths)");
		instruments.put(87, "Lead (bass + lead)");

		// Synth Pad
		instruments.put(88, "Pad (new age)");
		instruments.put(89, "Pad (warm)");
		//instruments.put(90, "Pad 3 (polysynth)"); // not working
		//instruments.put(91, "Pad 4 (choir)");
		instruments.put(92, "Pad (bowed)");
		//instruments.put(93, "Pad 6 (metallic)");
		instruments.put(94, "Pad (halo)");
		//instruments.put(95, "Pad 8 (sweep)"); // not working

		// Ethnic
		instruments.put(104, "Sitar");
		instruments.put(105, "Banjo");
		instruments.put(106, "Shamisen");
		instruments.put(107, "Koto");
		instruments.put(108, "Kalimba");
		instruments.put(109, "Bagpipe");
		instruments.put(110, "Fiddle");
		instruments.put(111, "Shanai");
	}

	public static String[] getNames()
	{
		String[] names = new String[instruments.size()];
		int i=0;
		for (String name : instruments.values())
			names[i++] = name;
		return names;
	}

	public static String getName(int id)
	{
		if (instruments.containsKey(id))
			return instruments.get(id);
		return "Unknown";
	}

	public static int getId(String name)
	{
		for (Entry<Integer, String> entry : instruments.entrySet()) {
	        if (name.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return DEFAULT_ID;
	}

	public static int findIndex(int id)
	{
		int idx = 0;
		for (Map.Entry<Integer, String> entry : instruments.entrySet())
		{
			if (entry.getKey() == id)
				return idx;
			idx++;
		}
		return -1;
	}
}

