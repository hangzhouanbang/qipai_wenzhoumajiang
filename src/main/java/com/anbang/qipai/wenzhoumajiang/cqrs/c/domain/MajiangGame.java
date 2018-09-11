package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.ju.finish.FixedPanNumbersJuFinishiDeterminer;
import com.dml.majiang.ju.nextpan.AllPlayersReadyCreateNextPanDeterminer;
import com.dml.majiang.ju.result.JuResult;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanActionFrame;
import com.dml.majiang.pan.guipai.RandomGuipaiDeterminer;
import com.dml.majiang.pan.publicwaitingplayer.WaitDaPlayerPanPublicWaitingPlayerDeterminer;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.action.chi.PengganghuFirstChiActionProcessor;
import com.dml.majiang.player.action.da.DachushoupaiDaActionProcessor;
import com.dml.majiang.player.action.gang.HuFirstGangActionProcessor;
import com.dml.majiang.player.action.guo.DoNothingGuoActionProcessor;
import com.dml.majiang.player.action.hu.PlayerSetHuHuActionProcessor;
import com.dml.majiang.player.action.initial.ZhuangMoPaiInitialActionUpdater;
import com.dml.majiang.player.action.listener.comprehensive.DianpaoDihuOpportunityDetector;
import com.dml.majiang.player.action.listener.comprehensive.JuezhangStatisticsListener;
import com.dml.majiang.player.action.listener.mo.MoGuipaiCounter;
import com.dml.majiang.player.action.peng.HuFirstPengActionProcessor;
import com.dml.majiang.player.menfeng.RandomMustHasDongPlayersMenFengDeterminer;
import com.dml.majiang.player.menfeng.ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer;
import com.dml.majiang.player.shoupaisort.BaibanDangGuipaiBenpaiShoupaiSortComparator;
import com.dml.majiang.player.zhuang.MenFengDongZhuangDeterminer;
import com.dml.majiang.position.MajiangPosition;
import com.dml.majiang.position.MajiangPositionUtil;
import com.dml.mpgame.game.GamePlayerOnlineState;
import com.dml.mpgame.game.GamePlayerState;
import com.dml.mpgame.game.GamePlayerValueObject;
import com.dml.mpgame.game.GameState;
import com.dml.mpgame.game.GameValueObject;

public class MajiangGame {
	private String gameId;
	private int lianzhuangCount = 1;
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
	private MajiangGameState state;
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap = new HashMap<>();
	private Map<String, MajiangGamePlayerState> playerStateMap = new HashMap<>();
	private Map<String, GamePlayerOnlineState> playerOnlineStateMap = new HashMap<>();
	private Map<String, Integer> playeTotalScoreMap = new HashMap<>();

	public MaidiResult maidi(String playerId, MajiangGamePlayerMaidiState state) throws Exception {
		MaidiResult maidiResult = new MaidiResult();
		playerMaidiStateMap.put(playerId, state);
		boolean start = true;
		List<String> playerIdList = new ArrayList<>();
		for (String pid : playerMaidiStateMap.keySet()) {
			playerIdList.add(pid);
			if (MajiangGamePlayerMaidiState.weimai.equals(playerMaidiStateMap.get(pid))
					|| MajiangGamePlayerMaidiState.dingdi.equals(playerMaidiStateMap.get(pid))) {
				start = false;
			}
		}
		if (start) {
			WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = (WenzhouMajiangPanResultBuilder) ju
					.getCurrentPanResultBuilder();
			wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(playerMaidiStateMap);
			PanActionFrame firstActionFrame = startPan(playerIdList, System.currentTimeMillis());
			maidiResult.setFirstActionFrame(firstActionFrame);
		}
		return maidiResult;
	}

