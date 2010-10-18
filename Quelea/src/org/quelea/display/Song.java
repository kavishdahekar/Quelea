package org.quelea.display;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.quelea.Background;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A song that contains a number of sections (verses, choruses, etc.)
 * @author Michael
 */
public class Song implements Displayable, Searchable {

    private String title;
    private String author;
    private List<SongSection> sections;
    private Background background;

    /**
     * Create a new, empty song.
     * @param title the title of the song.
     * @param author the author of the song.
     */
    public Song(String title, String author) {
        this(title, author, new Background(Color.BLACK));
    }

    /**
     * Create a new, empty song.
     * @param title the title of the song.
     * @param author the author of the song.
     * @param background the default background of the song.
     */
    public Song(String title, String author, Background background) {
        this.title = title;
        this.author = author;
        this.background = background;
        sections = new ArrayList<SongSection>();
    }

    /**
     * Get the title of this song.
     * @return the title of this song.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the author of this song.
     * @return the author of the song.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Add a section to this song.
     * @param section the section to add.
     */
    public void addSection(SongSection section) {
        if(section.getBackground() == null) {
            section.setBackground(background);
        }
        sections.add(section);
    }

    /**
     * Get an array of all the sections in this song.
     * @return the song sections.
     */
    public SongSection[] getSections() {
        return sections.toArray(new SongSection[sections.size()]);
    }

    /**
     * Determine whether this song matches a particular search.
     * @param s the search term.
     * @return true if the song matches, false otherwise.
     */
    public boolean search(String s) {
        return title.toLowerCase().contains(s.toLowerCase());
    }

    /**
     * Get a representation of this song in XML format.
     * @return the song in XML format.
     */
    public String getXML() {
        StringBuilder xml = new StringBuilder();
        xml.append("<song>");
        xml.append("<title>");
        xml.append(title);
        xml.append("</title>");
        xml.append("<author>");
        xml.append(author);
        xml.append("</author>");
        xml.append("<lyrics>");
        for(SongSection section : sections) {
            xml.append("<section ").append("title=\"").append(section.getTitle()).append("\">");
            for(String line : section.getLyrics()) {
                xml.append(line).append('\n');
            }
            xml.append("</section>");
        }
        xml.append("</lyrics>");
        xml.append("</song>");
        return xml.toString();
    }

    /**
     * Parse a song in XML format and return the song object.
     * @param inputStream the inputstream to parse.
     * @return the song, or null if an error occurs.
     */
    public static Song parseXML(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            NodeList list = doc.getElementsByTagName("*");
            String title = "";
            String author = "";
            List<SongSection> songSections = new ArrayList<SongSection>();
            Background background = new Background(Color.BLACK);
            for(int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if(node.getNodeName().equals("title")) {
                    title = node.getTextContent();
                }
                if(node.getNodeName().equals("author")) {
                    author = node.getTextContent();
                }
                if(node.getNodeName().equals("lyrics")) {
                    NodeList sections = node.getChildNodes();
                    for(int j = 0; j < sections.getLength(); j++) {
                        Node sectionNode = sections.item(j);
                        NamedNodeMap attributes = sectionNode.getAttributes();
                        String sectionTitle = null;
                        if(attributes!=null) {
                            Node titleNode = attributes.getNamedItem("title");
                            if(titleNode != null) {
                                sectionTitle = titleNode.getTextContent();
                            }
                        }
                        if(sectionNode.getNodeName().equals("section")) {
                            songSections.add(new SongSection(sectionTitle, sectionNode.getTextContent().split("\n")));
                        }
                    }
                }
            }
            Song ret = new Song(title, author, background);
            for(SongSection section : songSections) {
                ret.addSection(section);
            }
            return ret;
        }
        catch(Exception ex) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 29 * hash + (this.author != null ? this.author.hashCode() : 0);
        hash = 29 * hash + (this.sections != null ? this.sections.hashCode() : 0);
        hash = 29 * hash + (this.background != null ? this.background.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Song other = (Song) obj;
        if((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if((this.author == null) ? (other.author != null) : !this.author.equals(other.author)) {
            return false;
        }
        if(this.sections != other.sections && (this.sections == null || !this.sections.equals(other.sections))) {
            return false;
        }
        if(this.background != other.background && (this.background == null || !this.background.equals(other.background))) {
            return false;
        }
        return true;
    }

    /**
     * Get a string representation of this song.
     * @return a string representation of the song.
     */
    @Override
    public String toString() {
        return getTitle();
    }
}
