package com.anbang.qipai.wenzhoumajiang.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dml.mpgame.game.watch.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.PlayerAuthService;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.service.MajiangGameQueryService;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.service.MajiangPlayQueryService;
import com.anbang.qipai.wenzhoumajiang.msg.msjobj.MajiangHistoricalJuResult;
import com.anbang.qipai.wenzhoumajiang.msg.service.WenzhouMajiangGameMsgService;
import com.anbang.qipai.wenzhoumajiang.msg.service.WenzhouMajiangResultMsgService;
import com.dml.mpgame.game.Canceled;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.GameState;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.player.GamePlayerState;
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
	private MajiangPlayQueryService majiangPlayQueryService;

	@Autowired
	private WenzhouMajiangGameMsgService gameMsgService;

	@Autowired
	private WenzhouMajiangResultMsgService wenzhouMajiangResultMsgService;

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
		wsNotifier.removeSession(session.getId());
		// 有可能断的只是一个已经废弃了的session，新的session已经建立。这个时候其实不是leave的
		if (wsNotifier.hasSessionForPlayer(closedPlayerId)) {
			return;
		}
		MajiangGameValueObject majiangGameValueObject = gameCmdService.leaveGameByOffline(closedPlayerId);
		if (majiangGameValueObject != null) {
			majiangGameQueryService.leaveGame(majiangGameValueObject);
			gameMsgService.gamePlayerLeave(majiangGameValueObject, closedPlayerId);

			String gameId = majiangGameValueObject.getId();
			if (majiangGameValueObject.getState().name().equals(FinishedByVote.name)
					|| majiangGameValueObject.getState().name().equals(Canceled.name)
					|| majiangGameValueObject.getState().name().equals(Finished.name)) {
				JuResultDbo juResultDbo = majiangPlayQueryService.findJuResultDbo(gameId);
				if (juResultDbo != null) {
					MajiangGameDbo majiangGameDbo = majiangGameQueryService.findMajiangGameDboById(gameId);
					MajiangHistoricalJuResult juResult = new MajiangHistoricalJuResult(juResultDbo, majiangGameDbo);
					wenzhouMajiangResultMsgService.recordJuResult(juResult);
				}
				gameMsgService.gameFinished(gameId);
			}
			// 通知其他人
			for (String otherPlayerId : majiangGameValueObject.allPlayerIds()) {
				if (!otherPlayerId.equals(closedPlayerId)) {
					List<QueryScope> scopes = QueryScope.scopesForState(majiangGameValueObject.getState(),
							majiangGameValueObject.findPlayerState(otherPlayerId));
					scopes.remove(QueryScope.panResult);
					wsNotifier.notifyToQuery(otherPlayerId, scopes);
				}
			}
		}

		//心跳停止时踢出观战者

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

		//查询观战信息
		Map<String, Watcher> watcherMap = gameCmdService.getwatch(gameId);
		if (!CollectionUtils.isEmpty(watcherMap) && watcherMap.containsKey(playerId)) {
			List<String> playerIds = new ArrayList<>();
			playerIds.add(playerId);
			wsNotifier.notifyToWatchQuery(playerIds,"query");
			return;
		}

		// 给用户安排query scope
		MajiangGameDbo majiangGameDbo = majiangGameQueryService.findMajiangGameDboById(gameId);
		if (majiangGameDbo != null) {

			GameState gameState = majiangGameDbo.getState();
			GamePlayerState playerState = majiangGameDbo.findPlayer(playerId).getState();

			List<QueryScope> scopes = QueryScope.scopesForState(gameState, playerState);
			wsNotifier.notifyToQuery(playerId, scopes);
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
