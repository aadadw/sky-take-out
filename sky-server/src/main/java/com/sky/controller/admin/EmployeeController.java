package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.create.table.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

//    新增员工
    @PostMapping()
    @ApiOperation("新增员工")
    public Result add(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增{}",employeeDTO);

        employeeService.add(employeeDTO);

        return Result.success();
    }

//    员工分页查询
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> queryPage(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return  Result.success(pageResult);
    }
//修改员工账号启用/禁用状态
    @PostMapping("status/{status}")
    @ApiOperation("设置员工启用禁用状态")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("员工id：{}，将要修改状态代码：{}",status,id);
        employeeService.changeStatus(id,status);
        return Result.success();
    }
    //根据id查询员工信息
    @GetMapping("/{id}")
    @ApiOperation("查询员工信息")
    public Result getEmpById(@PathVariable Integer id){
        log.info("查询员工id：{}",id);
        Employee employee = employeeService.getEmpById(id);

        return Result.success(employee);
    }
    //修改员工
    @PutMapping()
    @ApiOperation("修改员工")
    public Result<Employee> updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息:{}",employeeDTO);
        Employee employee = employeeService.updateEmployee(employeeDTO);
        return Result.success(employee);
    }

}
