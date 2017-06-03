package hr.vinko.ims.ai.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class SARSAFeaturesPacmanAI extends AgentAI {

	private QValues qValues = new QValues();

	private final static Random rand = new Random();

	private final static int[] DONT_MOVE = new int[] { 0, 0 };

	private final static double ALPHA = 0.1;
	private final static double GAMMA = 0.9;

	private final static double EPS = 0.99;

	private static IFeature[] features = new IFeature[] { new FeatureBias(), new FeatureGhostsNearby(3),
			new FeatureNearestFood() };
	private static double[] featureWeights = new double[] { 1, -100, -20 };

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings,
			WorldEntity.WorldEntityInfo myInfo) {

		Vector3f pos = myInfo.getPosition();

		int bestIndex = 0;
		double bestValue = -Double.MAX_VALUE;
		System.out.println("START");
		for (int i = 0; i < moves.size(); i++) {
			int[] move = moves.get(i);

			double nextQ = qValues.getValueAt(pos, new Move(move));
			System.out.println(pos + " [" + Arrays.toString(move) + "] " + nextQ);

			if (nextQ > bestValue) {
				bestValue = nextQ;
				bestIndex = i;
			}

		}
		System.out.println();

		if (rand.nextDouble() > EPS) {
			bestIndex = rand.nextInt(moves.size());
		}

		int[] bestMove = moves.get(bestIndex);

		double r = R(myInfo, mySurroundings, bestMove);
		double currentQ = Q(mySurroundings, bestMove);

		List<int[]> nextMoves = new ArrayList<>();
		nextMoves.add(new int[] { 1, 0 });
		nextMoves.add(new int[] { -1, 0 });
		nextMoves.add(new int[] { 0, 1 });
		nextMoves.add(new int[] { 0, -1 });

		double nextQ = -Double.MAX_VALUE;

		for (int i = 0; i < 4; i++) {
			int[] move = nextMoves.get(i);
			int[] nextMove = new int[] { move[0] + bestMove[0], move[1] + bestMove[1] };
			nextQ = Math.max(nextQ, Q(mySurroundings, nextMove));
		}

//		double correction = r + GAMMA * nextQ - currentQ;
//
//		double sum = 0;
//
		double[] featuresCalc = new double[features.length];

		for (int i = 0; i < featureWeights.length; i++) {
			double fCalc = features[i].calculateValue(mySurroundings, bestMove);
			featuresCalc[i] = fCalc;
//			// featureWeights[i] += ALPHA * correction * fCalc;
//			sum += Math.abs(featureWeights[i]);
		}

		// for (int i = 0; i < features.length; i++) {
		// featureWeights[i] /= sum;
		// }

		for (int [] move : moves)
		qValues.setValueAt(pos, new Move(move), Q(mySurroundings, move));

		System.out.println(Arrays.toString(featureWeights));
		System.out.println(Arrays.toString(featuresCalc));
		System.out.println("Qnext: " + nextQ);
		System.out.println();

		return bestIndex;

	}

	private double Q(PacmanVisibleWorld surroundings, int[] move) {
		double sum = 0;

		for (int i = 0; i < features.length; i++) {
			sum += features[i].calculateValue(surroundings, move) * featureWeights[i];
		}
		if (Double.isNaN(sum))
			sum = 1.0;
		return sum;
	}

	private double R(WorldEntityInfo myInfo, PacmanVisibleWorld surroundings, int[] move) {
		ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = surroundings.getWorldInfoAt(move[0], move[1]);

		double R = 0.0;

		for (WorldEntityInfo pos : neighPosInfos) {
			switch (pos.getIdentifier().toLowerCase()) {
			case "point":
				R += 10;
				break;
			case "powerup":
				R += 10;
				break;
			case "ghost":
				boolean powerUP = myInfo.hasProperty(PacmanAgent.powerupPropertyName);
				if (!powerUP)
					R -= 1000;
				break;
			}
		}

		if (Double.isNaN(R))
			R = 0.0;

		return R;
	}

}
