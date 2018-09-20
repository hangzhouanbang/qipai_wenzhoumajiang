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

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.PlayerVotingWhenMaidi;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.VotingWhenMaidi;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.PlayerAuthService;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.service.MajiangGameQueryService;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.service.MajiangPlayQueryService;
import com.anbang.qipai.wenzhoumajiang.msg.service.WenzhouMajiangGameMsgService;
import com.dml.mpgame.game.Canceled;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.GameState;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.WaitingStart;
import com.dml.mpgame.game.extend.fpmpv.VotingWhenWaitingNextPan;
import com.dml.mpgame.game.extend.multipan.WaitingNextPan;
import com.dml.mpgame.game.extend.multipan.player.PlayerPanFinished;
import com.dml.mpgame.game.extend.multipan.player.PlayerReadyToStartNextPan;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.extend.vote.VotingWhenPlaying;
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

			GameState gameState = majiangGameDbo.getState();
			GamePlayerState playerState = majiangGameDbo.findPlayer(playerId).getState();

			if (gameState.name().equals(WaitingStart.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
			} else if (gameState.name().equals(Canceled.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
			} else if (gameState.name().equals(MaidiState.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.maidiState.name());
			} else if (gameState.name().equals(Playing.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.maidiState.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.panForMe.name());
			} else if (gameState.name().equals(VotingWhenPlaying.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.maidiState.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.panForMe.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.gameFinishVote.name());
			} else if (gameState.name().equals(VotingWhenMaidi.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.maidiState.name());
				if (playerState.name().equals(PlayerVotingWhenMaidi.name)) {
					wsNotifier.notifyToQuery(playerId, QueryScope.maidi.name());
				}
			} else if (gameState.name().equals(FinishedByVote.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.juResult.name());
			} else if (gameState.name().equals(WaitingNextPan.name)) {
				if (playerState.name().equals(PlayerPanFinished.name)) {
					wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
					wsNotifier.notifyToQuery(playerId, QueryScope.panResult.name());
				} else if (playerState.name().equals(PlayerReadyToStartNextPan.name)) {
					wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				}
			} else if (gameState.name().equals(VotingWhenWaitingNextPan.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.gameInfo.name());
				wsNotifier.notifyToQuery(playerId, QueryScope.gameFinishVote.name());
			} else if (gameState.name().equals(Finished.name)) {
				wsNotifier.notifyToQuery(playerId, QueryScope.juResult.name());
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
