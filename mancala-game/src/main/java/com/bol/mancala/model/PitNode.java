package com.bol.mancala.model;

/**
 * @author Ahmed
 */

public class PitNode {

	// Pits prefix aligned with frontend
	public final static String PIT_PREFEX = "pit_";
	
	private int stonesNumber;
	private int pitPosition;
	private PitNode next;

	public PitNode(int pitPosition, int initialStoneNumber, PitNode nextNode) {
		this.pitPosition = pitPosition;
		this.stonesNumber = initialStoneNumber;
		next = nextNode;
	}

	public void setNextPitNode(PitNode n) {
		next = n;
	}

	public PitNode getNextPitNode() {
		return next;
	}

	public void setStonesNumber(int d) {
		stonesNumber = d;
	}

	public int getStonesNumber() {
		return stonesNumber;
	}

	public int getPitPosition() {
		return pitPosition;
	}
}