package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dml.majiang.ju.Ju;
import com.dml.majiang.ju.result.JuResult;
import com.dml.majiang.ju.result.JuResultBuilder;
import com.dml.majiang.pan.result.PanResult;

public class WenzhouMajiangJuResultBuilder implements JuResultBuilder {

	@Override
	public JuResult buildJuResult(Ju ju) {
		WenzhouMajiangJuResult wenzhouMajiangJuResult = new WenzhouMajiangJuResult();
		wenzhouMajiangJuResult.setFinishedPanCount(ju.countFinishedPan());
		if (ju.countFinishedPan() > 0) {
			Map<String, WenzhouMajiangJuPlayerResult> juPlayerResultMap = new HashMap<>();
			for (PanResult panResult : ju.getFinishedPanResultList()) {
				WenzhouMajiangPanResult wenzhouMajiangPanResult = (WenzhouMajiangPanResult) panResult;
				for (WenzhouMajiangPanPlayerResult panPlayerResult : wenzhouMajiangPanResult.getPlayerResultList()) {
					WenzhouMajiangJuPlayerResult juPlayerResult = juPlayerResultMap.get(panPlayerResult.getPlayerId());
					if (juPlayerResult == null) {
						juPlayerResult = new WenzhouMajiangJuPlayerResult();
						juPlayerResult.setPlayerId(panPlayerResult.getPlayerId());
						juPlayerResultMap.put(panPlayerResult.getPlayerId(), juPlayerResult);
					}
					if (wenzhouMajiangPanResult.ifPlayerHu(panPlayerResult.getPlayerId())) {
						juPlayerResult.increaseHuCount();
					}
					juPlayerResult.increaseCaishenCount(
							wenzhouMajiangPanResult.playerGuipaiCount(panPlayerResult.getPlayerId()));
					if (panPlayerResult.getHufan().getValue() == 4) {
						juPlayerResult.increaseShuangfanCount();
					}
					juPlayerResult.tryAndUpdateMaxScore(panPlayerResult.getScore());
					juPlayerResult.increaseTotalScore(panPlayerResult.getScore());
				}
			}

			WenzhouMajiangJuPlayerResult dayingjia = null;
			WenzhouMajiangJuPlayerResult datuhao = null;
			for (WenzhouMajiangJuPlayerResult juPlayerResult : juPlayerResultMap.values()) {
				if (dayingjia == null) {
					dayingjia = juPlayerResult;
				} else {
					if (juPlayerResult.getTotalScore() > dayingjia.getTotalScore()) {
						dayingjia = juPlayerResult;
					}
				}

				if (datuhao == null) {
					datuhao = juPlayerResult;
				} else {
					if (juPlayerResult.getTotalScore() < datuhao.getTotalScore()) {
						datuhao = juPlayerResult;
					}
				}
			}
			wenzhouMajiangJuResult.setDatuhaoId(datuhao.getPlayerId());
			wenzhouMajiangJuResult.setDayingjiaId(dayingjia.getPlayerId());
			wenzhouMajiangJuResult.setPlayerResultList(new ArrayList<>(juPlayerResultMap.values()));
		}
		return wenzhouMajiangJuResult;
	}

}
