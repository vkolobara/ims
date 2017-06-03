package hr.vinko.ims.ai.feature;

import mmaracic.gameaiframework.PacmanVisibleWorld;

public class FeatureBias implements IFeature{

	@Override
	public double calculateValue(PacmanVisibleWorld surroundings, int[] move) {
		return 1;
	}

}
