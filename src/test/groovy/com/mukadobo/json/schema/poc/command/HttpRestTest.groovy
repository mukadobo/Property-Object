package com.mukadobo.json.schema.poc.command

import org.apache.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * <PRE>
 * TODO:
 * - Setup for local HTTP server response (using a simple in-memory server? 3PLib?)
 * - Unreachable server
 * - Bad port
 * - Alternate port
 * - TLS
 *   - not allowed
 *   - required
 *   - authentication
 *     - TLS required
 *     - header (token)
 *     - qparams
 *       - token
 *       - user/passwd
 *   - Server-cert not required (ala --insecure-skip-tls-cert)
 * - POST
 * - UPDATE
 * - DELETE
 * - HTTP Response data (3PLib?)
 *   - JSON
 *   - YAML
 *   - XML
 *   - text
 *   - binary
 * - perform() Result
 *   -
 * <PRE>
 */
class HttpRestTest extends Specification
{
	static private Logger logger = Logger.getLogger(HttpRestTest.class)
	
	def "Perform ~ #sampleName (UNROLL)"() { expect: true }
	@Unroll
	def "Perform ~ #sampleName"()
	{
		// curl -X GET "https://api-v3.mbta.com/stops/8553?api_key=2f1ac323439541c194f027bad0e59f5c" -H "accept: application/vnd.api+json"
		// curl -X GET "https://api-v3.mbta.com/stops/8553" -H "accept: application/vnd.api+json" -H "x-api-key: 2f1ac323439541c194f027bad0e59f5c"
		
		def path = "samples/${sampleName}.json"
		
		when:
			
			InputStream stream    = CommandTest.class.getResourceAsStream(path)
			Command     command   = new Command(stream)
		
		then:
			
			Command.class.getSimpleName() == command.getKind()
		
		when:
			
			Command.Result result = command.perform()
		
		then:
			
			result.status == Command.Result.Status.SUCCESS
			
			logger.info("result: ${result}")
		
		where:
			
			x | sampleName
			0 | "HttpRest/mbta-list"
	}
	
}
