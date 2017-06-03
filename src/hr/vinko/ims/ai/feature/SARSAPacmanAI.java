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

public class SARSAPacmanAI extends AgentAI {

	private QValues qValues = new QValues();

	private final static Random rand = new Random();

	private final static int[] DONT_MOVE = new int[] { 0, 0 };

	private final static double ALPHA = 0.1;
	private final static double GAMMA = 0.9;

	private final static double EPS = 0.95;

	private static IFeature[] features = new IFeature[] { new FeatureBias(), new FeatureGhostsNearby(1),
			new FeatureNearestFood() };
	private static double[] featureWeights = new double[] { 0, 0, 0 };

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings,
			WorldEntity.WorldEntityInfo myInfo) {

		int bestIndex = 0;
		double bestQ = -Double.MAX_VALUE;

		Vector3f pos = myInfo.getPosition();

		for (int i = 0; i < moves.size(); i++) {
			Move move = new Move(moves.get(i));

			List<int[]> nextMoves = new ArrayList<>();
			nextMoves.add(new int[] { 1, 0 });
			nextMoves.add(new int[] { -1, 0 });
			nextMoves.add(new int[] { 0, 1 });
			nextMoves.add(new int[] { 0, -1 });

			double nextQ = -Double.MAX_VALUE;

			for (int j = 0; j < 4; j++) {
				int[] nextMove = nextMoves.get(j);
				nextQ = Math.max(nextQ, qValues.getValueAt(pos.add(new Vector3f(nextMove[0], nextMove[1], 0)), new Move(nextMove)));
			}
			

			double q = qValues.getValueAt(pos, move);

			double error = R(myInfo, mySurroundings, DONT_MOVE) + GAMMA * nextQ - q;

			qValues.setValueAt(pos, move, q + ALPHA * error);

			q = qValues.getValueAt(pos, move);
			
			if (q >= bestQ) {
				bestIndex = i;
				bestQ = q;
			}
		
		}

		if (rand.nextDouble() > EPS) {
			bestIndex = rand.nextInt(moves.size());
		}
		
		printStatus(qValues.getValuesAt(pos).toString());


		return bestIndex;

	}

	private double R(WorldEntityInfo myInfo, PacmanVisibleWorld surroundings, int[] move) {
		ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = surroundings.getWorldInfoAt(move[0], move[1]);

		double R = 0.0;

		for (WorldEntityInfo pos : neighPosInfos) {
			switch (pos.getIdentifier().toLowerCase()) {
			case "point":
				R += 20;
				break;
			case "powerup":
				R += 15;
				break;
			case "ghost":
				boolean powerUP = myInfo.hasProperty(PacmanAgent.powerupPropertyName);
				if (!powerUP)
					R -= 200;
				break;
			}
		}

		if (Double.isNaN(R))
			R = 0.0;

		return R;
	}

}
