package com.ape.apeadmin.controller.task;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeTaskTest;
import com.ape.apesystem.domain.ApeTestItem;
import com.ape.apesystem.service.ApeTaskTestService;
import com.ape.apesystem.service.ApeTestItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 课程题目controller
 * @date 2024/05/13 04:38
 */
@Controller
@ResponseBody
@RequestMapping("test")
public class ApeTaskTestController {

    @Autowired
    private ApeTaskTestService apeTaskTestService;
    @Autowired
    private ApeTestItemService apeTestItemService;

    /** 分页获取课程题目 */
    @Log(name = "分页获取课程题目", type = BusinessType.OTHER)
    @PostMapping("getApeTaskTestPage")
    public Result getApeTaskTestPage(@RequestBody ApeTaskTest apeTaskTest) {
        Page<ApeTaskTest> page = new Page<>(apeTaskTest.getPageNumber(),apeTaskTest.getPageSize());
        QueryWrapper<ApeTaskTest> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeTaskTest.getTaskId()),ApeTaskTest::getTaskId,apeTaskTest.getTaskId())
                .like(StringUtils.isNotBlank(apeTaskTest.getTag()),ApeTaskTest::getTag,apeTaskTest.getTag())
                .like(StringUtils.isNotBlank(apeTaskTest.getTitle()),ApeTaskTest::getTitle,apeTaskTest.getTitle())
                .eq(apeTaskTest.getType() != null,ApeTaskTest::getType,apeTaskTest.getType());
        Page<ApeTaskTest> apeTaskTestPage = apeTaskTestService.page(page, queryWrapper);
        return Result.success(apeTaskTestPage);
    }

    /** 根据id获取课程题目 */
    @Log(name = "根据id获取课程题目", type = BusinessType.OTHER)
    @GetMapping("getApeTaskTestById")
    public Result getApeTaskTestById(@RequestParam("id")String id) {
        ApeTaskTest apeTaskTest = apeTaskTestService.getById(id);
        return Result.success(apeTaskTest);
    }

    /** 根据课程id获取课程题目 */
    @Log(name = "根据课程id获取课程题目", type = BusinessType.OTHER)
    @GetMapping("getApeTaskTestByTaskId")
    public Result getApeTaskTestByTaskId(@RequestParam("id")String id) {
        QueryWrapper<ApeTaskTest> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTaskTest::getTaskId,id);
        List<ApeTaskTest> testList = apeTaskTestService.list(queryWrapper);
        return Result.success(testList);
    }

    /** 保存课程题目 */
    @Log(name = "保存课程题目", type = BusinessType.INSERT)
    @PostMapping("saveApeTaskTest")
    public Result saveApeTaskTest(@RequestBody ApeTaskTest apeTaskTest) {
        boolean save = apeTaskTestService.save(apeTaskTest);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑课程题目 */
    @Log(name = "编辑课程题目", type = BusinessType.UPDATE)
    @PostMapping("editApeTaskTest")
    @Transactional(rollbackFor = Exception.class)
    public Result editApeTaskTest(@RequestBody ApeTaskTest apeTaskTest) {
        boolean save = apeTaskTestService.updateById(apeTaskTest);
        QueryWrapper<ApeTestItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTestItem::getItemId,apeTaskTest.getId());
        List<ApeTestItem> itemList = apeTestItemService.list(queryWrapper);
        for (ApeTestItem apeTestItem : itemList) {
            apeTestItem.setTag(apeTestItem.getTag());
            apeTestItem.setTitle(apeTaskTest.getTitle());
            apeTestItem.setType(apeTaskTest.getType());
            apeTestItem.setScore(apeTaskTest.getScore());
            apeTestItem.setKeyword(apeTaskTest.getKeyword());
            apeTestItem.setAnswer(apeTaskTest.getAnswer());
            apeTestItem.setContent(apeTaskTest.getContent());
            apeTestItem.setRemark(apeTaskTest.getRemark());
            apeTestItem.setAnalysis(apeTaskTest.getAnalysis());
            apeTestItemService.updateById(apeTestItem);
        }
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除课程题目 */
    @GetMapping("removeApeTaskTest")
    @Log(name = "删除课程题目", type = BusinessType.DELETE)
    public Result removeApeTaskTest(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTaskTestService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("课程题目id不能为空！");
        }
    }

    @GetMapping("addTestItem")
    public Result addTestItem(@RequestParam("id")String id,@RequestParam("testId")String testId) {
        ApeTaskTest taskTest = apeTaskTestService.getById(id);
        ApeTestItem apeTestItem = new ApeTestItem();
        apeTestItem.setItemId(id);
        apeTestItem.setTestId(testId);
        apeTestItem.setTag(taskTest.getTag());
        apeTestItem.setTitle(taskTest.getTitle());
        apeTestItem.setType(taskTest.getType());
        apeTestItem.setScore(taskTest.getScore());
        apeTestItem.setKeyword(taskTest.getKeyword());
        apeTestItem.setContent(taskTest.getContent());
        apeTestItem.setAnswer(taskTest.getAnswer());
        apeTestItem.setRemark(taskTest.getRemark());
        apeTestItem.setAnalysis(taskTest.getAnalysis());
        boolean save = apeTestItemService.save(apeTestItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

}