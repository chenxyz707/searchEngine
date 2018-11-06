package com.chenxyz.searchEngine.lucene.analyzer.ik;

import org.apache.lucene.analysis.Analyzer;

/**
 * Created by chenxyz on 2018/11/4.
 */
public class IKAnalyzer4Lucene7 extends Analyzer {

    private boolean useSmart = false;

    public IKAnalyzer4Lucene7() {
        this(false);
    }

    public IKAnalyzer4Lucene7(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        IKTokenizer4Lucene7 tk = new IKTokenizer4Lucene7(this.useSmart);
        return new TokenStreamComponents(tk);
    }


}
