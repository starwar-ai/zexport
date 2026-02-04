package com.syj.eplus.module.crm.indexing;

// match top 10 customer names from lucene index by input name, and return the name list

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.FIND_SIMILAR_FAIL;
import static com.syj.eplus.module.crm.indexing.Constants.INDEX_PATH;

@Service
public class CustomerNameIndexService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${eplus.index.path}")
    private String indexPath;
    public List<String> findSimilarNames(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> names = new ArrayList<>();
        
        try (Directory directory = FSDirectory.open(Paths.get(indexPath, INDEX_PATH));
             IndexReader reader = DirectoryReader.open(directory);
             Analyzer analyzer = new StandardAnalyzer()) {
            
            IndexSearcher searcher = new IndexSearcher(reader);

			//创建查询器
			QueryParser queryParser = new QueryParser("name", new StandardAnalyzer());
			Query query = queryParser.parse(name);
			
            
            // 执行查询并获取结果
            TopDocs topDocs = searcher.search(query, 10);
            for (int i = 0; i < topDocs.totalHits.value && i < topDocs.scoreDocs.length; i++) {
                names.add(searcher.doc(topDocs.scoreDocs[i].doc).get("name"));
            }
            
        } catch (Exception e) {
            logger.error("查找相似名称失败: {}", name, e);
            throw exception(FIND_SIMILAR_FAIL);
        }
        
        return names;
    }

    public String prepareName(String name) {
        String[] tokens = Strings.split(name, ' ');
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if(!token.isBlank())
                sb.append(token).append("~ ");
        }
        return sb.toString();

    }
}
