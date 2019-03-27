package com.anbang.qipai.wenzhoumajiang.cqrs.c.service;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangActionResult;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.ReadyToNextPanResult;

public interface MajiangPlayCmdService {
	MajiangGameValueObject xipai(String playerId) throws Exception;

	MajiangActionResult action(String playerId, Integer actionId, Integer actionNo, Long actionTime) throws Exception;

	ReadyToNextPanResult readyToNextPan(String playerId) throws Exception;

	MaidiResult maidi(String playerId, Boolean state) throws Exception;

}
