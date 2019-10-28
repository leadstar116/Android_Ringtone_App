package randyg.titlewaves.music;

import java.util.ArrayList;

public class DrumPatterns
{
	/*public enum Percussion
	{
	    NONE(0),
        ACOUSTIC_BASS_DRUM(35),
        BASS_DRUM_1(36),
        SIDE_STICK(37), // RIMSHOT
        ACOUSTIC_SNARE(38),
        HAND_CLAP(39),
        ELECTRIC_SNARE(40),
        LOW_FLOOR_TOM(41),
        CLOSED_HI_HAT(42),
        HIGH_FLOOR_TOM(43),
        PEDAL_HI_HAT(44),
        LOW_TOM(45),
        OPEN_HI_HAT(46),
        LOW_MID_TOM(47),
        HI_MID_TOM(48),
        CRASH_CYMBAL_1(49),
        HIGH_TOM(50),
        RIDE_CYMBAL_1(51),
        CHINESE_CYMBAL(52),
        RIDE_BELL(53),
        TAMBOURINE(54),
        SPLASH_CYMBAL(55),
        COWBELL(56),
        CRASH_CYMBAL_2(57),
        VIBRA_SLAP(58),
        RIDE_CYMBAL_2(59),
        HIGH_BONGO(60),
        LOW_BONGO(61),
        MUTE_HIGH_CONGA(62),
        OPEN_HIGH_CONGA(63),
        LOW_CONGA(64),
        HIGH_TIMBALE(65),
        LOW_TIMBALE(66),
        HIGH_AGOGO(67),
        LOW_AGOGO(68),
        CABASA(69),
        MARACAS(70),
        SHORT_WHISTLE(71),
        LONG_WHISTLE(72),
        SHORT_GUIRO(73),
        LONG_GUIRO(74),
        CLAVES(75),
        HIGH_WOOD_BLOCK(76),
        LOW_WOOD_BLOCK(77),
        MUTE_CUICA(78),
        OPEN_CUICA(79),
        MUTE_TRIANGLE(80),
        OPEN_TRIANGLE(81)
        ;

        private final int midiCode;

        Percussion(int code) {
            this.midiCode = code;
        }

        public int toMidiCode() {
            return midiCode;
        }
    }*/

	public static class Pattern
	{
		public String name;
		public int[] notes;

		public Pattern(String name, int[] notes)
		{
		    this.name = name;
		    this.notes = notes;
		}
	}

	public static ArrayList<Pattern> patterns;
	public static int DEFAULT_INDEX = 1;

	static
	{
		patterns = new ArrayList<>();
		patterns.add(new Pattern("Live",    new int[]{ }));
        patterns.add(new Pattern("None",    new int[]{ }));

		patterns.add(new Pattern("Drums 1", new int[]{
			35, 0, 39, 0,
			35, 0, 39, 0
		}));
        patterns.add(new Pattern("Drums 2", new int[]{
            35, 0,  42, 0,
            35, 35, 42, 0
        }));
        patterns.add(new Pattern("Drums 3", new int[]{
        	35, 42, 39, 42,
        	35, 42, 39, 42
        }));

        //patterns.add(new Pattern("Drums 3 (Test)", new int[]{
        //        35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
        //        51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,
        //        67,68,69,70,71,72,73,74,75,76,77,78,79,80,81}));
	}

	public static String[] getNames() {
	    String[] names = new String[patterns.size()];
	    for (int i=0; i<names.length; i++)
	        names[i] = patterns.get(i).name;
	    return names;
	}

	public static int findIndex(String name)
	{
	    for (int i=0; i<patterns.size(); i++)
	        if (patterns.get(i).name.equals(name))
	            return i;
	    return -1;
	}

	public static String getName(int index)
	{
	    if (index >= 0 && index < patterns.size())
	        return patterns.get(index).name;
	    return "None";
	}

	public static int[] getNotes(int index)
	{
	    if (index >= 0 && index < patterns.size())
	        return patterns.get(index).notes;
	    return new int[0];
	}
}

