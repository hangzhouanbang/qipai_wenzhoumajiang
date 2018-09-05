package com.anbang.qipai.wenzhoumajiang.msg.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MajiangGameValueObject;
import com.anbang.qipai.wenzhoumajiang.msg.channel.WenzhouMajiangGameSource;
import com.anbang.qipai.wenzhoumajiang.msg.msjobj.CommonMO;
import com.dml.majiang.pan.frame.PanValueObject;

@EnableBinding(WenzhouMajiangGameSource.class)
public class WenzhouMajiangGameMsgService {

	@Autowired
	private WenzhouMajiangGameSource wenzhouMajiangGameSource;

	public void gamePlayerLeave(MajiangGameValueObject majiangGameValueObject, String playerId) {
		boolean playerIsQuit = true;
		for (String pid : majiangGameValueObject.allPlayerIds()) {
			if (pid.equals(playerId)) {
				playerIsQuit = false;
				break;
			}
		}
		if (playerIsQuit) {
			CommonMO mo = new CommonMO();
			mo.setMsg("playerQuit");
			Map data = new HashMap();
			data.put("gameId", majiangGameValueObject.getGameId());
			data.put("playerId", playerId);
			mo.setData(data);
			wenzhouMajiangGameSource.wenzhouMajiangGame().send(MessageBuilder.withPayload(mo).build());
		}
	}

	public void gameFinished(String gameId) {
		CommonMO mo = new CommonMO();
		mo.setMsg("ju finished");
		Map data = new HashMap();
		data.put("gameId", gameId);
		mo.setData(data);
		wenzhouMajiangGameSource.wenzhouMajiangGame().send(MessageBuilder.withPayload(mo).build());
	}

	public void panFinished(MajiangGameValueObject majiangGameValueObject, PanValueObject panAfterAction) {
		CommonMO mo = new CommonMO();
		mo.setMsg("pan finished");
		Map data = new HashMap();
		data.put("gameId", majiangGameValueObject.getGameId());
		data.put("no", panAfterAction.getNo());
		data.put("playerIds", majiangGameValueObject.allPlayerIds());
		mo.setData(data);
		wenzhouMajiangGameSource.wenzhouMajiangGame().send(MessageBuilder.withPayload(mo).build());
	}
}
