package com.anbang.qipai.wenzhoumajiang.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.PlayerAuthService;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.service.MajiangGameQueryService;
import com.anbang.qipai.wenzhoumajiang.msg.service.WenzhouMajiangGameMsgService;
import com.google.gson.Gson;

@Component
public class GamePlayWsController extends TextWebSocketHandler {
	@Autowired
	private GamePlayWsNotifier wsNotifier;

	@Autowired
	private PlayerAuthService playerAuthService;

	@Autowired
	private GameCmdService gameCmdService;

	@Autowired
	private MajiangGameQueryService majiangGameQueryService;

	@Autowired
	private WenzhouMajiangGameMsgService gameMsgService;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Gson gson = new Gson();

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		executorService.submit(() -> {
			CommonMO mo = gson.fromJson(message.getPayload(), CommonMO.class);
			String msg = mo.getMsg();
			if ("bindPlayer".equals(msg)) {// 绑定玩家
				processBindPlayer(session, mo.getData());
			}
			if ("heartbeat".equals(msg)) {// 心跳
				processHeartbeat(session, mo.getData());
			} else {
			}
		});

	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		wsNotifier.addSession(session);
		CommonMO mo = new CommonMO();
		mo.setMsg("bindPlayer");
		sendMessage(session, gson.toJson(mo));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String closedPlayerId = wsNotifier.findPlayerIdBySessionId(session.getId());
		// TODO 测试代码
		System.out.println("连接断了 <" + closedPlayerId + "> (" + System.currentTimeMillis() + ")");
		wsNotifier.removeSession(session.getId());
		MajiangGameValueObject majiangGameValueObject = gameCmdService.leaveGame(closedPlayerId);
		if (majiangGameValueObject != null) {
			majiangGameQueryService.leaveGame(majiangGameValueObject);
			gameMsgService.gamePlayerLeave(majiangGameValueObject, closedPlayerId);
			// 通知其他玩家
			majiangGameValueObject.allPlayerIds().forEach((playerId) -> {
				if (!playerId.equals(closedPlayerId)) {
					wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
					// TODO 测试代码
					System.out.println("通知 ConnectionClosed <" + playerId + "> (" + System.currentTimeMillis() + ")");
				}
			});
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable error) throws Exception {
		executorService.submit(() -> {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		error.printStackTrace();
	}

	/**
	 * 绑定玩家
	 * 
	 * @param session
	 * @param data
	 */
	private void processBindPlayer(WebSocketSession session, Object data) {
		Map map = (Map) data;
		String token = (String) map.get("token");
		String gameId = (String) map.get("gameId");
		if (token == null) {// 非法访问
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String playerId = playerAuthService.getPlayerIdByToken(token);
		if (playerId == null) {// 非法的token
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		wsNotifier.bindPlayer(session.getId(), playerId);
		gameCmdService.bindPlayer(playerId, gameId);
		// 给用户安排query scope
		MajiangGameDbo majiangGameDbo = majiangGameQueryService.findMajiangGameDboById(gameId);
		if (majiangGameDbo != null) {
			if (!majiangGameDbo.getState().equals(MajiangGameState.finished)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				if (majiangGameDbo.getState().equals(MajiangGameState.playing)) {
					wsNotifier.notifyToQuery(playerId, QueryScope.panForMe.name());
				}
			}
		}

	}

	/**
	 * 心跳
	 *
	 * @param session
	 * @param data
	 */
	private void processHeartbeat(WebSocketSession session, Object data) {
		Map map = (Map) data;
		String token = (String) map.get("token");
		if (token == null) {// 非法访问
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String playerId = playerAuthService.getPlayerIdByToken(token);
		if (playerId == null) {// 非法的token
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		wsNotifier.updateSession(session.getId());
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
}
