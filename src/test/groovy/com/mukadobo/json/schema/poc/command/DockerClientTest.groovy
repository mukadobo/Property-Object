package com.mukadobo.json.schema.poc.command

import org.apache.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

class DockerClientTest extends Specification
{
	static private Logger logger = Logger.getLogger(CommandPOC.class)

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
			0 | "DockerClient/pull-busybox"
	}
	
}
