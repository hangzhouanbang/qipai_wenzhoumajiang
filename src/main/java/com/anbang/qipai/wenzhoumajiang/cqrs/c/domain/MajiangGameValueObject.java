package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dml.majiang.ju.result.JuResult;
import com.dml.mpgame.game.extend.fpmpv.FixedPlayersMultipanAndVotetofinishGameValueObject;

public class MajiangGameValueObject extends FixedPlayersMultipanAndVotetofinishGameValueObject {
	private int panshu;
	private int renshu;
	private boolean jinjie1;
	private boolean jinjie2;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean queyise;// 缺一色
	private boolean lazila;
	private boolean gangsuanfen;
	private Map<String, Integer> playerLianZhuangCountMap = new HashMap<>();
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap;
	private Map<String, Integer> playeTotalScoreMap = new HashMap<>();
	private Set<String> xipaiPlayerIds = new HashSet<>();
	private JuResult juResult;

	public MajiangGameValueObject(MajiangGame majiangGame) {
		super(majiangGame);
		playerLianZhuangCountMap = majiangGame.getPlayerLianZhuangCountMap();
		panshu = majiangGame.getPanshu();
		renshu = majiangGame.getRenshu();
		jinjie1 = majiangGame.isJinjie1();
		jinjie2 = majiangGame.isJinjie2();
		teshushuangfan = majiangGame.isTeshushuangfan();
		caishenqian = majiangGame.isCaishenqian();
		shaozhongfa = majiangGame.isShaozhongfa();
		queyise = majiangGame.isQueyise();
		lazila = majiangGame.isLazila();
		gangsuanfen = majiangGame.isGangsuanfen();
		playerMaidiStateMap = majiangGame.getPlayerMaidiStateMap();
		playeTotalScoreMap.putAll(majiangGame.getPlayeTotalScoreMap());
		xipaiPlayerIds = new HashSet<>(majiangGame.getXipaiPlayerIds());
		if (majiangGame.getJu() != null) {
			juResult = majiangGame.getJu().getJuResult();
		}
	}

	public boolean isQueyise() {
		return queyise;
	}

	public void setQueyise(boolean queyise) {
		this.queyise = queyise;
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

	public boolean isGangsuanfen() {
		return gangsuanfen;
	}

	public void setGangsuanfen(boolean gangsuanfen) {
		this.gangsuanfen = gangsuanfen;
	}

	public Map<String, MajiangGamePlayerMaidiState> getPlayerMaidiStateMap() {
		return playerMaidiStateMap;
	}

	public void setPlayerMaidiStateMap(Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		this.playerMaidiStateMap = playerMaidiStateMap;
	}

	public Map<String, Integer> getPlayeTotalScoreMap() {
		return playeTotalScoreMap;
	}

	public Set<String> getXipaiPlayerIds() {
		return xipaiPlayerIds;
	}

	public void setXipaiPlayerIds(Set<String> xipaiPlayerIds) {
		this.xipaiPlayerIds = xipaiPlayerIds;
	}

	public void setPlayeTotalScoreMap(Map<String, Integer> playeTotalScoreMap) {
		this.playeTotalScoreMap = playeTotalScoreMap;
	}

	public JuResult getJuResult() {
		return juResult;
	}

	public void setJuResult(JuResult juResult) {
		this.juResult = juResult;
	}

	public Map<String, Integer> getPlayerLianZhuangCountMap() {
		return playerLianZhuangCountMap;
	}

	public void setPlayerLianZhuangCountMap(Map<String, Integer> playerLianZhuangCountMap) {
		this.playerLianZhuangCountMap = playerLianZhuangCountMap;
	}

}
