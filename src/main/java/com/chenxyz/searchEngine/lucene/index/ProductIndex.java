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
import org.apache.lucene.util.NumericUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by chenxyz on 2018/11/4.
 */
public class ProductIndex {

    public static void main(String[] args) {
        // 创建使用的分词器
        Analyzer analyzer = new IKAnalyzer4Lucene7(true);

        // 索引配置对象
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try (
                // 索引存放目录
                // 存放到文件系统中
                Directory directory = FSDirectory
                        .open((new File("D:\\lucene_index\\indextest")).toPath());

                // 存放到内存中
                // Directory directory = new RAMDirectory();

                // 创建索引写对象
                IndexWriter writer = new IndexWriter(directory, config);) {

            // 准备document
            Document doc = new Document();
            // 商品id：字符串，不索引、但存储
            String prodId = "p0001";
            FieldType onlyStoredType = new FieldType();
            onlyStoredType.setTokenized(false);
            onlyStoredType.setIndexOptions(IndexOptions.NONE);
            onlyStoredType.setStored(true);
            onlyStoredType.freeze();
            doc.add(new Field("prodId", prodId, onlyStoredType));

            // 商品名称：字符串，分词索引(存储词频、位置、偏移量)、存储
            String name = "ThinkPad X1 Carbon 20KH0009CD/25CD 超极本轻薄笔记本电脑联想";
            FieldType indexedAllStoredType = new FieldType();
            indexedAllStoredType.setStored(true);
            indexedAllStoredType.setTokenized(true);
            indexedAllStoredType.setIndexOptions(
                    IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            indexedAllStoredType.freeze();
            doc.add(new Field("name", name, indexedAllStoredType));

            // 图片链接：仅存储
            String imgUrl = "http://www.baidu.com";
            doc.add(new Field("imgUrl", imgUrl, onlyStoredType));

            // 商品简介：文本，分词索引（不需要支持短语、临近查询）、存储，结果中支持高亮显示
            String simpleIntro = "集成显卡 英特尔 酷睿 i5-8250U 14英寸";
            FieldType indexedTermVectorsStoredType = new FieldType();
            indexedTermVectorsStoredType.setStored(true);
            indexedTermVectorsStoredType.setTokenized(true);
            indexedTermVectorsStoredType
                    .setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            indexedTermVectorsStoredType.setStoreTermVectors(true);
            indexedTermVectorsStoredType.setStoreTermVectorPositions(true);
            indexedTermVectorsStoredType.setStoreTermVectorOffsets(true);
            indexedTermVectorsStoredType.freeze();

            doc.add(new Field("simpleIntro", simpleIntro,
                    indexedTermVectorsStoredType));

            // 价格，整数，单位分，不索引、存储
            int price = 2999900;
            // Field 类有整数类型值的构造方法吗？
            // 用字节数组来存储试试，还是转为字符串？
            byte[] result = new byte[Integer.BYTES];
            NumericUtils.intToSortableBytes(price, result, 0);

            doc.add(new Field("price", result, onlyStoredType));
            writer.addDocument(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
