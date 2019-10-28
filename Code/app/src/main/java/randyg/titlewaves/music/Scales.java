package randyg.titlewaves.music;

import java.util.ArrayList;

public class Scales
{
    public static class ScaleData
    {
        String name;
        int[ ] intervals;

        public ScaleData(String name, int[] intervals)
        {
            this.name = name;
            this.intervals = intervals;
        }
    }

    public static int DEFAULT_INDEX = 0;
    public static ArrayList<ScaleData> scales = new ArrayList<ScaleData>();

    static
    {
        scales.add(new ScaleData("Aeolian",                     new int[]{ 0, 2, 3, 5, 7, 8, 10 }));
        scales.add(new ScaleData("Chromatic",                   new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }));
        scales.add(new ScaleData("Major",                       new int[]{ 0, 2, 4, 5, 7, 9, 11 }));
        scales.add(new ScaleData("Major Blues",                 new int[]{ 0, 2, 3, 4, 7, 9 }));
        scales.add(new ScaleData("Major Flat Two Pentatonic",   new int[]{ 0, 1, 4, 7, 9 }));
        scales.add(new ScaleData("Major Pentatonic",            new int[]{ 0, 2, 4, 7, 9 }));
        scales.add(new ScaleData("Melodic Minor",               new int[]{ 0, 2, 3, 5, 7, 9, 11 }));
        scales.add(new ScaleData("Melodic Minor Fifth Mode",    new int[]{ 0, 2, 4, 5, 7, 8, 10 }));
        scales.add(new ScaleData("Melodic Minor Second Mode",   new int[]{ 0, 1, 3, 5, 7, 9, 10 }));
        scales.add(new ScaleData("Minor Pentatonic",            new int[]{ 0, 3, 5, 7, 10 }));
        scales.add(new ScaleData("Minor Hexatonic",             new int[]{ 0, 2, 3, 5, 7, 11 }));
        scales.add(new ScaleData("Minor Bebop",                 new int[]{ 0, 2, 3, 5, 7, 8, 10, 11 }));
        scales.add(new ScaleData("Minor Blues",                 new int[]{ 0, 3, 5, 6, 7, 10 }));
        scales.add(new ScaleData("Minor Six Diminished",        new int[]{ 0, 2, 3, 5, 7, 8, 9, 11 }));
        scales.add(new ScaleData("Minor Six Pentatonic",        new int[]{ 0, 3, 5, 7, 9 }));
        scales.add(new ScaleData("Minor #7 Pentatonic",         new int[]{ 0, 3, 5, 7, 11 }));
        
