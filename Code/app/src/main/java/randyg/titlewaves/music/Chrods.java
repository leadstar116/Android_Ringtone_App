package randyg.titlewaves.music;

import java.util.LinkedHashMap;

public class Chrods
{
	public static class Chord
	{
		public String name;
		public int[] notes;

		Chord(String name, int[] notes)
		{
			this.name = name;
			this.notes = notes;

			final int midiBase = 60; // C4
			for (int i=0; i<this.notes.length; i++) {
				this.notes[i] %= 12;
				this.notes[i] += midiBase;
			}
		}
	}

	public static LinkedHashMap<Integer, Chord> chords_majMin;
	public static LinkedHashMap<Integer, Chord> chords_augDim;

	static
	{
		chords_majMin = new LinkedHashMap<>();
		chords_majMin.put((int)'A', new Chord("A Maj",  new int[]{ 9, 13, 16 }));
		chords_majMin.put((int)'H', new Chord("Bb Maj", new int[]{ 10, 14, 17 })); // A#
		chords_majMin.put((int)'B', new Chord("B Maj",  new int[]{ 11, 15, 18 }));
		chords_majMin.put((int)'C', new Chord("C Maj",  new int[]{ 0, 4, 7 }));
		chords_majMin.put((int)'I', new Chord("C# Maj", new int[]{ 1, 5, 8 }));
		chords_majMin.put((int)'D', new Chord("D Maj",  new int[]{ 2, 6, 9 }));
		chords_majMin.put((int)'J', new Chord("Eb Maj", new int[]{ 3, 7, 10 })); // Eb
		chords_majMin.put((int)'E', new Chord("E Maj",  new int[]{ 4, 8, 11 }));
		chords_majMin.put((int)'F', new Chord("F Maj",  new int[]{ 5, 9, 12 }));
		chords_majMin.put((int)'K', new Chord("F# Maj", new int[]{ 6, 10, 13 }));
		chords_majMin.put((int)'G', new Chord("G Maj",  new int[]{ 7, 11, 14 }));
		chords_majMin.put((int)'L', new Chord("Ab Maj", new int[]{ 8, 12, 15 })); // G#

		chords_majMin.put((int)'M', new Chord("A Min",  new int[]{ 9, 12, 16 }));
		chords_majMin.put((int)'T', new Chord("Bb Min", new int[]{ 10, 13, 17 })); // A#
		chords_majMin.put((int)'N', new Chord("B Min",  new int[]{ 11, 14, 18 }));
		chords_majMin.put((int)'O', new Chord("C Min",  new int[]{ 0, 3, 7 }));
		chords_majMin.put((int)'U', new Chord("C# Min", new int[]{ 1, 4, 8 }));
		chords_majMin.put((int)'P', new Chord("D Min",  new int[]{ 2, 5, 9 }));
		chords_majMin.put((int)'V', new Chord("Eb Min", new int[]{ 3, 6, 10 })); // D#
		chords_majMin.put((int)'Q', new Chord("E Min",  new int[]{ 4, 7, 11 }));
		chords_majMin.put((int)'R', new Chord("F Min",  new int[]{ 5, 8, 12 }));
		chords_majMin.put((int)'W', new Chord("F# Min", new int[]{ 6, 9, 13 }));
		chords_majMin.put((int)'S', new Chord("G Min",  new int[]{ 7, 10, 14 }));
		chords_majMin.put((int)'X', new Chord("Ab Min", new int[]{ 8, 11, 15 })); // G#

		chords_augDim = new LinkedHashMap<>();
        chords_augDim.put((int)'A', new Chord("A Aug", new int[]{ 9, 13, 17 }));
        chords_augDim.put((int)'H', new Chord("A# Aug", new int[]{ 10, 14, 18 }));
        chords_augDim.put((int)'B', new Chord("B Aug", new int[]{ 11, 15, 19 }));
		chords_augDim.put((int)'C', new Chord("C Aug", new int[]{ 0, 4, 8 }));
		chords_augDim.put((int)'I', new Chord("C# Aug", new int[]{ 1, 5, 9 }));
		chords_augDim.put((int)'D', new Chord("D Aug", new int[]{ 2, 6, 10 }));
		chords_augDim.put((int)'J', new Chord("D# Aug", new int[]{ 3, 7, 11 }));
		chords_augDim.put((int)'E', new Chord("E Aug", new int[]{ 4, 8, 12 }));
		chords_augDim.put((int)'F', new Chord("F Aug", new int[]{ 5, 9, 13 }));
		chords_augDim.put((int)'K', new Chord("F# Aug", new int[]{ 6, 10, 14 }));
		chords_augDim.put((int)'G', new Chord("G Aug", new int[]{ 7, 11, 15 }));
		chords_augDim.put((int)'L', new Chord("G# Aug", new int[]{ 8, 12, 16 }));

        chords_augDim.put((int)'M', new Chord("A Dim", new int[]{ 9, 12, 15 }));
        chords_augDim.put((int)'T', new Chord("A# Dim", new int[]{ 10, 13, 16 }));
        chords_augDim.put((int)'N', new Chord("B Dim", new int[]{ 11, 14, 17 }));
		chords_augDim.put((int)'O', new Chord("C Dim", new int[]{ 0, 3, 6 }));
		chords_augDim.put((int)'U', new Chord("C# Dim", new int[]{ 1, 4, 7 }));
		chords_augDim.put((int)'P', new Chord("D Dim", new int[]{ 2, 5, 8 }));
		chords_augDim.put((int)'V', new Chord("Eb Dim", new int[]{ 3, 6, 9 })); // D#
		chords_augDim.put((int)'Q', new Chord("E Dim", new int[]{ 4, 7, 10 }));
		chords_augDim.put((int)'R', new Chord("F Dim", new int[]{ 5, 8, 11 }));
		chords_augDim.put((int)'W', new Chord("F# Dim", new int[]{ 6, 9, 12 }));
		chords_augDim.put((int)'S', new Chord("G Dim", new int[]{ 7, 10, 13 }));
		chords_augDim.put((int)'X', new Chord("Ab Dim", new int[]{ 8, 11, 14 })); // G#
	}
}

