package com.bricks.crawler.lianjia;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bricks.crawler.house.HouseInfo;
import com.bricks.lang.log.LogAble;
import com.bricks.utils.SimpleResult;

/**
 * @author bricks <long1795@gmail.com>
 */
@RestController
@RequestMapping(path = "/lianjia", produces = "application/json")
public class LianjiaCrawlerController implements LogAble {

	@Autowired
	MongoTemplate template;

	@RequestMapping(value = { "/", "" }, method = GET)
	public SimpleResult search(@RequestParam(defaultValue = "") String keyword, HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		Query q = new Query().with(new Sort("city", "zoon", "community", "price"));
		if (!"".equals(keyword.trim())) {
			q.addCriteria(new Criteria("zoon").not().in(Arrays.asList(keyword.split(","))));
		}
		return SimpleResult.succ().ext("data", template.find(q, HouseInfo.class));
	}

}
