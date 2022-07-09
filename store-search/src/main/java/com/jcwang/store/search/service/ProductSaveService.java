package com.jcwang.store.search.service;

import com.jcwang.store.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;

}
