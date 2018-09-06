package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.player.shoupai.gouxing.GouXingPanHu;

public class WenzhouMajiangGouXingPanHu extends GouXingPanHu {

	@Override
	protected boolean panHu(int chichuShunziCount, int pengchuKeziCount, int gangchuGangziCount, int shoupaiDanpaiCount,
			int shoupaiDuiziCount, int shoupaiKeziCount, int shoupaiGangziCount, int shoupaiShunziCount) {
		return (shoupaiDanpaiCount == 0 && shoupaiDuiziCount == 1)
				|| (shoupaiDanpaiCount == 1 && shoupaiDuiziCount == 8);
	}

}
