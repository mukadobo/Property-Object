package com.mukadobo.version

import groovy.json.JsonOutput
import org.junit.Test
import spock.lang.Specification

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class VersionChainTestSpock extends Specification
{
	def "JsonOutput generated"()
	{
		when:
			
			Set<String> expect = new LinkedHashSet<>()
			Set<String> result = new LinkedHashSet<>()
			
			expect.add("\"$canonical\"".toString())
			
			variants.each {
				
				VersionChain  chain  = new VersionChain(it)
				String        json   = JsonOutput.toJson(chain)
				
				result.add(json)
			}
			
		then:
			
			expect == result
		
		where:
			
			canonical      | variants
			"0.0.0"        | [ " 0", "0 ", " 0 ", " 000", "0.0", "0.0.0", "0 . 0 . 00"]
			"3.0.0"        | [ " 3", "3 ", " 3 ", " 003", "3.0", "3.0.0", "3 . 0 . 00"]
			"0.14.0"       | [ "0.14", " 00.  0014", "0.14.0" ]
			"3.14.0"       | [ "3.14", " 03.  0014", "3.14.0" ]
			"9.87.6"       | [ "9.87.6", "00009.  087  .  6", "9.87.6.0.00.000.0000" ]
			"1.0.3.0.0.6"  | [ "1.0.3.0.0.6" ]
	}
}

class VersionChainTestJunit
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
	
			int expectedSize = sample.size()
	
			while ((expectedSize > 1) && (sample[expectedSize-1] == 0)) { --expectedSize }
	
			assertEquals("<<${chain}>>.size()", expectedSize, chain.size())
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

			int expectedSize = sample.size()
			
			while ((expectedSize > 1) && (sample[expectedSize-1] == 0)) { --expectedSize }
			
			assertEquals("<<${chain}>>.size()", expectedSize, chain.size())
			
            for(idx in (0 ..< expectedSize)) {
                assertEquals("<<${chain}>>.at($idx)", chain.at(idx), sample.get(idx))
            }
        }
    }
}