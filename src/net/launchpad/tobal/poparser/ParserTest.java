/**
 * @author Balázs Tóth (tobal17@gmail.com)
 * 
 * Modified by Kevin POCHAT for ATCS
 */
package net.launchpad.tobal.poparser;

import java.io.File;

public class ParserTest
{
    public static void main(String args[])
    {
        File file = new File("C:\\file.po");
        POParser parser = new POParser();
        POFile po = parser.parseFile(file);
        po.printHeader();
        po.printFile();
        // is the 3th entry fuzzy?
        boolean fuzzy = po.checkFlag("fuzzy", 3);
        // give me the msgid of the 4th entry
        String[] str = po.getStringsFromEntryByType(4, POEntry.StringType.MSGID);
    }
}
