package hr.vinko.ims.ai.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class ApproximateQPacmanAI extends AgentAI {

	private final static Random rand = new Random();
	private QValues qValues = new QValues();

	private final static double ALPHA = 0.1;
	private final static double GAMMA = 0.9;
	private final static double EPS = 0.95;

	private IFeature[] features = new IFeature[] { new FeatureBias(), new FeatureGhostsNearby(3),
			new FeatureNearestFood(), new FeatureEatsFood() };
	private double[] featureWeights = new double[features.length];

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings,
			WorldEntity.WorldEntityInfo myInfo) {

		Vector3f pos = myInfo.getPosition();

		double[] V = new double[moves.size()];

		double bestV = -Double.MAX_VALUE;
		for (int i = 0; i < moves.size(); i++) {

			int[] move = moves.get(i);

			for (int j = 0; j < featureWeights.length; j++) {
				V[i] += featureWeights[j] * features[j].calculateValue(mySurroundings, move);
			}
			bestV = Math.max(bestV, V[i]);

		}

		for (int i = 0; i < moves.size(); i++) {
			int[] move = moves.get(i);

			double oldQVal = 0;

			for (int j = 0; j < featureWeights.length; j++) {
				oldQVal += featureWeights[j] * features[j].calculateValue(mySurroundings, move);
			}

			for (int j = 0; j < featureWeights.length; j++) {
				double correction = R(myInfo, mySurroundings, move) + GAMMA * bestV - oldQVal;
				featureWeights[j] += ALPHA * correction * features[j].calculateValue(mySurroundings, move);
			}
		}

		double value = 0;
		double bestVal = -Double.MAX_VALUE;
		int bestPos = 0;
		for (int i = 0; i < moves.size(); i++) {
			int[] move = moves.get(i);

			for (int j = 0; j < featureWeights.length; j++)
				value += featureWeights[j] * features[j].calculateValue(mySurroundings, move);
			
			if (value > bestVal) {
				bestVal = value;
				bestPos = i;
			}
			
			// qValues.setValueAt(pos, m, value);
		}

		// int[] bestMove = qValues.bestMove(pos).getMove();
		//
		// printStatus(qValues.getValuesAt(pos).toString());
		//
		// return
		// moves.stream().map(Move::new).collect(Collectors.toList()).indexOf(new
		// Move(bestMove));

		if (rand.nextDouble() <= 1.0) {
			return bestPos;
		} else {
			return rand.nextInt(moves.size());
		}

	}

	private double R(WorldEntityInfo myInfo, PacmanVisibleWorld surroundings, int[] move) {
		ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = surroundings.getWorldInfoAt(move[0], move[1]);

		double R = 0.0;

		for (WorldEntityInfo pos : neighPosInfos) {
			switch (pos.getIdentifier().toLowerCase()) {
			case "point":
				R += 1.0;
				break;
			case "powerup":
				R += 0.8;
				break;
			case "wall":
				R -= 100;
				break;
			case "ghost":
				boolean powerUP = myInfo.hasProperty(PacmanAgent.powerupPropertyName);
				if (!powerUP)
					R -= 100;
				break;
			}
		}

		return R;
	}

}
