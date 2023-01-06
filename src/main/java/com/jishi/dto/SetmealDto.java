package com.jishi.dto;

import com.jishi.entity.Setmeal;
import com.jishi.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish>  setmealDishes;

    private  String  categoryName;

}
