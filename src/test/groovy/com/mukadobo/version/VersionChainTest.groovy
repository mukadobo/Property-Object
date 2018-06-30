package com.mukadobo.version

import com.mukadobo.version.VersionChain
import org.junit.Test;

import static org.junit.Assert.*;

class VersionChainTest
{
    @Test(expected = NullPointerException)
    void constructor_ByString_Null_Exception()
    {
        new VersionChain(null as String)
    }

    @Test(expected = IllegalArgumentException)
    void constructor_ByString_Empty_Exception()
    {
        new VersionChain("")
    }

    @Test(expected = NumberFormatException)
    void constructor_ByString_Blank_Exception()
    {
        new VersionChain(" \t")
    }

    @Test(expected = NullPointerException)
    void constructor_ByList_Null_Exception()
    {
        new VersionChain(null as List<Integer>)
    }

    @Test(expected = IllegalArgumentException)
    void constructor_ByList_Empty_Exception()
    {
        new VersionChain(Collections.EMPTY_LIST)
    }

    @Test(expected = NullPointerException)
    void constructor_ByArray_Null_Exception()
    {
        new VersionChain(null as Integer[])
    }

    @Test(expected = IllegalArgumentException)
    void constructor_ByArray_ZeroLength_Exception()
    {
        new VersionChain(new Integer[0])
    }

    @Test
    void toString_Basic()
    {
        def samples = [
                "0",
                "1",
                "1.0",
                "2.0.1"
        ]

        samples.each {
            VersionChain chain = new VersionChain(it)

            assertEquals(it, chain.toString())
        }
    }

    private boolean assert_comparesAsEqual(String textLeft, String textRight)
    {
        VersionChain chainLeft  = new VersionChain(textLeft)
        VersionChain chainRight = new VersionChain(textRight)

        assertTrue("NOT: <${chainLeft}> == <${chainRight}>", chainLeft == chainRight)
    }

    @Test
    void compareTo_Equals()
    {
        def samples = [
                "0",
                "1",
                "1.0",
                "2.0.1"
        ]

        samples.each { sample ->

            ["", ".0", ".0.0", ".0.0.0"].each { trailing ->
                assert_comparesAsEqual(sample, sample + trailing)
                assert_comparesAsEqual(sample + trailing, sample)
            }
        }
    }

    private boolean assert_comparesAsGreater(String textLeft, String textRight)
    {
        VersionChain chainLeft  = new VersionChain(textLeft)
        VersionChain chainRight = new VersionChain(textRight)

        assertTrue("NOT: <<${chainLeft}>>  >  <<${chainRight}>>", chainLeft > chainRight)
    }

    @Test
    void compareTo_Greater()
    {
        def samples = [
                "0",
                "1",
                "1.0",
                "2.0.1"
        ]

        samples.each { sample ->

            [".1", ".0.2", ".0.3.0"].each { trailing ->
                assert_comparesAsGreater(sample + trailing, sample)
            }
        }
    }

    private boolean assert_comparesAsLessor(String textLeft, String textRight)
    {
        VersionChain chainLeft  = new VersionChain(textLeft)
        VersionChain chainRight = new VersionChain(textRight)

        assertTrue("NOT: <<${chainLeft}>>  <  <<${chainRight}>>", chainLeft < chainRight)
    }

    @Test
    void compareTo_Lessor()
    {
        def samples = [
                "0",
                "1",
                "1.0",
                "2.0.1"
        ]

        samples.each { sample ->

            [".1", ".0.2", ".0.3.0"].each { trailing ->
                assert_comparesAsLessor(sample, sample + trailing)
            }
        }
    }

    @Test
    void length()
    {
        def samples = [
                [0],
                [0, 1],
                [1, 2, 0],
        ]

        samples.each { sample ->

            VersionChain chain = new VersionChain(sample)

            assertEquals("$sample", chain.length(), sample.size())
        }
    }

    @Test
    void at()
    {
        def samples = [
                [0],
                [0, 1],
                [1, 2, 0],
        ]

        samples.each { sample ->

            VersionChain chain = new VersionChain(sample)

            for(idx in (0..<sample.size())) {
                assertEquals("<<${chain}>>.at($idx)", chain.at(idx), sample.get(idx))
            }
        }
    }
}