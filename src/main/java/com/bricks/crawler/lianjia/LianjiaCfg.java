package com.bricks.crawler.lianjia;

import java.util.regex.Pattern;

/**
 * @author bricks <long1795@gmail.com>
 */
public class LianjiaCfg {

	public static final String HOST = "http://sh.lianjia.com";

	public static final Pattern PRICE_PATTERN = Pattern.compile("([0-9.]*?)万");
	public static final Pattern ID_PATTERN = Pattern.compile("/ershoufang/sh([0-9]*?)\\.html");
	public static final Pattern WHERE_PATTERN = Pattern.compile("(.*) .*([0-9])室([0-9])厅.*?([0-9.]*?)平");
	public static final Pattern DETAIL_PATTERN = Pattern.compile("(.*)\\|(.*)/([0-9]*?)层.*?([0-9]*?)年建");
}
