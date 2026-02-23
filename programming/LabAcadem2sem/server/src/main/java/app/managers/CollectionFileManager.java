package app.managers;

import app.model.*;
import app.server.Server;
import ru.bright.model.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.time.ZonedDateTime;
import java.util.PriorityQueue;
import java.util.logging.Level;

/**
 * Менеджер чтения/записи коллекции в XML-файл.
 * Чтение: java.io.FileReader
 * Запись: java.io.PrintWriter
 */
public class CollectionFileManager {

    private final String filePath;
    private final Server server;

    public CollectionFileManager(Server server, String filePath) {
        this.server = server;
        this.filePath = filePath;
    }

    public PriorityQueue<Person> readCollection() {
        File f = new File(filePath);
        if (!f.exists()) {
            server.getLogger().log(Level.INFO, "Collection file not found, starting with empty collection");
            return new PriorityQueue<>();
        }

        try (FileReader fr = new FileReader(f)) {
            InputSource is = new InputSource(fr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement();
            NodeList persons = root.getElementsByTagName("person");
            PriorityQueue<Person> pq = new PriorityQueue<>();
            for (int i = 0; i < persons.getLength(); i++) {
                Element pElem = (Element) persons.item(i);
                Person p = parsePersonElement(pElem);
                if (p != null) pq.add(p);
            }
            return pq;
        } catch (Exception e) {
            server.getLogger().log(Level.SEVERE, "Error reading collection file (XML)", e);
            return new PriorityQueue<>();
        }
    }

    private Person parsePersonElement(Element e) {
        try {
            String idStr = getChildText(e, "id");
            long id = idStr != null ? Long.parseLong(idStr) : 0L;
            String owner = getChildText(e, "ownerLogin");
            String name = getChildText(e, "name");

            Element coordsElem = getChildElement(e, "coordinates");
            Long cx = coordsElem != null ? Long.parseLong(getChildText(coordsElem, "x")) : null;
            Integer cy = coordsElem != null ? Integer.parseInt(getChildText(coordsElem, "y")) : null;
            Coordinates coordinates = new Coordinates(cx, cy);

            String creation = getChildText(e, "creationDate");
            ZonedDateTime creationDate = creation != null ? ZonedDateTime.parse(creation) : ZonedDateTime.now();

            Long height = getChildText(e, "height") != null ? Long.parseLong(getChildText(e, "height")) : null;
            String eye = getChildText(e, "eyeColor");
            String hair = getChildText(e, "hairColor");
            String nat = getChildText(e, "nationality");

            Element locElem = getChildElement(e, "location");
            Long lx = locElem != null ? Long.parseLong(getChildText(locElem, "x")) : null;
            Long ly = locElem != null ? Long.parseLong(getChildText(locElem, "y")) : null;
            Long lz = locElem != null ? Long.parseLong(getChildText(locElem, "z")) : null;
            String lname = locElem != null ? getChildText(locElem, "name") : null;
            Location location = new Location(lx, ly, lz, lname);

            Person p = new Person();
            p.setId(id);
            p.setOwnerLogin(owner);
            p.setName(name);
            p.setCoordinates(coordinates);
            p.setCreationDate(creationDate);
            p.setHeight(height);
            p.setEyeColor(eye != null ? EyeColor.valueOf(eye) : null);
            p.setHairColor(hair != null ? HairColor.valueOf(hair) : null);
            p.setNationality(nat != null && !nat.isEmpty() ? Country.valueOf(nat) : null);
            p.setLocation(location);
            return p;
        } catch (Exception ex) {
            server.getLogger().log(Level.WARNING, "Skipping malformed person element", ex);
            return null;
        }
    }

    private static Element getChildElement(Element parent, String name) {
        NodeList nl = parent.getElementsByTagName(name);
        if (nl.getLength() == 0) return null;
        return (Element) nl.item(0);
    }

    private static String getChildText(Element parent, String name) {
        Element el = getChildElement(parent, name);
        if (el == null) return null;
        return el.getTextContent();
    }

    public boolean saveCollection(java.util.Collection<Person> collection) {
        File f = new File(filePath);
        try (PrintWriter pw = new PrintWriter(f, "UTF-8")) {
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<collection>");
            for (Person p : collection) {
                pw.println("  <person>");
                pw.printf("    <id>%d</id>%n", p.getId());
                pw.printf("    <ownerLogin>%s</ownerLogin>%n", escapeXml(p.getOwnerLogin()));
                pw.printf("    <name>%s</name>%n", escapeXml(p.getName()));
                pw.println("    <coordinates>");
                pw.printf("      <x>%d</x>%n", p.getCoordinates().getX());
                pw.printf("      <y>%d</y>%n", p.getCoordinates().getY());
                pw.println("    </coordinates>");
                pw.printf("    <creationDate>%s</creationDate>%n", p.getCreationDate().toString());
                pw.printf("    <height>%d</height>%n", p.getHeight());
                pw.printf("    <eyeColor>%s</eyeColor>%n", p.getEyeColor());
                pw.printf("    <hairColor>%s</hairColor>%n", p.getHairColor());
                pw.printf("    <nationality>%s</nationality>%n", p.getNationality() == null ? "" : p.getNationality().name());
                pw.println("    <location>");
                pw.printf("      <x>%d</x>%n", p.getLocation().getX());
                pw.printf("      <y>%d</y>%n", p.getLocation().getY());
                pw.printf("      <z>%d</z>%n", p.getLocation().getZ());
                pw.printf("      <name>%s</name>%n", escapeXml(p.getLocation().getName()));
                pw.println("    </location>");
                pw.println("  </person>");
            }
            pw.println("</collection>");
            pw.flush();
            server.getLogger().log(Level.INFO, "Collection saved to file (XML): " + filePath);
            return true;
        } catch (Exception e) {
            server.getLogger().log(Level.SEVERE, "Error saving collection to XML file", e);
            return false;
        }
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}