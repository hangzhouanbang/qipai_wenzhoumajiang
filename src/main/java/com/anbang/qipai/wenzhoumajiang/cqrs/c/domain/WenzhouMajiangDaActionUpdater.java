package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.Set;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.Shunzi;
import com.dml.majiang.pan.Pan;
import com.dml.majiang.player.MajiangPlayer;
import com.dml.majiang.player.action.chi.MajiangChiAction;
import com.dml.majiang.player.action.da.MajiangDaAction;
import com.dml.majiang.player.action.da.MajiangPlayerDaActionUpdater;
import com.dml.majiang.player.action.hu.MajiangHuAction;
import com.dml.majiang.player.action.listener.comprehensive.DianpaoDihuOpportunityDetector;
import com.dml.majiang.player.action.mo.LundaoMopai;
import com.dml.majiang.player.action.mo.MajiangMoAction;
import com.dml.majiang.player.shoupai.ShoupaiCalculator;
import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangDaActionUpdater implements MajiangPlayerDaActionUpdater {

	@Override
	public void updateActions(MajiangDaAction daAction, Ju ju) throws Exception {
		Pan currentPan = ju.getCurrentPan();
		MajiangPlayer daPlayer = currentPan.findPlayerById(daAction.getActionPlayerId());
		// 是否是地胡
		DianpaoDihuOpportunityDetector dianpaoDihuOpportunityDetector = ju.getActionStatisticsListenerManager()
				.findListener(DianpaoDihuOpportunityDetector.class);
		boolean couldDihu = dianpaoDihuOpportunityDetector.ifDihuOpportunity();
		daPlayer.clearActionCandidates();

		MajiangPlayer xiajiaPlayer = currentPan.findXiajia(daPlayer);
		xiajiaPlayer.clearActionCandidates();
		Set<MajiangPai> guipaiTypeSet = xiajiaPlayer.getGuipaiTypeSet();
		MajiangPai[] guipaiType = new MajiangPai[guipaiTypeSet.size()];
		MajiangPai daPai = daAction.getPai();
		if (daPai.equals(MajiangPai.baiban) && guipaiType.length > 0) {
			daPai = guipaiType[0];
		}
		// 下家可以吃
		ShoupaiCalculator shoupaiCalculator = xiajiaPlayer.getShoupaiCalculator();
		Shunzi shunzi1 = shoupaiCalculator.tryAndMakeShunziWithPai1(daPai);
		if (shunzi1 != null) {
			shunzi1.setPai1(daPai);
			xiajiaPlayer.addActionCandidate(
					new MajiangChiAction(xiajiaPlayer.getId(), daAction.getActionPlayerId(), daPai, shunzi1));
		}

		Shunzi shunzi2 = shoupaiCalculator.tryAndMakeShunziWithPai2(daPai);
		if (shunzi2 != null) {
			shunzi2.setPai2(daPai);
			xiajiaPlayer.addActionCandidate(
					new MajiangChiAction(xiajiaPlayer.getId(), daAction.getActionPlayerId(), daPai, shunzi2));
		}

		Shunzi shunzi3 = shoupaiCalculator.tryAndMakeShunziWithPai3(daPai);
		if (shunzi3 != null) {
			shunzi3.setPai3(daPai);
			xiajiaPlayer.addActionCandidate(
					new MajiangChiAction(xiajiaPlayer.getId(), daAction.getActionPlayerId(), daPai, shunzi3));
		}

		boolean anyPlayerHu = false;
		while (true) {
			if (!xiajiaPlayer.getId().equals(daAction.getActionPlayerId())) {
				// 其他的可以碰杠胡
				xiajiaPlayer.tryPengAndGenerateCandidateAction(daAction.getActionPlayerId(), daAction.getPai());
				xiajiaPlayer.tryGangdachuAndGenerateCandidateAction(daAction.getActionPlayerId(), daAction.getPai());

				if (!anyPlayerHu) {
					// 点炮胡
					WenzhouMajiangPanResultBuilder wenzhouMajiangJuResultBuilder = (WenzhouMajiangPanResultBuilder) ju
							.getCurrentPanResultBuilder();
					GouXingPanHu gouXingPanHu = ju.getGouXingPanHu();
					// 先把这张牌放入计算器
					xiajiaPlayer.getShoupaiCalculator().addPai(daAction.getPai());
					WenzhouMajiangHu bestHu = WenzhouMajiangJiesuanCalculator.calculateBestDianpaoHu(couldDihu,
							gouXingPanHu, xiajiaPlayer, false, daAction.getPai());// 少中发
					// 再把这张牌拿出计算器
					xiajiaPlayer.getShoupaiCalculator().removePai(daAction.getPai());
					if (bestHu != null) {
						bestHu.setZimo(false);
						bestHu.setDianpaoPlayerId(daPlayer.getId());
						xiajiaPlayer.addActionCandidate(new MajiangHuAction(xiajiaPlayer.getId(), bestHu));
						anyPlayerHu = true;
					}
				}

				xiajiaPlayer.checkAndGenerateGuoCandidateAction();
			} else {
				break;
			}
			xiajiaPlayer = currentPan.findXiajia(xiajiaPlayer);
			xiajiaPlayer.clearActionCandidates();
		}

		// 如果所有玩家啥也做不了,那就下家摸牌
		if (currentPan.allPlayerHasNoActionCandidates()) {
			xiajiaPlayer = currentPan.findXiajia(daPlayer);
			xiajiaPlayer.addActionCandidate(new MajiangMoAction(xiajiaPlayer.getId(), new LundaoMopai()));
		}

		// TODO 接着做

	}

}
