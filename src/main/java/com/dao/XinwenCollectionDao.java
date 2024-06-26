package com.dao;

import com.entity.XinwenCollectionEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.XinwenCollectionView;

/**
 * 新闻收藏 Dao 接口
 *
 * @author 
 */
public interface XinwenCollectionDao extends BaseMapper<XinwenCollectionEntity> {

   List<XinwenCollectionView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
