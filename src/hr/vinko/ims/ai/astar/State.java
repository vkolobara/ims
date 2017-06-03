package hr.vinko.ims.ai.astar;

import java.util.ArrayList;

import com.jme3.math.Vector3f;

import mmaracic.gameaiframework.WorldEntity;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class State {

	private Vector3f position;
	ArrayList<WorldEntityInfo> infos;
	private double cost;

	public State(Vector3f position, ArrayList<WorldEntityInfo> infos) {
		super();
		this.position = position;
		this.infos = infos;
		cost = 0;
	}

	public Vector3f getPosition() {
		return position;
	}

	public ArrayList<WorldEntityInfo> getInfos() {
		return infos;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public double getCost() {
		return cost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

}
