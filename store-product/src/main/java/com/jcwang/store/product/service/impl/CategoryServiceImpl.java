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

        //递归查询是否还有父节点
        List<Long> parentPath = findParentPath(catelogId, paths);

        //进行一个逆序排列
        Collections.reverse(parentPath);

        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {

        //1、收集当前节点id
        paths.add(catelogId);

        //根据当前分类id查询信息
        CategoryEntity byId = this.getById(catelogId);
        //如果当前不是父分类
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }

        return paths;
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1 查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 2 组装成父子的树形结构
        // 2 1 找出一级分类，没有父级id
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
        // TODO 检查当前删除的菜单，是否被别的地方引用

        // 这个是物理删除，删除了真的没了
        baseMapper.deleteBatchIds(asList);

        // 我们要做逻辑删除，可以加一列标志，来指示，yml中配置，mybatisplus支持
    }

    // 每一个需要缓存的数据都来制定放到哪个名字的缓存
    @Cacheable(value = {"category"}, key = "#root.method.name") // 代表当前方法的结果需要缓存，如果缓存中有，方法不调用；如果缓存中没有，会调用方法，最后将方法的结果返回
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));

        return categoryEntities;
    }

    @Cacheable(value = "category", key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        // 不是查询三次，而是首先把所有的目录都查询到
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        // 2 封装数据
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查到这个1级分类的所有2级分类
            List<CategoryEntity> category2Entities = getParentCid(selectList, v.getCatId());
            // 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;

            if (category2Entities != null) {
                catelog2Vos = category2Entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 找出二级分类的三级分类，封装成VO
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


    // TODO 会有OutofDirectMemory，堆外内存溢出
    // sprintboot2。0，以后默认使用lettuce作为操作redis的客户端，他使用netty进行网络通信。
    // lettuce的bug导致netty堆外内存溢出，-Xmx300m；netty如果没有置顶堆外内存，默认使用-Xmx300m
    // 可以通过-Dio.netty.maxDirectMemory进行设置
    // 解决方案：不能使用-Dio.netty.maxDirectMemory调大内存，这个只是延缓了出现异常的时间
    //         1 升级lettuce客户端，   2 切换使用jedis。
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {

        /**
         * 给缓存中放入json字符串，拿出来的json字符串，还能逆转为能用的对象类型。（序列化与反序列化）
         *
         * 1 、空结果缓存，解决缓存穿透
         * 2 、设置过期时间（加随机值），解决缓存雪崩
         * 3 、加锁：解决缓存击穿
         */

        // 首先查询缓存，缓存中存的数据是json字符串
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        // 缓存中没有数据，查询数据库
        if (StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedissionLock();
            // 将查询到的数据再放入缓存，将对象转化为json放在缓存中
            String s = JSON.toJSONString(catalogJsonFromDB);
            stringRedisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);

            return catalogJsonFromDB;
        }

        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        return result;
    }

    /**
     * 级联更新所有关联的数据
     *
     * @CacheEvict:失效模式
     * @CachePut:双写模式，需要有返回值
     * 1、同时进行多种缓存操作：@Caching
     * 2、指定删除某个分区下的所有数据 @CacheEvict(value = "category",allEntries = true)
     * 3、存储同一类型的数据，都可以指定为同一分区
     * @param category
     */
    // @Caching(evict = {
    //         @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
    //         @CacheEvict(value = "category",key = "'getCatalogJson'")
    // })
    @CacheEvict(value = "category", allEntries = true) //删除某个分区下的所有数据，value指定的是分区，分区在redis中是不显示的，只不过springboot中自己维护了
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("catalogJson-lock");
        //创建写锁
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

        //同时修改缓存中的数据
        //删除缓存,等待下一次主动查询进行更新
    }

    /**
     * 缓存里面的数据，如何容数据库中过保持一致
     * 1 双写模式
     * 2 失效模式，先更新数据库，再删除缓存，不过更新数据库期间，会读取到旧数据
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissionLock() {
        // 1 锁的名字，锁的粒度，越细越快
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock(); // 阻塞式加锁

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getCatalogJsonFromDB();
        } finally {
            lock.unlock();
        }

        return dataFromDb;
    }


    /**
     * 从数据库查询并封装数据
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {
        // 不是查询三次，而是首先把所有的目录都查询到
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);

        // 2 封装数据
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查到这个1级分类的所有2级分类
            List<CategoryEntity> category2Entities = getParentCid(selectList, v.getCatId());
            // 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;

            if (category2Entities != null) {
                catelog2Vos = category2Entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 找出二级分类的三级分类，封装成VO
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

    // 递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            // 递归找子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}