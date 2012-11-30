package com.morty.barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 * This is a simple wrapper for Barcode4j
 * 
 * Libraries required are currently
 * barcode4j
 * avalon framework
 * 
 * TODO: Package up in jar
 * TODO: Build Scripts
 * TODO: Allow spring config in beans
 * TODO: Logging
 * TODO: Testing (really? - barcode4j does some awesome stuff already!)
 * 
 * @author amorton
 */
public class SimpleBarcodeGenerator 
{
    
    /*
     * This takes 2 parameters
     * 1) The directory to place all generated barcodes (whatever format)
     * 2) The barcode number
     * 
     * At present we deal only with singleshot EAN13 for ISBN Number generation
     * We might at some stage do something else.
     * 
     * Really this is just a simpler/more refined version of the Barcode4J cli stuff.
     * 
     */
    public static void main(String[] args)
    {
        try
        {
            checkargs(args);
            
            System.out.println("Generating barcode ["+args[1]+"] in directory ["+args[0]+"]");
            
            //generate
            BarcodeUtil util = BarcodeUtil.getInstance();
            Configuration cfg = getConfiguration();
            BarcodeGenerator gen = util.createBarcodeGenerator(cfg);
            
            //Default resolution and orientation
            int dpi = 300;
            int orientation = 0;
            BitmapCanvasProvider bitmap;
            
            File outFile = new File(args[0]+File.separator+args[1]+".jpg");
            OutputStream out = new java.io.FileOutputStream(outFile);
            String format = "image/jpeg";
            
            bitmap = new BitmapCanvasProvider(out,
                        format, dpi, BufferedImage.TYPE_BYTE_GRAY, true, orientation);
                
            gen.generateBarcode(bitmap, args[1]);
            bitmap.finish();
            
            System.out.println("Barcode Generated");

        
        } 
        catch (Exception ex)
        {
            System.out.println("Something bad happened ["+ex.getMessage()+"]");
        }
        
    }

    private static Configuration getConfiguration()
    {
        DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        DefaultConfiguration child = new DefaultConfiguration("ean13");
        cfg.addChild(child);
        return cfg;
    }
    
    
    private static void printUsage()
    {
        System.out.println("Usage:\n java -jar barcodeGenerator.jar <directory> <barcodeNumber>");
    }
    
    private static void printUsageError(String error)
    {
        System.out.println(error);
        printUsage();
    }

    private static void checkargs(String[] args)
    {
        if(args.length != 2)
        {
            printUsageError("Invalid number of arguments");
            System.exit(-1);
        }
        
        
        //Check to see if the 1st parameter is a directory
        File dir = new File(args[0]);
        if(!dir.exists())
        {
            printUsageError("Directory does not exist.");
            System.exit(-1);
        }
        
        try
        {
            //Check and see if the 2nd parameter is a number
            Long number = Long.parseLong(args[1]);
        }
        catch(Exception e)
        {
            printUsageError("Barcode must be a number ["+e.getMessage()+"]");
            System.exit(-1);
        }
    }

}
