package com.jcwang.store.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jcwang.store.product.service.CategoryBrandRelationService;
import com.jcwang.store.product.vo.Catelog2Vo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcwang.store.utils.PageUtils;
import com.jcwang.store.utils.Query;

import com.jcwang.store.product.dao.CategoryDao;
import com.jcwang.store.product.entity.CategoryEntity;
import com.jcwang.store.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> paths = new ArrayList<>();

        //?????????????????????????????????
        List<Long> parentPath = findParentPath(catelogId, paths);

        //????????????????????????
        Collections.reverse(parentPath);

        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {

        //1?????????????????????id
        paths.add(catelogId);

        //??????????????????id????????????
        CategoryEntity byId = this.getById(catelogId);
        //???????????????????????????
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }

        return paths;
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1 ??????????????????
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 2 ??????????????????????????????
        // 2 1 ?????????????????????????????????id
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        })).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return menu1.getSort() - menu2.getSort();
        }).collect(Collectors.toList());

        return entities;
    }

    @Override
    public void remoMenuByIds(List<Long> asList) {
        // TODO ?????????????????????????????????????????????????????????

        // ?????????????????????????????????????????????
        baseMapper.deleteBatchIds(asList);

        // ???????????????????????????????????????????????????????????????yml????????????mybatisplus??????
    }

    // ?????????????????????????????????????????????????????????????????????
    @Cacheable(value = {"category"}, key = "#root.method.name") // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));

        return categoryEntities;
    }

    @Cacheable(value = "category", key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        // ???????????????????????????????????????????????????????????????
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1 ????????????1?????????
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        // 2 ????????????
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // ????????????1??????????????????2?????????
            List<CategoryEntity> category2Entities = getParentCid(selectList, v.getCatId());
            // ?????????????????????
            List<Catelog2Vo> catelog2Vos = null;

            if (category2Entities != null) {
                catelog2Vos = category2Entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // ?????????????????????????????????????????????VO
                    List<CategoryEntity> category3Entities = getParentCid(selectList, l2.getCatId());
                    if (category3Entities != null) {
                        List<Object> catelog3Vos = category3Entities.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catelog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(catelog3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        return collect;
    }


    // TODO ??????OutofDirectMemory?????????????????????
    // sprintboot2???0?????????????????????lettuce????????????redis????????????????????????netty?????????????????????
    // lettuce???bug??????netty?????????????????????-Xmx300m???netty?????????????????????????????????????????????-Xmx300m
    // ????????????-Dio.netty.maxDirectMemory????????????
    // ???????????????????????????-Dio.netty.maxDirectMemory?????????????????????????????????????????????????????????
    //         1 ??????lettuce????????????   2 ????????????jedis???
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {

        /**
         * ??????????????????json????????????????????????json?????????????????????????????????????????????????????????????????????????????????
         *
         * 1 ???????????????????????????????????????
         * 2 ????????????????????????????????????????????????????????????
         * 3 ??????????????????????????????
         */

        // ?????????????????????????????????????????????json?????????
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        // ???????????????????????????????????????
        if (StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedissionLock();
            // ?????????????????????????????????????????????????????????json???????????????
            String s = JSON.toJSONString(catalogJsonFromDB);
            stringRedisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);

            return catalogJsonFromDB;
        }

        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @CacheEvict:????????????
     * @CachePut:?????????????????????????????????
     * 1????????????????????????????????????@Caching
     * 2????????????????????????????????????????????? @CacheEvict(value = "category",allEntries = true)
     * 3???????????????????????????????????????????????????????????????
     * @param category
     */
    // @Caching(evict = {
    //         @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
    //         @CacheEvict(value = "category",key = "'getCatalogJson'")
    // })
    @CacheEvict(value = "category", allEntries = true) //???????????????????????????????????????value??????????????????????????????redis??????????????????????????????springboot??????????????????
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("catalogJson-lock");
        //????????????
        RLock rLock = readWriteLock.writeLock();

        try {
            rLock.lock();
            this.baseMapper.updateById(category);
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        //??????????????????????????????
        //????????????,???????????????????????????????????????
    }

    /**
     * ????????????????????????????????????????????????????????????
     * 1 ????????????
     * 2 ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissionLock() {
        // 1 ??????????????????????????????????????????
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock(); // ???????????????

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getCatalogJsonFromDB();
        } finally {
            lock.unlock();
        }

        return dataFromDb;
    }


    /**
     * ?????????????????????????????????
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {
        // ???????????????????????????????????????????????????????????????
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1 ????????????1?????????
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        // 2 ????????????
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // ????????????1??????????????????2?????????
            List<CategoryEntity> category2Entities = getParentCid(selectList, v.getCatId());
            // ?????????????????????
            List<Catelog2Vo> catelog2Vos = null;

            if (category2Entities != null) {
                catelog2Vos = category2Entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // ?????????????????????????????????????????????VO
                    List<CategoryEntity> category3Entities = getParentCid(selectList, l2.getCatId());
                    if (category3Entities != null) {
                        List<Object> catelog3Vos = category3Entities.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catelog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(catelog3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        return collect;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid().equals(parentCid);
        }).collect(Collectors.toList());

        return collect;
    }

    // ????????????????????????????????????
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            // ??????????????????
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}