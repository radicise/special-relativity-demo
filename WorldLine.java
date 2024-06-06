import java.util.ArrayList;
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
	public Matrix resolvePosition(Matrix transformation, double time) {
		int i = 0;
		while (true) {
			Matrix event;
			try {
				event = transformation.transform(events.get(i));
			}
			catch (IndexOutOfBoundsException E) {
				return null;
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
					return null;
				}
				double transformedPrevTime = prevEvent.getEntry(0, 0);
				Matrix pos = Matrix.weightedAvg(prevEvent, event, (time - transformedPrevTime) / (transformedTime - transformedPrevTime));
				return pos;
			}
			i++;
		}
	}
	
}
