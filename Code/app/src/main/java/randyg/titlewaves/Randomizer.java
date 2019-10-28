package randyg.titlewaves;

import java.security.SecureRandom;

public class Randomizer
{
	static SecureRandom rnd = new SecureRandom();

	private static String randomLine(int len, int padTo, String[] words, int wordFrequency)
	{
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ        ";
	    StringBuilder sb = new StringBuilder(len);

	    char lastChar = ' ';

	    int i = 0;
	    while (i < len)
	    {
	        char ch = lastChar;
	        do {
	            ch = chars.charAt(rnd.nextInt(chars.length()));
	        } while (ch == lastChar);

	        sb.append(ch);
	        lastChar = ch;

	        if (words.length != 0 && rnd.nextInt(100) < wordFrequency)
	        {
	        	String word = words[rnd.nextInt(words.length)];
	        	int j = 0;
	        	int wordLen = word.length();
	        	if (i+wordLen <= len)
	        	{
		        	while (i < len && j < wordLen)
		        	{
		        		sb.append(Character.toUpperCase(word.charAt(j)));
		        		i++;
		        		j++;
		        	}
	        	}
	        }

	        if (ch != ' ')
	        {
	            double x = (double)rnd.nextInt(100);
	            int noteLen = (int)(x*x*0.00032);
	            while (i < len && noteLen > 0)
	            {
	                sb.append('.');
	                noteLen--;
	                i++;
	            }
	        }

	        i++;
	    }

	    while (i < padTo) {
	        sb.append(' ');
	        i++;
	    }

	    return sb.toString();
	}

	public static String generateRandomSection(int numLines, int lineLen, int lineLenRand, String[] words, int wordFrequency)
	{
	    String result = "";

	    for (int i=0; i<numLines; i++) {
	        int extra = rnd.nextInt(lineLenRand);
			result += randomLine(lineLen + extra, lineLen + lineLenRand, words, wordFrequency);
			result += "\n";
	    }

	    return result;
	}

	public static String generateRandom(String[] words, int wordFrequency)
	{
	    String result = "";

	    int numMeasures = 3 + rnd.nextInt(3);
	    for (int i=0; i<numMeasures; i++) {
	        int numLines = 4 + rnd.nextInt(2);
			result += generateRandomSection(numLines, 26, 2, words, wordFrequency);
			result += "\n";
	    }

	    return result;
	}
}