        scales.add(new ScaleData("Altered",                     new int[]{ 0, 1, 3, 4, 6, 8, 10 }));
        scales.add(new ScaleData("Augmented Heptatonic",        new int[]{ 0, 3, 4, 5, 7, 8, 11 }));
        scales.add(new ScaleData("Augmented",                   new int[]{ 0, 3, 4, 7, 8, 11 }));
        scales.add(new ScaleData("Balinese",                    new int[]{ 0, 1, 3, 5, 7, 8, 11 }));
        scales.add(new ScaleData("Bebop Dominant",              new int[]{ 0, 2, 4, 5, 7, 9, 10, 11 }));
        scales.add(new ScaleData("Bebop Locrian",               new int[]{ 0, 1, 3, 5, 6, 7, 8, 10 }));
        scales.add(new ScaleData("Bebop Major",                 new int[]{ 0, 2, 4, 5, 7, 8, 9, 11 }));
        scales.add(new ScaleData("Bebop Minor",                 new int[]{ 0, 2, 3, 4, 5, 7, 9, 10 }));
        scales.add(new ScaleData("Bebop",                       new int[]{ 0, 2, 4, 5, 7, 9, 10, 11 }));
        scales.add(new ScaleData("Composite Blues",             new int[]{ 0, 2, 3, 4, 5, 6, 7, 9, 10 }));
        scales.add(new ScaleData("Diminished",                  new int[]{ 0, 2, 3, 5, 6, 8, 9, 11 }));
        scales.add(new ScaleData("Dorian #4",                   new int[]{ 0, 2, 3, 6, 7, 9, 10 }));
        scales.add(new ScaleData("Dorian",                      new int[]{ 0, 2, 3, 5, 7, 9, 10 }));
        scales.add(new ScaleData("Double Harmonic Lydian",      new int[]{ 0, 1, 4, 6, 7, 8, 11 }));
        scales.add(new ScaleData("Double Harmonic Major",       new int[]{ 0, 1, 4, 5, 7, 8, 11 }));
        scales.add(new ScaleData("Egyptian",                    new int[]{ 0, 2, 5, 7, 10 }));
        scales.add(new ScaleData("Enigmatic",                   new int[]{ 0, 1, 4, 6, 8, 10, 11 }));
        scales.add(new ScaleData("Flamenco",                    new int[]{ 0, 1, 3, 4, 6, 7, 10 }));
        scales.add(new ScaleData("Flat Six Pentatonic",         new int[]{ 0, 2, 4, 7, 8 }));
        scales.add(new ScaleData("Flat Three Pentatonic",       new int[]{ 0, 2, 3, 7, 9 }));
        scales.add(new ScaleData("Harmonic Major",              new int[]{ 0, 2, 4, 5, 7, 8, 11 }));
        scales.add(new ScaleData("Harmonic Minor",              new int[]{ 0, 2, 3, 5, 7, 8, 11 }));
        scales.add(new ScaleData("Hirajoshi",                   new int[]{ 0, 2, 3, 7, 8 }));
        scales.add(new ScaleData("Hungarian Major",             new int[]{ 0, 3, 4, 6, 7, 9, 10 }));
        scales.add(new ScaleData("Hungarian Minor",             new int[]{ 0, 2, 3, 6, 7, 8, 11 }));
        scales.add(new ScaleData("Ichikosucho",                 new int[]{ 0, 2, 4, 5, 6, 7, 9, 11 }));
        scales.add(new ScaleData("In-sen",                      new int[]{ 0, 1, 5, 7, 10 }));
        scales.add(new ScaleData("Ionian Augmented",            new int[]{ 0, 2, 4, 5, 8, 9, 11 }));
        scales.add(new ScaleData("Ionian Pentatonic",           new int[]{ 0, 4, 5, 7, 11 }));
        scales.add(new ScaleData("Iwato",                       new int[]{ 0, 1, 5, 6, 10 }));
        scales.add(new ScaleData("Kafi Raga",                   new int[]{ 0, 3, 4, 5, 7, 9, 10, 11 }));
        scales.add(new ScaleData("Kumoijoshi",                  new int[]{ 0, 1, 5, 7, 8 }));
        scales.add(new ScaleData("Leading Whole Tone",          new int[]{ 0, 2, 4, 6, 8, 10, 11 }));
        scales.add(new ScaleData("Locrian #2",                  new int[]{ 0, 2, 3, 5, 6, 8, 10 }));
        scales.add(new ScaleData("Locrian Major",               new int[]{ 0, 2, 4, 5, 6, 8, 10 }));
        scales.add(new ScaleData("Locrian Pentatonic",          new int[]{ 0, 3, 5, 6, 10 }));
        scales.add(new ScaleData("Locrian",                     new int[]{ 0, 1, 3, 5, 6, 8, 10 }));
        scales.add(new ScaleData("Lydian #5 Pentatonic",        new int[]{ 0, 4, 6, 8, 11 }));
        scales.add(new ScaleData("Lydian #9",                   new int[]{ 0, 1, 4, 6, 7, 9, 11 }));
        scales.add(new ScaleData("Lydian Augmented",            new int[]{ 0, 2, 4, 6, 8, 9, 11 }));
        scales.add(new ScaleData("Lydian Diminished",           new int[]{ 0, 2, 3, 6, 7, 9, 11 }));
        scales.add(new ScaleData("Lydian Dominant Pentatonic",  new int[]{ 0, 4, 6, 7, 10 }));
        scales.add(new ScaleData("Lydian Dominant",             new int[]{ 0, 2, 4, 6, 7, 9, 10 }));
        scales.add(new ScaleData("Lydian Minor",                new int[]{ 0, 2, 4, 6, 7, 8, 10 }));
        scales.add(new ScaleData("Lydian Pentatonic",           new int[]{ 0, 4, 6, 7, 11 }));
        scales.add(new ScaleData("Lydian",                      new int[]{ 0, 2, 4, 6, 7, 9, 11 }));
        scales.add(new ScaleData("Malkos Raga",                 new int[]{ 0, 3, 5, 8, 10 }));
        scales.add(new ScaleData("Mixolydian pentatonic",       new int[]{ 0, 4, 5, 7, 10 }));
        scales.add(new ScaleData("Mixolydian",                  new int[]{ 0, 2, 4, 5, 7, 9, 10 }));
        scales.add(new ScaleData("Neopolitan Major Pentatonic", new int[]{ 0, 4, 5, 6, 10 }));
        scales.add(new ScaleData("Neopolitan Major",            new int[]{ 0, 1, 3, 5, 7, 9, 11 }));
        scales.add(new ScaleData("Neopolitan Minor",            new int[]{ 0, 1, 3, 5, 7, 8, 10 }));
        scales.add(new ScaleData("Neopolitan",                  new int[]{ 0, 1, 3, 5, 7, 8, 11 }));
        scales.add(new ScaleData("Oriental",                    new int[]{ 0, 1, 4, 5, 6, 9, 10 }));
        scales.add(new ScaleData("Pelog",                       new int[]{ 0, 1, 3, 7, 8 }));
        scales.add(new ScaleData("Persian",                     new int[]{ 0, 1, 4, 5, 6, 8, 11 }));
        scales.add(new ScaleData("Phrygian",                    new int[]{ 0, 1, 3, 5, 7, 8, 10 }));
        scales.add(new ScaleData("Piongio",                     new int[]{ 0, 2, 5, 7, 9, 10 }));
        scales.add(new ScaleData("Prometheus Neopolitan",       new int[]{ 0, 1, 4, 6, 9, 10 }));
        scales.add(new ScaleData("Prometheus",                  new int[]{ 0, 2, 4, 6, 9, 10 }));
        scales.add(new ScaleData("Purvi Raga",                  new int[]{ 0, 1, 4, 5, 6, 7, 8, 11 }));
        scales.add(new ScaleData("Ritusen",                     new int[]{ 0, 2, 5, 7, 9 }));
        scales.add(new ScaleData("Romanian Minor",              new int[]{ 0, 2, 3, 6, 7, 9, 10 }));
        scales.add(new ScaleData("Scriabin",                    new int[]{ 0, 1, 4, 7, 9 }));
        scales.add(new ScaleData("Six Tone Symmetric",          new int[]{ 0, 1, 4, 5, 8, 9 }));
        scales.add(new ScaleData("Spanish Heptatonic",          new int[]{ 0, 1, 3, 4, 5, 7, 8, 10 }));
        scales.add(new ScaleData("Spanish",                     new int[]{ 0, 1, 4, 5, 7, 8, 10 }));
        scales.add(new ScaleData("Super Locrian Pentatonic",    new int[]{ 0, 3, 4, 6, 10 }));
        scales.add(new ScaleData("Todi Raga",                   new int[]{ 0, 1, 3, 6, 7, 8, 11 }));
        scales.add(new ScaleData("Vietnamese 1",                new int[]{ 0, 3, 5, 7, 8 }));
        scales.add(new ScaleData("Vietnamese 2",                new int[]{ 0, 3, 5, 7, 10 }));
        scales.add(new ScaleData("Whole Tone Pentatonic",       new int[]{ 0, 4, 6, 8, 10 }));
        scales.add(new ScaleData("Whole Tone",                  new int[]{ 0, 2, 4, 6, 8, 10 }));

        DEFAULT_INDEX = findIndex("Major Pentatonic");
    }

    public static String[] getNames() {
        String[] names = new String[scales.size()];
        for (int i=0; i<names.length; i++)
            names[i] = scales.get(i).name;
        return names;
    }

    public static int findIndex(String name)
    {
        for (int i=0; i<scales.size(); i++)
            if (scales.get(i).name.equals(name))
                return i;
        return -1;
    }

    public static String getName(int index)
    {
        if (index >= 0 && index < scales.size())
            return scales.get(index).name;
        return "Unknown";
    }

    public static int[] getIntervals(int index)
    {
        if (index >= 0 && index < scales.size())
            return scales.get(index).intervals;
        return new int[0];
    }
}

