package hr.vinko.ims.ai.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class GhostAI extends AgentAI {

	private Random rand = new Random();

	private Vector3f pacmanLastSeen = null;

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {

		int radiusX = mySurroundings.getDimensionX() / 2;
		int radiusY = mySurroundings.getDimensionY() / 2;

		Vector3f pos = myInfo.getPosition();

		Vector3f newPacmanPosition = null;

		PosWithTimestamp mostRecentPosition = null;

		outer: for (int i = -radiusX; i <= radiusX; i++) {
			for (int j = -radiusY; j <= radiusY; j++) {
				if (i == 0 && j == 0)
					continue;

				PosWithTimestamp metadata = null;
				try {
					metadata = (PosWithTimestamp) mySurroundings.getWorldMetadataAt(i, j).get(1);
				} catch (Exception e) {
				}
				;
				if (metadata != null) {
					if (mostRecentPosition == null || mostRecentPosition.compareTo(metadata) < 0)
						mostRecentPosition = metadata;
				}

				ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
				if (neighPosInfos != null) {
					for (WorldEntity.WorldEntityInfo info : neighPosInfos) {
						if (info.getIdentifier().compareToIgnoreCase("Pacman") == 0) {
							newPacmanPosition = info.getPosition();
							mySurroundings.getWorldMetadataAt(i, j).put(1, new PosWithTimestamp(newPacmanPosition));
							break outer;
						}
					}
				}
			}
		}

		if (newPacmanPosition == null && mostRecentPosition != null) {
			newPacmanPosition = mostRecentPosition.pos;
		}
		if (newPacmanPosition != null)
			pacmanLastSeen = newPacmanPosition;

		boolean powerUP = myInfo.hasProperty(PacmanAgent.powerupPropertyName);
		if (powerUP)
			powerUP = Boolean.parseBoolean(myInfo.getProperty(PacmanAgent.powerupPropertyName));

		int bestIndex = -1;
		int powerUpMult = powerUP ? -1 : 1;
		double bestDistance = powerUP ? -Double.MAX_VALUE : Double.MAX_VALUE;

		if (newPacmanPosition == null) {
			float xOffset = rand.nextFloat() * 14 - 7;
			float yOffset = rand.nextFloat() * 14 - 7;
			newPacmanPosition = new Vector3f(pos.x + xOffset, pos.y + yOffset, pos.z);
		}

		if (pacmanLastSeen != null) {
			newPacmanPosition = pacmanLastSeen;
		}

		for (int i = 0; i < moves.size(); i++) {
			int[] move = moves.get(i);
			Vector3f nextPoint = new Vector3f(pos.x + move[0], pos.y + move[1], pos.z);
			double distance = manhattanDistance(nextPoint, newPacmanPosition);

			Vector3f m = pos.subtract(newPacmanPosition);
			if ((int) m.x == move[0] && (int) m.y == move[1]) {

				bestIndex = i;
				break;
			}

			if (powerUpMult * Double.compare(distance, bestDistance) < 0) {
				bestDistance = distance;
				bestIndex = i;
			}

		}

		return bestIndex == -1 ? rand.nextInt(moves.size()) : bestIndex;
	}

	private double manhattanDistance(Vector3f vec1, Vector3f vec2) {
		return Math.abs(vec1.x - vec2.x) + Math.abs(vec1.y - vec2.y);
	}

	private static class PosWithTimestamp implements Comparable<PosWithTimestamp> {
		Vector3f pos;
		Date time;

		public PosWithTimestamp(Vector3f pos) {
			this.pos = pos;
			time = new Date();
		}

		@Override
		public int compareTo(PosWithTimestamp o) {
			return time.compareTo(o.time);
		}

	}

}
