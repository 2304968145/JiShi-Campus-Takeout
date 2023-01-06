package com.jishi.mapper;

import com.jishi.common.R;
import com.jishi.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestBody;

/**
* @author 23049
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2023-01-03 19:48:00
* @Entity com.jishi.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {


}




