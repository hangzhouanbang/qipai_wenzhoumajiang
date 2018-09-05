package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.JuResultDbo;

public interface JuResultDboDao {

	void save(JuResultDbo juResultDbo);

	JuResultDbo findByGameId(String gameId);

}
