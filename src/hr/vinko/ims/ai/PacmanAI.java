package hr.vinko.ims.ai;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

public class PacmanAI extends AgentAI {
	private final Random rand = new Random();

	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings,
			WorldEntity.WorldEntityInfo myInfo) {

		int radiusX = mySurroundings.getDimensionX() / 2;
		int radiusY = mySurroundings.getDimensionY() / 2;

		boolean powerUP = myInfo.hasProperty(PacmanAgent.powerupPropertyName);

		Vector3f pos = myInfo.getPosition();

		Queue<State> states = new PriorityQueue<>();

		for (int i = -radiusX; i <= radiusX; i++) {
			for (int j = -radiusY; j <= radiusY; j++) {
				if (i == 0 && j == 0)
					continue;
				Vector3f location = new Vector3f(pos.x + i, pos.y + j, 1f);
				ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
				if (neighPosInfos != null) {
					float h = hValue(pos, neighPosInfos, location);
					State state = new State(h, location);
					states.add(state);
				}
			}
		}

		State best = states.poll();

		Vector3f distance = best.position.subtract(pos);

		for (int i = 0; i < moves.size(); i++) {
			int[] move = moves.get(i);

			if ((distance.x < 0 && move[0] < 0) || (distance.x > 0 && move[0] > 0) || (distance.y < 0 && move[1] < 0)
					|| (distance.y > 0 && move[1] > 0))
				return i;
		}

		return rand.nextInt(moves.size());

	}

	private float hValue(Vector3f pos, ArrayList<WorldEntity.WorldEntityInfo> infos, Vector3f location) {
		float h = 0;
		for (WorldEntity.WorldEntityInfo info : infos) {
			if (info.getIdentifier().compareToIgnoreCase("Point") == 0
					|| info.getIdentifier().compareToIgnoreCase("Powerup") == 0) {
				h += pos.distance(location);
			} else if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
				h -= 100 + pos.distance(location);
			}
		}
		return h;
	}

	private static class State implements Comparable<State> {
		float h;
		Vector3f position;

		public State(float h, Vector3f position) {
			this.h = h;
			this.position = position;
		}

		@Override
		public int compareTo(State o) {
			return (int) (o.h - h);
		}
	}

}
