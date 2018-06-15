package sgs.controller.fileManagement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * 
 * @author Tobi, Schweiger
 */
public class SafeSAX {

	public static final String defaultXmlFile = "data.xml";
	
    /** 
     * Ignorant implementation of a dtd resolver, does not try to read a dtd but delivers
     * a generic xml dtd description instead
     */
    static EntityResolver resolver = new EntityResolver() {
        @Override
		public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
        }
    };

    /**
     * Write XML-document in a zip-file
     * @param writeFile
     * @param zip
     */
    public static void write(File zipFile, Document doc) {
    	
		ZipOutputStream zos;
		XMLWriter wr;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setLineSeparator(System.getProperty("line.separator"));
			ZipEntry ze = new ZipEntry("data.xml");
			zipFile.delete();
			zipFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			zos.putNextEntry(ze);
			wr = new XMLWriter(zos, format); //for ZIP replace FOS by ZOS
			wr.write(doc);
			wr.flush();
			zos.flush();
			zos.closeEntry();
			wr.close();
			zos.close();
			doc = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    /**
     * Creates a new SAXReader and tries to load the given file into an XML document
     * In case the dtd could not be loaded, the process is restarted using a generic 
     * resolver 
     * @param loadFile the file to be loaded from
     * @param zip if File is Ziped
     * @return the XML document
     */
    public static Document read(File loadFile, boolean zip) {
        Document doc = null;
        boolean error = false;

        try {
            SAXReader xmlReader = new SAXReader(false);

            if (zip) {
                @SuppressWarnings("resource")
				ZipFile zf = new ZipFile(loadFile);
                Enumeration<?> entries = zf.entries();
                ZipEntry ze = (ZipEntry) entries.nextElement();
                InputStream input = zf.getInputStream(ze);
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                doc = xmlReader.read(br);
            } else {
                doc = xmlReader.read(loadFile);
            }
        } catch (OutOfMemoryError mem) {
            System.err.println("Could not import! (Out of memory)");
        } catch (DocumentException e) {
            //System.err.println("Document type description could not be loaded (probably wrong path) - don't worry, using a generic dtd...");
            error = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (error) {
            try {
                SAXReader xmlReader = new SAXReader(false);
                if (zip) {
                    @SuppressWarnings("resource")
					ZipFile zf = new ZipFile(loadFile);
                    ZipEntry ze = zf.getEntry(defaultXmlFile);
                    InputStream input = zf.getInputStream(ze);
                    BufferedReader br = new BufferedReader(new InputStreamReader(input));
                    doc = xmlReader.read(br);
                } else {
                    xmlReader.setEntityResolver(resolver);
                    doc = xmlReader.read(loadFile);
                }
            } catch (OutOfMemoryError mem) {
                System.err.println("Could not import! (Out of memory)");
            } catch (DocumentException e) {
                String msg = e.getMessage();
                System.err.println("Error while import: DocumentException\n" + msg);
                error = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return doc;
    }
}
