package com.bol.mancala.model;

import java.util.Map;

/**
 * @author Ahmed
 */

public class ActionResponse {

	private Integer nextPlayClient;

	private Integer winner;

	private Map<String, Integer> pitsStoneNumber;

	public Integer getWinner() {
		return winner;
	}

	public void setWinner(Integer winner) {
		this.winner = winner;
	}

	public Integer getNextPlayClient() {
		return nextPlayClient;
	}

	public void setNextPlayClient(Integer nextPlayClient) {
		this.nextPlayClient = nextPlayClient;
	}

	public Map<String, Integer> getPitsStoneNumber() {
		return pitsStoneNumber;
	}

	public void setPitsStoneNumber(Map<String, Integer> pitsStoneNumber) {
		this.pitsStoneNumber = pitsStoneNumber;
	}

}
