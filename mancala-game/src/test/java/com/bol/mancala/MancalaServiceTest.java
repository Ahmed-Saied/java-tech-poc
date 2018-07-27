package com.bol.mancala;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.bol.mancala.dao.BoardCachingUtil;
import com.bol.mancala.model.ActionResponse;
import com.bol.mancala.model.BoardLinkedList;
import com.bol.mancala.model.PitNode;
import com.bol.mancala.service.MancalaService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class MancalaServiceTest {

	@InjectMocks
	private MancalaService mancalaService;

	@Spy
	private BoardCachingUtil boardCachingUtil;

	private final String sessionId = "737tyhdj2y38";

	private int pitNumber;

	@Test
	public void givenFirstMove_whenBoardIsNotInitialized_thenAssertValidStones() throws ExecutionException {

		pitNumber = 1;

		doReturn(null).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertEquals(1,actionResponse.getNextPlayClient(), 0);
		assertNull(actionResponse.getWinner());
		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 1), 0);
		for (int i = 2; i < 7; i++)
			assertEquals(7,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + i), 0);
		assertEquals(1,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 7), 0);

	}

	@Test
	public void givenPlayerOnePitFourHasZeroStones_thenAssertNoActions() throws ExecutionException {

		pitNumber = 4;

		doReturn(getBoardLinkedListForEmptyPitCase(pitNumber)).when(boardCachingUtil)
				.getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertEquals(1,actionResponse.getNextPlayClient(), 0);
		assertNull(actionResponse.getWinner());
		assertNull(actionResponse.getPitsStoneNumber());

	}

	@Test
	public void givenPlayerTwoPitNineHasFiveStonesAreZero_thenAssertNoActions() throws ExecutionException {

		pitNumber = 9;

		BoardLinkedList boardLinkedList = getBoardLinkedWithPitStones(pitNumber, 5);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertEquals(2,actionResponse.getNextPlayClient(), 0);
		assertNull(actionResponse.getWinner());

		for (int i = 10; i < 14; i++)
			assertEquals(7,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + i), 0);
		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + pitNumber), 0);
		assertEquals(1,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + boardLinkedList.size()), 0);

	}

	@Test
	public void givenPlayerTwoPitStonesAreZero_thenAssertNoActions() throws ExecutionException {

		pitNumber = 8;

		BoardLinkedList boardLinkedList = getBoardLinkedListForEmptyPitCase(pitNumber);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertEquals(2,actionResponse.getNextPlayClient(), 0);
		assertNull(actionResponse.getWinner());
		assertNull(actionResponse.getPitsStoneNumber());

	}

	@Test
	public void givenPlayerOnePitNumberSixHasEightStones_thenAssertPlayerTwoPitStoreIsTheSame()
			throws ExecutionException {

		pitNumber = 6;

		BoardLinkedList boardLinkedList = getBoardLinkedWithPitStones(pitNumber, 13);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertEquals(2,actionResponse.getNextPlayClient(), 0);
		assertNull(actionResponse.getWinner());
		assertNull(actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + boardLinkedList.size()));

		for (int i = 9; i < 14; i++)
			assertEquals(7,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + i), 0);
		for (int i = 1; i < 6; i++)
			assertEquals(7,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + i), 0);
		assertEquals(0, actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + pitNumber), 0);
		assertEquals(0, actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 8), 0);
		assertEquals(9, actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 7), 0);

	}

	@Test
	public void givenPlayerTwoPitNumberSixHasEightStones_thenAssertPlayerTwoPitStoreIsTheSame()
			throws ExecutionException {

		pitNumber = 8;

		BoardLinkedList boardLinkedList = getBoardLinkedWithPitStones(pitNumber, 13);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertEquals(1,actionResponse.getNextPlayClient(), 0);
		assertNull(actionResponse.getWinner());

		for (int i = 9; i < 14; i++)
			assertEquals(7,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + i), 0);

		for (int i = 1; i < 6; i++)
			assertEquals(7,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + i), 0);

		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 6), 0);
		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + pitNumber), 0);
		assertEquals(9,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + boardLinkedList.size()), 0);

	}

	@Test
	public void givenOnePlayerHasNoMoreStones_thenAssertGameDraw() throws ExecutionException {

		pitNumber = 6;

		BoardLinkedList boardLinkedList = getBoardLinkedWithOneStonePitAndFiveEmptyPits(pitNumber, true);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertNull(actionResponse.getNextPlayClient());
		assertEquals(0,actionResponse.getWinner(), 0);

		for (int i = 1; i < 7; i++)
			assertEquals(0,boardLinkedList.getPitByPosition(i).getStonesNumber(), 0);

		for (int i = 8; i < 14; i++)
			assertEquals(6,boardLinkedList.getPitByPosition(i).getStonesNumber(), 0);

		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 6), 0);
		assertEquals(42,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 7), 0);

	}

	@Test
	public void givenPlayerOneHasNoMoreStones_thenAssertPlayerOneWinning() throws ExecutionException {

		pitNumber = 6;

		BoardLinkedList boardLinkedList = getBoardLinkedWithOneStonePitAndFiveEmptyPitsForPlayerOne(pitNumber);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertNull(actionResponse.getNextPlayClient());
		assertEquals(1,actionResponse.getWinner(), 0);

		for (int i = 1; i < 7; i++)
			assertEquals(0,boardLinkedList.getPitByPosition(i).getStonesNumber(), 0);

		for (int i = 8; i < 14; i++)
			assertEquals(6,boardLinkedList.getPitByPosition(i).getStonesNumber(), 0);

		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 6), 0);
		assertEquals(45,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 7), 0);

	}

	@Test
	public void givenPlayerTwoHasNoMoreStones_thenAssertPlayerTwoWinning() throws ExecutionException {

		pitNumber = 13;

		BoardLinkedList boardLinkedList = getBoardLinkedWithOneStonePitAndFiveEmptyPitsForPlayerTwo(pitNumber);
		doReturn(boardLinkedList).when(boardCachingUtil).getBoardLinkedListBySessionId(sessionId);

		ActionResponse actionResponse = mancalaService.getActionResponse(sessionId, pitNumber);
		assertNotNull(actionResponse);
		assertNull(actionResponse.getNextPlayClient());
		assertEquals(2,actionResponse.getWinner(), 0);

		for (int i = 1; i < 7; i++)
			assertEquals(6,boardLinkedList.getPitByPosition(i).getStonesNumber(), 0);

		for (int i = 8; i < 14; i++)
			assertEquals(0,boardLinkedList.getPitByPosition(i).getStonesNumber(), 0);

		assertEquals(0,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 13), 0);
		assertEquals(45,actionResponse.getPitsStoneNumber().get(PitNode.PIT_PREFEX + 14), 0);

	}

	private BoardLinkedList getBoardLinkedListForEmptyPitCase(int pitNumber) {

		BoardLinkedList boardLinkedList = new BoardLinkedList();

		for (int i = 1; i < 15; i++) {
			if (i == pitNumber || i == 7 || i == 14) {
				boardLinkedList.insertAtTail(i, 0);
			} else {
				boardLinkedList.insertAtTail(i, 6);
			}
		}

		return boardLinkedList;
	}

	private BoardLinkedList getBoardLinkedWithPitStones(int pitPosition, int stonesNumber) {

		BoardLinkedList boardLinkedList = new BoardLinkedList();

		for (int i = 1; i < 15; i++) {
			if (i == pitPosition) {
				boardLinkedList.insertAtTail(i, stonesNumber);
			} else if (i == 7 || i == 14) {
				boardLinkedList.insertAtTail(i, 0);
			} else {
				boardLinkedList.insertAtTail(i, 6);
			}
		}

		return boardLinkedList;
	}

	private BoardLinkedList getBoardLinkedWithOneStonePitAndFiveEmptyPits(int pitPosition, boolean playerOne) {

		BoardLinkedList boardLinkedList = new BoardLinkedList();

		for (int i = 1; i < 15; i++) {
			if (i == pitPosition) {
				boardLinkedList.insertAtTail(i, 1);
			} else {
				if (playerOne) {
					if (i > 7)
						boardLinkedList.insertAtTail(i, 6);
					else if (i == 7)
						boardLinkedList.insertAtTail(i, 41);
					else
						boardLinkedList.insertAtTail(i, 0);
				} else {

					if (i < 7)
						boardLinkedList.insertAtTail(i, 6);
					else if (i == 14)
						boardLinkedList.insertAtTail(i, 41);
					else
						boardLinkedList.insertAtTail(i, 0);
				}

			}
		}

		return boardLinkedList;
	}

	private BoardLinkedList getBoardLinkedWithOneStonePitAndFiveEmptyPitsForPlayerOne(int pitPosition) {

		BoardLinkedList boardLinkedList = new BoardLinkedList();

		for (int i = 1; i < 15; i++) {
			if (i == pitPosition) {
				boardLinkedList.insertAtTail(i, 1);
			} else {
				if (i > 7)
					boardLinkedList.insertAtTail(i, 6);
				else if (i == 7)
					boardLinkedList.insertAtTail(i, 44);
				else
					boardLinkedList.insertAtTail(i, 0);
			}
		}

		return boardLinkedList;
	}

	private BoardLinkedList getBoardLinkedWithOneStonePitAndFiveEmptyPitsForPlayerTwo(int pitPosition) {

		BoardLinkedList boardLinkedList = new BoardLinkedList();

		for (int i = 1; i < 15; i++) {
			if (i == pitPosition) {
				boardLinkedList.insertAtTail(i, 1);
			} else {
				if (i < 7)
					boardLinkedList.insertAtTail(i, 6);
				else if (i == 14)
					boardLinkedList.insertAtTail(i, 44);
				else
					boardLinkedList.insertAtTail(i, 0);
			}
		}

		return boardLinkedList;
	}

}