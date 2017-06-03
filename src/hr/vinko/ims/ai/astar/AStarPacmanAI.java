package hr.vinko.ims.ai.astar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class AStarPacmanAI extends AgentAI {

	private AStarSearch search = new AStarSearch();
	
	@Override
	public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
		
		State state = new State(myInfo.getPosition(), mySurroundings.getWorldInfoAt(0, 0));
		
		State goal = findTargetState(mySurroundings);
		
		System.out.println("GOAL");
		System.out.println(goal.getPosition());
		
		Map<State, State> path = search.search(state, goal, mySurroundings);
		
		State next = path.get(state);
		
		Vector3f diff = next.getPosition().subtract(state.getPosition());
		
		int[] move = new int[] {(int) diff.x, (int) diff.y};
		
		for (int i=0; i<moves.size(); i++) {
			if (Arrays.equals(move, moves.get(i))) return i;
		}
		
		return 0;
	}
	
	private State findTargetState(PacmanVisibleWorld surroundings) {
		
		int rangeX = surroundings.getDimensionX() / 2;
		int rangeY = surroundings.getDimensionY() / 2;

		for (int i = -rangeX; i <= rangeX; i++) {
			for (int j = -rangeY; j <= rangeY; j++) {
				ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = null;
				try {
					neighPosInfos = surroundings.getWorldInfoAt(i, j);
				} catch (Exception e) {
				}
				if (neighPosInfos == null)
					continue;
				
				for (WorldEntityInfo info : neighPosInfos) {
					if (info.getIdentifier().compareToIgnoreCase("Point") == 0) {
						return new State(info.getPosition(), neighPosInfos);
					}
				}

			}
		}
		
		return null;
	}
	
}
