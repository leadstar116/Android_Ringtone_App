package randyg.titlewaves;

import randyg.titlewaves.music.Keys;
import randyg.titlewaves.music.SongBlueprint;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database
{
	static final int VERSION = 1;
	static final String DATABASE_FILE_NAME = "melodies_db_v" + VERSION + ".json";

	private MainActivity mainActivity;
	private JSONObject jsonObject = null;

	Database(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;

		load();
	}

	private String getInitialJson()
	{
		return String.format(""
			+ "\n{"
			+ "\n    \"version\": %d,"
			+ "\n    \"melodies\": [],"
			+ "\n    \"additional_songs_1\": true"
			+ "\n}"

			, VERSION
		);
	}

	public boolean hasName(String name)
	{
		try {
			JSONArray melodies = jsonObject.getJSONArray("melodies");
			for (int i = 0; i < melodies.length(); i++) {
				if (melodies.getJSONObject(i).getString("name").equals(name))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public int numSongBlueprints()
	{
		try {
			return jsonObject.getJSONArray("melodies").length();
		} catch (Exception e) {
			CrashReport.logException(e);
			return 0;
		}
	}

	public SongBlueprint getSongBlueprint(int index)
	{
		try {
			JSONObject obj = jsonObject.getJSONArray("melodies").getJSONObject(index);
			return SongBlueprint.fromJSON(obj);
		} catch (JSONException e) {
			CrashReport.logException(e);
			return new SongBlueprint();
		}
	}

	public void setSongBlueprint(int index, SongBlueprint blueprint)
    {
        try {
            jsonObject.getJSONArray("melodies").put(index, SongBlueprint.toJSON(blueprint));
        } catch (JSONException e) {
            CrashReport.logException(e);
        }
    }

    public SongBlueprint removeSongBlueprint(int index)
    {
        try {
            JSONObject obj = (JSONObject) jsonObject.getJSONArray("melodies").remove(index);
            return null; //SongBlueprint.fromJSON(obj);
        } catch (JSONException e) {
            CrashReport.logException(e);
            return null; //new SongBlueprint();
        }
    }

	public void addSongBlueprint(SongBlueprint blueprint)
	{
		addSongBlueprint(blueprint, -1);
	}

	public void addSongBlueprint(SongBlueprint blueprint, int index)
	{
		try {
			if (index == -1)
				jsonObject.getJSONArray("melodies").put(SongBlueprint.toJSON(blueprint));
			else
				jsonObject.getJSONArray("melodies").put(index, SongBlueprint.toJSON(blueprint));
		}
		catch (Exception e) {
			CrashReport.logException(e);
		}
	}

    public static JSONArray sort(JSONArray array, Comparator c){
        List asList = new ArrayList(array.length());
        for (int i=0; i<array.length(); i++){
            asList.add(array.opt(i));
        }
        Collections.sort(asList, c);
        JSONArray res = new JSONArray();
        for (Object o : asList){
            res.put(o);
        }
        return res;
    }

    void loadAdditionalSongs1()
	{
		{
			SongBlueprint furElise = new SongBlueprint();
			furElise.setName("Für Elise");
			furElise.setInstrument("Acoustic Grand Piano");
			furElise.setDrumPattern("None");
			furElise.setScale("Chromatic");
			furElise.setRootNote("A");
			furElise.setBaseOctave(2);
			furElise.setOctaveRange(5);
			furElise.setTempo(220);
			furElise.setText(
			 "    FEFEFADB      A.   AB.  FEFEFADB      A.  BA    FEFEFADB      A.   AB.  FEFEFADB      A.  BA   ABDF.. GFD.. FDB.. DBA.  F   FEFEF..EFEFEFADB      A.   AB.  FEFEFADB      A.  BA    \n"
			+"                                                                                                                                     E                                                  \n"
			+"            Y.MPTYHLOTX AHMT        Y.MPTYHLOT  Y...        Y.MPTYHLOTX AHMT        Y.MPTYHLOT  Y.M   DKPW  KORU  AHMT  HOTT HTT                Y.MPTYHLOTX AHMT        Y.MPTYHLOT  Y...\n"
			+"            AH                      AH          AHM         AH                      AH          AH                                              AH                      AH          T...\n"
			+"                                                                                                                                                                                    P...\n"
			+"                                                                                                                                                                                    M...\n");

			SongBlueprint.LineOverride override_0 = furElise.createOverride();
			override_0.rootNote = Keys.findIndex("B");
			override_0.baseOctave = 4;
			furElise.getHorizontalOverride().put(0, override_0);
			furElise.getHorizontalOverride().put(1, override_0);

			addSongBlueprint(furElise);
		}

		{
			SongBlueprint carolOfTheBells = new SongBlueprint();
			carolOfTheBells.setName("Carol Of The Bells");
			carolOfTheBells.setInstrument("Acoustic Grand Piano");
			carolOfTheBells.setDrumPattern("None");
			carolOfTheBells.setScale("Chromatic");
			carolOfTheBells.setRootNote("C");
			carolOfTheBells.setBaseOctave(3);
			carolOfTheBells.setOctaveRange(5);
			carolOfTheBells.setTempo(360);
			carolOfTheBells.setText(
			 "                                                                        A. A  A. A  A. A  A. A  F.FFDBA.AA      A              A           A                                                          \n"
			+"                                                                                                A.                                                                                                    \n"
			+"W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.Y W.W.Y W.W.Y W.W.Y W.W.TTRPW....WY.YY YW.VWT.OQSTVWY Y.W.OQSTVWY Y.W.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.W.VWT.......\n"
			+"T...  R.....P.....O.....T...  R.....P.....O.....T.    T.    T.    T.    O.....Q.....R.....Q.P.T.T.    T.....T.....T...  G.....Q.S.T.G.....Q.S.T.O.....M.....R.....P.....O.....O.....                  \n"
			+"                        P.....O.....M.....H.....M.....O.....M.....O.....H.....H.....H.....H.H.O.O.....O.....P.....O.....C.....J...H.C.....J...H.H.....H.....H.....H.....H.............................\n"
			+"                                                D.....H.....D.....H.....    T.    T.    T.    H.K.....K.....K.....K.....      C...C.      C...C.F.....D.....C.....A.....                              \n"
			+"                                                                                                          Y                                                                                           \n");

			SongBlueprint.LineOverride override_0 = carolOfTheBells.createOverride();
			override_0.rootNote = Keys.findIndex("D");
			override_0.baseOctave = 5;
			carolOfTheBells.getHorizontalOverride().put(0, override_0);
			carolOfTheBells.getHorizontalOverride().put(1, override_0);

			addSongBlueprint(carolOfTheBells);
		}
	}

	public void load()
	{
		try {
			FileInputStream in = mainActivity.openFileInput(DATABASE_FILE_NAME);
			String str = Utilities.readTextFile(in);
			jsonObject = new JSONObject(str);

			if (!jsonObject.has("additional_songs_1"))
			{
				loadAdditionalSongs1();
				jsonObject.put("additional_songs_1", true);
			}

		} catch (Exception e) {
			try {
				jsonObject = new JSONObject(getInitialJson());
				
				addSongBlueprint(new SongBlueprint(
					"Happy Birthday",
					"L..LM...L...O...N...... L..LM...L...P...O...... L..LS...Q...O...N...M...R..RQ...O...P...O..........\n" +
					"    J.......... K.......... K.......... J.......... J.......... K.......... J...... K...J..........\n" +
					"    H.......... E.......... E.......... H.......... H.......... H.......... H...... E...H..........",

					"Acoustic Grand Piano",
					"None",
					
					"Major", "F",

					2, 4, 360
				));

				addSongBlueprint(new SongBlueprint(
					"Jingle Bells",
					"     KIG     KIG\n"
				  + "   BB...B..BB...\n"
				  + "\n"
				  + "     LKI    NNLI\n"
				  + "D..DD...F...F...\n"
				  + "\n"
				  + "K..  KIG     KIG\n"
				  + "G..BB...B..BB...\n"
				  + "\n"
				  + "     LKINNNNPNLI\n"
				  + "D..DD...F...F...\n"
				  + "\n"
				  + "    KKK.KKK.KNGI\n"
				  + "G.N.G.B.G.B.G.B.\n"
				  + "\n"
				  + "K...LLLLLKKKKIIK\n"
				  + "G.B.F.B.G.B.A...\n"
				  + "\n"
				  + "I.N.KKK.KKK.KNGI\n"
				  + "B...G.B.G.B.G.B.\n"
				  + "\n"
				  + "K...LLLLLKKKNNLI\n"
				  + "G.B.F.B.G.B.BBDF\n"
				  + "\n"
				  + "G...\n",

					"Acoustic Grand Piano",
					"None",
					
					"Chromatic", "F#",

					3, 3, 200
				));

				loadAdditionalSongs1();

				save();
			} catch (Exception e2) {
				CrashReport.logException(e);
			}
		}
	}

	public void save()
	{
		try {
		    FileOutputStream out = mainActivity.openFileOutput(DATABASE_FILE_NAME, Context.MODE_PRIVATE);
		    out.write(jsonObject.toString().getBytes());
		    out.close();
		} catch (Exception e) {
            CrashReport.logException(e);
		}
	}
}
