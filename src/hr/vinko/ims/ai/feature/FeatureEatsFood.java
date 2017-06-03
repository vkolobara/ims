package hr.vinko.ims.ai.feature;

import java.util.ArrayList;

import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

public class FeatureEatsFood implements IFeature {

	@Override
	public double calculateValue(PacmanVisibleWorld surroundings, int[] move) {
		ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = null;
		try { neighPosInfos = surroundings.getWorldInfoAt(move[0], move[1]); }
		catch (Exception e) {};
		if (neighPosInfos == null)
			return 0;
		for (WorldEntity.WorldEntityInfo pos : neighPosInfos) {
			if (pos.getIdentifier().compareToIgnoreCase("Point") == 0) {
				return 1;
			}
		}
		return 0;
	}

}
