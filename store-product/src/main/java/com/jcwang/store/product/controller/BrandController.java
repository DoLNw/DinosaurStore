package com.jcwang.store.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.jcwang.store.valid.AddGroup;
import com.jcwang.store.valid.UpdateGourp;
import com.jcwang.store.valid.UpdateStatusGourp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.jcwang.store.product.entity.BrandEntity;
import com.jcwang.store.product.service.BrandService;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author jcwang
 * @email jcwang0717@163.com
 * @date 2022-06-23 09:59:08
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
//        if (result.hasErrors()) {
//            Map<String, String> map = new HashMap<>();
//            // 获取校验的错误结果
//            result.getFieldErrors().forEach((item) -> {
//                String message = item.getDefaultMessage();
//                String field = item.getField();
//                map.put(field, message);
//            });
//            return R.error(400, "提交的数据不合法").put("data", map);
//        } else {
//            brandService.save(brand);


        //  出现的异常@ControllerAdvice来集中处理

        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGourp.class) @RequestBody BrandEntity brand){
		brandService.updateById(brand);

        return R.ok();
    }

    @RequestMapping("/update/status")
    // @RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGourp.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    @GetMapping("/infos")
    public R info(@RequestParam("brandIds") List<Long> brandIds) {
        List<BrandEntity> brandEntities = brandService.getBrandsByIds(brandIds);

//        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brandEntities);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
