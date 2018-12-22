package com.anbang.qipai.wenzhoumajiang.cqrs.c.service;

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

	MajiangGameValueObject finish(String playerId, Long currentTime) throws Exception;

	MajiangGameValueObject voteToFinish(String playerId, Boolean yes) throws Exception;

	MajiangGameValueObject voteToFinishByTimeOver(String playerId, Long currentTime) throws Exception;

	GameValueObject finishGameImmediately(String gameId) throws Exception;

	MajiangGameValueObject leaveGameByOffline(String playerId) throws Exception;

	MajiangGameValueObject leaveGameByHangup(String playerId) throws Exception;
}
