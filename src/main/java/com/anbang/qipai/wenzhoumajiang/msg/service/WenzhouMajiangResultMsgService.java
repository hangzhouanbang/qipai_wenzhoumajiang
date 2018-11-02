package com.anbang.qipai.wenzhoumajiang.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.wenzhoumajiang.msg.channel.WenzhouMajiangResultSource;
import com.anbang.qipai.wenzhoumajiang.msg.msjobj.CommonMO;
import com.anbang.qipai.wenzhoumajiang.msg.msjobj.MajiangHistoricalPanResult;
import com.anbang.qipai.wenzhoumajiang.msg.msjobj.MajiangHistoricalJuResult;

@EnableBinding(WenzhouMajiangResultSource.class)
public class WenzhouMajiangResultMsgService {

	@Autowired
	private WenzhouMajiangResultSource wenzhouMajiangResultSource;

	public void recordJuResult(MajiangHistoricalJuResult juResult) {
		CommonMO mo = new CommonMO();
		mo.setMsg("wenzhoumajiang ju result");
		mo.setData(juResult);
		wenzhouMajiangResultSource.wenzhouMajiangResult().send(MessageBuilder.withPayload(mo).build());
	}

	public void recordPanResult(MajiangHistoricalPanResult panResult) {
		CommonMO mo = new CommonMO();
		mo.setMsg("wenzhoumajiang pan result");
		mo.setData(panResult);
		wenzhouMajiangResultSource.wenzhouMajiangResult().send(MessageBuilder.withPayload(mo).build());
	}
}
