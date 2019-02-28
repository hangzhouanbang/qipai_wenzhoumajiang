package com.anbang.qipai.wenzhoumajiang.cqrs.c.service.impl;

import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangActionResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGame;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyToNextPanResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.VoteNotPassWhenMaidi;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.exception.GameNotMaiDi;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.MajiangPlayCmdService;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.player.PlayerNotInGameException;
import com.dml.mpgame.server.GameServer;

@Component
public class MajiangPlayCmdServiceImpl extends CmdServiceBase implements MajiangPlayCmdService {

	@Override
	public MajiangActionResult action(String playerId, Integer actionId, Integer actionNo, Long actionTime)
			throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		String gameId = gameServer.findBindGameId(playerId);
		if (gameId == null) {
			throw new PlayerNotInGameException();
		}

		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);
		MajiangActionResult majiangActionResult = majiangGame.action(playerId, actionId, actionNo, actionTime);

		if (majiangActionResult.getJuResult() != null) {// 全部结束
			gameServer.finishGame(gameId);
		}

		return majiangActionResult;
	}

	@Override
	public ReadyToNextPanResult readyToNextPan(String playerId) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		String gameId = gameServer.findBindGameId(playerId);
		if (gameId == null) {
			throw new PlayerNotInGameException();
		}
		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);

		ReadyToNextPanResult readyToNextPanResult = new ReadyToNextPanResult();
		majiangGame.readyToNextPan(playerId);
		if (majiangGame.getState().name().equals(Playing.name)) {// 开始下一盘了
			PanActionFrame firstActionFrame = majiangGame.getJu().getCurrentPan().findLatestActionFrame();
			readyToNextPanResult.setFirstActionFrame(firstActionFrame);
		}
		readyToNextPanResult.setMajiangGame(new MajiangGameValueObject(majiangGame));
		return readyToNextPanResult;
	}

	@Override
	public MaidiResult maidi(String playerId, Boolean state) throws Exception {
		GameServer gameServer = singletonEntityRepository.getEntity(GameServer.class);
		String gameId = gameServer.findBindGameId(playerId);
		if (gameId == null) {
			throw new PlayerNotInGameException();
		}
		MajiangGame majiangGame = (MajiangGame) gameServer.findGame(gameId);
		if (!(majiangGame.getState().name().equals(MaidiState.name)
				|| majiangGame.getState().name().equals(VoteNotPassWhenMaidi.name))) {
			throw new GameNotMaiDi();
		}
		MaidiResult maidiResult = majiangGame.maidi(playerId, state);
		return maidiResult;
	}

}
