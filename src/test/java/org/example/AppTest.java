package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.example.util.CompressionService;

import java.util.Base64;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        CompressionService compressionService = new CompressionService();
        var payload = "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";
        byte[] decodedPayload = Base64.getDecoder().decode(payload);
        String uncompressedPayload = compressionService.decompressPayload(decodedPayload);

    }
}
