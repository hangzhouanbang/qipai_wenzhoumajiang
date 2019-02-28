package com.anbang.qipai.wenzhoumajiang.msg.service;

import com.anbang.qipai.wenzhoumajiang.msg.channel.WatchRecordSource;
import com.anbang.qipai.wenzhoumajiang.msg.msjobj.CommonMO;
import com.dml.mpgame.game.watch.WatchRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author yins
 * @Description: 观战记录消息发送
 */
@EnableBinding(WatchRecordSource.class)
public class WatchRecordMsgService {
    @Autowired
    private WatchRecordSource watchRecordSource;

    public void joinWatch(String gameId, String playerId) {
        CommonMO mo = new CommonMO();
        mo.setMsg("joinWatch");
        WatchRecord watchRecord = new WatchRecord();
        watchRecord.setId(gameId);
        watchRecord.setPlayerId(playerId);
        watchRecord.setJoinTime(System.currentTimeMillis());
        mo.setData(watchRecord);
        watchRecordSource.watchRecordSink().send(MessageBuilder.withPayload(mo).build());
    }

    public void leaveWatch(String gameId, String playerId) {
        CommonMO mo = new CommonMO();
        mo.setMsg("leaveWatch");
        WatchRecord watchRecord = new WatchRecord();
        watchRecord.setId(gameId);
        watchRecord.setPlayerId(playerId);
        watchRecord.setLeaveTime(System.currentTimeMillis());
        watchRecordSource.watchRecordSink().send(MessageBuilder.withPayload(mo).build());
    }
}
