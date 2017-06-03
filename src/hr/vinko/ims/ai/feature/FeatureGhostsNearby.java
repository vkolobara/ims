package hr.vinko.ims.ai.feature;

import java.util.ArrayList;

import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

public class FeatureGhostsNearby implements IFeature {

	private int range;

	public FeatureGhostsNearby(int range) {
		super();
		this.range = range;
	}

	@Override
	public double calculateValue(PacmanVisibleWorld surroundings, int[] move) {
		int ghostCount = 0;
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = null;
				try {
					neighPosInfos = surroundings.getWorldInfoAt(move[0] + i, move[1] + j);
				} catch (Exception e) {}
				if (neighPosInfos == null)
					continue;

				for (WorldEntity.WorldEntityInfo pos : neighPosInfos) {
					if (pos.getIdentifier().compareToIgnoreCase("GHOST") == 0)
						ghostCount++;
				}

			}
		}
		return ghostCount;
	}

}
