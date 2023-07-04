package it.polito.tdp.PremierLeague.model;

public class PlayerDelta implements Comparable<PlayerDelta> {

	private Player p;
	private Integer deltaTime;
	public PlayerDelta(Player p, Integer deltaTime) {
		super();
		this.p = p;
		this.deltaTime = deltaTime;
	}
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public Integer getDeltaTime() {
		return deltaTime;
	}
	public void setDeltaTime(Integer deltaTime) {
		this.deltaTime = deltaTime;
	}
	@Override
	public int compareTo(PlayerDelta o) {
		// TODO Auto-generated method stub
		return o.deltaTime-this.deltaTime;
	}
	
	
}
