package com.chenxyz.searchEngine.lucene.analyzer;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

import java.io.IOException;
import java.util.Arrays;

/**
 * 使用一个Tokenizer和TokenFilter
 * 按照空格分词并且转换为小写
 * Created by chenxyz on 2018/11/4.
 */
public class WhitespaceAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new WhitespaceTokenizer();
        TokenStream filter = new LowerCaseTokenFilter(source);
        return new TokenStreamComponents(source, filter);
    }

    public static void main(String[] args) {
        String text = "An AttributeSource contains a list of different AttributeImpls, and methods to add and get them. ";

        try (Analyzer ana = new WhitespaceAnalyzer();
             TokenStream ts = ana.tokenStream("aa", text)) {
            CharAttribute ca = ts.getAttribute(CharAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                System.out.print(ca.getString() + "|");
            }
            ts.end();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class WhitespaceTokenizer extends Tokenizer {

        //需要记录的属性
        CharAttribute charAttr = addAttribute(CharAttribute.class);

        char[] buffer = new char[127];
        int length = 0;
        int c;
        @Override
        public boolean incrementToken() throws IOException {
            //清除所有的词项属性
            clearAttributes();
            length = 0;
            while((c = this.input.read()) != -1) {

                if (Character.isWhitespace(c)) {
                    if (length > 0) {
                        //如果是空格，并且buffer中有元素拷贝进分词项
                        charAttr.setChars(buffer, length);
                    }
                    return true;
                }
                buffer[length++] = (char)c;
            }
            if (this.input.read() == -1) {
                if (length > 0) {
                    charAttr.setChars(buffer, length);
                }
                return false;
            }
            return true;
        }
    }

    public static class LowerCaseTokenFilter extends TokenFilter {

        CharAttribute charAttr = addAttribute(CharAttribute.class);

        public LowerCaseTokenFilter(TokenStream input) {
            super(input);
        }

        @Override
        public boolean incrementToken() throws IOException {
            boolean incr = input.incrementToken();
            if (incr) {
                char[] chars = charAttr.getChars();
                int length = charAttr.getLength();
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        chars[i] = Character.toLowerCase(chars[i]);
                    }
                }
            }
            return incr;
        }
    }

    interface CharAttribute extends Attribute {
        void setChars(char[] buffer, int length);
        char[] getChars();
        int getLength();
        String getString();
    }

    public static class CharAttributeImpl extends AttributeImpl implements CharAttribute {
        private char[] charTerm = new char[127];
        private int length = 0;
        @Override
        public void setChars(char[] buffer, int length) {
            this.length = length;
            if (length > 0) {
                charTerm = Arrays.copyOf(buffer, length);
            }
        }

        @Override
        public char[] getChars() {
            return charTerm;
        }

        @Override
        public int getLength() {
            return length;
        }

        @Override
        public String getString() {
            if (length > 0) {
                return new String(charTerm, 0 ,length);
            }
            return null;
        }

        @Override
        public void clear() {
            length = 0;
        }

        @Override
        public void reflectWith(AttributeReflector reflector) {

        }

        @Override
        public void copyTo(AttributeImpl target) {

        }
    }
}
