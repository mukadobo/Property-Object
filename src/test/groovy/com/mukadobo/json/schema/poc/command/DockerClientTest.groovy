package com.mukadobo.json.schema.poc.command

import org.apache.log4j.Logger
import spock.lang.Specification
import spock.lang.Ignore
import spock.lang.Unroll

class DockerClientTest extends Specification
{
	static private Logger logger = Logger.getLogger(DockerClientTest.class)
	
	def "Errors ~ #sampleName (UNROLL)"() { expect: true }
	@Ignore @Unroll
	def "Errors ~ #sampleName"()
	{
		def path = "samples/${sampleName}.json"
		
		when:
			
			InputStream    stream  = CommandTest.class.getResourceAsStream(path)
			Command        command = new Command(stream)
			Command.Result result  = command.perform()
		
		then:
			
			result.status == Command.Result.Status.FAILURE
			
			logger.info("result: ${result}")
		
		where:
			
			x | sampleName
			0 | "DockerClient/error-nyi"
			0 | "DockerClient/error-nosup"
	}
	
	
	def "Perform ~ #sampleName (UNROLL)"() { expect: true }
	@Unroll
	def "Perform ~ #sampleName"()
	{
		def path = "samples/${sampleName}.json"
		
		when:
			
			InputStream    stream  = CommandTest.class.getResourceAsStream(path)
			Command        command = new Command(stream)
			
			logger.info("command: ${command}")
			
			Command.Result result  = command.perform()
		
			logger.info("result: ${result}")
		
		then:
			
			result.status == Command.Result.Status.SUCCESS
		
		where:
			
			x | sampleName
			0 | "DockerClient/pull-busybox"
//			0 | "DockerClient/pull-busybox-bbb"
			0 | "DockerClient/images-vanilla"
	}
	
}
