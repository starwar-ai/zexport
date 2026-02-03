package cn.iocoder.yudao.framework.security.core.aop;

import cn.iocoder.yudao.framework.security.core.annotations.FieldPermission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/26 11:13
 */
@Slf4j
@Aspect
public class FieldPermissionAspect {
    private static final String DATA_KEY = "data";

    /**
     *
     */
    @Pointcut("@annotation(cn.iocoder.yudao.framework.security.core.annotations.FieldPermissionEnable)")
    public void pointCut() {
        System.out.println("111");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //解密
        Object result = handle(joinPoint);
        return result;
    }

    public Object handle(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            Object obj = joinPoint.proceed();
            if (obj != null) {
                //也可以不对请求返回结果，针对接口返回接口
                result = handleJsonData(obj);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 注意一般接口返回形式
     * {
     * "code": 0,
     * "msg": "成功",
     * "data": {}
     * }
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    private Object handleJsonData(Object obj) throws IllegalAccessException {

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (DATA_KEY.equals(field.getName())) {
                field.setAccessible(true);
                Object fieldObject = field.get(obj);
                if (fieldObject instanceof ArrayList) {
                    handleListObject(fieldObject);
                } else {
                    handleObj(fieldObject);
                }
            }

        }
        return obj;
    }

    /**
     * 针对对象类型字段权限校验
     *
     * @param obj
     * @throws IllegalAccessException
     */
    private void handleObj(Object obj) throws IllegalAccessException {

        List<Field> fields = listAllFields(obj);
        for (Field field : fields) {

            if (field.getType().equals(List.class)) {
                field.setAccessible(true);
                Object fieldObject = field.get(obj);
                handleListObject(fieldObject);
                continue;
            }

            boolean hasSecureField = field.isAnnotationPresent(FieldPermission.class);

            //TODO 调用权限认证接口
            if (hasSecureField) {
                field.setAccessible(true);
                //TODO 结合自己的权限系统处理
                // 通过字段权限 不设为null
                // 没通过 设为null
                field.set(obj, null);
            }
        }
    }

    /**
     * 获取本类和超类字段
     *
     * @param obj
     */
    private List<Field> listAllFields(Object obj) {

        List fieldList = new ArrayList();

        Class tmpClass = obj.getClass();

        while (tmpClass != null) {

            fieldList.addAll(Arrays.asList(tmpClass.getDeclaredFields()));

            tmpClass = tmpClass.getSuperclass();

        }

        return fieldList;
    }

    /**
     * 针对list<实体来> 进行反射、解密
     *
     * @param obj
     * @throws IllegalAccessException
     */
    private void handleListObject(Object obj) throws IllegalAccessException {
        List<Object> result = new ArrayList<>();
        if (obj instanceof ArrayList) {
            for (Object o : (List<?>) obj) {
                result.add(o);
            }
        }
        for (Object object : result) {
            handleObj(object);
        }
    }
}
