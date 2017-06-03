package hr.vinko.ims.ai.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class SmartPacmanAI extends AgentAI {

	private Random rand = new Random();

	private Set<Vector3f> targetStates = new HashSet<>();
	private Set<Vector3f> unreachableStates = new HashSet<>();
	private Vector3f currentTarget;

	private static int tries = 0;

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
		int radiusX = mySurroundings.getDimensionX() / 2;
		int radiusY = mySurroundings.getDimensionY() / 2;

		Vector3f pos = myInfo.getPosition();
		
		targetStates.remove(pos);
		unreachableStates.remove(pos);

		List<Vector3f> ghosts = new ArrayList<>();

		boolean powerUP = myInfo.hasProperty(PacmanAgent.powerupPropertyName);
		if (powerUP)
			powerUP = Boolean.parseBoolean(myInfo.getProperty(PacmanAgent.powerupPropertyName));

		for (int i = -radiusX; i <= radiusX; i++) {
			for (int j = -radiusY; j <= radiusY; j++) {
				if (i == 0 && j == 0)
					continue;
				ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
				if (neighPosInfos != null) {
					for (WorldEntity.WorldEntityInfo info : neighPosInfos) {
						if (info.getIdentifier().compareToIgnoreCase("Point") == 0
								|| info.getIdentifier().compareToIgnoreCase("Powerup") == 0) {
							targetStates.add(info.getPosition());
						} else if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
							ghosts.add(info.getPosition());
						}
					}
				}
			}
		}

		targetStates.removeAll(unreachableStates);

		ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = null;

		if (pos.equals(currentTarget)) {
			currentTarget = null;
		}

		if (tries > 10) {
			targetStates.remove(currentTarget);
			unreachableStates.add(currentTarget);
			currentTarget = null;
			tries = 0;
		}

		System.out.println();
		while (currentTarget == null) {
			System.out.println("ZAPO");
			Vector3f target = null;
			System.out.println(unreachableStates.size());
			System.out.println(targetStates.size());
			if (!unreachableStates.isEmpty()) {
				int index = rand.nextInt(unreachableStates.size());
				target = new ArrayList<>(unreachableStates).get(index);
				System.out.println("CHOOSE TARGET");
				System.out.println(target);
			}
			if (!targetStates.isEmpty()) {
				target = targetStates.stream().min((s1, s2) -> Double.compare(s1.distance(pos), s2.distance(pos)))
						.get();
			}

			Vector3f move = target.subtract(pos);

			try {
				neighPosInfos = mySurroundings.getWorldInfoAt(move.x, move.y);
			} catch (Exception e) {
			}
			;

			if (neighPosInfos == null) {
				targetStates.remove(target);
				unreachableStates.add(target);
			} else {
				for (WorldEntityInfo info : neighPosInfos) {
					if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
						targetStates.remove(target);
						unreachableStates.add(target);
						target = null;
						break;
					}
				}
			}
			currentTarget = target;
			tries = 0;

		}

		Vector3f totalMove = currentTarget.subtract(pos);
		try {
			neighPosInfos = mySurroundings.getWorldInfoAt(totalMove.x, totalMove.y);
		} catch (Exception e) {
		}
		;

		int bestIndex = -1;
		double bestDistance = Double.MAX_VALUE;

		outer: for (int i = 0; i < moves.size(); i++) {
			int[] move = moves.get(i);
			Vector3f nextPoint = new Vector3f(pos.x + move[0], pos.y + move[1], 0.5f);
			double distance = nextPoint.distance(currentTarget);

			if (!powerUP) {
				for (Vector3f ghost : ghosts) {
					if (pos.subtract(ghost).equals(new Vector3f(move[0], move[1], 0))) {
						bestIndex = i;
						break;
					}
					if (ghost.distance(nextPoint) < 2 && ghost.distance(pos) >= ghost.distance(nextPoint))
						continue outer;
				}
			}
			if (distance < bestDistance) {
				bestDistance = distance;
				bestIndex = i;
			}

		}

		tries++;

		return bestIndex;
	}

}
