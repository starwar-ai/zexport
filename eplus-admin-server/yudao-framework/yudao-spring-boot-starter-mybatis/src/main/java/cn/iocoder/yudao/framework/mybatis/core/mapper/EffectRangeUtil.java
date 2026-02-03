package cn.iocoder.yudao.framework.mybatis.core.mapper;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/05/15:29
 * @Description:
 */
public class EffectRangeUtil {

//    public static <T extends ChangeExInterface> List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType, Integer confirmSourceType, BaseMapperX<T> itemBaseMapperX) {
//        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<T>().select(T::getId,T::getSourceCode)
//                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(targetCode).setConfirmFlag(0)));
//        List<T> TList = itemBaseMapperX.selectList(queryWrapper);
//        if (CollUtil.isEmpty(TList)) {
//            return List.of();
//        }
//        return TList.stream().map(s -> new ConfirmSourceEntity().setId(s.getId()).setCode(s.getSourceCode()).setConfirmSourceType(confirmSourceType)).toList();
//    }

//    public static <T extends ChangeExInterface> void updateChangeConfirmFlag(Integer effectRangeType, String code,BaseMapperX<T> itemBaseMapperX) {
//        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<T>()
//                .apply("JSON_CONTAINS(effect_range_list,cast({0} as json))", JsonUtils.toJsonString(new JsonEffectRange().setEffectRangeType(effectRangeType).setEffectRangeCode(code).setConfirmFlag(0)));
//        List<T> TList = itemBaseMapperX.selectList(queryWrapper);
//        if (CollUtil.isEmpty(TList)){
//            return;
//        }
//        TList.forEach(s->{
//            List<JsonEffectRange> effectRangeList = s.getEffectRangeList();
//            if (CollUtil.isEmpty(effectRangeList)){
//                return;
//            }
//            effectRangeList.forEach(e->{
//                if (e.getEffectRangeCode().equals(code) && e.getEffectRangeType().equals(effectRangeType)){
//                    e.setConfirmFlag(1);
//                }
//            });
//        });
//        itemBaseMapperX.updateBatch(TList);
//    }
}
