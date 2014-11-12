package com.teamdev.arseniuk;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

public class SystemInformationWriter {
    private String configFile;
    private final Object flag;

    public SystemInformationWriter(Object flag) {
        this.flag = flag;
    }


    public void setFile(String configFile) {
        this.configFile = configFile;
    }

    public void saveSystemInfo(Iterator<Item> items) throws FileNotFoundException, XMLStreamException {
        synchronized (flag) {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(configFile));
            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);

            // create config open tag
            eventWriter.add(end);
            StartElement configStartElement = eventFactory.createStartElement("", "", Item.SYSTEM_INFO);
            eventWriter.add(configStartElement);
            eventWriter.add(end);
            writeItems(eventWriter, items);


            eventWriter.add(eventFactory.createEndElement("", "", Item.SYSTEM_INFO));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        }
    }

    private void writeItems(XMLEventWriter eventWriter, Iterator<Item> items) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        while (items.hasNext()) {
            Item item = items.next();
            StartElement configStartElement = eventFactory.createStartElement("", "", Item.ITEM);
            eventWriter.add(configStartElement);
            eventWriter.add(end);

            createNode(eventWriter, Item.KEY, item.getKey());
            createNode(eventWriter, Item.PATH, item.getPath());
            createNode(eventWriter, Item.SIZE, String.valueOf(item.getSize()));
            createNode(eventWriter, Item.EXPIRATION_TIME, String.valueOf(item.getExpirationTime()));
            createNode(eventWriter, Item.CREATION_TIME, String.valueOf(item.getCreationTime()));

            eventWriter.add(eventFactory.createEndElement("", "", Item.ITEM));
            eventWriter.add(end);
        }
    }

    private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);

    }
}
