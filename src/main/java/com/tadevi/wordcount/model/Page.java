package com.tadevi.wordcount.model;

public class Page {
    private final String content;
    private final String title;

    public Page(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Page[\n"
                + "\ttitle=" + title + "\n"
                + "\tcontent=" + content + "\n"
                + "]";
    }


    public final static Page POISON_PILL_PAGE = new Page(null,null);
}
