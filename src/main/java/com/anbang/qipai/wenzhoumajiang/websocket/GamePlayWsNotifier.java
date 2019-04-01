package com.anbang.qipai.wenzhoumajiang.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger logger = LoggerFactory.getLogger(getClass());

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
		long bindTime = System.currentTimeMillis();
		logger.info("bindPlayer,bindTime:" + bindTime + ",playerId:" + playerId + ",sessionId:" + sessionId);
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

	public void notifyToQuery(String playerId, List<QueryScope> scopes) {
		executorService.submit(() -> {
			for (QueryScope scope : scopes) {
				CommonMO mo = new CommonMO();
				mo.setMsg("query");
				Map data = new HashMap();
				data.put("scope", scope.name());
				mo.setData(data);
				String payLoad = gson.toJson(mo);
				String sessionId = playerIdSessionIdMap.get(playerId);
				if (sessionId == null) {
					return;
				}
				WebSocketSession session = idSessionMap.get(sessionId);
				if (session != null) {
					long notifyTime = System.currentTimeMillis();
					sendMessage(session, payLoad);
					long endTime = System.currentTimeMillis();
					logger.info("notifyToQuery,notifyTime:" + notifyTime + ",endTime:" + endTime + ",playerId:"
							+ playerId + ",scope:" + scope + ",use:" + (endTime - notifyTime) + "ms");
				} else {

				}
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

	public void notifyToListenSpeak(String playerId, String wordId, String speakerId, boolean isPlayer) {
		executorService.submit(() -> {
			CommonMO mo = new CommonMO();
			mo.setMsg("speaking");
			Map data = new HashMap();
			data.put("wordId", wordId);
			data.put("speakerId", speakerId);
			data.put("isPlayer", isPlayer);
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

	/**
	 * 进入离开观战
	 * 
	 * @param key
	 *            input(进入) leave(离开)
	 * @param playerId
	 *            接收方id
	 */
	public void notifyWatchInfo(String playerId, String key, String id, String watcher, String headimgurl) {
		executorService.submit(() -> {
			CommonMO mo = new CommonMO();
			mo.setMsg("watcher");
			Map data = new HashMap();
			data.put("key", key);
			data.put("id", id);
			data.put("watcher", watcher);
			data.put("headimgurl", headimgurl);
			data.put("scope", "watcher");
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

	/**
	 * 通知观战者
	 */
	public void notifyToWatchQuery(List<String> playerIds, String flag) {
		executorService.submit(() -> {
			for (String playerId : playerIds) {
				for (WatchQueryScope list : WatchQueryScope.getQueryList(flag)) {
					CommonMO mo = new CommonMO();
					mo.setMsg("watch query");
					Map data = new HashMap();
					data.put("scope", list.name());
					mo.setData(data);
					String payLoad = gson.toJson(mo);
					String sessionId = playerIdSessionIdMap.get(playerId);
					if (sessionId == null) {
						continue;
					}
					WebSocketSession session = idSessionMap.get(sessionId);
					if (session != null) {
						sendMessage(session, payLoad);
					}
				}
			}
		});
	}

	// /**
	// * 观战结束(待废弃)
	// */
	// public void notifyToWatchEnd(List<String> playerIds) {
	// executorService.submit(() -> {
	// for (String playerId : playerIds) {
	// CommonMO mo = new CommonMO();
	// mo.setMsg("watch end");
	// Map data = new HashMap();
	// data.put("scope","watch end");
	// mo.setData(data);
	// String payLoad = gson.toJson(mo);
	// String sessionId = playerIdSessionIdMap.get(playerId);
	// if (sessionId == null) {
	// continue;
	// }
	// WebSocketSession session = idSessionMap.get(sessionId);
	// if (session != null) {
	// sendMessage(session, payLoad);
	// }
	// }
	// });
	// }

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
		return playerIdSessionIdMap.containsKey(playerId);
	}

}
