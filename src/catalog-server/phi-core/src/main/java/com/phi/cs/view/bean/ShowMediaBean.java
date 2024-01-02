package com.phi.cs.view.bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.dataTypes.ED;

/**
 * This bean gets binary media types from RIM and write them in an OutputStrem.
 * Used as backing bean for a4j:mediaOutput
 * 
 * @author Francesco Bruni
 *
 */
@BypassInterceptors
@Name("ShowMedia")
@Scope(ScopeType.EVENT)
public class ShowMediaBean implements Serializable {

	private static final long serialVersionUID = 7187970136725008427L;
	private final static Logger log = Logger.getLogger(ShowMediaBean.class);

	/**
	 * Gets a file object as input, and writes it into the Output Stream
	 * 
	 * @param stream - the output stream where write 
	 * @param object - the file object to write 
	 * @throws IOException
	 */
	public void paint(OutputStream stream, Object object){

		if (object instanceof File) {
			// create a file and its readers
			File file = (File)object;
			if ((int)file.length() < 1) {
				file.delete();
				return;
//				paintFile(stream, new File(RepositoryManager.getImages_repository_full_path() + File.separator + "noimg.png"));
			} else {
				paintFile(stream, file);
			}
			file.delete();
			if (log.isDebugEnabled()) {
				log.debug("Current media painting done");
			}
		}
	}

	private void paintFile(OutputStream stream, File file) {
		try {
			FileInputStream fileIS = new FileInputStream(file);
			BufferedInputStream fileBuffer = new BufferedInputStream(fileIS);
			// create a new byte array to store file data
			byte[] b =  new byte[(int)file.length()];
			// read file bytes into byte array
			fileBuffer.read(b);
			// write byte array into current stream
			stream.write(b);
			// close all and delete temporary file
			fileBuffer.close();
			fileIS.close();
		} catch (FileNotFoundException e) {
			log.error("File "+ file.getName() + " not found!");
		} catch (IOException e) {
			log.error("Error reading/writing file " + file.getName());
		} finally {

		}
	}

	/**
	 * Reads appropriate data from RIM and writes it into a temporary file
	 * 
	 * @param bindingOrObject - RIM binding expression
	 * @return a file containing data read from RIM or null
	 */
	public Object getObject(Object bindingOrObject) {
		return this.getObject(bindingOrObject, null);
	}

	/**
	 * Reads appropriate data from RIM and writes it into a temporary file
	 * 
	 * @param bindingExpression - RIM binding expression
	 * @param rimObject - the current RIM Object
	 * @return a file containing data read from RIM or null if object isn't a RimObject instance
	 */
	public Object getObject(Object bindingOrObject, Object rimObject) {
		
		//FIXME PHI 2
		throw new IllegalStateException("TO BE IMPLEMENTED WITH PHI 2");
		
//		try {
//			byte[] temp = null;
//			if (bindingOrObject instanceof String) {
//				Object b = null;
//				String bindingExpression = (String)bindingOrObject;
//				// get data from RIM
//				if (rimObject instanceof RimObject) {
//					b = getObjectValue(bindingExpression, (RimObject)rimObject);//this.getRimValue(bindingExpression, (RimObject)rimObject);
//				} else {
//					b = getObjectValue(bindingExpression, null);
//				}
//				temp = getBytesArray(b);
//			} else {
//				temp = getBytesArray(bindingOrObject);
//			}
//			// temporary file and file writers creation. Filename is the hash code of previous instantiated byte array
//			File file = File.createTempFile(String.valueOf(temp.hashCode()), ".tmp");
//			FileOutputStream fileOS = new FileOutputStream(file);
//			BufferedOutputStream fileBuffer = new BufferedOutputStream(fileOS);
//			// writing file
//			fileBuffer.write(temp);
//			// Closing all
//			fileBuffer.close();
//			fileOS.close();
//
//			return file;
//		}catch (IOException e) {
//			log.error("Error reading data from RIM or writing it into a temporary file!", e);
//		}
//
//		return null;
	}


	private byte[] getBytesArray(Object b) {
		byte[] result;
		// converts RIM data from ByteBuffer to byte array
		if (b instanceof ByteBuffer) {
			result = ((ByteBuffer)b).array();
		} else if (b instanceof ED) {
			result = ((ED)b).getBytes();
		} else {
			// create a new empty 1-lenght byte array if data is null or not instance of ByteBuffer
			result = new byte[0];
		}
		return result;
	}

}