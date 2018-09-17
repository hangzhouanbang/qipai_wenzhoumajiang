package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.pan.frame.PanValueObject;
import com.dml.majiang.pan.result.CurrentPanResultBuilder;
import com.dml.majiang.pan.result.PanResult;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.menfeng.ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer;

public class WenzhouMajiangPanResultBuilder implements CurrentPanResultBuilder {
	private Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap;
	private boolean jinjie1;
	private boolean jinjie2;
	private boolean teshushuangfan;
	private boolean caishenqian;
	private boolean gangsuanfen;
	private boolean shaozhongfa;
	private boolean lazila;

	@Override
	public PanResult buildCurrentPanResult(Ju ju, long panFinishTime) {
		Pan currentPan = ju.getCurrentPan();
		WenzhouMajiangPanResult latestFinishedPanResult = (WenzhouMajiangPanResult) ju.findLatestFinishedPanResult();
		Map<String, Integer> playerTotalScoreMap = new HashMap<>();
		if (latestFinishedPanResult != null) {
			for (WenzhouMajiangPanPlayerResult panPlayerResult : latestFinishedPanResult.getPlayerResultList()) {
				playerTotalScoreMap.put(panPlayerResult.getPlayerId(), panPlayerResult.getTotalScore());
			}
		}
		ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer menfengDeterminer = (ZhuangXiajiaIsDongIfZhuangNotHuPlayersMenFengDeterminer) ju
				.getPlayersMenFengDeterminerForNextPan();
		int difen = 1;
		if (menfengDeterminer.getLianZhuangCount() == 1) {
			if (jinjie1 || jinjie2) {
				difen = 2;
			}
		}
		if (menfengDeterminer.getLianZhuangCount() == 2) {
			if (jinjie1 || jinjie2) {
				difen = 4;
			} else {
				difen = 2;
			}
		}
		if (menfengDeterminer.getLianZhuangCount() == 3) {
			if (jinjie1) {
				difen = 6;
			} else if (jinjie2) {
				difen = 8;
			} else {
				difen = 3;
			}
		}
		if (menfengDeterminer.getLianZhuangCount() >= 4) {
			if (jinjie1) {
				difen = 8;
			} else if (jinjie2) {
				difen = 16;
			} else {
				difen = 4;
			}
		}
		List<MajiangPlayer> huPlayers = currentPan.findAllHuPlayers();
		List<String> playerIdList = currentPan.sortedPlayerIdList();
		List<WenzhouMajiangPanPlayerResult> playerResultList = new ArrayList<>();
		if (huPlayers.size() > 0) {// 正常有人胡
			MajiangPlayer bestHuPlayer = huPlayers.get(0);
			WenzhouMajiangHu bestHu = (WenzhouMajiangHu) bestHuPlayer.getHu();
			if (huPlayers.size() == 1) {// 一人胡

			} else {
				String dianpaoPlayerId = bestHu.getDianpaoPlayerId();
				MajiangPlayer xiajiaPlayer = currentPan.findPlayerById(dianpaoPlayerId);
				// 按点炮者开始遍历出最佳胡
				while (true) {
					if (!xiajiaPlayer.getId().equals(dianpaoPlayerId)) {
						WenzhouMajiangHu hu = (WenzhouMajiangHu) xiajiaPlayer.getHu();
						if (hu != null && bestHu.getHufan().getValue() < hu.getHufan().getValue()) {
							bestHuPlayer = xiajiaPlayer;
							bestHu = hu;
						}
					} else {
						break;
					}
					xiajiaPlayer = currentPan.findXiajia(xiajiaPlayer);
				}
				// 将其他胡的玩家的胡设置为null
				xiajiaPlayer = currentPan.findXiajia(xiajiaPlayer);
				while (true) {
					if (!xiajiaPlayer.getId().equals(dianpaoPlayerId)) {
						if (!xiajiaPlayer.getId().equals(bestHuPlayer.getId())) {
							xiajiaPlayer.setHu(null);
						}
					} else {
						break;
					}
				}
			}
			WenzhouMajiangPanPlayerHufan huPlayerHufan = bestHu.getHufan();
			int paixingbeishu = huPlayerHufan.getValue();
			for (String playerId : playerIdList) {
				MajiangPlayer player = currentPan.findPlayerById(playerId);
				WenzhouMajiangPanPlayerResult playerResult = new WenzhouMajiangPanPlayerResult();
				playerResult.setPlayerId(playerId);
				if (playerId.equals(bestHuPlayer.getId())) {
					playerResult.setHufan(huPlayerHufan);
					WenzhouMajiangPanPlayerCaishenqian caishenqian = new WenzhouMajiangPanPlayerCaishenqian(player);
					caishenqian.calculate(true, this.caishenqian, playerIdList.size());
					playerResult.setCaishenqian(caishenqian);
					WenzhouMajiangGang gang = new WenzhouMajiangGang(player);
					gang.calculate(playerIdList.size(), gangsuanfen);
					playerResult.setGang(gang);
				} else {
					// 计算非胡玩家分数
					playerResult.setHufan(new WenzhouMajiangPanPlayerHufan());
					WenzhouMajiangPanPlayerCaishenqian caishenqian = new WenzhouMajiangPanPlayerCaishenqian(player);
					caishenqian.calculate(false, this.caishenqian, playerIdList.size());
					playerResult.setCaishenqian(caishenqian);
					WenzhouMajiangGang gang = new WenzhouMajiangGang(player);
					gang.calculate(playerIdList.size(), gangsuanfen);
					playerResult.setGang(gang);
				}
				playerResultList.add(playerResult);
			}

			for (int i = 0; i < playerResultList.size(); i++) {
				WenzhouMajiangPanPlayerResult playerResult1 = playerResultList.get(i);
				WenzhouMajiangPanPlayerCaishenqian caishenqian1 = playerResult1.getCaishenqian();
				WenzhouMajiangGang gang1 = playerResult1.getGang();
				String playerId1 = playerResult1.getPlayerId();
				for (int j = (i + 1); j < playerResultList.size(); j++) {
					WenzhouMajiangPanPlayerResult playerResult2 = playerResultList.get(j);
					WenzhouMajiangPanPlayerCaishenqian caishenqian2 = playerResult2.getCaishenqian();
					WenzhouMajiangGang gang2 = playerResult2.getGang();
					String playerId2 = playerResult2.getPlayerId();
					// 结算财神钱
					int qian1 = caishenqian1.getValue();
					int qian2 = caishenqian2.getValue();
					caishenqian1.jiesuan(-qian2);
					caishenqian2.jiesuan(-qian1);
					// 结算杠分
					int gangfen1 = gang1.getValue();
					int gangfen2 = gang2.getValue();
					gang1.jiesuan(-gangfen2);
					gang2.jiesuan(-gangfen1);
					if (playerId1.equals(bestHuPlayer.getId())) {// 1胡2不胡
						// 是不是庄家胡
						boolean zhuangHu = currentPan.getZhuangPlayerId().equals(playerId1);
						if (zhuangHu) {// 闲家输庄家
							int maidiCount = 1;// 底分倍数
							if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)
									|| playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.dingdi)) {
								maidiCount += 1;
							}
							if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)
									|| playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.dingdi)) {
								maidiCount += 1;
							}
							int score1 = playerResult1.getScore();
							int score2 = playerResult2.getScore();
							score1 += difen * maidiCount * paixingbeishu;
							score2 -= difen * maidiCount * paixingbeishu;
							playerResult1.setScore(score1);
							playerResult2.setScore(score2);
						} else {
							// 是不是庄家输
							boolean zhuangShu = currentPan.getZhuangPlayerId().equals(playerId2);
							if (zhuangShu) {// 庄家输闲家
								int maidiCount = 1;// 底分倍数
								if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)
										|| playerMaidiStateMap.get(playerId1)
												.equals(MajiangGamePlayerMaidiState.dingdi)) {
									maidiCount += 1;
								}
								if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)
										|| playerMaidiStateMap.get(playerId1)
												.equals(MajiangGamePlayerMaidiState.dingdi)) {
									maidiCount += 1;
								}
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 += difen * maidiCount * paixingbeishu;
								score2 -= difen * maidiCount * paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							} else {// 闲家输闲家
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 += paixingbeishu;
								score2 -= paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							}
						}
					} else if (playerId2.equals(bestHuPlayer.getId())) {// 2胡1不胡
						// 是不是庄家胡
						boolean zhuangHu = currentPan.getZhuangPlayerId().equals(playerId2);
						if (zhuangHu) {// 闲家输庄家
							int maidiCount = 1;// 底分倍数
							if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)
									|| playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.dingdi)) {
								maidiCount += 1;
							}
							if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)
									|| playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.dingdi)) {
								maidiCount += 1;
							}
							int score1 = playerResult1.getScore();
							int score2 = playerResult2.getScore();
							score1 -= difen * maidiCount * paixingbeishu;
							score2 += difen * maidiCount * paixingbeishu;
							playerResult1.setScore(score1);
							playerResult2.setScore(score2);
						} else {
							// 是不是庄家输
							boolean zhuangShu = currentPan.getZhuangPlayerId().equals(playerId1);
							if (zhuangShu) {// 庄家输闲家
								int maidiCount = 1;// 底分倍数
								if (playerMaidiStateMap.get(playerId1).equals(MajiangGamePlayerMaidiState.maidi)
										|| playerMaidiStateMap.get(playerId1)
												.equals(MajiangGamePlayerMaidiState.dingdi)) {
									maidiCount += 1;
								}
								if (playerMaidiStateMap.get(playerId2).equals(MajiangGamePlayerMaidiState.maidi)
										|| playerMaidiStateMap.get(playerId1)
												.equals(MajiangGamePlayerMaidiState.dingdi)) {
									maidiCount += 1;
								}
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 -= difen * maidiCount * paixingbeishu;
								score2 += difen * maidiCount * paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							} else {// 闲家输闲家
								int score1 = playerResult1.getScore();
								int score2 = playerResult2.getScore();
								score1 -= paixingbeishu;
								score2 += paixingbeishu;
								playerResult1.setScore(score1);
								playerResult2.setScore(score2);
							}
						}
					} else {// 不胡之间

					}
				}
			}

			playerResultList.forEach((playerResult) -> {
				// 计算当盘总分
				playerResult.setScore(playerResult.getScore() + playerResult.getCaishenqian().getTotalscore()
						+ playerResult.getGang().getTotalscore());
				// 计算累计总分
				if (latestFinishedPanResult != null) {
					playerResult.setTotalScore(
							playerTotalScoreMap.get(playerResult.getPlayerId()) + playerResult.getScore());
				} else {
					playerResult.setTotalScore(playerResult.getScore());
				}
			});

			WenzhouMajiangPanResult wenzhouMajiangPanResult = new WenzhouMajiangPanResult();
			wenzhouMajiangPanResult.setPan(new PanValueObject(currentPan));
			wenzhouMajiangPanResult.setPanFinishTime(panFinishTime);
			wenzhouMajiangPanResult.setPlayerResultList(playerResultList);
			wenzhouMajiangPanResult.setHu(true);
			wenzhouMajiangPanResult.setZimo(bestHu.isZimo());
			wenzhouMajiangPanResult.setDianpaoPlayerId(bestHu.getDianpaoPlayerId());
			return wenzhouMajiangPanResult;
		} else {// 流局
			playerIdList.forEach((playerId) -> {
				MajiangPlayer player = currentPan.findPlayerById(playerId);
				WenzhouMajiangPanPlayerResult playerResult = new WenzhouMajiangPanPlayerResult();
				playerResult.setPlayerId(playerId);
				playerResult.setHufan(new WenzhouMajiangPanPlayerHufan());
				WenzhouMajiangPanPlayerCaishenqian caishenqian = new WenzhouMajiangPanPlayerCaishenqian(player);
				caishenqian.calculate(false, this.caishenqian, playerIdList.size());
				playerResult.setCaishenqian(caishenqian);
				WenzhouMajiangGang gang = new WenzhouMajiangGang(player);
				gang.calculate(playerIdList.size(), gangsuanfen);
				playerResult.setGang(gang);
				playerResultList.add(playerResult);
			});

			for (int i = 0; i < playerResultList.size(); i++) {
				WenzhouMajiangPanPlayerResult playerResult1 = playerResultList.get(i);
				WenzhouMajiangPanPlayerCaishenqian caishenqian1 = playerResult1.getCaishenqian();
				WenzhouMajiangGang gang1 = playerResult1.getGang();
				for (int j = (i + 1); j < playerResultList.size(); j++) {
					WenzhouMajiangPanPlayerResult playerResult2 = playerResultList.get(j);
					WenzhouMajiangPanPlayerCaishenqian caishenqian2 = playerResult2.getCaishenqian();
					WenzhouMajiangGang gang2 = playerResult2.getGang();
					// 结算财神钱
					int qian1 = caishenqian1.getValue();
					int qian2 = caishenqian2.getValue();
					caishenqian1.jiesuan(-qian2);
					caishenqian2.jiesuan(-qian1);
					// 结算杠分
					int gangfen1 = gang1.getValue();
					int gangfen2 = gang2.getValue();
					gang1.jiesuan(-gangfen2);
					gang2.jiesuan(-gangfen1);
				}
			}
			// shoupailist放入结果
			playerResultList.forEach((playerResult) -> {
				// 计算当盘总分
				playerResult.setScore(playerResult.getScore() + playerResult.getCaishenqian().getTotalscore()
						+ playerResult.getGang().getTotalscore());
				// 计算累计总分
				if (latestFinishedPanResult != null) {
					playerResult.setTotalScore(
							playerTotalScoreMap.get(playerResult.getPlayerId()) + playerResult.getScore());
				} else {
					playerResult.setTotalScore(playerResult.getScore());
				}
			});

			WenzhouMajiangPanResult wenzhouMajiangPanResult = new WenzhouMajiangPanResult();
			wenzhouMajiangPanResult.setPan(new PanValueObject(currentPan));
			wenzhouMajiangPanResult.setPanFinishTime(panFinishTime);
			wenzhouMajiangPanResult.setPlayerResultList(playerResultList);
			wenzhouMajiangPanResult.setHu(false);

			return wenzhouMajiangPanResult;
		}
	}

	public Map<String, MajiangGamePlayerMaidiState> getPlayerMaidiStateMap() {
		return playerMaidiStateMap;
	}

	public void setPlayerMaidiStateMap(Map<String, MajiangGamePlayerMaidiState> playerMaidiStateMap) {
		this.playerMaidiStateMap = playerMaidiStateMap;
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

	public boolean isGangsuanfen() {
		return gangsuanfen;
	}

	public void setGangsuanfen(boolean gangsuanfen) {
		this.gangsuanfen = gangsuanfen;
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

}
