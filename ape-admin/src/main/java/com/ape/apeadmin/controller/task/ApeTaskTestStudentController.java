package com.ape.apeadmin.controller.task;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeTaskTestStudent;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeTaskTestStudentService;
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
 * @description: 学生练习controller
 * @date 2024/05/14 08:38
 */
@Controller
@ResponseBody
@RequestMapping("student")
public class ApeTaskTestStudentController {

    @Autowired
    private ApeTaskTestStudentService apeTaskTestStudentService;

    /** 分页获取学生练习 */
    @Log(name = "分页获取学生练习", type = BusinessType.OTHER)
    @PostMapping("getApeTaskTestStudentPage")
    public Result getApeTaskTestStudentPage(@RequestBody ApeTaskTestStudent apeTaskTestStudent) {
        Page<ApeTaskTestStudent> page = new Page<>(apeTaskTestStudent.getPageNumber(),apeTaskTestStudent.getPageSize());
        QueryWrapper<ApeTaskTestStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getUserId()),ApeTaskTestStudent::getUserId,apeTaskTestStudent.getUserId())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getItemId()),ApeTaskTestStudent::getItemId,apeTaskTestStudent.getItemId())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getTaskId()),ApeTaskTestStudent::getTaskId,apeTaskTestStudent.getTaskId())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getTag()),ApeTaskTestStudent::getTag,apeTaskTestStudent.getTag())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getTitle()),ApeTaskTestStudent::getTitle,apeTaskTestStudent.getTitle())
                .eq(apeTaskTestStudent.getType() != null,ApeTaskTestStudent::getType,apeTaskTestStudent.getType())
                .eq(apeTaskTestStudent.getScore() != null,ApeTaskTestStudent::getScore,apeTaskTestStudent.getScore())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getKeyword()),ApeTaskTestStudent::getKeyword,apeTaskTestStudent.getKeyword())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getAnswer()),ApeTaskTestStudent::getAnswer,apeTaskTestStudent.getAnswer())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getContent()),ApeTaskTestStudent::getContent,apeTaskTestStudent.getContent())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getAnalysis()),ApeTaskTestStudent::getAnalysis,apeTaskTestStudent.getAnalysis())
                .eq(apeTaskTestStudent.getPoint() != null,ApeTaskTestStudent::getPoint,apeTaskTestStudent.getPoint())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getSolution()),ApeTaskTestStudent::getSolution,apeTaskTestStudent.getSolution())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getRemark()),ApeTaskTestStudent::getRemark,apeTaskTestStudent.getRemark())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getCreateBy()),ApeTaskTestStudent::getCreateBy,apeTaskTestStudent.getCreateBy())
                .eq(apeTaskTestStudent.getCreateTime() != null,ApeTaskTestStudent::getCreateTime,apeTaskTestStudent.getCreateTime())
                .eq(StringUtils.isNotBlank(apeTaskTestStudent.getUpdateBy()),ApeTaskTestStudent::getUpdateBy,apeTaskTestStudent.getUpdateBy())
                .eq(apeTaskTestStudent.getUpdateTime() != null,ApeTaskTestStudent::getUpdateTime,apeTaskTestStudent.getUpdateTime());
        Page<ApeTaskTestStudent> apeTaskTestStudentPage = apeTaskTestStudentService.page(page, queryWrapper);
        return Result.success(apeTaskTestStudentPage);
    }

    /** 根据id获取学生练习 */
    @Log(name = "根据id获取学生练习", type = BusinessType.OTHER)
    @GetMapping("getApeTaskTestStudentById")
    public Result getApeTaskTestStudentById(@RequestParam("id")String id) {
        ApeTaskTestStudent apeTaskTestStudent = apeTaskTestStudentService.getById(id);
        return Result.success(apeTaskTestStudent);
    }

    /** 保存学生练习 */
    @Log(name = "保存学生练习", type = BusinessType.INSERT)
    @PostMapping("saveApeTaskTestStudent")
    public Result saveApeTaskTestStudent(@RequestBody ApeTaskTestStudent apeTaskTestStudent) {
        ApeUser user = ShiroUtils.getUserInfo();
        apeTaskTestStudent.setUserId(user.getId());
        if (StringUtils.isNotBlank(apeTaskTestStudent.getSolution())) {
            if (apeTaskTestStudent.getType() == 0 || apeTaskTestStudent.getType() == 1 || apeTaskTestStudent.getType() == 2 || apeTaskTestStudent.getType() == 3 ) {
                if (apeTaskTestStudent.getAnswer().equals(apeTaskTestStudent.getSolution())) {
                    apeTaskTestStudent.setPoint(apeTaskTestStudent.getScore());
                }
            }
            if (apeTaskTestStudent.getType() == 4) {
                JSONArray parseArray = JSONArray.parseArray(apeTaskTestStudent.getKeyword());
                int score = 0;
                for (int j = 0; j < parseArray.size();j++) {
                    JSONObject item = parseArray.getJSONObject(j);
                    if (apeTaskTestStudent.getSolution().contains(item.getString("option"))) {
                        score += item.getInteger("value");
                    }
                }
                apeTaskTestStudent.setPoint(score);
            }
        }
        QueryWrapper<ApeTaskTestStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTaskTestStudent::getUserId,user.getId())
                .eq(ApeTaskTestStudent::getTaskId,apeTaskTestStudent.getTaskId())
                .eq(ApeTaskTestStudent::getItemId,apeTaskTestStudent.getItemId()).last("limit 1");
        ApeTaskTestStudent student = apeTaskTestStudentService.getOne(queryWrapper);
        boolean save;
        if (student == null) {
            save = apeTaskTestStudentService.save(apeTaskTestStudent);
        } else {
            apeTaskTestStudent.setId(student.getId());
            save = apeTaskTestStudentService.updateById(apeTaskTestStudent);
        }
        if (save) {
            return Result.success(apeTaskTestStudent);
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑学生练习 */
    @Log(name = "编辑学生练习", type = BusinessType.UPDATE)
    @PostMapping("editApeTaskTestStudent")
    public Result editApeTaskTestStudent(@RequestBody ApeTaskTestStudent apeTaskTestStudent) {
        boolean save = apeTaskTestStudentService.updateById(apeTaskTestStudent);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除学生练习 */
    @GetMapping("removeApeTaskTestStudent")
    @Log(name = "删除学生练习", type = BusinessType.DELETE)
    public Result removeApeTaskTestStudent(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeTaskTestStudentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("学生练习id不能为空！");
        }
    }

    @GetMapping("getItemByTaskId")
    public Result getItemByTaskId(@RequestParam("id")String id) {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeTaskTestStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeTaskTestStudent::getTaskId,id)
                .eq(ApeTaskTestStudent::getUserId,user.getId())
                .orderByAsc(ApeTaskTestStudent::getCreateTime);
        List<ApeTaskTestStudent> studentList = apeTaskTestStudentService.list(queryWrapper);
        return Result.success(studentList);
    }

}