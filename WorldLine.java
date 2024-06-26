import java.util.ArrayList;
import java.io.PrintStream;
public class WorldLine {
	/*
	 *
	 * Assumptions:
	 *         The particle does not travel faster than the speed of light
	 *         The particle does not travel backward in time
	 *         The particle's defining events are added in chronological order
	 *
	 */
	ArrayList<Matrix> events;
	public WorldLine() {
		events = new ArrayList<Matrix>();
	}
	public void addEvent(Matrix event) {
		events.add(event);
		return;
	}
	public void print(PrintStream ps) {
		int m = events.size();
		for (int i = 0; i < m; i++) {
			System.out.println(i + ":");
			System.out.println(events.get(i));
		}
		System.out.println();
	}
	public Matrix resolvePosition(Matrix transformation, double time) {
		int i = 0;
		while (true) {
			Matrix event;
			try {
				event = transformation.transform(events.get(i));
			}
			catch (IndexOutOfBoundsException E) {
				Matrix prevEvent = transformation.transform(events.get(i - 2));
				event = transformation.transform(events.get(i - 1));
				double transformedPrevTime = prevEvent.getEntry(0, 0);
				double transformedTime = event.getEntry(0, 0);
				Matrix pos = Matrix.weightedAvg(prevEvent, event, (time - transformedPrevTime) / (transformedTime - transformedPrevTime));
				return pos;
			}
			double transformedTime = event.getEntry(0, 0);
			if (transformedTime == time) {
				return event;
			}
			if (transformedTime > time) {
				Matrix prevEvent;
				try {
					prevEvent = transformation.transform(events.get(i - 1));
				}
				catch (IndexOutOfBoundsException E) {
					prevEvent = event;
					event = transformation.transform(events.get(i + 1));
					transformedTime = event.getEntry(0, 0);
				}
				double transformedPrevTime = prevEvent.getEntry(0, 0);
				Matrix pos = Matrix.weightedAvg(prevEvent, event, (time - transformedPrevTime) / (transformedTime - transformedPrevTime));
				return pos;
			}
			i++;
		}
	}
	
}