	public Map<String, MajiangGamePlayerMaidiState> createJuAndReadyFirstPan(GameValueObject game, long currentTime)
			throws Exception {
		ju = new Ju();
		// 因为买底动作将开始阶段分为两部分
		// ju.setStartFirstPanProcess(new ClassicStartFirstPanProcess());
		// ju.setStartNextPanProcess(new ClassicStartNextPanProcess());
		game.allPlayerIds().forEach((pid) -> playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.weimai));
		ju.setPlayersMenFengDeterminerForFirstPan(new RandomMustHasDongPlayersMenFengDeterminer(currentTime));
		ju.setPlayersMenFengDeterminerForNextPan(new ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer());
		ju.setZhuangDeterminerForFirstPan(new MenFengDongZhuangDeterminer());
		ju.setZhuangDeterminerForNextPan(new MenFengDongZhuangDeterminer());
		ju.setAvaliablePaiFiller(new WenzhouMajiangRandomAvaliablePaiFiller(currentTime + 1, shaozhongfa));
		ju.setGuipaiDeterminer(new RandomGuipaiDeterminer(currentTime + 2));
		ju.setFaPaiStrategy(new WenzhouMajiangFaPaiStrategy(16));
		ju.setCurrentPanFinishiDeterminer(new WenzhouMajiangPanFinishDeterminer());
		ju.setGouXingPanHu(new WenzhouMajiangGouXingPanHu());
		ju.setCurrentPanPublicWaitingPlayerDeterminer(new WaitDaPlayerPanPublicWaitingPlayerDeterminer());

		WenzhouMajiangPanResultBuilder wenzhouMajiangPanResultBuilder = new WenzhouMajiangPanResultBuilder();
		wenzhouMajiangPanResultBuilder.setPlayerMaidiStateMap(playerMaidiStateMap);
		wenzhouMajiangPanResultBuilder.setJinjie1(jinjie1);
		wenzhouMajiangPanResultBuilder.setJinjie2(jinjie2);
		wenzhouMajiangPanResultBuilder.setTeshushuangfan(teshushuangfan);
		wenzhouMajiangPanResultBuilder.setCaishenqian(caishenqian);
		wenzhouMajiangPanResultBuilder.setShaozhongfa(shaozhongfa);
		wenzhouMajiangPanResultBuilder.setLazila(lazila);
		wenzhouMajiangPanResultBuilder.setGangsuanfen(gangsuanfen);
		ju.setCurrentPanResultBuilder(wenzhouMajiangPanResultBuilder);

		AllPlayersReadyCreateNextPanDeterminer createNextPanDeterminer = new AllPlayersReadyCreateNextPanDeterminer();
		game.allPlayerIds().forEach((pid) -> createNextPanDeterminer.addPlayer(pid));
		ju.setCreateNextPanDeterminer(createNextPanDeterminer);

		ju.setJuFinishiDeterminer(new FixedPanNumbersJuFinishiDeterminer(panshu));

		ju.setJuResultBuilder(new WenzhouMajiangJuResultBuilder());

		ju.setInitialActionUpdater(new ZhuangMoPaiInitialActionUpdater());
		ju.setMoActionProcessor(new WenzhouMajiangMoActionProcessor());
		ju.setMoActionUpdater(new WenzhouMajiangMoActionUpdater());
		ju.setDaActionProcessor(new DachushoupaiDaActionProcessor());
		ju.setDaActionUpdater(new WenzhouMajiangDaActionUpdater());
		ju.setChiActionProcessor(new PengganghuFirstChiActionProcessor());
		ju.setChiActionUpdater(new WenzhouMajiangChiActionUpdater());
		ju.setPengActionProcessor(new HuFirstPengActionProcessor());
		ju.setPengActionUpdater(new WenzhouMajiangPengActionUpdater());
		ju.setGangActionProcessor(new HuFirstGangActionProcessor());
		ju.setGangActionUpdater(new WenzhouMajiangGangActionUpdater());
		ju.setGuoActionProcessor(new DoNothingGuoActionProcessor());
		ju.setGuoActionUpdater(new WenzhouMajiangGuoActionUpdater());
		ju.setHuActionProcessor(new PlayerSetHuHuActionProcessor());

		ju.addActionStatisticsListener(new JuezhangStatisticsListener());
		ju.addActionStatisticsListener(new MoGuipaiCounter());
		ju.addActionStatisticsListener(new DianpaoDihuOpportunityDetector());

		Pan firstPan = new Pan();
		firstPan.setNo(1);
		game.allPlayerIds().forEach((pid) -> firstPan.addPlayer(pid));
		ju.setCurrentPan(firstPan);

		// 开始定第一盘的门风
		ju.determinePlayersMenFengForFirstPan();

