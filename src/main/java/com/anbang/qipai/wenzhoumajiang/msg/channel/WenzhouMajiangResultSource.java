package com.anbang.qipai.wenzhoumajiang.msg.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface WenzhouMajiangResultSource {
	@Output
	MessageChannel wenzhouMajiangResult();
}
