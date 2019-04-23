package com.anbang.qipai.wenzhoumajiang.web.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.MaidiState;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.VoteNotPassWhenMaidi;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.VotingWhenMaidi;
import com.anbang.qipai.wenzhoumajiang.cqrs.q.dbo.MajiangGameDbo;
import com.dml.mpgame.game.Canceled;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.WaitingStart;
import com.dml.mpgame.game.extend.fpmpv.VoteNotPassWhenWaitingNextPan;
import com.dml.mpgame.game.extend.fpmpv.VotingWhenWaitingNextPan;
import com.dml.mpgame.game.extend.multipan.WaitingNextPan;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.extend.vote.VoteNotPassWhenPlaying;
import com.dml.mpgame.game.extend.vote.VotingWhenPlaying;

public class GameVO {
	private String id;// 就是gameid
	private int panshu;
	private int renshu;
	private boolean jinjie1;
	private boolean jinjie2;
	private boolean gangsuanfen;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean queyise;// 缺一色
	private boolean lazila;
	private int panNo;
	private Map<String, Integer> playerLianZhuangCountMap;
	private List<MajiangGamePlayerVO> playerList;
	private Set<String> xipaiPlayerIds;
	private String state;

	public GameVO(MajiangGameDbo majiangGameDbo) {
		id = majiangGameDbo.getId();
		playerLianZhuangCountMap = majiangGameDbo.getPlayerLianZhuangCountMap();
		panshu = majiangGameDbo.getPanshu();
		renshu = majiangGameDbo.getRenshu();
		jinjie1 = majiangGameDbo.isJinjie1();
		jinjie2 = majiangGameDbo.isJinjie2();
		gangsuanfen = majiangGameDbo.isGangsuanfen();
		teshushuangfan = majiangGameDbo.isTeshushuangfan();
		caishenqian = majiangGameDbo.isCaishenqian();
		shaozhongfa = majiangGameDbo.isShaozhongfa();
		queyise = majiangGameDbo.isQueyise();
		lazila = majiangGameDbo.isLazila();
		xipaiPlayerIds = majiangGameDbo.getXipaiPlayerIds();
		playerList = new ArrayList<>();
		majiangGameDbo.getPlayers().forEach((dbo) -> playerList.add(new MajiangGamePlayerVO(dbo)));
		panNo = majiangGameDbo.getPanNo();
		String sn = majiangGameDbo.getState().name();
		if (sn.equals(Canceled.name)) {
			state = "canceled";
		} else if (sn.equals(Finished.name)) {
			state = "finished";
		} else if (sn.equals(FinishedByVote.name)) {
			state = "finishedbyvote";
		} else if (sn.equals(Playing.name)) {
			state = "playing";
		} else if (sn.equals(VotingWhenPlaying.name)) {
			state = "playing";
		} else if (sn.equals(VoteNotPassWhenPlaying.name)) {
			state = "playing";
		} else if (sn.equals(VotingWhenWaitingNextPan.name)) {
			state = "waitingNextPan";
		} else if (sn.equals(VoteNotPassWhenWaitingNextPan.name)) {
			state = "waitingNextPan";
		} else if (sn.equals(WaitingNextPan.name)) {
			state = "waitingNextPan";
		} else if (sn.equals(WaitingStart.name)) {
			state = "waitingStart";
		} else if (sn.equals(MaidiState.name)) {
			state = "maidi";
		} else if (sn.equals(VotingWhenMaidi.name)) {
			state = "maidi";
		} else if (sn.equals(VoteNotPassWhenMaidi.name)) {
			state = "maidi";
		} else {
		}
	}

	public boolean isQueyise() {
		return queyise;
	}

	public void setQueyise(boolean queyise) {
		this.queyise = queyise;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPanshu() {
		return panshu;
	}

	public void setPanshu(int panshu) {
		this.panshu = panshu;
	}

	public int getRenshu() {
		return renshu;
	}

	public void setRenshu(int renshu) {
		this.renshu = renshu;
	}

	public boolean isJinjie1() {
		return jinjie1;
	}

	public void setJinjie1(boolean jinjie1) {
		this.jinjie1 = jinjie1;
	}

	public boolean isJinjie2() {
		return jinjie2;
	}

	public void setJinjie2(boolean jinjie2) {
		this.jinjie2 = jinjie2;
	}

	public boolean isGangsuanfen() {
		return gangsuanfen;
	}

	public void setGangsuanfen(boolean gangsuanfen) {
		this.gangsuanfen = gangsuanfen;
	}

	public boolean isTeshushuangfan() {
		return teshushuangfan;
	}

	public void setTeshushuangfan(boolean teshushuangfan) {
		this.teshushuangfan = teshushuangfan;
	}

	public boolean isCaishenqian() {
		return caishenqian;
	}

	public void setCaishenqian(boolean caishenqian) {
		this.caishenqian = caishenqian;
	}

	public boolean isShaozhongfa() {
		return shaozhongfa;
	}

	public void setShaozhongfa(boolean shaozhongfa) {
		this.shaozhongfa = shaozhongfa;
	}

	public boolean isLazila() {
		return lazila;
	}

	public void setLazila(boolean lazila) {
		this.lazila = lazila;
	}

	public Map<String, Integer> getPlayerLianZhuangCountMap() {
		return playerLianZhuangCountMap;
	}

	public void setPlayerLianZhuangCountMap(Map<String, Integer> playerLianZhuangCountMap) {
		this.playerLianZhuangCountMap = playerLianZhuangCountMap;
	}

	public List<MajiangGamePlayerVO> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<MajiangGamePlayerVO> playerList) {
		this.playerList = playerList;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getPanNo() {
		return panNo;
	}

	public void setPanNo(int panNo) {
		this.panNo = panNo;
	}

	public Set<String> getXipaiPlayerIds() {
		return xipaiPlayerIds;
	}

	public void setXipaiPlayerIds(Set<String> xipaiPlayerIds) {
		this.xipaiPlayerIds = xipaiPlayerIds;
	}

}
