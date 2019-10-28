package randyg.titlewaves.music;

import java.util.LinkedHashMap;

public class MidiTransform
{
	public static int charToMidiNote(
		int ch,
		int baseOctave,
		int numOctaves,
		int rootNote,
		int[] intervals)
	{
		ch = Character.toUpperCase(ch);

		if (ch < 'A' || ch > 'Z') {
			return 0;
		} else {
			ch -= (int)'A';
		}

		int base = (baseOctave + 1) * 12 + rootNote;
		int note = intervals[ch % intervals.length];
		note += ((ch / intervals.length) % (numOctaves + 1)) * 12;
		return base + note;
	}

	public static void charToMidiChord(int ch, int offset, int[] notes, boolean majorMinor)
	{
		ch = Character.toUpperCase(ch);

		final LinkedHashMap<Integer, Chrods.Chord> chords = majorMinor
			? Chrods.chords_majMin : Chrods.chords_augDim;

		if (chords.containsKey(ch))
		{
			int[] chordNotes = chords.get(ch).notes;
			notes[offset] = chordNotes[0];
			notes[offset + 1] = chordNotes[1];
			notes[offset + 2] = chordNotes[2];
		}
		else
		{
			notes[offset] = 0;
			notes[offset + 1] = 0;
			notes[offset + 2] = 0;
		}
	}

	public static int[] transform(SongBlueprint blueprint, SongRawData rawData)
	{
		int[] midiNotes;

		if (blueprint.getMode() == 0) // single note mode
		{
			midiNotes = new int[ rawData.notes.size() ];

			int index = 0;
			for (SongRawData.Note note : rawData.notes)
			{
				midiNotes[index++] = blueprint.getSingleMidiNote(note.ch, note.column, note.line);
			}
		}
		else // chord mode (musician's mode)
		{
			midiNotes = new int[ rawData.notes.size() * 3 ];

			int offset = 0;
			
			for (SongRawData.Note note : rawData.notes)
			{
				boolean majorMinor = blueprint.getMMScale() == 0;

				SongBlueprint.LineOverride lineOverride = blueprint.getOverride(note.column, note.line);

				if (lineOverride != null)
				{
					majorMinor = lineOverride.mmScale == 0;
				}

				int ch = note.ch;
				ch = Character.toUpperCase(ch);

				charToMidiChord(ch, offset, midiNotes, majorMinor);

				offset += 3;
			}
		}

		return midiNotes;
	}
}
