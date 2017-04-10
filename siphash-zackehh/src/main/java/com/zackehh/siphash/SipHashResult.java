package com.zackehh.siphash;

import static com.zackehh.siphash.SipHashConstants.DEFAULT_CASE;
import static com.zackehh.siphash.SipHashConstants.DEFAULT_PADDING;

/**
 * A container class of the result of a hash. This class exists
 * to allow the developer to retrieve the result in any format
 * they like. Currently available formats are `long` and ${@link java.lang.String}.
 * When retrieving as a String, the developer can specify the case
 * they want it in, and whether or not we should pad the left side
 * to 16 characters with 0s.
 */
public class SipHashResult {

    /**
     * The internal hash result.
     */
    private final long result;

    /**
     * A package-private constructor, as only
     * SipHash should be creating results.
     *
     * @param result the result of a hash
     */
    SipHashResult(long result){
        this.result = result;
    }

    /**
     * Simply returns the hash result as a long.
     *
     * @return the hash value as a long
     */
    public long get(){
        return result;
    }

    /**
     * Returns the result as a Hex String, using
     * the default padding and casing values.
     *
     * @return the hash value as a Hex String
     */
    public String getHex(){
        return getHex(DEFAULT_PADDING, DEFAULT_CASE);
    }

    /**
     * Returns the result as a Hex String, using
     * a custom padding value and default casing value.
     *
     * @param padding whether or not to pad the string
     * @return the hash value as a Hex String
     */
    public String getHex(boolean padding){
        return getHex(padding, DEFAULT_CASE);
    }

    /**
     * Returns the result as a Hex String, using
     * a default padding value and custom casing value.
     *
     * @param s_case the case to convert the output to
     * @return the hash value as a Hex String
     */
    public String getHex(SipHashCase s_case){
        return getHex(DEFAULT_PADDING, s_case);
    }

    /**
     * Returns the result as a Hex String, taking in
     * various arguments to customize the output further,
     * such as casing and padding.
     *
     * @param padding whether or not to pad the string
     * @param s_case the case to convert the output to
     * @return a Hex String in the custom format
     */
    public String getHex(boolean padding, SipHashCase s_case){
        String str = Long.toHexString(get());
        if (padding) {
            str = leftPad(str, 16, "0");
        }
        if (s_case == SipHashCase.UPPER) {
            str = str.toUpperCase();
        }
        return str;
    }
    
    /**
     * Modified for https://github.com/Zukero/ATCS
     * Replaces the StringUtils.leftPad from apache commons, to remove dependency.
     * 
     * @param str the string to pad
     * @param len the total desired length
     * @param pad the padding string
     * @return str prefixed with enough repetitions of the pad to have a total length matching len
     */
    public String leftPad(String str, int len, String pad) {
    	StringBuilder sb = new StringBuilder(len);
    	
    	int padlen = len - str.length();
    	int partialPadLen = padlen % pad.length();
    	int padCount = padlen / pad.length();
    	
    	while (padCount >= 0) {
    		sb.append(pad);
    		padCount--;
    	}
    	
    	if (partialPadLen > 0) {
    		sb.append(pad.substring(0, partialPadLen));
    	}
    	
    	sb.append(str);
    	
    	return sb.toString();
    }
}
