package randyg.titlewaves.music;

import java.io.IOException;

public class Song
{
	private SongBlueprint blueprint;
	private SongRawData rawData;
	private SongMidiData midiData;

	public Song(SongBlueprint blueprint) throws IOException
	{
	    boolean stackChords = blueprint.getMode() == 0;

		this.blueprint = blueprint;
		this.rawData = new SongRawData(blueprint.getText(), stackChords);
		this.midiData = new SongMidiData(this);
	}

	public SongBlueprint getBlueprint()
	{
		return blueprint;
	}

	public SongRawData getRawData()
	{
		return rawData;
	}

	public SongMidiData getMidiData()
	{
		return midiData;
	}
}

