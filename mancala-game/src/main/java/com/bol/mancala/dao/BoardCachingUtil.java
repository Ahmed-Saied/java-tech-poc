package com.bol.mancala.dao;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.bol.mancala.model.BoardLinkedList;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Ahmed
 */

@Component
public class BoardCachingUtil {

	// Cache object contains the updated board(pits with stones) linked with unique
	// session Id
	private Cache<String, BoardLinkedList> mutlipleBoardCachedLinkedList = CacheBuilder.newBuilder().maximumSize(1000)
			.expireAfterWrite(60, TimeUnit.MINUTES).build();

	/**
	 * @param sessionId the unique id of the session, used as caching key
	 * @return BoardLinkedList related to session
	 */
	public BoardLinkedList getBoardLinkedListBySessionId(String sessionId) {
		return mutlipleBoardCachedLinkedList.getIfPresent(sessionId);
	}

	/**
	 * Adding board (circular linked list) to cache using session id as caching key
	 */
	public void addBoard(String sessionId, BoardLinkedList boardLinkedList) {
		mutlipleBoardCachedLinkedList.put(sessionId, boardLinkedList);
	}

	/**
	 * remove the board from caching using session id
	 */
	public void removeBoard(String sessionId) {
		mutlipleBoardCachedLinkedList.invalidate(sessionId);
	}
}
