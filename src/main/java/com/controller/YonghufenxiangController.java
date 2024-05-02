
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 用户分享
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/yonghufenxiang")
public class YonghufenxiangController {
    private static final Logger logger = LoggerFactory.getLogger(YonghufenxiangController.class);

    @Autowired
    private YonghufenxiangService yonghufenxiangService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service

    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = yonghufenxiangService.queryPage(params);

        //字典表数据转换
        List<YonghufenxiangView> list =(List<YonghufenxiangView>)page.getList();
        for(YonghufenxiangView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        YonghufenxiangEntity yonghufenxiang = yonghufenxiangService.selectById(id);
        if(yonghufenxiang !=null){
            //entity转view
            YonghufenxiangView view = new YonghufenxiangView();
            BeanUtils.copyProperties( yonghufenxiang , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody YonghufenxiangEntity yonghufenxiang, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,yonghufenxiang:{}",this.getClass().getName(),yonghufenxiang.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<YonghufenxiangEntity> queryWrapper = new EntityWrapper<YonghufenxiangEntity>()
            .eq("yonghufenxiang_name", yonghufenxiang.getYonghufenxiangName())
            .eq("yonghufenxiang_types", yonghufenxiang.getYonghufenxiangTypes())
            .eq("yonghufenxiang_video", yonghufenxiang.getYonghufenxiangVideo())
            .eq("zan_number", yonghufenxiang.getZanNumber())
            .eq("cai_number", yonghufenxiang.getCaiNumber())
            .eq("clicknum", yonghufenxiang.getClicknum())
            .eq("insert_time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
            .eq("yonghufenxiang_yesno_types", yonghufenxiang.getYonghufenxiangYesnoTypes())
            .eq("yonghufenxiang_yesno_text", yonghufenxiang.getYonghufenxiangYesnoText())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YonghufenxiangEntity yonghufenxiangEntity = yonghufenxiangService.selectOne(queryWrapper);
        if(yonghufenxiangEntity==null){
            yonghufenxiang.setInsertTime(new Date());
            yonghufenxiang.setYonghufenxiangYesnoTypes(1);
            yonghufenxiang.setCreateTime(new Date());
            yonghufenxiangService.insert(yonghufenxiang);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody YonghufenxiangEntity yonghufenxiang, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,yonghufenxiang:{}",this.getClass().getName(),yonghufenxiang.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        //根据字段查询是否有相同数据
        Wrapper<YonghufenxiangEntity> queryWrapper = new EntityWrapper<YonghufenxiangEntity>()
            .notIn("id",yonghufenxiang.getId())
            .andNew()
            .eq("yonghufenxiang_name", yonghufenxiang.getYonghufenxiangName())
            .eq("yonghufenxiang_types", yonghufenxiang.getYonghufenxiangTypes())
            .eq("yonghufenxiang_video", yonghufenxiang.getYonghufenxiangVideo())
            .eq("zan_number", yonghufenxiang.getZanNumber())
            .eq("cai_number", yonghufenxiang.getCaiNumber())
            .eq("clicknum", yonghufenxiang.getClicknum())
            .eq("insert_time", yonghufenxiang.getInsertTime())
            .eq("yonghufenxiang_yesno_types", yonghufenxiang.getYonghufenxiangYesnoTypes())
            .eq("yonghufenxiang_yesno_text", yonghufenxiang.getYonghufenxiangYesnoText())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YonghufenxiangEntity yonghufenxiangEntity = yonghufenxiangService.selectOne(queryWrapper);
        if("".equals(yonghufenxiang.getYonghufenxiangPhoto()) || "null".equals(yonghufenxiang.getYonghufenxiangPhoto())){
                yonghufenxiang.setYonghufenxiangPhoto(null);
        }
        if("".equals(yonghufenxiang.getYonghufenxiangVideo()) || "null".equals(yonghufenxiang.getYonghufenxiangVideo())){
                yonghufenxiang.setYonghufenxiangVideo(null);
        }
        if(yonghufenxiangEntity==null){
            yonghufenxiangService.updateById(yonghufenxiang);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        yonghufenxiangService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        try {
            List<YonghufenxiangEntity> yonghufenxiangList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            YonghufenxiangEntity yonghufenxiangEntity = new YonghufenxiangEntity();
//                            yonghufenxiangEntity.setYonghufenxiangName(data.get(0));                    //分享标题 要改的
//                            yonghufenxiangEntity.setYonghufenxiangTypes(Integer.valueOf(data.get(0)));   //分享类型 要改的
//                            yonghufenxiangEntity.setYonghufenxiangPhoto("");//照片
//                            yonghufenxiangEntity.setYonghufenxiangVideo(data.get(0));                    //分享视频 要改的
//                            yonghufenxiangEntity.setZanNumber(Integer.valueOf(data.get(0)));   //赞 要改的
//                            yonghufenxiangEntity.setCaiNumber(Integer.valueOf(data.get(0)));   //踩 要改的
//                            yonghufenxiangEntity.setClicknum(Integer.valueOf(data.get(0)));   //点击次数 要改的
//                            yonghufenxiangEntity.setInsertTime(date);//时间
//                            yonghufenxiangEntity.setYonghufenxiangYesnoTypes(Integer.valueOf(data.get(0)));   //审核状态 要改的
//                            yonghufenxiangEntity.setYonghufenxiangYesnoText(data.get(0));                    //审核原因 要改的
//                            yonghufenxiangEntity.setYonghufenxiangContent("");//照片
//                            yonghufenxiangEntity.setCreateTime(date);//时间
                            yonghufenxiangList.add(yonghufenxiangEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        yonghufenxiangService.insertBatch(yonghufenxiangList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }





    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = yonghufenxiangService.queryPage(params);

        //字典表数据转换
        List<YonghufenxiangView> list =(List<YonghufenxiangView>)page.getList();
        for(YonghufenxiangView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        YonghufenxiangEntity yonghufenxiang = yonghufenxiangService.selectById(id);
            if(yonghufenxiang !=null){


                //entity转view
                YonghufenxiangView view = new YonghufenxiangView();
                BeanUtils.copyProperties( yonghufenxiang , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody YonghufenxiangEntity yonghufenxiang, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,yonghufenxiang:{}",this.getClass().getName(),yonghufenxiang.toString());
        Wrapper<YonghufenxiangEntity> queryWrapper = new EntityWrapper<YonghufenxiangEntity>()
            .eq("yonghufenxiang_name", yonghufenxiang.getYonghufenxiangName())
            .eq("yonghufenxiang_types", yonghufenxiang.getYonghufenxiangTypes())
            .eq("yonghufenxiang_video", yonghufenxiang.getYonghufenxiangVideo())
            .eq("zan_number", yonghufenxiang.getZanNumber())
            .eq("cai_number", yonghufenxiang.getCaiNumber())
            .eq("clicknum", yonghufenxiang.getClicknum())
            .eq("yonghufenxiang_yesno_types", yonghufenxiang.getYonghufenxiangYesnoTypes())
            .eq("yonghufenxiang_yesno_text", yonghufenxiang.getYonghufenxiangYesnoText())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YonghufenxiangEntity yonghufenxiangEntity = yonghufenxiangService.selectOne(queryWrapper);
        if(yonghufenxiangEntity==null){
            yonghufenxiang.setInsertTime(new Date());
            yonghufenxiang.setYonghufenxiangYesnoTypes(1);
            yonghufenxiang.setCreateTime(new Date());
        yonghufenxiangService.insert(yonghufenxiang);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


}
