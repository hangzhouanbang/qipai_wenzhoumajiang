package com.anbang.qipai.wenzhoumajiang.cqrs.c.service;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangActionResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyToNextPanResult;

public interface MajiangPlayCmdService {

	MajiangActionResult action(String playerId, Integer actionId, Long actionTime) throws Exception;

	ReadyToNextPanResult readyToNextPan(String playerId) throws Exception;

	MaidiResult maidi(String playerId, Boolean state) throws Exception;

}
