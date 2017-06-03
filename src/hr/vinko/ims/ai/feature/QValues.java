package hr.vinko.ims.ai.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.jme3.math.Vector3f;

public class QValues {
	private final static Random rand = new Random();
	private Map<Vector3f, Map<Move, Double>> qValues;

	public QValues() {
		qValues = new HashMap<>();
	}

	public Map<Move, Double> getValuesAt(Vector3f pos) {
		return qValues.get(pos);
	}

	public Double getValueAt(Vector3f pos, Move move) {
		double val = qValues.getOrDefault(pos, new HashMap<>()).getOrDefault(move, 0.);
		return val;
		
	}

	public Move bestMove(Vector3f pos) {
		return getValuesAt(pos).entrySet().stream()
				.max((entry1, entry2) -> Double.compare(entry1.getValue(), entry2.getValue())).get().getKey();
	}

	public void setValueAt(Vector3f pos, Move move, double value) {
		Map<Move, Double> values = qValues.getOrDefault(pos, new HashMap<>());
		values.put(move, value);
		qValues.put(pos, values);
	}

	public void reduceValues() {
		for (Vector3f pos : qValues.keySet()) {
			Map<Move, Double> values = qValues.getOrDefault(pos, new HashMap<>());
			for (Move move : values.keySet()) {
				values.put(move, 0.5 * values.get(move));
			}
			qValues.put(pos, values);
		}
	}

}
