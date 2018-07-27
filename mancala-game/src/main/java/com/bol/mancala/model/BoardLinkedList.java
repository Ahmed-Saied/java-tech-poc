package com.bol.mancala.model;

/**
 * @author Ahmed
 */

public class BoardLinkedList {

	// board head node
	private PitNode head;

	private int size = 0;

	/**
	 * insert new node at the end of list
	 */
	public void insertAtTail(int nodeLocation, int value) {
		PitNode newNode = new PitNode(nodeLocation, value, null);
		if (null == head) {
			head = newNode;
		} else {
			PitNode temp = head;
			while (temp.getNextPitNode() != head) {
				temp = temp.getNextPitNode();
			}
			temp.setNextPitNode(newNode);
		}
		newNode.setNextPitNode(head);
		size++;
	}

	/**
	 * @param positon the location of pit within board, starts with 1
	 * @return PitNode regarding position
	 */
	public PitNode getPitByPosition(int position) {
		if (position < 1 || position > size) {
			throw new IndexOutOfBoundsException("Index is Invalid");
		}
		PitNode temp = head;
		for (int i = 1; i < position; i++) {
			temp = temp.getNextPitNode();
		}
		return temp;
	}

	/**
	 * @return true if player one has no more stones left on his pits
	 */
	public boolean noStonesLeftForPlayerOne() {
		if (size < 1) {
			throw new IndexOutOfBoundsException("Size is Invalid");
		}
		PitNode temp = getPitByPosition(1);
		for (int i = 1; i < size / 2; i++) {
			if (temp.getStonesNumber() > 0) {
				return false;
			} else {
				temp = temp.getNextPitNode();
			}
		}
		return true;
	}

	/**
	 * @return true if player two has no more stones left on his pits
	 */
	public boolean noStonesLeftForPlayerTwo() {
		if (size < 1) {
			throw new IndexOutOfBoundsException("Size is Invalid");
		}
		PitNode temp = getPitByPosition(size / 2 + 1);
		for (int i = size / 2 + 1; i < size; i++) {
			if (temp.getStonesNumber() > 0) {
				return false;
			} else {
				temp = temp.getNextPitNode();
			}
		}
		return true;
	}

	/**
	 * @return total number of stones for player one
	 */
	public Integer getPlayerOneTotalStones() {
		Integer total = 0;
		if (size < 1) {
			throw new IndexOutOfBoundsException("Index is Invalid");
		}
		PitNode temp = getPitByPosition(1);
		for (int i = 1; i <= size / 2; i++) {
			total += temp.getStonesNumber();
			temp = temp.getNextPitNode();
		}

		return total;
	}

	/**
	 * @return total number of stones for player two
	 */
	public Integer getPlayerTwoTotalStones() {
		Integer total = 0;
		if (size < 1) {
			throw new IndexOutOfBoundsException("Index is Invalid");
		}
		PitNode temp = getPitByPosition(size / 2 + 1);
		for (int i = size / 2 + 1; i <= size; i++) {
			total += temp.getStonesNumber();
			temp = temp.getNextPitNode();
		}

		return total;

	}

	/**
	 * @return integer board size
	 */
	public int size() {
		return size;
	}

	/**
	 * true if board is empty
	 */
	public boolean isEmpty() {
		return size == 0;
	}
}