package randyg.titlewaves.music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SongBlueprint
{
	public static final int OVERRIDE_ROW = 1;
	public static final int OVERRIDE_COLUMN = 2;
	public static final int OVERRIDE_CELL = 3;

	public static class LineOverride
	{
		public int color = 0;
		public int instrument = Instruments.DEFAULT_ID;
		public int mmScale = 0; // scale in musician's mode
		public int scale = Scales.DEFAULT_INDEX;
		public int rootNote = Keys.DEFAULT_INDEX;
		public int baseOctave = 3;
		public int octaveRange = 1;

		public LineOverride()
		{ }

		public LineOverride(int instrument, int mmScale, int scale, int rootNote, int baseOctave, int octaveRange)
		{
			this.instrument = instrument;
			this.mmScale = mmScale;
			this.scale = scale;
			this.rootNote = rootNote;
			this.baseOctave = baseOctave;
			this.octaveRange = octaveRange;
		}
	}

	public LineOverride createOverride()
	{
		LineOverride override = new LineOverride();
		override.instrument = this.instrument;
		override.mmScale = this.mmScale;
		override.scale = this.scale;
		override.rootNote = this.rootNote;
		override.baseOctave = this.baseOctave;
		override.octaveRange = this.octaveRange;
		return override;
	}

	public static int cellOverrideKey(int x, int y)
	{
		return (y << 16) | x;
	}

	public static final int MODE_SINGLE_NOTE = 0;
	public static final int MODE_CHORDS = 1;

	private String name      = "";
	private String text      = "";
	private int mode         = MODE_SINGLE_NOTE;
	private int mmScale      = 0; // scale in musician's mode
	private int instrument   = Instruments.DEFAULT_ID;
	private int drumPattern  = DrumPatterns.DEFAULT_INDEX;
	private int scale        = Scales.DEFAULT_INDEX;
	private int rootNote     = Keys.DEFAULT_INDEX;
	private int baseOctave   = 3;
	private int octaveRange  = 1;
	private int tempo        = 220; // 240
	//public int timeSignatureA = 4;
	//public int timeSignatureB = 4;

	private HashMap<Integer, LineOverride> horizontalOverride = new HashMap<>();
	private HashMap<Integer, LineOverride> columnOverrides = new HashMap<>();
	private HashMap<Integer, LineOverride> singleOverride = new HashMap<>();

	public SongBlueprint()
	{
	}

	public SongBlueprint(
		String name,
		String text,
		int instrument,
		int drumPattern,
		int scale,
		int rootNote,
		int baseOctave,
		int octaveRange,
		int tempo)
	{
		this.name = name;
		this.text = text;
		this.instrument = instrument;
		this.drumPattern = drumPattern;
		this.scale = scale;
		this.rootNote = rootNote;
		this.baseOctave = baseOctave;
		this.octaveRange = octaveRange;
		this.tempo = tempo;
	}

	public SongBlueprint(
		String name,
		String text,
		String instrumentName,
		String drumPatternName,
		String scaleName,
		String rootNoteName,
		int baseOctave,
		int octaveRange,
		int tempo)
	{
		setName(name);
		setText(text);
		setInstrument(instrumentName);
		setDrumPattern(drumPatternName);
		setScale(scaleName);
		setRootNote(rootNoteName);
		setBaseOctave(baseOctave);
		setOctaveRange(octaveRange);
		setTempo(tempo);
	}


	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public int getMode() { return mode; }
	public void setMode(int mode) { this.mode = mode; }

	public int getMMScale() { return mmScale; }
	public String getMMScaleName() {
		if (mmScale == 0) {
			return "Major - Minor";
		} else if (mmScale == 1) {
			return "Augmented - Diminished";
		} else {
			return "Invalid";
		}
	}
	public void setMMScale(int mmScale) { this.mmScale = mmScale; }
	public void setMMScale(String name) {
		if (name.equals("Major - Minor")) {
			this.mmScale = 0;
		} else if (name.equals("Augmented - Diminished")) {
			this.mmScale = 1;
		}
	}

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	public int getInstrument() { return instrument; }
	public void setInstrument(int instrument) { this.instrument = instrument; }
	public void setInstrument(String name) { this.instrument = Instruments.getId(name); }
	public String getInstrumentName() { return Instruments.getName(this.instrument); }

	public int getDrumPattern() { return drumPattern; }
	public void setDrumPattern(int drumPattern) { this.drumPattern = drumPattern; }
	public void setDrumPattern(String name) { this.drumPattern = DrumPatterns.findIndex(name); }
	public String getDrumPatternName() { return DrumPatterns.getName(this.drumPattern); }

	public int getScale() { return scale; }
	public void setScale(int scaleIndex) { this.scale = scaleIndex; }
	public void setScale(String name) { this.scale = Scales.findIndex(name); }
	public String getScaleName() { return Scales.getName(this.scale); }
	public int[] getScaleIntervals() { return Scales.getIntervals(this.scale); }

	public int getRootNote() { return rootNote; }
	public void setRootNote(int rootNote) { this.rootNote = rootNote; }
	public void setRootNote(String name) { this.rootNote = Keys.findIndex(name); }
	public String getRootNoteName() { return Keys.getName(this.rootNote); }

	public int getBaseOctave() { return baseOctave; }
	public void setBaseOctave(int baseOctave) { this.baseOctave = baseOctave; }

	public int getOctaveRange() { return octaveRange; }
	public void setOctaveRange(int octaveRange) { this.octaveRange = octaveRange; }

	public int getTempo() { return tempo; }
	public void setTempo(int tempo) { this.tempo = tempo; }

	public HashMap<Integer, LineOverride> getHorizontalOverride() { return horizontalOverride; }
	public HashMap<Integer, LineOverride> getVerticalOverride() { return columnOverrides; }
	public HashMap<Integer, LineOverride> getCellOverride() { return singleOverride; }

	public LineOverride getOverride(int x, int y)
	{
		if (x != -1 && y != -1)
		{
			SongBlueprint.LineOverride override = getCellOverride().get(cellOverrideKey(x, y));
			if (override != null)
				return override;

			override = getVerticalOverride().get(x);
			if (override != null)
				return override;

			override = getHorizontalOverride().get(y);
			if (override != null)
				return override;
		}

		return null;
	}

	public int getSingleMidiNote(int ch, int x, int y)
	{
		SongBlueprint.LineOverride lineOverride = getOverride(x, y);

		if (lineOverride != null) {
			return MidiTransform.charToMidiNote(
					ch,
					lineOverride.baseOctave,
					lineOverride.octaveRange,
					lineOverride.rootNote,
					Scales.getIntervals(lineOverride.scale));
		} else {
			return MidiTransform.charToMidiNote(
					ch,
					getBaseOctave(),
					getOctaveRange(),
					getRootNote(),
					getScaleIntervals());
		}
	}

	public int[] getChordMidiNotes(int ch, int x, int y)
	{
		SongBlueprint.LineOverride lineOverride = getOverride(x, y);

		boolean majorMinor = getMMScale() == 0;

		if (lineOverride != null)
		{
			majorMinor = lineOverride.mmScale == 0;
		}

		ch = Character.toUpperCase(ch);

		int[] midiNotes = new int[3];
		MidiTransform.charToMidiChord(ch, 0, midiNotes, majorMinor);

		return midiNotes;
	}

	public int[] getMidiNote(int ch, int x, int y)
	{
		if (getMode() == 0)
		{
			return new int[] { getSingleMidiNote(ch, x, y) };
		}
		else
		{
			return getChordMidiNotes(ch, x, y);
		}
	}

	public static JSONArray overridesToJson(HashMap<Integer, LineOverride> overrides) throws JSONException
	{
		JSONArray jsonLineOverrides = new JSONArray();
		for (Map.Entry<Integer, LineOverride> entry : overrides.entrySet())
		{
			LineOverride lineOverride = entry.getValue();

			JSONObject obj = new JSONObject();
			obj.put("line", (int)entry.getKey());
			obj.put("color", lineOverride.color);
			obj.put("instrument", Instruments.getName(lineOverride.instrument));
			obj.put("scale", Scales.getName(lineOverride.scale));
			obj.put("mmscale", lineOverride.mmScale);
			obj.put("root_note", Keys.getName(lineOverride.rootNote));
			obj.put("base_octave", lineOverride.baseOctave);
			obj.put("octave_range", lineOverride.octaveRange);
			jsonLineOverrides.put(obj);
		}
		return jsonLineOverrides;
	}

	public static HashMap<Integer, LineOverride> overridesFromJson(JSONArray jsonOverrides) throws JSONException
	{
		HashMap<Integer, LineOverride> overrides = new HashMap<>();

		for (int i = 0; i < jsonOverrides.length(); i++)
		{
			JSONObject obj = jsonOverrides.getJSONObject(i);

			LineOverride lineOverride = new LineOverride(
					// instrument
					Instruments.getId(obj.getString("instrument")),

					// mmscale
					obj.optInt("mmscale", 0),

					// scale
					Scales.findIndex(obj.getString("scale")),

					// rootNote
					Keys.findIndex(obj.getString("root_note")),

					// baseOctave
					obj.getInt("base_octave"),

					// octaeRange
					obj.getInt("octave_range")
			);

			lineOverride.color = obj.optInt("color", 0);

			int line = obj.getInt("line");

			overrides.put(line, lineOverride);
		}

		return overrides;
	}

	public static JSONObject toJSON(SongBlueprint blueprint) throws JSONException
	{
		JSONObject js = new JSONObject();
		js.put("name",         blueprint.getName());
		js.put("notes",        blueprint.getText());
		js.put("instrument",   blueprint.getInstrumentName());
		js.put("drum_pattern", blueprint.getDrumPatternName());
		js.put("scale",        blueprint.getScaleName());
		js.put("root_note",    blueprint.getRootNoteName());
		js.put("base_octave",  blueprint.getBaseOctave());
		js.put("octave_range", blueprint.getOctaveRange());
		js.put("tempo",        blueprint.getTempo());
		js.put("mode",         blueprint.getMode());
		js.put("mmscale",      blueprint.getMMScale());

		JSONArray jsonLineOverrides = overridesToJson(blueprint.horizontalOverride);
		js.put("overrides", jsonLineOverrides); // horizontal

		JSONArray jsonColumnOverrides = overridesToJson(blueprint.columnOverrides);
		js.put("vertical_overrides", jsonColumnOverrides);

		JSONArray jsonCellOverrides = overridesToJson(blueprint.singleOverride);
		js.put("cell_overrides", jsonCellOverrides);

		return js;
	}

	public static SongBlueprint fromJSON(JSONObject js) throws JSONException
	{
		SongBlueprint blueprint = new SongBlueprint();
		blueprint.setName       (js.getString("name"));
		blueprint.setText       (js.getString("notes"));
		blueprint.setInstrument (js.getString("instrument"));
		blueprint.setDrumPattern(js.has("drum_pattern") ? js.getString("drum_pattern") : "None");
		blueprint.setScale      (js.getString("scale"));
		blueprint.setRootNote   (js.getString("root_note"));
		blueprint.setBaseOctave (js.getInt("base_octave"));
		blueprint.setOctaveRange(js.getInt("octave_range"));
		blueprint.setTempo      (js.getInt("tempo"));
		blueprint.setMode       (js.optInt("mode", MODE_SINGLE_NOTE));
		blueprint.setMMScale    (js.optInt("mmScale", 0));

		JSONArray rowOverrides = js.optJSONArray("overrides");
		if (rowOverrides != null) {
			blueprint.horizontalOverride = overridesFromJson(rowOverrides);
		}

		JSONArray verticalOverrides = js.optJSONArray("vertical_overrides");
		if (verticalOverrides != null) {
			blueprint.columnOverrides = overridesFromJson(verticalOverrides);
		}

		JSONArray cellOverrides = js.optJSONArray("cell_overrides");
		if (cellOverrides != null) {
			blueprint.singleOverride = overridesFromJson(cellOverrides);
		}

		return blueprint;
	}
}

