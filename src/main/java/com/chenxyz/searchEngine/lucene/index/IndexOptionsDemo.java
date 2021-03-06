package com.chenxyz.searchEngine.lucene.index;

import com.chenxyz.searchEngine.lucene.analyzer.ik.IKAnalyzer4Lucene7;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by chenxyz on 2018/11/4.
 */
public class IndexOptionsDemo {
    public static void main(String[] args) {
        // 创建使用的分词器
        Analyzer analyzer = new IKAnalyzer4Lucene7(true);

        // 索引配置对象
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try ( // 索引存放到文件系统中
              Directory directory = FSDirectory
                      .open((new File("d:/lucene_index/indextest")).toPath());

              // 创建索引写对象
              IndexWriter writer = new IndexWriter(directory, config);) {

            // 准备document
            Document doc = new Document();
            // 字段content
            String name = "content";
            String value = "张三说的确实在理";
            FieldType type = new FieldType();
            // 设置是否存储该字段
            type.setStored(true);
            // 设置是否对该字段分词
            type.setTokenized(true);
            // 设置该字段的索引选项
            type.setIndexOptions(
                    IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            type.freeze(); // 使不可更改

            Field field = new Field(name, value, type);
            // 添加字段
            doc.add(field);
            // 加入到索引中
            writer.addDocument(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
