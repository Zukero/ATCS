/**
 *
 * @author Balázs Tóth (tobal17@gmail.com)
 * 
 * Modified by Kevin POCHAT for ATCS
 */
package net.launchpad.tobal.poparser;

import java.util.Vector;

public class POEntry
{
    private POLine[] Lines;
    
    public enum StringType
    {
        /**translator comments*/
        TRLCMNT,
        /**extracted comments*/
        EXTCMNT,
        /**reference*/
        REFERENCE,
        /**flag*/
        FLAG,
        /**previous context*/
        PREVCTXT,
        /**previous untranslated string singular*/
        PREVUNTRSTRSING,
        /**previous untranslated string plural*/
        PREVUNTRSTRPLUR,
        /**untranslated string singular*/
        MSGID,
        /**translated string*/
        MSGSTR,
        /**context*/
        MSGCTXT,
        /**header line*/
        HEADER

        // TODO: support for plural untranslated strings,
        // and translated string cases
    }

    POEntry()
    {
        Lines = new POLine[0];
    }

    public void addLine(StringType type, String string)
    {
        boolean hasType = false;
        POLine line = null;
        for(int i = 0; i < Lines.length; i++)
        {
            if(Lines[i].getType() == type)
            {
                hasType = true;
                line = Lines[i];
                break;
            }
        }
        if(hasType)
        {
            line.addString(string);
        }
        else
        {
            line = new POLine(type, string);
            POLine[] templines = Lines.clone();
            Lines = new POLine[Lines.length+1];
            for(int i = 0; i < Lines.length-1; i++)
            {
                Lines[i] = templines[i];
            }
            Lines[Lines.length-1] = line;
        }
    }

    public POLine[] getLines()
    {
        return Lines;
    }

    public Vector<String> getStringsFromLine(int index)
    {
        return Lines[index].getStrings();
    }

    public Vector<String> getStringsByType(POEntry.StringType type)
    {
        for(int i = 0; i < Lines.length; i++)
        {
            if(Lines[i].getType() == type)
            {
                return Lines[i].getStrings();
            }
        }
        return null;
    }
}
