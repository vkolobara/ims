package hr.vinko.ims.ai.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class AStarSearch {

	public Map<State, State> search(State state, State goal, PacmanVisibleWorld surroundings) {

		List<int[]> nextMoves = new ArrayList<>();
		nextMoves.add(new int[] { 1, 0 });
		nextMoves.add(new int[] { -1, 0 });
		nextMoves.add(new int[] { 0, 1 });
		nextMoves.add(new int[] { 0, -1 });

		Queue<State> frontier = new PriorityQueue<>((state1, state2) -> {
			return Double.compare(state1.getCost(), state2.getCost());
		});

		Map<State, Double> visited = new HashMap<>();
		Map<State, State> path = new HashMap<>();

		frontier.add(state);

		while (!frontier.isEmpty()) {

			State current = frontier.poll();
			if (current.getPosition().equals(goal.getPosition()))
				break;

			Vector3f currPos = current.getPosition().subtract(state.getPosition());

			int[] currMove = new int[] { (int) currPos.x, (int) currPos.y };

			double totalCost = 0;

			for (int[] move : nextMoves) {
				State next = new State(current.getPosition(),
						surroundings.getWorldInfoAt(currMove[0] + move[0], currMove[1] + move[1]));

				double cost = totalCost + nodeCost(state) + heuristicCost(state, goal)
						+ penalty(next, goal, surroundings);
				if (!visited.containsKey(next) || cost < visited.get(next)) {
					next.setCost(cost);
					totalCost = cost;
					visited.put(next, cost);
					frontier.add(next);
					path.put(next, current);
				}

			}

		}
		
		return path;

	}

	private double nodeCost(State state) {
		ArrayList<WorldEntityInfo> infos = state.getInfos();

		boolean powerup = false;
		boolean point = false;

		for (WorldEntityInfo info : infos) {
			if (info.getIdentifier().compareToIgnoreCase("point") == 0)
				point = true;
			else if (info.getIdentifier().compareToIgnoreCase("powerup") == 0)
				powerup = true;
		}

		boolean empty = !(powerup || point);

		double cost = 0;

		if (empty) {
			cost = 14;
		} else {
			if (powerup)
				cost += 2;
			if (point)
				cost += 10;
		}

		return cost;
	}

	private double heuristicCost(State state, State goalState) {
		return state.getPosition().distance(goalState.getPosition());
	}

	private double penalty(State startState, State goalState, PacmanVisibleWorld surroundings) {
		int rangeX = surroundings.getDimensionX() / 2;
		int rangeY = surroundings.getDimensionY() / 2;

		double distance = Double.MAX_VALUE;

		for (int i = -rangeX; i <= rangeX; i++) {
			for (int j = -rangeY; j <= rangeY; j++) {
				ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = null;
				try {
					neighPosInfos = surroundings.getWorldInfoAt(i, j);
				} catch (Exception e) {
				}
				if (neighPosInfos == null)
					continue;

				for (WorldEntity.WorldEntityInfo pos : neighPosInfos) {
					if (pos.getIdentifier().compareToIgnoreCase("GHOST") == 0)
						distance = Math.min(distance, pos.getPosition().distance(goalState.getPosition()));
				}

			}
		}

		return 1 / (distance + 1);
	}

}
