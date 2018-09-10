package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.List;
import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.hu.MajiangHuAction;
import com.dml.majiang.player.action.listener.comprehensive.JuezhangStatisticsListener;
import com.dml.majiang.player.action.listener.mo.MoGuipaiCounter;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.action.mo.MajiangPlayerMoActionUpdater;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangMoActionUpdater implements MajiangPlayerMoActionUpdater {

	@Override
	public void updateActions(MajiangMoAction moAction, Ju ju) throws Exception {
		int liupai = 0;
		Pan currentPan = ju.getCurrentPan();
		MajiangPlayer player = currentPan.findPlayerById(moAction.getActionPlayerId());
		player.clearActionCandidates();
		int avaliablePaiLeft = currentPan.countAvaliablePai();
		if (avaliablePaiLeft - liupai == 0) {// 没牌了
			// 当然啥也不干了
		} else {
			MajiangPai gangmoShoupai = player.getGangmoShoupai();
			// 有手牌或刻子可以杠这个摸来的牌
			player.tryShoupaigangmoAndGenerateCandidateAction();
			player.tryKezigangmoAndGenerateCandidateAction();

			// 杠四个手牌
			player.tryGangsigeshoupaiAndGenerateCandidateAction();

			// 刻子杠手牌
			player.tryKezigangshoupaiAndGenerateCandidateAction();

			// 胡
			WenzhouMajiangPanResultBuilder wenzhouMajiangJuResultBuilder = (WenzhouMajiangPanResultBuilder) ju
					.getCurrentPanResultBuilder();
			boolean teshushuangfan = wenzhouMajiangJuResultBuilder.isTeshushuangfan();
			boolean shaozhongfa = wenzhouMajiangJuResultBuilder.isShaozhongfa();
			boolean lazila = wenzhouMajiangJuResultBuilder.isLazila();
			GouXingPanHu gouXingPanHu = ju.getGouXingPanHu();

			boolean couldTianhu = false;
			if (currentPan.getZhuangPlayerId().equals(player.getId())) {
				if (player.countFangruShoupai() == 0) {
					couldTianhu = true;
				}
			}

			WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestZimoHu(couldTianhu, gouXingPanHu,
					player, moAction, shaozhongfa, teshushuangfan, lazila);// 少中发
			if (bestHu != null) {
				bestHu.setZimo(true);
				player.addActionCandidate(new MajiangHuAction(player.getId(), bestHu));
			} else {
				// 非胡牌型特殊胡-三财神
				MoGuipaiCounter moGuipaiCounter = ju.getActionStatisticsListenerManager()
						.findListener(MoGuipaiCounter.class);
				if (moGuipaiCounter.getCount() == 3) {
					WenzhouMajiangPanPlayerHufan hufan = new WenzhouMajiangPanPlayerHufan();
					WenzhouMajiangPanPlayerHuxing huxing = new WenzhouMajiangPanPlayerHuxing();
					hufan.setHuxing(huxing);
					hufan.setRuan(false);
					hufan.calculate(teshushuangfan, lazila);
					WenzhouMajiangHu sancaishenHu = new WenzhouMajiangHu(hufan);
					player.addActionCandidate(new MajiangHuAction(player.getId(), sancaishenHu));
				}
			}

			// 需要有“过”
			player.checkAndGenerateGuoCandidateAction();
			if (player.getActionCandidates().isEmpty()) {
				// 啥也不能干，那只能打出牌
				/*
				 * 绝风：抓牌后，手牌有绝张风牌字牌，需优先打出，其他牌颜色变灰无法点击
				 * 跟风：抓牌后，手牌有不成对、暗刻的风牌字牌，且该风牌字牌在已打的牌堆里也有，则该张牌需要优先打出 头风：抓牌后，手牌中单独一张的风牌字牌需要优先打出
				 */
				List<MajiangPai> fangruShoupaiList = player.getFangruShoupaiList();
				JuezhangStatisticsListener juezhangStatisticsListener = ju.getActionStatisticsListenerManager()
						.findListener(JuezhangStatisticsListener.class);
				Set<MajiangPai> guipaiTypeSet = player.getGuipaiTypeSet();
				MajiangPai[] guipaiTypes = new MajiangPai[guipaiTypeSet.size()];
				guipaiTypeSet.toArray(guipaiTypes);
				MajiangPai guipaiType = guipaiTypes[0];
				boolean juefeng = false;
				boolean genfeng = false;
				// 循环了三次、代码需要改进
				for (MajiangPai pai : fangruShoupaiList) {
					if (!MajiangPai.baiban.equals(pai)) {
						if (!guipaiTypeSet.contains(pai) && (MajiangPai.isZipai(pai) || MajiangPai.isFengpai(pai))) {
							if (juezhangStatisticsListener.ifJuezhang(pai)) {
								player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
								juefeng = true;
							}
						}
					} else if (!guipaiTypeSet.contains(pai)
							&& (MajiangPai.isZipai(guipaiType) || MajiangPai.isFengpai(guipaiType))) {
						if (juezhangStatisticsListener.ifJuezhang(pai)) {
							player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
							juefeng = true;
						}
					} else {

					}
				}
				for (MajiangPai pai : fangruShoupaiList) {
					if (!MajiangPai.baiban.equals(pai)) {
						if (!guipaiTypeSet.contains(pai) && !gangmoShoupai.equals(pai)
								&& (MajiangPai.isZipai(pai) || MajiangPai.isFengpai(pai))) {
							if (!juefeng && juezhangStatisticsListener.ifMingPai(pai)
									&& player.getShoupaiCalculator().count(pai) == 1) {
								player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
								genfeng = true;
							}
						}
					} else if (!guipaiTypeSet.contains(pai) && !gangmoShoupai.equals(pai)
							&& (MajiangPai.isZipai(guipaiType) || MajiangPai.isFengpai(guipaiType))) {
						if (!juefeng && juezhangStatisticsListener.ifMingPai(pai)
								&& player.getShoupaiCalculator().count(pai) == 1) {
							player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
							genfeng = true;
						}
					} else {

					}
				}
				for (MajiangPai pai : fangruShoupaiList) {
					if (!MajiangPai.baiban.equals(pai)) {
						if (!guipaiTypeSet.contains(pai) && !gangmoShoupai.equals(pai)
								&& (MajiangPai.isZipai(pai) || MajiangPai.isFengpai(pai))) {
							if (!juefeng && !genfeng && player.getShoupaiCalculator().count(pai) == 1) {
								player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
							}
						}
					} else if (!guipaiTypeSet.contains(pai) && !gangmoShoupai.equals(pai)
							&& (MajiangPai.isZipai(guipaiType) || MajiangPai.isFengpai(guipaiType))) {
						if (!juefeng && !genfeng && player.getShoupaiCalculator().count(pai) == 1) {
							player.addActionCandidate(new MajiangDaAction(player.getId(), pai));
						}
					} else {

					}
				}
				if (!MajiangPai.baiban.equals(gangmoShoupai)) {
					if (!guipaiTypeSet.contains(gangmoShoupai)
							&& (MajiangPai.isZipai(gangmoShoupai) || MajiangPai.isFengpai(gangmoShoupai))) {
						if (juezhangStatisticsListener.ifJuezhang(gangmoShoupai)) {
							player.addActionCandidate(new MajiangDaAction(player.getId(), gangmoShoupai));
						} else if (!juefeng && juezhangStatisticsListener.ifMingPai(gangmoShoupai)
								&& player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
							player.addActionCandidate(new MajiangDaAction(player.getId(), gangmoShoupai));
						} else if (!juefeng && !genfeng && player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
							player.addActionCandidate(new MajiangDaAction(player.getId(), gangmoShoupai));
						}
					}
				} else if (!guipaiTypeSet.contains(gangmoShoupai)
						&& (MajiangPai.isZipai(guipaiType) || MajiangPai.isFengpai(guipaiType))) {
					if (juezhangStatisticsListener.ifJuezhang(gangmoShoupai)) {
						player.addActionCandidate(new MajiangDaAction(player.getId(), gangmoShoupai));
					} else if (!juefeng && juezhangStatisticsListener.ifMingPai(gangmoShoupai)
							&& player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
						player.addActionCandidate(new MajiangDaAction(player.getId(), gangmoShoupai));
					} else if (!juefeng && !genfeng && player.getShoupaiCalculator().count(gangmoShoupai) == 0) {
						player.addActionCandidate(new MajiangDaAction(player.getId(), gangmoShoupai));
					}
				} else {

				}
			}
			if (player.getActionCandidates().isEmpty()) {
				player.generateDaActions();
			}
		}
	}
}
