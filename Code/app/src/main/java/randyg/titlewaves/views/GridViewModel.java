package randyg.titlewaves.views;

// TW!
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import randyg.titlewaves.music.SongBlueprint;

public class GridViewModel extends ViewModel
{
	public int currentCellX = 0;
	public int currentCellY = 0;
	public ArrayList<StringBuffer> lines = new ArrayList<>();
	public SongBlueprint songBlueprint = new SongBlueprint();

	public void clear()
	{
		lines.clear();
		currentCellX = 0;
		currentCellY = 0;
	}

	public void setText(String text)
	{
		clear();

		for (String line : text.split("\n"))
		{
			lines.add(new StringBuffer(line));
		}
	}

	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (StringBuffer line : lines)
		{
			if (line != null)
				stringBuilder.append(line + "\n");
			else
				stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	public StringBuffer getLine(int y)
	{
		if (y >= 0 && y < lines.size())
			return lines.get(y);

		return null;
	}

	public int charAt(int x, int y)
	{
		if ((x | y) < 0)
			return 0;

		if (y >= lines.size())
			return 0;

		StringBuffer line = lines.get(y);

		if (x >= line.length())
			return 0;

		return line.charAt(x);
	}

	public boolean insertAt(int x, int y, char ch)
	{
		// FIXME: quick hack
		if (lines.isEmpty() || lines.size() == y)
		{
			lines.add(new StringBuffer("" + ch));
			return true;
		}

		StringBuffer line = getLine(y);
		try {
			line.insert(x, ch);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void setCursor(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;

		StringBuffer line = getLine(y);
		if (line == null) {
			if (!lines.isEmpty()) {
				x = lines.get(lines.size() - 1).length();
			} else {
				x = 0;
			}

			y = Math.max(0, lines.size() - 1);
		}
		else {
			x = Math.min(x, line.length());
		}

		currentCellX = x;
		currentCellY = y;
	}

	public void handleCharInput(int uniChar)
	{
		if (uniChar == 0 || currentCellX < 0 || currentCellY < 0)
			return;

		if (uniChar == '\n')
		{
			if (currentCellY == lines.size())
			{
				lines.add(new StringBuffer());
			}
			else
			{
				int index_0 = currentCellY;
				int index_1 = currentCellY + 1;

				StringBuffer line = lines.get(index_0);
				int start_i = Math.min(currentCellX, line.length());
				String firstHalf = line.substring(0, start_i);
				String secondHalf = line.substring(start_i, line.length());

				lines.set(index_0, new StringBuffer(firstHalf));
				lines.add(index_1, new StringBuffer(secondHalf));
			}

			currentCellX = 0;
			currentCellY++;
		}
		else if (insertAt(currentCellX, currentCellY, (char)uniChar))
		{
			currentCellX++;
		}
	}

	public void handleBackspace()
	{
		StringBuffer line = getLine(currentCellY);

		if (line == null || currentCellX < 0 || currentCellY < 0)
			return;

		if (currentCellX == 0)
		{
			int prevLineIndex = currentCellY - 1;

			StringBuffer prevLine = getLine(prevLineIndex);
			if (prevLine != null)
			{
				currentCellX = prevLine.length();
				prevLine.append(line);
				lines.remove(currentCellY);
				currentCellY--;
			}
		}
		else
		{
			try
			{
				currentCellX--;
				line.delete(currentCellX, currentCellX + 1);
			} catch (Exception e) {
				// ...
			}
		}
	}
}

