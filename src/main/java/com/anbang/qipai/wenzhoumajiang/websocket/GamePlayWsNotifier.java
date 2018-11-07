package com.anbang.qipai.wenzhoumajiang.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

@Component
public class GamePlayWsNotifier {

	private Map<String, WebSocketSession> idSessionMap = new ConcurrentHashMap<>();

	private Map<String, Long> sessionIdActivetimeMap = new ConcurrentHashMap<>();

	private Map<String, String> sessionIdPlayerIdMap = new ConcurrentHashMap<>();

	private Map<String, String> playerIdSessionIdMap = new ConcurrentHashMap<>();

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Gson gson = new Gson();

	public WebSocketSession removeSession(String id) {
		WebSocketSession removedSession = idSessionMap.remove(id);
		sessionIdActivetimeMap.remove(id);
		if (removedSession != null) {
			String removedPlayerId = sessionIdPlayerIdMap.remove(id);
			if (removedPlayerId != null) {
				String currentSessionIdForPlayer = playerIdSessionIdMap.get(removedPlayerId);
				if (currentSessionIdForPlayer.equals(id)) {
					playerIdSessionIdMap.remove(removedPlayerId);
				}
			}
		}
		return removedSession;
	}

	public void addSession(WebSocketSession session) {
		idSessionMap.put(session.getId(), session);
		sessionIdActivetimeMap.put(session.getId(), System.currentTimeMillis());
	}

	public void bindPlayer(String sessionId, String playerId) {
		String sessionAlreadyExistsId = playerIdSessionIdMap.get(playerId);
		sessionIdPlayerIdMap.put(sessionId, playerId);
		playerIdSessionIdMap.put(playerId, sessionId);
		updateSession(sessionId);
		if (sessionAlreadyExistsId != null) {
			WebSocketSession sessionAlreadyExists = idSessionMap.get(sessionAlreadyExistsId);
			if (sessionAlreadyExists != null) {
				try {
					sessionAlreadyExists.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateSession(String id) {
		sessionIdActivetimeMap.put(id, System.currentTimeMillis());
	}

	public String findPlayerIdBySessionId(String sessionId) {
		return sessionIdPlayerIdMap.get(sessionId);
	}

	public void notifyToQuery(String playerId, String scope) {
		executorService.submit(() -> {
			CommonMO mo = new CommonMO();
			mo.setMsg("query");
			Map data = new HashMap();
			data.put("scope", scope);
			mo.setData(data);
			String payLoad = gson.toJson(mo);
			String sessionId = playerIdSessionIdMap.get(playerId);
			if (sessionId == null) {
				return;
			}
			WebSocketSession session = idSessionMap.get(sessionId);
			if (session != null) {
				sendMessage(session, payLoad);
			} else {

			}
		});
	}

	public void notifyToListenWisecrack(String playerId, String ordinal, String speakerId) {
		executorService.submit(() -> {
			CommonMO mo = new CommonMO();
			mo.setMsg("wisecrack");
			Map data = new HashMap();
			data.put("ordinal", ordinal);
			data.put("speakerId", speakerId);
			mo.setData(data);
			String payLoad = gson.toJson(mo);
			String sessionId = playerIdSessionIdMap.get(playerId);
			if (sessionId == null) {
				return;
			}
			WebSocketSession session = idSessionMap.get(sessionId);
			if (session != null) {
				sendMessage(session, payLoad);
			} else {

			}
		});
	}

	public void notifyToListenSpeak(String playerId, String speakerId) {
		executorService.submit(() -> {
			CommonMO mo = new CommonMO();
			mo.setMsg("speaking");
			Map data = new HashMap();
			data.put("speakerId", speakerId);
			mo.setData(data);
			String payLoad = gson.toJson(mo);
			String sessionId = playerIdSessionIdMap.get(playerId);
			if (sessionId == null) {
				return;
			}
			WebSocketSession session = idSessionMap.get(sessionId);
			if (session != null) {
				sendMessage(session, payLoad);
			} else {

			}
		});
	}

	private void sendMessage(WebSocketSession session, String message) {
		synchronized (session) {
			try {
				session.sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Scheduled(cron = "0/10 * * * * ?")
	public void closeOTSessions() {
		sessionIdActivetimeMap.forEach((id, time) -> {
			if ((System.currentTimeMillis() - time) > (30 * 1000)) {
				WebSocketSession sessionToClose = idSessionMap.get(id);
				if (sessionToClose != null) {
					try {
						sessionToClose.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void closeSessionForPlayer(String playerId) {
		String sessionId = playerIdSessionIdMap.get(playerId);
		if (sessionId != null) {
			WebSocketSession session = idSessionMap.get(sessionId);
			if (session != null) {
				try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasSessionForPlayer(String playerId) {
		return playerIdSessionIdMap.get(playerId) != null;
	}

}
