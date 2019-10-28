package randyg.titlewaves;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import randyg.titlewaves.music.*;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

public class CrashReport
{
	public static void setBool(String key, boolean value)
	{
		Crashlytics.setBool(key, value);
	}

	public static void setInt(String key, int value)
	{
		Crashlytics.setInt(key, value);
	}

	public static void setDouble(String key, double value)
	{
		Crashlytics.setDouble(key, value);
	}

	public static void setString(String key, String value)
	{
		Crashlytics.setString(key, value);
	}

	public static void setUserIdentifier(String value)
	{
		Crashlytics.setUserIdentifier(value);
	}

	public static void setSong(String key, Song song)
	{
	    try {
            String str = "null";
            if (song != null) {
                SongBlueprint blueprint = song.getBlueprint();
                String str_blueprint = "null";
                if (blueprint != null) {
                    try {
                        JSONObject json = SongBlueprint.toJSON(blueprint);
                        json.remove("name");
                        json.remove("notes");
                        str_blueprint = json.toString();
                    } catch (Exception e) {
                        str_blueprint = "invalid";
                    }
                }
                str += "blueprint: " + str_blueprint + "\n";

                SongRawData rawData = song.getRawData();
                String str_rawData = "null";
                if (rawData != null) {
                    try {
                        String s = "";
                        s += "totalLength:     " + rawData.totalLength + "\n";
                        s += "totalNoteLength: " + rawData.totalNoteLength + "\n";
                        s += "notes.size:      " + rawData.notes.size() + "\n";
                        s += "segments.size    " + rawData.segments.size() + "\n";
                        str_rawData = s;
                    } catch (Exception e) {
                        str_rawData = "invalid";
                    }
                }
                str += "raw data:  " + str_rawData + "\n";

                SongMidiData midiData = song.getMidiData();
                String str_midiData = "null";
                if (midiData != null) {
                    try {
                        str_midiData = midiData.toString();
                    } catch (Exception e) {
                        str_midiData = "invalid";
                    }
                }
                str += "midi data: " + str_midiData + "\n";
            }
            setString(key, str);
        } catch (Exception e) {

        }
	}

	public static void log(String msg)
	{
		Crashlytics.log(msg);
	}

	public static void logException(Exception e)
	{
		logException("N/A", e);
	}

	public static void logException(String context, Exception e)
	{
		setString("context", context);
		String reason = e.toString();

		AlertDialog.Builder builder = new AlertDialog.Builder(Utilities.mainActivity);
		builder.setTitle("Oops, something went wrong.");
		final View view = Utilities.mainActivity.getLayoutInflater().inflate(R.layout.crash_report_view, null);
		builder.setView(view);

		DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				EditText wordEdit = view.findViewById(R.id.crash_report_message);
				String message = wordEdit.getText().toString();

			    if (which == DialogInterface.BUTTON_NEGATIVE) {
					dialog.cancel();
					if (BuildConfig.DEBUG) {
						Utilities.makeToast(Utilities.exceptionStackTrace(e));
					}
				} else {
					setString("userMessage", message);
					Crashlytics.logException(e);
					Utilities.makeToast("Thank you!");
				}
			}
		};
		builder.setPositiveButton("Report", callback);
		builder.setNegativeButton(BuildConfig.DEBUG ? "Traceback" : "Cancel", callback);
		builder.show();
	}

	public static void crash()
	{
		Crashlytics.getInstance().crash();
	}
}
