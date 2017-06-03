package hr.vinko.ims.ai.feature;

import java.util.Arrays;

public class Move {
	
	private int[] move;
	
	public Move(int[] move) {
		super();
		this.move = move;
	}

	public int[] getMove() {
		return move;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(move);
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
		Move other = (Move) obj;
		if (!Arrays.equals(move, other.move))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(move);
	}
	
	
	

}
