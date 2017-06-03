package hr.vinko.ims.ai.feature;

import java.util.ArrayList;

import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class FeatureNearestFood implements IFeature {

	@Override
	public double calculateValue(PacmanVisibleWorld surroundings, int[] move) {
		int radiusX = surroundings.getDimensionX() / 2 - 1;
		int radiusY = surroundings.getDimensionY() / 2 - 1;

		double min_distance = 14;

		for (int i = -radiusX; i <= radiusX; i++) {
			for (int j = -radiusY; j <= radiusY; j++) {
				ArrayList<WorldEntityInfo> neighPosInfos = null;
				try { neighPosInfos = surroundings.getWorldInfoAt(move[0] + i, move[1] + j); }
				catch (Exception e) {};
				boolean flag = false;
				double distance = Math.abs(i) + Math.abs(j);

				if (neighPosInfos == null) continue;
				
				for (WorldEntityInfo pos : neighPosInfos) {
					if (pos.getIdentifier().compareToIgnoreCase("Point") == 0) {
						flag = true;
						break;
					}
				}

				if (flag)
					min_distance = Math.min(min_distance, distance);
			}

		}
		return min_distance;
	}

}
