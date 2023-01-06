package com.jishi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
* @author 23049
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2023-01-03 19:48:00
*/

public interface CategoryService extends IService<Category> {

    public R add(@RequestBody Category category);

    public R<Page<Category>> pageSelect(int page, int pageSize);

    public  R  delete(Long id);

    public  R   updateCategory(Category category);

    public R<List<Category>> categorySelect(Integer type);

}
