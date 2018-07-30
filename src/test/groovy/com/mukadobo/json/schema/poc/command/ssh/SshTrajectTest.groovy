package com.mukadobo.json.schema.poc.command.ssh

import com.mukadobo.json.schema.poc.command.Command
import com.mukadobo.json.schema.poc.command.CommandTest
import org.apache.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

class SshTrajectTest extends Specification
{
	static private Logger logger = Logger.getLogger(SshTrajectTest.class)
	
	def "Errors ~ #sampleName (UNROLL)"() { expect: true }
	@Unroll
	def "Errors ~ #sampleName"()
	{
		def path = "samples/${sampleName}.json"
		
		when:
			
			InputStream    stream  = CommandTest.class.getResourceAsStream(path)
		
		then:
			
			Command        command = new Command(stream)
			Command.Result result  = command.perform()
		
			result.status == Command.Result.Status.FAILURE
			
			logger.info("result: ${result}")
		
		where:
			
			x | sampleName
			0 | "SshTraject/error-no-action"
			0 | "SshTraject/error-no-host"
			0 | "SshTraject/error-no-credential"
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
			0 | "SshTraject/default-port"
			0 | "SshTraject/debbox-uname"
	}
	
}
