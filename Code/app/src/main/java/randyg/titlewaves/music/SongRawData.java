package randyg.titlewaves.music;

import java.util.*;

public class SongRawData
{
	public static class CharLocation
	{
		public int chr;
		public int row;
		public int col;

		CharLocation(int chr, int row, int col)
		{
			this.chr = chr;
			this.row = row;
			this.col = col;
		}
	}

	public static class Note
	{
		public int ch;
		//public int note;
		public int start;
		public int length;
		public int line;
		public int column;
	}

	public static class Segment
	{
		public ArrayList<CharLocation> chars = new ArrayList<>();
	}

	//public SongBlueprint blueprint;
	public String notesStr;
	public int totalLength = 0;
	public int totalNoteLength = 0;
	public ArrayList<Note> notes = new ArrayList<>();
	public ArrayList<Segment> segments = new ArrayList<>();

	public SongRawData(String notesStr, boolean stackChords)
	{
		//this.blueprint = blueprint;
		this.notesStr = notesStr.replace(' ', '_');
		parseNotes(this.notesStr, stackChords);
	}

	public void debugPrint()
	{
		System.out.println("SongRawData:");
		System.out.println("------------");
		System.out.println("Total Length: " + this.totalLength);
		System.out.println("Total Note Length: " + this.totalNoteLength);

		System.out.println("Segments:");
		System.out.println("---------");
		for (Segment segment : this.segments)
		{
			for (CharLocation loc : segment.chars)
			{
				System.out.format("%c(%d:%d) ", loc.chr, loc.row, loc.col);
			}
			System.out.print("\n");
		}

		System.out.println("Notes:");
		System.out.println("------");
		for (Note note : this.notes)
		{
			System.out.format("%c(%d:%d:L%d) ", note.ch, note.start, note.length, note.line);
		}
		System.out.print("\n");
	}

	private void addChar(int segment_index, int chr, int row, int col)
	{
		if (segments.size() <= segment_index)
			segments.add(new Segment());
		segments.get(segment_index).chars.add(new CharLocation(chr, row, col));
	}

	private void parseNotes(String notesStr, boolean stackChords)
	{
		notesStr += "\n ";

		String[] lines = notesStr.split("\\r?\\n");
		
		int position = 0;
		int line = -1;
		int longestLine = 0;

		for (String s : lines)
		{
			line++;

			if (s.trim().equals(""))
			{
				position += longestLine;
				longestLine = 0;
				continue;
			}
			else
			{
				if (longestLine < s.length())
					longestLine = s.length();
			}

			for (int i=0; i<s.length(); i++)
			{
				int ch = Character.toUpperCase(s.charAt(i));

				int segment_index = position + i;
				this.addChar(segment_index, ch, line, i);

				if (ch < 'A' || ch > 'Z')
					continue;

				int length = 1;
				for (int j=i+1; j<s.length(); j++)
				{
					int nextCh = s.charAt(j);
					if (nextCh != '.')
						break;
					length++;
				}

				Note note = new Note();
				note.ch = ch;
				//note.note = 0;
				note.start = position + i;
				note.length = length;
				note.line = line;
				note.column = i;
				this.notes.add(note);

				if (this.totalNoteLength < note.start + note.length)
					this.totalNoteLength = note.start + note.length;
			}

			if (!stackChords)
			{
				position += s.length();
				longestLine = 0;
			}
		}

		this.totalLength = position;
	}
}

