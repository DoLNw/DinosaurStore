package com.jcwang.store.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcwang.store.product.entity.BrandEntity;
import com.jcwang.store.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

}
