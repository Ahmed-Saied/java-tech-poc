package com.bol.mancala.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bol.mancala.dao.BoardCachingUtil;
import com.bol.mancala.model.ActionResponse;
import com.bol.mancala.model.BoardLinkedList;
import com.bol.mancala.model.PitNode;

/**
 * The Service layer where the logic of board actions is occuring
 * 
 * @author Ahmed
 */
@Service
public class MancalaService {


	@Autowired
	private BoardCachingUtil boardCachingUtil;

	/**
	 * @param sessionId unique session Id passed from mancala client
	 * @param pitNumber the on action pit where stones are removed and then added to the following pits
	 * @return ActionResponse defines next player, winner if game is over, pits stones regarding each one position
	 */
	public ActionResponse getActionResponse(String sessionId, int pitNumber) throws ExecutionException {

		BoardLinkedList boardLinkedList = boardCachingUtil.getBoardLinkedListBySessionId(sessionId);
		
		// Initialize the board if it's the first play
		if (boardLinkedList == null) {
			boardLinkedList = getInitialPitsLinkedList();
		}

		return getAllPitsWithNewValues(boardLinkedList, pitNumber, sessionId);
	}

	private ActionResponse getAllPitsWithNewValues(BoardLinkedList boardLinkedList, int originalPitPosition,
			String sessionId) {

		ActionResponse actionResponse = new ActionResponse();

		// changed pits as a map for ActionResponse message
		Map<String, Integer> pitMap = new HashMap<>();

		// board actual size, mainly 14 with the two stores
		int boardPitsSize = boardLinkedList.size();

		// getting the target pit on action
		PitNode targetPit = boardLinkedList.getPitByPosition(originalPitPosition);

		// if pit stones is less than one then no action, same player continues playing
		// should be handled from mancala client too
		if (targetPit.getStonesNumber() < 1) {
			int nextPlayer = originalPitPosition < boardPitsSize / 2 ? 1 : 2;
			actionResponse.setNextPlayClient(nextPlayer);
			return actionResponse;
		}

		// getting stones number on target pit
		int pitStonesNumber = targetPit.getStonesNumber();

		// setting target pit stones to zero
		// added to map initially, can be added during actions again with stones
		targetPit.setStonesNumber(0);
		pitMap.put(PitNode.PIT_PREFEX + (originalPitPosition), 0);

		// while there are stones in the original pit then
		// add one to the following pits counter clock wise
		while (pitStonesNumber > 0) {

			targetPit = targetPit.getNextPitNode();

			int targetPitPosition = targetPit.getPitPosition();
			int targetPitOldStonesNumber = targetPit.getStonesNumber();

			// avoid adding stones to other player stones pit store (big pit)
			if ((originalPitPosition > boardPitsSize / 2 && targetPitPosition == boardPitsSize / 2)
					|| (originalPitPosition < boardPitsSize / 2 && targetPitPosition == boardPitsSize)) {
				continue;
			}

			// adding one stone to the current pit stones
			targetPit.setStonesNumber(targetPitOldStonesNumber + 1);
			// adding pit position, stones number as key value pair
			pitMap.put(PitNode.PIT_PREFEX + (targetPitPosition), targetPitOldStonesNumber + 1);

			pitStonesNumber--;

			// check if this is the last added stone
			if (pitStonesNumber == 0) {

				// if the original pit is related to player two
				if (originalPitPosition > boardPitsSize / 2) {
					// if last stone added to player two stones pit store (big pit)
					// then player two continues playing
					if (targetPitPosition == boardPitsSize) {
						actionResponse.setNextPlayClient(2);
					} // else it would be player one turn to play
					else {
						actionResponse.setNextPlayClient(1);
						// checking if the last added stone has been added to empty pit related to
						// player two
						if (targetPit.getStonesNumber() == 1 && targetPitPosition > boardPitsSize / 2) {
							// add target pit player two stones + opposite player one pit stones to player
							// two stones pit store (big pit)
							addExtraStonesToOnePitStore(targetPit, boardLinkedList, pitMap);
						}
					}
				} // else the original pit is related to player one
				else {
					// if last stone added to player one stones pit store (big pit)
					// then player one continues playing
					if (targetPitPosition == boardPitsSize / 2) {
						actionResponse.setNextPlayClient(1);
					} // else it would be player two turn to play
					else {
						actionResponse.setNextPlayClient(2);
						// checking if the last added stone has been added to empty pit related to
						// player one
						if (targetPit.getStonesNumber() == 1 && targetPitPosition < boardPitsSize / 2) {
							// add target pit player one stones + opposite player two pit stones to player
							// one stones pit store (big pit)
							addExtraStonesToOnePitStore(targetPit, boardLinkedList, pitMap);
						}
					}
				}
			}
		}

		// add the finalized pit map to the ActionResponse message
		actionResponse.setPitsStoneNumber(pitMap);

		// check if game is over to calculate total players pit and announce the winner
		if (gameOver(boardLinkedList)) {
			if (boardLinkedList.getPlayerOneTotalStones() > boardLinkedList.getPlayerTwoTotalStones()) {
				actionResponse.setWinner(1);
			} else if (boardLinkedList.getPlayerOneTotalStones() < boardLinkedList.getPlayerTwoTotalStones()) {
				actionResponse.setWinner(2);
			}else {
				actionResponse.setWinner(0);
			}
			actionResponse.setNextPlayClient(null);
			// remove board from caching after game is over
			boardCachingUtil.removeBoard(sessionId);
		} else {
			// adding game board circular linked list to caching with the new pit stones values while game is continuing 
			boardCachingUtil.addBoard(sessionId, boardLinkedList);
		}
		
		return actionResponse;

	}

