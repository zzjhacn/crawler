package com.example;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import org.junit.Test;

import com.bricks.crawler.lianjia.LianjiaCfg;

/**
 * @author bricks <long1795@gmail.com>
 */
public class PatternTest {

	String where = "三林世博家园（东书房路560弄）   2室1厅   74.44平  ";
	String detail = "浦东三林|低层/17层|朝|2006年建";
	String price = "405万 54406元/平";
	String detailUrl = "/ershoufang/sh4238669.html";

	@Test
	public void test() throws Exception {

		Matcher m = LianjiaCfg.ID_PATTERN.matcher(detailUrl);
		m.find();
		System.out.println(m.group(1));

		m = LianjiaCfg.WHERE_PATTERN.matcher(where);
		m.find();
		System.out.println(m.group(1));
		System.out.println(Integer.valueOf(m.group(2)));
		System.out.println(m.group(2) + "室" + m.group(3) + "厅");
		System.out.println(new BigDecimal(m.group(4)));

		m = LianjiaCfg.DETAIL_PATTERN.matcher(detail);
		m.find();
		System.out.println(Integer.valueOf(m.group(3)));
		System.out.println(Integer.valueOf(m.group(4)));

		m = LianjiaCfg.PRICE_PATTERN.matcher(price);
		m.find();
		System.out.println(new BigDecimal(m.group(1)));

	}
}
