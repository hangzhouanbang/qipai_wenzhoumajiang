package com.anbang.qipai.wenzhoumajiang.cqrs.c.domain;

import com.dml.majiang.pai.MajiangPai;
import com.dml.majiang.pai.fenzu.GangType;
import com.dml.majiang.player.MajiangPlayer;

public class WenzhouMajiangGang {
	private int minggangCount;
	private int angangCount;
	private MajiangPai[] yijiupaiArray;
	private MajiangPai[] erbapaiArray;
	private MajiangPai[] fengzipaiArray;
	private int totalscore;// 总分
	private int value;// 单人结算分

	public WenzhouMajiangGang() {

	}

	public WenzhouMajiangGang(MajiangPlayer player) {
		yijiupaiArray = new MajiangPai[] { MajiangPai.yiwan, MajiangPai.jiuwan, MajiangPai.yitong, MajiangPai.jiutong,
				MajiangPai.yitiao, MajiangPai.jiutiao };
		erbapaiArray = new MajiangPai[] { MajiangPai.erwan, MajiangPai.sanwan, MajiangPai.siwan, MajiangPai.wuwan,
				MajiangPai.liuwan, MajiangPai.qiwan, MajiangPai.bawan,

				MajiangPai.ertong, MajiangPai.santong, MajiangPai.sitong, MajiangPai.wutong, MajiangPai.liutong,
				MajiangPai.qitong, MajiangPai.batong,

				MajiangPai.ertiao, MajiangPai.santiao, MajiangPai.sitiao, MajiangPai.wutiao, MajiangPai.liutiao,
				MajiangPai.qitiao, MajiangPai.batiao };
		fengzipaiArray = new MajiangPai[] { MajiangPai.dongfeng, MajiangPai.nanfeng, MajiangPai.xifeng,
				MajiangPai.beifeng, MajiangPai.hongzhong, MajiangPai.facai };
		minggangCount = 0;
		for (int i = 0; i < yijiupaiArray.length; i++) {
			if (player.ifGangchu(yijiupaiArray[i], GangType.gangdachu)
					|| player.ifGangchu(yijiupaiArray[i], GangType.kezigangmo)
					|| player.ifGangchu(yijiupaiArray[i], GangType.kezigangshoupai)) {
				minggangCount++;
			}
		}
		for (int i = 0; i < erbapaiArray.length; i++) {
			if (player.ifGangchu(erbapaiArray[i], GangType.gangdachu)
					|| player.ifGangchu(erbapaiArray[i], GangType.kezigangmo)
					|| player.ifGangchu(erbapaiArray[i], GangType.kezigangshoupai)) {
				minggangCount++;
			}
		}
		for (int i = 0; i < fengzipaiArray.length; i++) {
			if (player.ifGangchu(fengzipaiArray[i], GangType.gangdachu)
					|| player.ifGangchu(fengzipaiArray[i], GangType.kezigangmo)
					|| player.ifGangchu(fengzipaiArray[i], GangType.kezigangshoupai)) {
				minggangCount++;
			}
		}
		angangCount = 0;
		for (int i = 0; i < yijiupaiArray.length; i++) {
			if (player.ifGangchu(yijiupaiArray[i], GangType.shoupaigangmo)
					|| player.ifGangchu(yijiupaiArray[i], GangType.gangsigeshoupai)) {
				angangCount++;
			}
		}

		for (int i = 0; i < erbapaiArray.length; i++) {
			if (player.ifGangchu(erbapaiArray[i], GangType.shoupaigangmo)
					|| player.ifGangchu(erbapaiArray[i], GangType.gangsigeshoupai)) {
				angangCount++;
			}
		}

		for (int i = 0; i < fengzipaiArray.length; i++) {
			if (player.ifGangchu(fengzipaiArray[i], GangType.shoupaigangmo)
					|| player.ifGangchu(fengzipaiArray[i], GangType.gangsigeshoupai)) {
				angangCount++;
			}
		}
	}

	public void calculate(int playerCount, boolean gangsuanfen) {
		if (gangsuanfen) {
			value = minggangCount + angangCount * 2;
			totalscore = (minggangCount + angangCount * 2) * (playerCount - 1);
		}
	}

	public int jiesuan(int delta) {
		return totalscore += delta;
	}

	public int getMinggangCount() {
		return minggangCount;
	}

	public void setMinggangCount(int minggangCount) {
		this.minggangCount = minggangCount;
	}

	public int getAngangCount() {
		return angangCount;
	}

	public void setAngangCount(int angangCount) {
		this.angangCount = angangCount;
	}

	public MajiangPai[] getYijiupaiArray() {
		return yijiupaiArray;
	}

	public void setYijiupaiArray(MajiangPai[] yijiupaiArray) {
		this.yijiupaiArray = yijiupaiArray;
	}

	public MajiangPai[] getErbapaiArray() {
		return erbapaiArray;
	}

	public void setErbapaiArray(MajiangPai[] erbapaiArray) {
		this.erbapaiArray = erbapaiArray;
	}

	public MajiangPai[] getFengzipaiArray() {
		return fengzipaiArray;
	}

	public void setFengzipaiArray(MajiangPai[] fengzipaiArray) {
		this.fengzipaiArray = fengzipaiArray;
	}

	public int getTotalscore() {
		return totalscore;
	}

	public void setTotalscore(int totalscore) {
		this.totalscore = totalscore;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