	/**
	 * Adding stones from active pit and the opposite one to active player stones stones pit store
	 * @param activeplayerPitNode the pit on action
	 * @param boardLinkedList the cached target board for the active session 
	 * @param pitMap the map that is included in ActionResponse message to the client
	 * */
	private void addExtraStonesToOnePitStore(PitNode activeplayerPitNode, BoardLinkedList boardLinkedList, Map<String, Integer> pitMap) {

		int boardSize = boardLinkedList.size();

		int tobeSubtractedFromSize = activeplayerPitNode.getPitPosition() - 1;

		// getting the opposite pit position
		int positionOfEliminatedStonesPit = boardSize - tobeSubtractedFromSize - 1;

		PitNode otherPlayerPitNode = boardLinkedList.getPitByPosition(positionOfEliminatedStonesPit);

		// getting play stones pit store regarding active pit position
		PitNode activePlayerStorePit = null;
		if (activeplayerPitNode.getPitPosition() < boardSize / 2) {
			activePlayerStorePit = boardLinkedList.getPitByPosition(boardSize / 2);
		} else {
			activePlayerStorePit = boardLinkedList.getPitByPosition(boardSize);
		}

		// adding all stones from the active and opposite pit to the active player stones stones pit store
		activePlayerStorePit.setStonesNumber(activePlayerStorePit.getStonesNumber() + otherPlayerPitNode.getStonesNumber()
				+ activeplayerPitNode.getStonesNumber());
		pitMap.put(PitNode.PIT_PREFEX + activePlayerStorePit.getPitPosition(), activePlayerStorePit.getStonesNumber());

		//removing stones from active pit
		activeplayerPitNode.setStonesNumber(0);
		pitMap.put(PitNode.PIT_PREFEX + activeplayerPitNode.getPitPosition(), 0);

		//removing stones from opposite pit
		otherPlayerPitNode.setStonesNumber(0);
		pitMap.put(PitNode.PIT_PREFEX + otherPlayerPitNode.getPitPosition(), 0);
	}

	/**
	 * @param boardLinkedList the board to check stones within it
	 * @return true if one of the two players has no more stones on his own pits
	 * */
	private boolean gameOver(BoardLinkedList boardLinkedList) {

		if (boardLinkedList.noStonesLeftForPlayerOne() || boardLinkedList.noStonesLeftForPlayerTwo()) {
			return true;
		}
		return false;
	}

	/**
	 * @return
	 * Board linked list, 6 pits for every player and one stones pit store for each
	 * */
	private BoardLinkedList getInitialPitsLinkedList() {
		BoardLinkedList pitsLinkedList = new BoardLinkedList();

		for (int i = 1; i <= 14; i++) {
			if (i == 7 || i == 14) {
				pitsLinkedList.insertAtTail(i, 0);
			} else {
				pitsLinkedList.insertAtTail(i, 6);
			}
		}
		return pitsLinkedList;
	}
}