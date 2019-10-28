package randyg.titlewaves;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ListIterator;

import org.billthefarmer.mididriver.MidiDriver;
import android.os.Handler;
import android.util.Log;

public class LiveMidiPlayer implements MidiDriver.OnMidiStartListener
{
    static final int CMD_SET_INSTRUMENT = 0;
    static final int CMD_NOTE_ON = 1;
    static final int CMD_NOTE_OFF = 2;

    public class MidiCommand
    {
        int time=0;
        int type=0;
        byte arg0=0;
        byte arg1=0;
        byte arg2=0;
        byte arg3=0;
    }

    boolean started = false;
    int mInstrument = 10;
    MidiDriver midiDriver;
    ArrayDeque<MidiCommand> queue;
    ArrayList<MidiCommand> noteOffCommands;
    Handler handler;

    LiveMidiPlayer()
    {
        queue = new ArrayDeque<MidiCommand>();
        noteOffCommands = new ArrayList<MidiCommand>();

        midiDriver = new MidiDriver();
        midiDriver.setOnMidiStartListener(this);
        midiDriver.setVolume(96);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                LiveMidiPlayer.this.threadloop();
                handler.postDelayed(this, 50);
            }
        }, 50);
    }

    public void start()
    {
        midiDriver.start();
    }

    public void stop()
    {
        midiDriver.stop();
        started = false;
    }

    protected void threadloop()
    {
        if (!started)
            return;

        synchronized (queue)
        {
            while (!queue.isEmpty())
            {
                MidiCommand cmd = queue.poll();
                switch (cmd.type) {
                    case CMD_SET_INSTRUMENT:
                        //if (mInstrument != cmd.arg0) {
                        //    sendMidiInstrument(cmd.arg0);
                        //    mInstrument = cmd.arg0;
                        //}
                        break;
                    case CMD_NOTE_ON:
                        //sendMidiNote(0x90 + cmd.arg0, cmd.arg1, cmd.arg2);
                        noteOffCommands.add(cmd);
                        break;
                }
            }

            ListIterator<MidiCommand> iter = noteOffCommands.listIterator();
            while (iter.hasNext()) {
                MidiCommand cmd = iter.next();
                if (cmd.time <= 0) {
                    sendMidiNote(0x80 + cmd.arg0, cmd.arg1, 0);
                    iter.remove();
                } else {
                    cmd.time--;
                }
            }
        }
    }

    protected void sendMidiInstrument(int instrument)
    {
        midiDriver.write(new byte[] { (byte)0xc0, (byte)instrument });
    }

    // Send a midi message, 3 bytes
    protected void sendMidiNote(int m, int n, int v)
    {
        byte[] msg = new byte[3];
        msg[0] = (byte)m;
        msg[1] = (byte)n;
        msg[2] = (byte)v;
        midiDriver.write(msg);
    }
    
    @Override
    public void onMidiStart()
    {
        Log.e("MidiDriver", "onMidiStart");
        started = true;
        sendMidiInstrument(mInstrument);
    }

    public void setInstrument(int instrument)
    {
        if (mInstrument != instrument)
        {
            synchronized (queue)
            {
                sendMidiInstrument(instrument);
                mInstrument = instrument;
                //MidiCommand cmd = new MidiCommand();
                //cmd.type = CMD_SET_INSTRUMENT;
                //cmd.arg0 = (byte)instrument;
                //queue.add(cmd);
            }
        }
    }
    
    public void noteOn(int channel, int[] notes, int velocity)
    {
        synchronized (queue) {
            for (int i=0; i<notes.length; i++) {
                sendMidiNote(0x90 + channel, notes[i], velocity);
            }
        }
    }

    public void noteOff(int channel, int[] notes)
    {
        synchronized (queue) {
            for (int i=0; i<notes.length; i++) {
                sendMidiNote(0x80 + channel, notes[i], 0);
            }
        }
    }

    public void playNote(int channel, int note, int velocity, int time)
    {
        if (!started) {
            Log.e("MidiDriver", "MidiDriver is not initialized");
            return;
        }
        
        synchronized (queue)
        {
            sendMidiNote(0x90 + channel, note, velocity);

            for (MidiCommand cmd1 : noteOffCommands)
            {
                if (cmd1.arg0 == channel && cmd1.arg1 == note)
                {
                    cmd1.time = time / 50;
                    return;
                }
            }

            MidiCommand cmd = new MidiCommand();
            cmd.type = CMD_NOTE_ON;
            cmd.arg0 = (byte)channel;
            cmd.arg1 = (byte)note;
            cmd.arg2 = (byte)velocity;
            cmd.time = time / 50;
            queue.add(cmd);
        }
    }

    public void playNote(int channel, int[] notes, int velocity, int time)
    {
        if (!started)
            return;
        
        for (int i=0; i<notes.length; i++)
            playNote(channel, notes[i], velocity, time);
    }
}

