package randyg.titlewaves.music;

import com.leff.midi.*;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SongMidiData
{
	private ByteArrayOutputStream midiByteStream;
	private MidiFile midiFile;
	private int[] midiNotes;

	public SongMidiData(Song song) throws IOException
	{
		this.midiByteStream = new ByteArrayOutputStream();
		this.midiFile = null;
		this.midiNotes = MidiTransform.transform(song.getBlueprint(), song.getRawData());

		composeMidi(song);
	}

	public String toString()
	{
		String str = "";
		str += "byteStream.size => " + midiByteStream.size() + "\n";
		str += "midiFile.lengthInTicks => " + midiFile.getLengthInTicks() + "\n";
		str += "midiNotes.size => " + midiNotes.length + "\n";
		return str;
	}

	public ByteArrayOutputStream getByteStream()
	{
		return midiByteStream;
	}


	public MidiFile getMidiFile()
	{
		return midiFile;
	}

	public static class InstrumentTrack {
		public MidiTrack midiTrack;
		public int channel;
	};

	private MidiFile composeMidi(Song song) throws IOException
	{
		int nextChannel = 0;

		SongBlueprint blueprint = song.getBlueprint();
		SongRawData rawData = song.getRawData();

		Random rand = new Random(92837465);

	    MidiTrack tempoTrack = new MidiTrack();
	    MidiTrack drumTrack = new MidiTrack();

	    TimeSignature ts = new TimeSignature();
	    Tempo tempo = new Tempo();
	    ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
	    tempo.setBpm(blueprint.getTempo());
	    tempoTrack.insertEvent(ts);
	    tempoTrack.insertEvent(tempo);

		HashMap<Integer, InstrumentTrack> instrumentTracks = new HashMap<>();

		InstrumentTrack mainTrack = new InstrumentTrack();
		mainTrack.channel = nextChannel++;
		mainTrack.midiTrack = new MidiTrack();
		mainTrack.midiTrack.insertEvent(new ProgramChange(0, mainTrack.channel, blueprint.getInstrument()));
		instrumentTracks.put(blueprint.getInstrument(), mainTrack);

	    final int DEFAULT_NOTE_LENGTH = MidiFile.DEFAULT_RESOLUTION;

	    int note_index = 0;
	    int stride = blueprint.getMode() == 0 ? 1 : 3;

	    for (SongRawData.Note note : rawData.notes)
	    {
	    	int velocity = 75 + rand.nextInt(10);
	    	long duration = note.length * DEFAULT_NOTE_LENGTH; // / 4
	    	long tick = note.start * DEFAULT_NOTE_LENGTH;

			InstrumentTrack track = mainTrack;

			SongBlueprint.LineOverride lineOverride = blueprint.getOverride(note.column, note.line); //lineOverrides.get(note.line);

			if (lineOverride != null && lineOverride.instrument != blueprint.getInstrument())
			{
				track = instrumentTracks.get(lineOverride.instrument);
				if (track == null)
				{
					track = new InstrumentTrack();

					track.channel = nextChannel++;
					if (track.channel == 9)
						track.channel = nextChannel++;

					track.midiTrack = new MidiTrack();
					track.midiTrack.insertEvent(new ProgramChange(tick, track.channel, lineOverride.instrument));
					instrumentTracks.put(lineOverride.instrument, track);
				}
			}

	    	for (int i = 0; i < stride; i++)
	    	{
	    		int midi_note = this.midiNotes[note_index];
	    		if (midi_note != 0)
	    		{
					track.midiTrack.insertNote(track.channel, midi_note, velocity, tick, duration);
	    		}

	    		note_index++;
	    	}
	    }

		List<MidiTrack> tracks = new ArrayList<MidiTrack>();
		tracks.add(tempoTrack);

		for (Map.Entry<Integer, InstrumentTrack> entry : instrumentTracks.entrySet())
		{
			InstrumentTrack track = entry.getValue();
			tracks.add(track.midiTrack);

			if (rawData.totalLength > 0)
			{
				track.midiTrack.insertNote(track.channel, 1, 1, 0, rawData.totalLength * DEFAULT_NOTE_LENGTH);
			}
		}

		final int drumPatternId = blueprint.getDrumPattern();
		if (drumPatternId > 1)
		{
			int[] drumPattern = DrumPatterns.getNotes(drumPatternId);
			int patternLen = drumPattern.length;
			for (int i=0; i<rawData.totalLength && patternLen != 0; i++)
	        {
	            long tick = i * DEFAULT_NOTE_LENGTH;
	            
	            int drumNote = drumPattern[i % patternLen];

	            drumTrack.insertNote(9, drumNote, 100, tick, DEFAULT_NOTE_LENGTH / 2);
	        }

	        tracks.add(drumTrack);
    	}

	    this.midiFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
	    this.midiFile.writeToStream(this.midiByteStream);

	    return this.midiFile;
	}

	public void writeToFile(File file) throws IOException
	{
		midiFile.writeToFile(file);
	}
}

