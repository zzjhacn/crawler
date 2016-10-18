package com.bricks.crawler.lianjia;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bricks.crawler.house.HouseInfo;
import com.bricks.lang.log.LogAble;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author bricks <long1795@gmail.com>
 */
@Service
public class LianjiaCrawler implements LogAble {

	Map<String, Boolean> history = Maps.newConcurrentMap();

	@Autowired
	MongoTemplate template;

	@Scheduled(cron = "0 0 0/6 * * ?")
	public void update() {
		log().info("Refreshing...");
		Map<String, HouseInfo> all = template.findAll(HouseInfo.class).stream().collect(Collectors.toMap(HouseInfo::getId, (v) -> {
			return v;
		}));

		List<HouseInfo> newer;
		try {
			newer = crawler("/ershoufang/pudongxinqu/d1b200to450m60to400o1y3");
		} catch (IOException e) {
			err(e);
			return;
		}

		newer.forEach((h) -> {
			HouseInfo oh = all.remove(h.getId());
			if (oh == null) {
				h.setCreateTime(new Date());
			}
			h.setUpdateTime(new Date());
			template.save(h);
		});

		all.forEach((id, h) -> {
			h.addMemo("已下架");
			h.setUpdateTime(new Date());
			template.save(h);
		});
		log().info("[{}] newer house refreshed.", newer.size());
		history.clear();
	}

	List<HouseInfo> crawler(String url) throws IOException {
		List<HouseInfo> hs = Lists.newArrayList();

		Document d = Jsoup.connect(LianjiaCfg.HOST + url).post();

		history.put(url, true);
		collectPages(d);

		hs.addAll(filter(d));

		history.forEach((h, v) -> {
			if (!v) {
				try {
					hs.addAll(crawler(h));
				} catch (Exception e) {
					err(e);
				}
			}
		});

		return hs;
	}

	void collectPages(Document d) {
		Elements pages = d.getElementsByClass("page-box");
		if (!pages.isEmpty()) {
			pages.first().getElementsByTag("a").forEach(p -> {
				String href = p.attr("href");
				if (!history.containsKey(href)) {
					history.put(href, false);
				}
			});
		}
	}

	List<HouseInfo> filter(Document d) {
		List<HouseInfo> hs = Lists.newArrayList();
		Element lst = d.getElementById("house-lst");
		lst.getElementsByTag("li").forEach(n -> {
			HouseInfo h = new HouseInfo();

			Element info = n.getElementsByClass("info-panel").first();
			String detailUrl = info.getElementsByTag("a").first().attr("href");
			String where = info.getElementsByClass("where").first().text();
			String detail = info.getElementsByClass("con").first().text().replaceAll(" ", "");
			String price = info.getElementsByClass("col-3").first().text();
			try {
				Matcher m = LianjiaCfg.ID_PATTERN.matcher(detailUrl);
				m.find();
				h.setId(m.group(1));

				m = LianjiaCfg.WHERE_PATTERN.matcher(where);
				m.find();
				h.setCommunity(m.group(1));
				h.setRooms(Integer.valueOf(m.group(2)));
				h.setType(m.group(2) + "室" + m.group(3) + "厅");
				h.setArea(new BigDecimal(m.group(4)));

				m = LianjiaCfg.DETAIL_PATTERN.matcher(detail);
				m.find();
				h.setZoon(m.group(1));
				h.setFloors(Integer.valueOf(m.group(3)));
				h.setYear(Integer.valueOf(m.group(4)));

				m = LianjiaCfg.PRICE_PATTERN.matcher(price);
				m.find();
				h.setPrice(new BigDecimal(m.group(1)));

				hs.add(h);
			} catch (Exception e) {
				err(e);
			}
		});
		return hs;
	}
}
