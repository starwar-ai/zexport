package com.syj.eplus.module.scm.service.qualification;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationPageReqVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationRespVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationSaveReqVO;
import com.syj.eplus.module.scm.convert.qualification.QualificationConvert;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;
import com.syj.eplus.module.scm.dal.mysql.qualification.QualificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.QUALIFICATION_NOT_EXISTS;

/**
 * 资质 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class QualificationServiceImpl implements QualificationService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private QualificationMapper qualificationMapper;


    @Override
    public Long createQualification(QualificationSaveReqVO createReqVO) {
QualificationDO qualification = QualificationConvert.INSTANCE.convertQualificationDO(createReqVO);
        // 插入
        qualificationMapper.insert(qualification);
        // 返回
        return qualification.getId();
    }

    @Override
    public void updateQualification(QualificationSaveReqVO updateReqVO) {
        // 校验存在
        validateQualificationExists(updateReqVO.getId());
        // 更新
QualificationDO updateObj = QualificationConvert.INSTANCE.convertQualificationDO(updateReqVO);
        qualificationMapper.updateById(updateObj);
    }

    @Override
    public void deleteQualification(Long id) {
        // 校验存在
        validateQualificationExists(id);
        // 删除
        qualificationMapper.deleteById(id);
    }

    private void validateQualificationExists(Long id) {
        if (qualificationMapper.selectById(id) == null) {
            throw exception(QUALIFICATION_NOT_EXISTS);
        }
    }
    @Override
public QualificationRespVO getQualification(Long id) {
    QualificationDO qualificationDO = qualificationMapper.selectById(id);
if (qualificationDO == null) {
return null;
}
return QualificationConvert.INSTANCE.convertQualificationRespVO(qualificationDO);
    }

    @Override
    public PageResult<QualificationDO> getQualificationPage(QualificationPageReqVO pageReqVO) {
        return qualificationMapper.selectPage(pageReqVO);
    }

}