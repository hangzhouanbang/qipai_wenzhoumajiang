package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao;

import java.util.Map;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGamePlayerMaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGamePlayerMaidiDbo;

public interface MajiangGamePlayerMaidiDboDao {

	void addMajiangGamePlayerMaidiDbo(MajiangGamePlayerMaidiDbo dbo);

	void updateMajiangGamePlayerMaidiDbo(String gameId, int panNo,
			Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap);

	MajiangGamePlayerMaidiDbo findLastByGameId(String gameId);

	MajiangGamePlayerMaidiDbo findByGameIdAndPanNo(String gameId, int panNo);
}
