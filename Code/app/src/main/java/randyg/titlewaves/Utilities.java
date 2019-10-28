
package randyg.titlewaves;

import android.app.Activity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;


public class Utilities
{
	public static MainActivity mainActivity = null;
	private static Toast universalToast = null;

	public static void initialize(MainActivity context)
	{
		Utilities.mainActivity = context;
	}

	public static void hideKeyboard() {
		View view = mainActivity.findViewById(android.R.id.content);
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void makeToast(String text, int length)
	{
		if (mainActivity == null)
			return;

	    if (universalToast != null)
	        universalToast.cancel();

	    universalToast = Toast.makeText(mainActivity, text, length);
	    universalToast.show();
	}

	public static void makeToast(String text)
	{
		makeToast(text, Toast.LENGTH_LONG);
	}

	public static String exceptionStackTrace(Exception e) {
		java.io.StringWriter sw = new java.io.StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static String readTextFile(InputStream inputStream) {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	    byte[] buf = new byte[1024];
	    int len;
	    try {
	        while ((len = inputStream.read(buf)) != -1) {
	            outputStream.write(buf, 0, len);
	        }
	        outputStream.close();
	        inputStream.close();
	    } catch (IOException e) {
	    	// ignore
	    }
	    return outputStream.toString();
	}

	interface ConfirmationDialogCallback {
	    void f(boolean choice);
	}

	public static void createConfirmationDialog(String title, ConfirmationDialogCallback cb)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
	    builder.setTitle(title);

	    builder.setPositiveButton("OK", (dialog, which) -> {
			cb.f(true);
	    });

	    builder.setNegativeButton("Cancel", (dialog, which) -> {
			cb.f(false);
	        dialog.cancel();
	    });

	    builder.show();
	}

	interface TextInputDialogFunc {
	    void f(String text);
	}

	public static AlertDialog createTextInputDialog(String title, TextInputDialogFunc func)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
	    builder.setTitle(title);

	    final EditText input = new EditText(mainActivity);
	    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_FILTER);
	    input.setPadding(25, 20, 25, 20);
	    input.setFocusable(true);
	    input.setFocusableInTouchMode(true);

	    builder.setView(input);

	    builder.setPositiveButton("OK", (dialog, which) -> {
	        String text = input.getText().toString();
	        func.f(text);
	        Utilities.hideKeyboard();
	    });

	    builder.setNegativeButton("Cancel", (dialog, which) -> {
	        Utilities.hideKeyboard();
	        dialog.cancel();
	    });

	    AlertDialog alertDialog = builder.create();
	    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    alertDialog.show();
	    return alertDialog;
	}

	interface SpinnerListener {
	    void onItemSelected(int position, String item);
	}

	public static void setupSpinner(Spinner spinner, String[] items, int selectedIdx, SpinnerListener listener)
	{
	    ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, R.layout.spinner_item, items);
	    spinner.setAdapter(adapter);
	    spinner.setSelection(selectedIdx);
	    spinner.setOnItemSelectedListener(
	        new AdapterView.OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	                String item = items[position];
	                listener.onItemSelected(position, item);
	            }

	            @Override
	            public void onNothingSelected(AdapterView<?> parent) {
	            }
	        }
	    );
	}

	interface SeekBarListener {
	    void onSeek(int progress);
	}

	public static void setupSeekBar(SeekBar seekbar, int curProgress, SeekBarListener listener)
	{
	    seekbar.setProgress(curProgress);
	    seekbar.setOnSeekBarChangeListener(
	        new SeekBar.OnSeekBarChangeListener() {
	            @Override
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	                if (fromUser)
	                    listener.onSeek(progress);
	            }

	            @Override
	            public void onStartTrackingTouch(SeekBar seekBar) {

	            }

	            @Override
	            public void onStopTrackingTouch(SeekBar seekBar) {

	            }
	        }
	    );
	}
}

