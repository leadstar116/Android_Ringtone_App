package randyg.titlewaves;

import android.content.Context;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;

import randyg.titlewaves.music.SongBlueprint;

public class UtilitiesMisc
{
    public static void createGenericLoadSaveDialog(MainActivity mainActivity, boolean isSave)
    {
        Database database = mainActivity.database;
        ArrayList<String> items = new ArrayList<>();

        if (isSave)
            items.add("Save As..");

        for (int i=0; i<database.numSongBlueprints(); i++)
        {
            SongBlueprint blueprint = database.getSongBlueprint(i);
            items.add(blueprint.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, items);

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(isSave?"Save Melody":"Load Melody");
        builder.setAdapter(adapter, (dialog, item) -> {
            if (isSave && item == 0)
            {
                Utilities.createTextInputDialog("Save Melody As", (text) -> {
                    SongBlueprint blueprint = mainActivity.saveMelodyData(text, true);
                    database.addSongBlueprint(blueprint);
                    database.save();

                    Utilities.hideKeyboard();
                    mainActivity.drawerLayout.closeDrawer(GravityCompat.START);
                });
            }
            else if (isSave)
            {
                SongBlueprint blueprint = mainActivity.saveMelodyData(items.get(item), false);

                Utilities.createConfirmationDialog("Overwrite Melody?", (isYes) ->
                {
                    if (isYes)
                    {
                        // overwrite
                        database.setSongBlueprint(item - 1, blueprint);
                        database.save();
                        mainActivity.drawerLayout.closeDrawer(GravityCompat.START);
                        dialog.cancel();
                    }
                });
            }
            else // load
            {
                SongBlueprint blueprint = database.getSongBlueprint(item);
                try {
                    mainActivity.loadMelodyFromData(blueprint);
                } catch (Exception e) {
                    Utilities.makeToast(e.toString());
                }
                mainActivity.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setLongClickable(true);
        alertDialog.getListView().setOnItemLongClickListener((parent, view, position, id) -> {
            if (isSave && position == 0)
                return true;

            Utilities.createConfirmationDialog("Delete Melody?", (isYes) ->
            {
                if (isYes)
                {
                    items.remove(position);
                    adapter.notifyDataSetChanged();
                    database.removeSongBlueprint(position - 1);
                    database.save();
                }
            });

            return true;
        });
        alertDialog.show();
    }

	public static void deleteCache(Context context) {
	    try {
	        File dir = context.getExternalCacheDir();
	        deleteDir(dir);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static boolean deleteDir(File dir)
	{
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	        return dir.delete();
	    } else if(dir!= null && dir.isFile()) {
	        return dir.delete();
	    } else {
	        return false;
	    }
	}
}


