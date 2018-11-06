package com.chenxyz.searchEngine.lucene.analyzer;

import com.chenxyz.searchEngine.lucene.analyzer.ik.IKAnalyzer4Lucene7;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Created by chenxyz on 2018/11/4.
 */
public class AnalyzerTest {

    static void doToken(TokenStream ts) throws IOException {
        ts.reset();
        CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
        while (ts.incrementToken()) {
            System.out.print(cta.toString() + "|");
        }
        System.out.println();
        ts.end();
        ts.close();
    }

    public static void main(String[] args) {
        String enText = "Spring Cloud Sleuth implements a distributed tracing solution for Spring Cloud.";
        String cnText = "近期，有外媒表示，支持5G网络的苹果手机将会在2020年正式推出。";

        //标准分词器
        try (Analyzer ana = new StandardAnalyzer()) {
            TokenStream ts = ana.tokenStream("en", enText);
            System.out.println("标准分词器，英文分词效果：");
            doToken(ts);
            ts = ana.tokenStream("cn", cnText);
            System.out.println("标准分词器，中文分词效果：");
            doToken(ts);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //smart中文分词器
        try (Analyzer smart = new SmartChineseAnalyzer()) {
            TokenStream ts = smart.tokenStream("content", enText);
            System.out.println("smart中文分词器，英文分词效果：");
            doToken(ts);
            ts = smart.tokenStream("content", cnText);
            System.out.println("smart中文分词器，中文分词效果：");
            doToken(ts);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // IKAnalyzer 细粒度切分
        try (Analyzer ik = new IKAnalyzer4Lucene7();) {
            TokenStream ts = ik.tokenStream("content", enText);
            System.out.println("IKAnalyzer中文分词器 细粒度切分，英文分词效果：");
            doToken(ts);
            ts = ik.tokenStream("content", cnText);
            System.out.println("IKAnalyzer中文分词器 细粒度切分，中文分词效果：");
            doToken(ts);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // IKAnalyzer 智能切分
        try (Analyzer ik = new IKAnalyzer4Lucene7(true)) {
            TokenStream ts = ik.tokenStream("content", enText);
            System.out.println("IKAnalyzer中文分词器 智能切分，英文分词效果：");
            doToken(ts);
            ts = ik.tokenStream("content", cnText);
            System.out.println("IKAnalyzer中文分词器 智能切分，中文分词效果：");
            doToken(ts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