		// 开始定第一盘庄家
		ju.determineZhuangForFirstPan();
		String zhuangPlayerId = firstPan.getZhuangPlayerId();
		playerMaidiStateMap.put(zhuangPlayerId, MajiangGamePlayerMaidiState.dingdi);
		return playerMaidiStateMap;
	}

	public PanActionFrame startPan(List<String> playerIdList, long currentTime) throws Exception {
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
		ju.action(ju.getCurrentPan().getZhuangPlayerId(), 1, System.currentTimeMillis());
		// 必然庄家已经先摸了一张牌了
		return ju.getCurrentPan().findLatestActionFrame();
	}

	public MajiangActionResult action(String playerId, int actionId, long actionTime) throws Exception {
		PanActionFrame panActionFrame = ju.action(playerId, actionId, actionTime);
		MajiangActionResult result = new MajiangActionResult();
		result.setPanActionFrame(panActionFrame);
		if (ju.getCurrentPan() == null) {// 盘结束了
			state = MajiangGameState.waitingNextPan;
			playerStateMap.keySet().forEach((pid) -> playerStateMap.put(pid, MajiangGamePlayerState.panFinished));
			WenzhouMajiangPanResult panResult = (WenzhouMajiangPanResult) ju.findLatestFinishedPanResult();
			panResult.getPlayerResultList()
					.forEach((pr) -> playeTotalScoreMap.put(pr.getPlayerId(), pr.getTotalScore()));
			result.setPanResult(panResult);
		}
		if (ju.getJuResult() != null) {// 局结束了
			state = MajiangGameState.finished;
			playerStateMap.keySet().forEach((pid) -> playerStateMap.put(pid, MajiangGamePlayerState.finished));
			result.setJuResult((WenzhouMajiangJuResult) ju.getJuResult());
		}
		result.setMajiangGame(new MajiangGameValueObject(this));
		return result;
	}

	public MajiangGameValueObject updateByGame(GameValueObject game) {
		GameState gameState = game.getState();
		if (gameState.equals(GameState.finished)) {
			state = MajiangGameState.finished;
		} else if (gameState.equals(GameState.playing)) {
			if (state == null || !state.equals(MajiangGameState.waitingNextPan)) {
				state = MajiangGameState.playing;
			}
		} else if (gameState.equals(GameState.waitingStart)) {
			state = MajiangGameState.waitingStart;
		} else {
		}

		List<GamePlayerValueObject> players = game.getPlayers();
		Set<String> playerIdsSet = new HashSet<>();
		players.forEach((player) -> {
			String playerId = player.getId();
			playerIdsSet.add(playerId);
			playerOnlineStateMap.put(playerId, player.getOnlineState());
			GamePlayerState gamePlayerState = player.getState();
			if (gamePlayerState.equals(GamePlayerState.finished)) {
				playerStateMap.put(playerId, MajiangGamePlayerState.finished);
			} else if (gamePlayerState.equals(GamePlayerState.joined)) {
				playerStateMap.put(playerId, MajiangGamePlayerState.joined);
			} else if (gamePlayerState.equals(GamePlayerState.playing)) {
				if (!state.equals(MajiangGameState.waitingNextPan)) {
					playerStateMap.put(playerId, MajiangGamePlayerState.playing);
				}
			} else if (gamePlayerState.equals(GamePlayerState.readyToStart)) {
				playerStateMap.put(playerId, MajiangGamePlayerState.readyToStart);
			} else {
			}
		});

		Set<String> currentPlayerIdsSet = new HashSet<>(playerStateMap.keySet());
		currentPlayerIdsSet.forEach((playerId) -> {
			if (!playerIdsSet.contains(playerId)) {
				playerStateMap.remove(playerId);
				playerOnlineStateMap.remove(playerId);
				playeTotalScoreMap.remove(playerId);
			}
		});

		return new MajiangGameValueObject(this);
	}

	public PanActionFrame readyToNextPan(String playerId) throws Exception {
		playerStateMap.put(playerId, MajiangGamePlayerState.readyToStart);
		AllPlayersReadyCreateNextPanDeterminer createNextPanDeterminer = (AllPlayersReadyCreateNextPanDeterminer) ju
				.getCreateNextPanDeterminer();
		createNextPanDeterminer.playerReady(playerId);
		// 如果可以创建下一盘,那就创建下一盘
		if (ju.determineToCreateNextPan()) {
			state = MajiangGameState.playing;
			playerStateMap.keySet().forEach((pid) -> playerStateMap.put(pid, MajiangGamePlayerState.playing));
			ju.getActionStatisticsListenerManager().updateListenersForNextPan();
			createNextPanDeterminer.reset();
			Pan nextPan = new Pan();
			nextPan.setNo(ju.countFinishedPan() + 1);
			PanResult latestFinishedPanResult = ju.findLatestFinishedPanResult();
			List<String> allPlayerIds = latestFinishedPanResult.allPlayerIds();
			allPlayerIds.forEach((pid) -> nextPan.addPlayer(pid));
			ju.setCurrentPan(nextPan);
			ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer menFengDeterminer = (ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer) ju
					.getPlayersMenFengDeterminerForNextPan();
			int lianZhuangCount = menFengDeterminer.getLianZhuangCount();
			this.lianzhuangCount = lianZhuangCount;
			if (lianZhuangCount < 5) {
				// 开始定下一盘的门风
				ju.determinePlayersMenFengForNextPan();
			} else {
				String zhuangPlayerId = latestFinishedPanResult.findZhuangPlayerId();
				// 先找出庄的下家
				String zhuangXiajiaPlayerId = latestFinishedPanResult.findXiajiaPlayerId(zhuangPlayerId);
				// 下家连庄次数为一
				menFengDeterminer.setZhuangPlayerId(zhuangXiajiaPlayerId);
				menFengDeterminer.setLianZhuangCount(1);
				// 再计算要顺时针移几步到东
				MajiangPosition p = latestFinishedPanResult.playerMenFeng(zhuangXiajiaPlayerId);
				int n = 0;
				while (true) {
					MajiangPosition np = MajiangPositionUtil.nextPositionClockwise(p);
					n++;
					if (np.equals(MajiangPosition.dong)) {
						break;
					} else {
						p = np;
					}
				}
				// 最后给所有玩家设置门风
				for (String pid : allPlayerIds) {
					MajiangPosition playerMenFeng = latestFinishedPanResult.playerMenFeng(pid);
					MajiangPosition newPlayerMenFeng = playerMenFeng;
					for (int i = 0; i < n; i++) {
						newPlayerMenFeng = MajiangPositionUtil.nextPositionClockwise(newPlayerMenFeng);
					}
					nextPan.updatePlayerMenFeng(pid, newPlayerMenFeng);
				}
			}

			// 开始定下一盘庄家
			ju.determineZhuangForNextPan();
			if (lianZhuangCount > 1 && lianZhuangCount < 5) {// 连庄
				PanActionFrame firstActionFrame = startPan(allPlayerIds, System.currentTimeMillis());
				return firstActionFrame;
			} else {// 重新买底
				allPlayerIds.forEach((pid) -> playerMaidiStateMap.put(pid, MajiangGamePlayerMaidiState.weimai));
				String zhuangPlayerId = menFengDeterminer.getZhuangPlayerId();
				playerMaidiStateMap.put(zhuangPlayerId, MajiangGamePlayerMaidiState.dingdi);
			}
		}
		return null;
	}

	public JuResult finishJu() {
		ju.finish();
		return ju.getJuResult();
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getLianzhuangCount() {
		return lianzhuangCount;
	}

	public void setLianzhuangCount(int lianzhuangCount) {
		this.lianzhuangCount = lianzhuangCount;
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

	public MajiangGameState getState() {
		return state;
	}

	public void setState(MajiangGameState state) {
		this.state = state;
	}

	public Map<String, MajiangGamePlayerMaidiState> getPlayerMaidiStateMap() {
		return playerMaidiStateMap;
	}

	public void setPlayerMaidiStateMap(Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		this.playerMaidiStateMap = playerMaidiStateMap;
	}

	public Map<String, MajiangGamePlayerState> getPlayerStateMap() {
		return playerStateMap;
	}

	public void setPlayerStateMap(Map<String, MajiangGamePlayerState> playerStateMap) {
		this.playerStateMap = playerStateMap;
	}

	public Map<String, GamePlayerOnlineState> getPlayerOnlineStateMap() {
		return playerOnlineStateMap;
	}

	public void setPlayerOnlineStateMap(Map<String, GamePlayerOnlineState> playerOnlineStateMap) {
		this.playerOnlineStateMap = playerOnlineStateMap;
	}

	public Map<String, Integer> getPlayeTotalScoreMap() {
		return playeTotalScoreMap;
	}

	public void setPlayeTotalScoreMap(Map<String, Integer> playeTotalScoreMap) {
		this.playeTotalScoreMap = playeTotalScoreMap;
	}

}
