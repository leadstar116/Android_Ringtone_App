package randyg.titlewaves.music;

public class MidiUtils
{
	// midiNote -> scientific pitch notation
	public static String getMidiNoteSPN(int midiNote)
	{
		if (midiNote == 0)
			return "";

		int octave = midiNote / 12;
		int note   = midiNote % 12;
		return String.format("%s%d", Keys.getName(note), octave);
	}

	public static String[] getMidiNoteSPN(int[] midiNotes)
	{
		String[] result = new String[midiNotes.length];
		for (int i=0; i<midiNotes.length; i++)
			result[i] = getMidiNoteSPN(midiNotes[i]);
		return result;
	}
}


