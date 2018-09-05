package com.anbang.qipai.wenzhoumajiang.plan.dao;

import com.anbang.qipai.wenzhoumajiang.plan.bean.PlayerInfo;

public interface PlayerInfoDao {

	PlayerInfo findById(String playerId);

	void save(PlayerInfo playerInfo);
}
