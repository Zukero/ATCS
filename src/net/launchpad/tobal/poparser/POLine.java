/**
 *
 * @author Balázs Tóth (tobal17@gmail.com)
 * 
 * Modified by Kevin POCHAT for ATCS
 */
package net.launchpad.tobal.poparser;

import java.util.Vector;

public class POLine
{
    private POEntry.StringType type;
    private Vector<String> strings;

    POLine(POEntry.StringType type, String string)
    {
        this.type = type;
        this.strings = new Vector<String>();
        this.strings.add(string);
    }

    public void addString(String string)
    {
        strings.add(string);
    }

    public Vector<String> getStrings()
    {
        return strings;
    }

    public POEntry.StringType getType()
    {
        return type;
    }

    public int getVectorSize()
    {
        return strings.size();
    }
}