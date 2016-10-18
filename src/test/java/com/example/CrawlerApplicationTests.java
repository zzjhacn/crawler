package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.bricks.crawler.CrawlerApplication;
import com.bricks.crawler.house.HouseInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerApplication.class)
public class CrawlerApplicationTests {

	@Autowired
	MongoTemplate template;

	@Test
	public void contextLoads() {
		template.find(new Query().with(new Sort("city", "zoon", "community", "price")), HouseInfo.class).forEach(h -> {
			System.out.println(h.getCommunity());
		});
	}

}
