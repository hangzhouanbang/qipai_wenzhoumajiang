package com.anbang.qipai.wenzhoumajiang.cqrs.q.dao;

import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.GameLatestPanActionFrameDbo;

public interface GameLatestPanActionFrameDboDao {

	GameLatestPanActionFrameDbo findById(String id);

	void save(String id, byte[] data);

}
