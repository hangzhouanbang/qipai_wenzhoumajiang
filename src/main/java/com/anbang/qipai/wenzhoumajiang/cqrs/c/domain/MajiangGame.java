package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anbang.qipai.wenzhoumajiang.cqrs.c.domain.listener.WenzhouMajiangChiPengGangActionStatisticsListener;
import com.dml.majiang.ju.Ju;
import com.dml.majiang.ju.finish.FixedPanNumbersJuFinishiDeterminer;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.pan.guipai.RandomGuipaiDeterminer;
import com.dml.majiang.pan.publicwaitingplayer.WaitDaPlayerPanPublicWaitingPlayerDeterminer;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.action.chi.PengganghuFirstBuChiActionProcessor;
import com.dml.majiang.player.action.gang.HuFirstBuGangActionProcessor;
import com.dml.majiang.player.action.guo.DoNothingGuoActionProcessor;
import com.dml.majiang.player.action.hu.PlayerHuAndClearAllActionHuActionUpdater;
import com.dml.majiang.player.action.hu.PlayerSetHuHuActionProcessor;
import com.dml.majiang.player.action.initial.ZhuangMoPaiInitialActionUpdater;
import com.dml.majiang.player.action.listener.comprehensive.GuoPengBuPengStatisticsListener;
import com.dml.majiang.player.action.listener.comprehensive.TianHuAndDihuOpportunityDetector;
import com.dml.majiang.player.action.listener.mo.MoGuipaiCounter;
import com.dml.majiang.player.action.peng.HuFirstBuPengActionProcessor;
import com.dml.majiang.player.menfeng.RandomMustHasDongPlayersMenFengDeterminer;
import com.dml.majiang.player.shoupaisort.BaibanDangGuipaiBenpaiShoupaiSortComparator;
import com.dml.majiang.player.zhuang.MenFengDongZhuangDeterminer;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.extend.fpmpv.FixedPlayersMultipanAndVotetofinishGame;
import com.dml.mpgame.game.extend.multipan.WaitingNextPan;
import com.dml.mpgame.game.extend.vote.VoteNotPassWhenPlaying;
import com.dml.mpgame.game.player.GamePlayer;
import com.dml.mpgame.game.player.PlayerPlaying;

public class MajiangGame extends FixedPlayersMultipanAndVotetofinishGame {
	private int panshu;
	private int renshu;
	private boolean jinjie1;
	private boolean jinjie2;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean shaozhongfa;
	private boolean lazila;
	private boolean gangsuanfen;
	private Ju ju;
	private Map<String, Integer> playerLianZhuangCountMap = new HashMap<>();
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = new HashMap<>();
	private Map<String, Integer> playeTotalScoreMap = new HashMap<>();

