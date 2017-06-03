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

public class PacmanQLearningAI extends AgentAI {

	private QValues qValues = new QValues();

	private final static Random rand = new Random();

	private final static int[] DONT_MOVE = new int[] { 0, 0 };

	private static double ALPHA = 1;
	private static double GAMMA = 0.9;

	private static double EPS = 1;

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings,
			WorldEntity.WorldEntityInfo myInfo) {

		qValues.reduceValues();
		
		int bestIndex = 0;
		double bestQ = -Double.MAX_VALUE;

		Vector3f pos = myInfo.getPosition();

		
		for (int i = 0; i < moves.size(); i++) {
			double q = qValues.getValueAt(pos, new Move(moves.get(i)));

			if (q >= bestQ) {
				bestQ = q;
				bestIndex = i;
			}
		}
		
		if (rand.nextDouble() <= EPS)
			bestIndex = rand.nextInt(moves.size());

		List<int[]> nextMoves = new ArrayList<>();
		nextMoves.add(new int[] { 1, 0 });
		nextMoves.add(new int[] { -1, 0 });
		nextMoves.add(new int[] { 0, 1 });
		nextMoves.add(new int[] { 0, -1 });

		double nextQ = 0;

		for (int i = 0; i < nextMoves.size(); i++) {
			int[] move = nextMoves.get(i);
			Vector3f nextPos = pos.add(move[0], move[1], 0);
			nextQ = Math.max(nextQ, qValues.getValueAt(nextPos, new Move(move)));
		}
		
		Move bestMove = new Move(moves.get(bestIndex));

		double q = qValues.getValueAt(pos, bestMove);
		
		System.out.println("R");
		System.out.println(bestMove);
		System.out.println(R(myInfo, mySurroundings, bestMove.getMove()));
		
		qValues.setValueAt(pos, bestMove, q + ALPHA * (R(myInfo, mySurroundings, bestMove.getMove()) + GAMMA * nextQ - q));

		System.out.println(qValues.getValuesAt(pos));
		
		ALPHA *= .99;
		EPS *= .98;
		
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
