package com.anbang.qipai.wenzhoumajiang.cqrs.c.service;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.FinishResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyForGameResult;
import com.dml.mpgame.game.GameValueObject;

public interface GameCmdService {

	MajiangGameValueObject newMajiangGame(String gameId, String playerId, Integer panshu, Integer renshu,
			Boolean jinjie1, Boolean jinjie2, Boolean teshushuangfan, Boolean caishenqian, Boolean shaozhongfa,
			Boolean lazila, Boolean gangsuanfen);

	MajiangGameValueObject leaveGame(String playerId) throws Exception;

	ReadyForGameResult readyForGame(String playerId, Long currentTime) throws Exception;

	MajiangGameValueObject joinGame(String playerId, String gameId) throws Exception;

	MajiangGameValueObject backToGame(String playerId, String gameId) throws Exception;

	void bindPlayer(String playerId, String gameId);

	FinishResult finish(String playerId) throws Exception;

	FinishResult voteToFinish(String playerId, Boolean yes) throws Exception;

	GameValueObject finishGameImmediately(String gameId) throws Exception;
}
