package com.tadevi.wordcount.parser;

import com.tadevi.wordcount.model.Page;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

public final class PageXmlParser {
    private final int totalPage;
    private final String name;

    public PageXmlParser(int totalPage, String name) {
        this.totalPage = totalPage;
        this.name = name;
    }

    private final static class PageIterator implements Iterator<Page> {
        private Page currentPage;
        private final int maxPages;
        private int totalPage = 0;

        private final XMLEventReader xmlEventReader;

        public PageIterator(XMLEventReader xmlEventReader, int maxPages) {
            this.maxPages = maxPages;
            this.xmlEventReader = xmlEventReader;
            currentPage = fetchNextPage();
        }

        private Page fetchNextPage() {
            Page page = null;

            StringBuilder content = new StringBuilder();
            StringBuilder title = new StringBuilder();

            boolean metTitleTag = false;
            boolean metTextTag = false;
            boolean isEnd = false;

            try {
                while (xmlEventReader.hasNext() && !isEnd) {
                    XMLEvent xmlEvent = xmlEventReader.nextEvent();

                    if (xmlEvent.isStartElement()) {
                        StartElement startElement = xmlEvent.asStartElement();
                        String tagName = startElement.getName().getLocalPart();
                        switch (tagName) {
                            case "title":
                                metTitleTag = true;
                                break;
                            case "text":
                                metTextTag = true;
                                break;
                        }
                    }

                    if (xmlEvent.isCharacters()) {
                        if (metTitleTag) {
                            title.append(xmlEvent.asCharacters().getData());
                        }
                        if (metTextTag) {
                            content.append(xmlEvent.asCharacters().getData());
                        }
                    }

                    if ((metTextTag || metTitleTag) && xmlEvent.isEndElement()) {
                        EndElement endElement = xmlEvent.asEndElement();
                        String tagName = endElement.getName().getLocalPart();
                        switch (tagName) {
                            case "title":
                                metTitleTag = false;
                                break;
                            case "text":
                                metTextTag = false;
                                isEnd = true;
                                break;
                        }

                        page = new Page(title.toString(), content.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return page;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = currentPage != null && totalPage < maxPages;
            if (!hasNext) {
                try {
                    xmlEventReader.close();
                } catch (XMLStreamException e) {
                    // ignored
                }
            }
            return hasNext;
        }

        @Override
        public Page next() {
            if (currentPage == null || totalPage == maxPages) return null;
            totalPage++;
            Page page = currentPage;
            currentPage = fetchNextPage();
            return page;
        }
    }

    public Iterable<Page> getLazyPages() throws Exception {
        Reader fileReader = new FileReader(name);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(fileReader);
        return () -> new PageIterator(xmlEventReader, totalPage);
    }

    public int getTotalPages() {
        return totalPage;
    }
}
