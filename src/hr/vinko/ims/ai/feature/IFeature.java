package hr.vinko.ims.ai.feature;

import mmaracic.gameaiframework.PacmanVisibleWorld;

public interface IFeature {
	
	public double calculateValue(PacmanVisibleWorld surroundings, int[] move);
	
}
