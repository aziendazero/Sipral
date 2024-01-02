/**
 * 
 */
package com.phi.cs.util;

import org.apache.commons.lang.RandomStringUtils;

/**
 * 
 * This class provides functionalities for random password generation
 * 
 * @author Riccardo Dalla Valle
 *
 */
public class RandomPasswordGenerator {


	/**
	 * Generate a random alphanumeric password within given length
	 * 
	 * @param length - the required length of the password
	 * @return random generated password
	 */
	public static String generateRandomPassword(int length){
		String password = RandomStringUtils.randomAlphanumeric(length);
		return password;
	}

	/**
	 * Generate a random alphabetic + numeric password 
	 * 
	 * @param alphabeticChars - number of alphabetic chars
	 * @param numericChars - number of numeric chars
	 * @return random generated password
	 */
	public static String generateRandomPassword(int alphabeticChars, int numericChars){
		String password = RandomStringUtils.randomAlphabetic(alphabeticChars) + RandomStringUtils.randomNumeric(numericChars);
		return password;
	}
}
