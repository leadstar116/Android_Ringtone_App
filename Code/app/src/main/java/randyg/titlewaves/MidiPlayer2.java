package randyg.titlewaves;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;

import com.leff.midi.MidiFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import randyg.titlewaves.music.Song;

public class MidiPlayer2
{
    public interface ProgressListener
    {
        void onMidiPlaybackStarted(Song song);
        void onMidiPlaybackFinished(Song song);
        void onMidiPlaybackProgress(Song song, int pos, int len);
    }

    private MainActivity context;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private ProgressListener progressListener;
    private Song currentSong;

    public MidiPlayer2(ProgressListener progressListener, MainActivity context)
    {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
        this.handler = new Handler();
        this.progressListener = progressListener;

        mediaPlayer.setVolume(95, 95);
        mediaPlayer.setOnCompletionListener(mp ->
        {
            if (progressListener != null)
                progressListener.onMidiPlaybackFinished(this.currentSong);
        });

        if (progressListener != null)
        {
            Runnable rUpdate = new Runnable() {
                @Override
                public void run()
                {
                    if (mediaPlayer.isPlaying() && currentSong != null)
                    {
                        int len = mediaPlayer.getDuration();
                        int pos = mediaPlayer.getCurrentPosition();

                        progressListener.onMidiPlaybackProgress(currentSong, pos, len);
                    }

                    handler.postDelayed(this, 50);
                }
            };
            handler.postDelayed(rUpdate, 50);
        }
    }

    private void setDataSource(Song song)
    {
        try
        {
            File tmpFile = File.createTempFile("titlewaves_", ".mid", context.getCacheDir());
            MidiFile midiFile = song.getMidiData().getMidiFile();
            midiFile.writeToFile(tmpFile);
            mediaPlayer.setDataSource(new FileInputStream(tmpFile).getFD());
        }
        catch (Exception e)
        {
            CrashReport.setSong("song", song);
            CrashReport.logException("Midi playback", e);
        }
    }

    //@TargetApi(23)
    public void play(Song song, double progress)
    {
        try
        {
            stop();

            currentSong = song;
            mediaPlayer.reset();
            setDataSource(song);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();

            if (progress > 0.0)
            {
                progress = Math.min(progress, 1.0);
                if (mediaPlayer.getDuration() > 0)
                    mediaPlayer.seekTo((int)(mediaPlayer.getDuration() * progress));
            }

            mediaPlayer.start();

            if (progressListener != null)
            {
                progressListener.onMidiPlaybackStarted(song);
            }
        }
        catch (Exception e)
        {
            CrashReport.setSong("song", song);
            CrashReport.logException("Midi playback", e);
        }
    }
    
    public void stop()
    {
        if (isPlaying())
        {
            mediaPlayer.stop();

            if (progressListener != null)
            {
                progressListener.onMidiPlaybackFinished(this.currentSong);
            }
        }
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }
}
