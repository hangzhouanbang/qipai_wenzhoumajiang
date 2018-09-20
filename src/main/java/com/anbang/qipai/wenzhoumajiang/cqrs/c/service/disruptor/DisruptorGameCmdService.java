package com.anbang.qipai.wenzhoumajiang.cqrs.c.service.disruptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyForGameResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.GameCmdService;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.impl.GameCmdServiceImpl;
import com.dml.mpgame.game.GameValueObject;
import com.highto.framework.concurrent.DeferredResult;
import com.highto.framework.ddd.CommonCommand;

@Component(value = "gameCmdService")
public class DisruptorGameCmdService extends DisruptorCmdServiceBase implements GameCmdService {

	@Autowired
	private GameCmdServiceImpl gameCmdServiceImpl;

	@Override
	public MajiangGameValueObject newMajiangGame(String gameId, String playerId, Integer panshu, Integer renshu,
			Boolean jinjie1, Boolean jinjie2, Boolean teshushuangfan, Boolean caishenqian, Boolean shaozhongfa,
			Boolean lazila, Boolean gangsuanfen) {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "newMajiangGame", gameId, playerId,
				panshu, renshu, jinjie1, jinjie2, teshushuangfan, caishenqian, shaozhongfa, lazila, gangsuanfen);
		DeferredResult<MajiangGameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd,
				() -> {
					MajiangGameValueObject majiangGameValueObject = gameCmdServiceImpl.newMajiangGame(
							cmd.getParameter(), cmd.getParameter(), cmd.getParameter(), cmd.getParameter(),
							cmd.getParameter(), cmd.getParameter(), cmd.getParameter(), cmd.getParameter(),
							cmd.getParameter(), cmd.getParameter(), cmd.getParameter());
					return majiangGameValueObject;
				});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MajiangGameValueObject leaveGame(String playerId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "leaveGame", playerId);
		DeferredResult<MajiangGameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd,
				() -> {
					MajiangGameValueObject majiangGameValueObject = gameCmdServiceImpl.leaveGame(cmd.getParameter());
					return majiangGameValueObject;
				});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ReadyForGameResult readyForGame(String playerId, Long currentTime) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "readyForGame", playerId,
				currentTime);
		DeferredResult<ReadyForGameResult> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			ReadyForGameResult readyForGameResult = gameCmdServiceImpl.readyForGame(cmd.getParameter(),
					cmd.getParameter());
			return readyForGameResult;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public MajiangGameValueObject joinGame(String playerId, String gameId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "joinGame", playerId, gameId);
		DeferredResult<MajiangGameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd,
				() -> {
					MajiangGameValueObject majiangGameValueObject = gameCmdServiceImpl.joinGame(cmd.getParameter(),
							cmd.getParameter());
					return majiangGameValueObject;
				});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public MajiangGameValueObject backToGame(String playerId, String gameId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "backToGame", playerId, gameId);
		DeferredResult<MajiangGameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd,
				() -> {
					MajiangGameValueObject majiangGameValueObject = gameCmdServiceImpl.backToGame(cmd.getParameter(),
							cmd.getParameter());
					return majiangGameValueObject;
				});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void bindPlayer(String playerId, String gameId) {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "bindPlayer", playerId, gameId);
		DeferredResult<Object> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			gameCmdServiceImpl.bindPlayer(cmd.getParameter(), cmd.getParameter());
			return null;
		});
		try {
			result.getResult();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MajiangGameValueObject finish(String playerId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "finish", playerId);
		DeferredResult<MajiangGameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd,
				() -> {
					MajiangGameValueObject majiangGameValueObject = gameCmdServiceImpl.finish(cmd.getParameter());
					return majiangGameValueObject;
				});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public MajiangGameValueObject voteToFinish(String playerId, Boolean yes) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "voteToFinish", playerId, yes);
		DeferredResult<MajiangGameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd,
				() -> {
					MajiangGameValueObject majiangGameValueObject = gameCmdServiceImpl.voteToFinish(cmd.getParameter(),
							cmd.getParameter());
					return majiangGameValueObject;
				});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public GameValueObject finishGameImmediately(String gameId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameCmdServiceImpl.class.getName(), "finishGameImmediately", gameId);
		DeferredResult<GameValueObject> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			GameValueObject gameValueObject = gameCmdServiceImpl.finishGameImmediately(cmd.getParameter());
			return gameValueObject;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

}
