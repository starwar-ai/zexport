package com.syj.eplus.module.crm.convert.collectionaccount;

import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountRespVO;
import com.syj.eplus.module.crm.controller.admin.collectionaccount.vo.CollectionAccountSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.collectionaccount.CollectionAccountDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface CollectionAccountConvert {

        CollectionAccountConvert INSTANCE = Mappers.getMapper(CollectionAccountConvert.class);

        CollectionAccountRespVO convert(CollectionAccountDO collectionAccountDO);

        default CollectionAccountRespVO convertCollectionAccountRespVO(CollectionAccountDO collectionAccountDO){
                CollectionAccountRespVO collectionAccountRespVO = convert(collectionAccountDO);
                return collectionAccountRespVO;
        }

    CollectionAccountDO convertCollectionAccountDO(CollectionAccountSaveReqVO saveReqVO);
}