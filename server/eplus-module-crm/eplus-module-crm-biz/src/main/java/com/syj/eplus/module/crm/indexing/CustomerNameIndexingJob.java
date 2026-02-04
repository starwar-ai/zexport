package com.syj.eplus.module.crm.indexing;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import com.syj.eplus.module.crm.dal.dataobject.cust.CustDO;
import com.syj.eplus.module.crm.service.cust.CustService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.file.Paths;
import java.util.List;

import static com.syj.eplus.module.crm.indexing.Constants.INDEX_PATH;

@Slf4j
@Component
public class CustomerNameIndexingJob implements JobHandler {
    @Value("${eplus.index.path}")
    private String indexPath;
    @Autowired
    private CustomerLastIdManager customerLastIdManager;

    @Resource
    private CustService custService;

    @Override
    public String execute(String param) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(indexPath,INDEX_PATH));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        long lastId = customerLastIdManager.getLastId();
        List<CustDO> newCusts = custService.getCustomersWithIdGreaterThan(lastId);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try (IndexWriter writer = new IndexWriter(directory, config)) {
            for (CustDO cust : newCusts) {
                Document doc = new Document();
                doc.add(new TextField("name", cust.getName(), Field.Store.YES));
                doc.add(new StringField("id", String.valueOf(cust.getId()), Field.Store.YES));
                writer.addDocument(doc);
                if (cust.getId() > lastId) {
                    lastId = cust.getId();
                }
            }
            writer.commit();
        }
        customerLastIdManager.saveLastId(lastId);
        return "Indexed " + newCusts.size() + " customers. Last ID indexed: " + lastId;
    }
}