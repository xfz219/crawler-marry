package com.crawler.marry.parser.factory;

import com.crawler.marry.parser.Parser;

/**
 * Created by finup on 2017/2/19.
 */
public class ParserFactory {
    /**
     * 
     * @param t
     * @param <T>
     * @return
     */
    public static <T extends Parser> T createParser(Class<T> t) {
        T parser = null;
        try {
            parser = (T) Class.forName(t.getName()).newInstance();
        } catch (Exception e) {

        }
        return parser;
    }

    public static void main(String[] args) {
    }
}