	public MaidiResult maidi(String playerId, boolean maidiState) throws Exception {
		MaidiResult maidiResult = new MaidiResult();
		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = (WenzhouMajiangPanResultBuilder) ju
				.getCurrentPanResultBuilder();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = wenzhouMajiangPanResultBuilder
				.getPlayerMaidiStateMap();
		String zhuangPlayerId = ju.getCurrentPan().getZhuangPlayerId();
		List<String> playerIdList = new ArrayList<>(playerMaidiStateMap.keySet());
		if (maidiState) {
			if (zhuangPlayerId.equals(playerId)) {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.maidi);
				for (String pid : playerIdList) {
					if (!zhuangPlayerId.equals(pid)) {
						playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.startDingdi);
					}
				}
			} else {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.dingdi);
			}
		} else {
			if (zhuangPlayerId.equals(playerId)) {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.bumai);
				for (String pid : playerIdList) {
					if (!zhuangPlayerId.equals(pid)) {
						playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.startDingdi);
					}
				}
			} else {
				playerMaidiStateMap.put(playerId, MajiangGamePlayerMaidiState.buding);
			}
		}
		if (state.name().equals(VoteNotPassWhenMaidi.name)) {
			state = new MaidiState();
		}
		updatePlayerState(playerId, new PlayerAfterMaidi());
		this.playerMaidiStateMap.putAll(playerMaidiStateMap);
		boolean start = true;
		for (String pid : playerIdList) {
			if (MajiangGamePlayerMaidiState.startDingdi.equals(playerMaidiStateMap.get(pid))
					|| MajiangGamePlayerMaidiState.startMaidi.equals(playerMaidiStateMap.get(pid))
					|| MajiangGamePlayerMaidiState.waitForMaidi.equals(playerMaidiStateMap.get(pid))) {
				start = false;
			}
		}
		if (start) {
			PanActionFrame firstActionFrame = startPan(playerIdList);
			state = new Playing();
			updateAllPlayersState(new PlayerPlaying());
			maidiResult.setFirstActionFrame(firstActionFrame);
		}
		MajiangGameValueObject majiangGame = new MajiangGameValueObject(this);
		majiangGame.setPlayerMaidiStateMap(playerMaidiStateMap);
		maidiResult.setMajiangGame(majiangGame);
		return maidiResult;
	}

	public void createJuAndReadyFirstPan(long currentTime) throws Exception {
		ju = new Ju();
		// 因为买底动作将开始阶段分为两部分
		// ju.setStartFirstPanProcess(new ClassicStartFirstPanProcess());
		// ju.setStartNextPanProcess(new ClassicStartNextPanProcess());
		ju.setPlayersMenFengDeterminerForFirstPan(new RandomMustHasDongPlayersMenFengDeterminer(currentTime));
		ju.setPlayersMenFengDeterminerForNextPan(new WenzhouMajiangPlayersMenFengDeterminer());
		ju.setZhuangDeterminerForFirstPan(new MenFengDongZhuangDeterminer());
		ju.setZhuangDeterminerForNextPan(new MenFengDongZhuangDeterminer());
		ju.setAvaliablePaiFiller(new WenzhouMajiangRandomAvaliablePaiFiller(currentTime + 1, shaozhongfa));
		ju.setGuipaiDeterminer(new RandomGuipaiDeterminer(currentTime + 2));
		ju.setFaPaiStrategy(new WenzhouMajiangFaPaiStrategy(16));
		ju.setCurrentPanFinishiDeterminer(new WenzhouMajiangPanFinishDeterminer());
		ju.setGouXingPanHu(new WenzhouMajiangGouXingPanHu());
		ju.setCurrentPanPublicWaitingPlayerDeterminer(new WaitDaPlayerPanPublicWaitingPlayerDeterminer());

		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = new WenzhouMajiangPanResultBuilder();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = new HashMap<>();
		wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(playerMaidiStateMap);
		wenzhouMajiangPanResultBuilder.setJinjie1(jinjie1);
		wenzhouMajiangPanResultBuilder.setJinjie2(jinjie2);
		wenzhouMajiangPanResultBuilder.setTeshushuangfan(teshushuangfan);
		wenzhouMajiangPanResultBuilder.setCaishenqian(caishenqian);
		wenzhouMajiangPanResultBuilder.setShaozhongfa(shaozhongfa);
		wenzhouMajiangPanResultBuilder.setLazila(lazila);
		wenzhouMajiangPanResultBuilder.setGangsuanfen(gangsuanfen);
		ju.setCurrentPanResultBuilder(wenzhouMajiangPanResultBuilder);

		ju.setJuFinishiDeterminer(new FixedPanNumbersJuFinishiDeterminer(panshu));

		ju.setJuResultBuilder(new WenzhouMajiangJuResultBuilder());

		ju.setInitialActionUpdater(new ZhuangMoPaiInitialActionUpdater());
		ju.setMoActionProcessor(new WenzhouMajiangMoActionProcessor());
		ju.setMoActionUpdater(new WenzhouMajiangMoActionUpdater());
		ju.setDaActionProcessor(new WenzhouMajiangDaActionProcessor());
		ju.setDaActionUpdater(new WenzhouMajiangDaActionUpdater());
		ju.setChiActionProcessor(new PengganghuFirstBuChiActionProcessor());
		ju.setChiActionUpdater(new WenzhouMajiangChiActionUpdater());
		ju.setPengActionProcessor(new HuFirstBuPengActionProcessor());
		ju.setPengActionUpdater(new WenzhouMajiangPengActionUpdater());
		ju.setGangActionProcessor(new HuFirstBuGangActionProcessor());
		ju.setGangActionUpdater(new WenzhouMajiangGangActionUpdater());
		ju.setGuoActionProcessor(new DoNothingGuoActionProcessor());
		ju.setGuoActionUpdater(new WenzhouMajiangGuoActionUpdater());
		ju.setHuActionProcessor(new PlayerSetHuHuActionProcessor());
		ju.setHuActionUpdater(new PlayerHuAndClearAllActionHuActionUpdater());

		ju.addActionStatisticsListener(new MoGuipaiCounter());
		ju.addActionStatisticsListener(new TianHuAndDihuOpportunityDetector());
		ju.addActionStatisticsListener(new GuoPengBuPengStatisticsListener());
		ju.addActionStatisticsListener(new WenzhouMajiangChiPengGangActionStatisticsListener());

		Pan firstPan = new Pan();
		firstPan.setNo(1);
		allPlayerIds().forEach((pid) -> firstPan.addPlayer(pid));
		ju.setCurrentPan(firstPan);

		// 开始定第一盘的门风
		ju.determinePlayersMenFengForFirstPan();

		// 开始定第一盘庄家
		ju.determineZhuangForFirstPan();
		String zhuangPlayerId = firstPan.getZhuangPlayerId();
		playerLianZhuangCountMap.put(zhuangPlayerId, 1);
		allPlayerIds().forEach((pid) -> playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.waitForMaidi));
		playerMaidiStateMap.put(zhuangPlayerId, MajiangGamePlayerMaidiState.startMaidi);
		this.playerMaidiStateMap.putAll(playerMaidiStateMap);
	}

	public PanActionFrame startPan(List<String> playerIdList) throws Exception {
		// 开始填充可用的牌
		ju.fillAvaliablePai();

		// 开始定财神
		ju.determineGuipai();

		// 设置手牌排序器
		Pan currenPan = ju.getCurrentPan();
		Set<MajiangPai> publicGuipaiSet = currenPan.getPublicGuipaiSet();
		MajiangPai[] guipaiTypes = new MajiangPai[publicGuipaiSet.size()];
		publicGuipaiSet.toArray(guipaiTypes);
		MajiangPai guipai = guipaiTypes[0];
		ju.updateShoupaiListSortComparatorForAllPlayersInCurrentPan(
				new BaibanDangGuipaiBenpaiShoupaiSortComparator(guipai));
		// 开始发牌
		ju.faPai();

		// 庄家可以摸第一张牌
		ju.updateInitialAction();

		// 庄家摸第一张牌,进入正式行牌流程
		ju.action(ju.getCurrentPan().getZhuangPlayerId(), 1, 1, System.currentTimeMillis());
		// 必然庄家已经先摸了一张牌了
		return ju.getCurrentPan().findLatestActionFrame();
	}

	public MajiangActionResult action(String playerId, int actionId, int actionNo, long actionTime) throws Exception {
		PanActionFrame panActionFrame = ju.action(playerId, actionId, actionNo, actionTime);
		MajiangActionResult result = new MajiangActionResult();
		result.setPanActionFrame(panActionFrame);
		if (state.name().equals(VoteNotPassWhenPlaying.name)) {
			state = new Playing();
		}
		checkAndFinishPan();

		if (state.name().equals(WaitingNextPan.name) || state.name().equals(Finished.name)) {// 盘结束了
			WenzhouMajiangPanResult panResult = (WenzhouMajiangPanResult) ju.findLatestFinishedPanResult();
			panResult.getPlayerResultList()
					.forEach((pr) -> playeTotalScoreMap.put(pr.getPlayerId(), pr.getTotalScore()));
			result.setPanResult(panResult);
			if (state.name().equals(Finished.name)) {// 局结束了
				result.setJuResult((WenzhouMajiangJuResult) ju.getJuResult());
			}
		}
		result.setMajiangGame(new MajiangGameValueObject(this));
		return result;
	}

	@Override
	public void finish() throws Exception {
		if (ju != null) {
			ju.finish();
		}
	}

	@Override
	protected boolean checkToFinishGame() throws Exception {
		return ju.getJuResult() != null;
	}

	@Override
	protected boolean checkToFinishCurrentPan() throws Exception {
		return ju.getCurrentPan() == null;
	}

	@Override
	protected void startNextPan() throws Exception {
		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = (WenzhouMajiangPanResultBuilder) ju
				.getCurrentPanResultBuilder();
		Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = wenzhouMajiangPanResultBuilder
				.getPlayerMaidiStateMap();
		Map<String, MajiangGamePlayerMaidiState> newMaidiStateMap = null;
		ju.getActionStatisticsListenerManager().updateListenersForNextPan();
		Pan nextPan = new Pan();
		nextPan.setNo(ju.countFinishedPan() + 1);
		PanResult latestFinishedPanResult = ju.findLatestFinishedPanResult();
		List<String> allPlayerIds = latestFinishedPanResult.allPlayerIds();
		allPlayerIds.forEach((pid) -> nextPan.addPlayer(pid));
		ju.setCurrentPan(nextPan);
		WenzhouMajiangPlayersMenFengDeterminer menFengDeterminer = (WenzhouMajiangPlayersMenFengDeterminer) ju
				.getPlayersMenFengDeterminerForNextPan();
		playerLianZhuangCountMap.clear();

		// 开始定下一盘的门风
		ju.determinePlayersMenFengForNextPan();

		// 开始定下一盘庄家
		ju.determineZhuangForNextPan();
		String lianZhuangPlayerId = menFengDeterminer.getZhuangPlayerId();
		int lianZhuangCount = menFengDeterminer.getLianZhuangCount();
		playerLianZhuangCountMap.put(lianZhuangPlayerId, lianZhuangCount);
		if (lianZhuangCount > 1) {// 连庄
			newMaidiStateMap = playerMaidiStateMap;
			wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(newMaidiStateMap);
			startPan(allPlayerIds);
			state = new Playing();
			updateAllPlayersState(new PlayerPlaying());
		} else {// 重新买底
			newMaidiStateMap = new HashMap<>();
			for (String pid : allPlayerIds) {
				newMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.waitForMaidi);
			}
			wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(newMaidiStateMap);
			String zhuangPlayerId = menFengDeterminer.getZhuangPlayerId();
			newMaidiStateMap.put(zhuangPlayerId, MajiangGamePlayerMaidiState.startMaidi);
			state = new MaidiState();
			updateAllPlayersState(new PlayerMaidi());
		}
		this.playerMaidiStateMap.putAll(newMaidiStateMap);
	}

	@Override
	protected void updatePlayerToExtendedVotingState(GamePlayer player) {
		if (player.getState().name().equals(PlayerMaidi.name)) {
			player.setState(new PlayerVotingWhenMaidi());
		} else if (player.getState().name().equals(PlayerAfterMaidi.name)) {
			player.setState(new PlayerVotingWhenAfterMaidi());
		}
	}

	@Override
	protected void updateToExtendedVotingState() {
		if (state.name().equals(MaidiState.name) || state.name().equals(VoteNotPassWhenMaidi.name)) {
			state = new VotingWhenMaidi();
		}
	}

	@Override
	protected void updatePlayerToExtendedVotedState(GamePlayer player) {
		if (player.getState().name().equals(PlayerVotingWhenMaidi.name)) {
			player.setState(new PlayerVotedWhenMaidi());
		} else if (player.getState().name().equals(PlayerVotingWhenAfterMaidi.name)) {
			player.setState(new PlayerVotedWhenAfterMaidi());
		}
	}

	@Override
	protected void recoveryPlayersStateFromExtendedVoting() throws Exception {
		if (state.name().equals(VoteNotPassWhenMaidi.name)) {
			for (GamePlayer player : idPlayerMap.values()) {
				if (player.getState().name().equals(PlayerVotingWhenMaidi.name)
						|| player.getState().name().equals(PlayerVotedWhenMaidi.name)) {
					updatePlayerState(player.getId(), new PlayerMaidi());
				} else if (player.getState().name().equals(PlayerVotingWhenAfterMaidi.name)
						|| player.getState().name().equals(PlayerVotedWhenAfterMaidi.name)) {
					updatePlayerState(player.getId(), new PlayerAfterMaidi());
				}
			}
		}
	}

	@Override
	protected void updateToVoteNotPassStateFromExtendedVoting() throws Exception {
		if (state.name().equals(VotingWhenMaidi.name)) {
			state = new VoteNotPassWhenMaidi();
		}
	}

	@Override
	public void start(long currentTime) throws Exception {
		state = new MaidiState();
		updateAllPlayersState(new PlayerMaidi());
		createJuAndReadyFirstPan(currentTime);
	}

	@Override
	public MajiangGameValueObject toValueObject() {
		return new MajiangGameValueObject(this);
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

	public Ju getJu() {
		return ju;
	}

	public void setJu(Ju ju) {
		this.ju = ju;
	}

	public Map<String, Integer> getPlayerLianZhuangCountMap() {
		return playerLianZhuangCountMap;
	}

	public void setPlayerLianZhuangCountMap(Map<String, Integer> playerLianZhuangCountMap) {
		this.playerLianZhuangCountMap = playerLianZhuangCountMap;
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

	public void setPlayeTotalScoreMap(Map<String, Integer> playeTotalScoreMap) {
		this.playeTotalScoreMap = playeTotalScoreMap;
	}

}
