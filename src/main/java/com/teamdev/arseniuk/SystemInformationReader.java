package com.teamdev.arseniuk;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SystemInformationReader {

    private final Object flag;

    public SystemInformationReader(Object flag) {
        this.flag = flag;
    }

    public List<Item> readConfig(String configFile) {
        synchronized (flag) {
            List<Item> items = new ArrayList<Item>();
            try {
                // First, create a new XMLInputFactory
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                // Setup a new eventReader
                InputStream in = new FileInputStream(configFile);
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
                // read the XML document
                Item item = null;

                while (eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();

                    if (event.isStartElement()) {
                        final String tag = event.asStartElement().getName().getLocalPart();
                        if (tag.equals(Item.ITEM)) {
                            item = new Item();
                        } else if (tag.equals(Item.KEY)) {
                            event = eventReader.nextEvent();
                            item.setKey(event.asCharacters().getData());
                            continue;
                        } else if (tag.equals(Item.PATH)) {
                            event = eventReader.nextEvent();
                            item.setPath(event.asCharacters().getData());
                            continue;
                        } else if (tag.equals(Item.SIZE)) {
                            event = eventReader.nextEvent();
                            item.setSize(Long.parseLong(event.asCharacters().getData()));
                            continue;
                        } else if (tag.equals(Item.EXPIRATION_TIME)) {
                            event = eventReader.nextEvent();
                            item.setExpirationTime(Long.parseLong(event.asCharacters().getData()));
                            continue;
                        } else if (tag.equals(Item.CREATION_TIME)) {
                            event = eventReader.nextEvent();
                            item.setCreationTime(Long.parseLong(event.asCharacters().getData()));
                            continue;
                        }
                    }
                    // If we reach the end of an item element, we add it to the list
                    if (event.isEndElement()) {
                        final String tag = event.asEndElement().getName().getLocalPart();
                        if (tag.equals(Item.ITEM)) {
                            items.add(item);
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return items;
        }
    }


}
