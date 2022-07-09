package com.jcwang.store.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcwang.store.product.entity.BrandEntity;
import com.jcwang.store.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreProductApplicationTests {
	@Autowired
	BrandService brandService;

	@Test
	public void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
//		brandEntity.setDescript("asd");
//		brandEntity.setName("华为");
//		brandService.save(brandEntity);
//		System.out.println("保存成功");

//		brandEntity.setBrandId(1L);
//		brandEntity.setDescript("更新后的描述");
//		brandService.updateById(brandEntity);

		List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		list.forEach((item) -> {
			System.out.println(item);
		});
	}


	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void testSringRedisTemplate() {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set("hello", "world_" + UUID.randomUUID().toString());

		String hello = ops.get("hello");
		System.out.println("之前保存的数据是：" + hello);
	}

}
