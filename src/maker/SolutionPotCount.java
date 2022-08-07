package maker;

public class SolutionPotCount {
	private int potId;
	private int potCount;
	
	SolutionPotCount(int id, int count){
		setPotId(id);
		setPotCount(count);
	}

	public int getPotId() {
		return potId;
	}

	public void setPotId(int potId) {
		this.potId = potId;
	}

	public int getPotCount() {
		return potCount;
	}

	public void setPotCount(int potCount) {
		this.potCount = potCount;
	}

}
