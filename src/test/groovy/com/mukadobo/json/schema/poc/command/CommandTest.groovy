package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.JsonSchema
import com.mukadobo.propertyobject.KindAndVersion.Base
import groovy.json.JsonOutput
import org.apache.commons.io.IOUtils
import org.apache.log4j.Logger
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification
import spock.lang.Unroll

class CommandTest extends Specification
{
	static private Logger  logger = Logger.getLogger(CommandTest.class)
	
	static private Map<String, String>     sampleText    = new LinkedHashMap<>()
	static private Map<String, JSONObject> sampleJsonDom = new LinkedHashMap<>()
	
	static String getSampleText(String name)
	{
		if (sampleText.get(name) == null) {
			String path = "samples/$name"
			
			InputStream stream = CommandTest.class.getResourceAsStream(path)
			if (!stream) {
				throw new RuntimeException("can't open resource-stream for: $path")
			}
			
			stream.withCloseable {
				String text = IOUtils.toString(it, "UTF-8")
				
				sampleText.put(name, text)
			}
		}
		
		sampleText.get(name)
	}
	
	static JSONObject getSampleJsonDom(String name)
	{
		if (sampleJsonDom.get(name) == null) {
			String text = getSampleText(name)
			JSONObject jsonDom = new JSONObject(new JSONTokener(text))
			
			sampleJsonDom.put(name, jsonDom)
		}
		
		sampleJsonDom.get(name)
	}
	
	def "JSON validation ~ #pogoClass.getSimpleName() [UNROLL]"() { expect: true }
	
	@Unroll
	def "JSON validation ~ #pogoClass.getSimpleName()"()
	{
		expect:
			
			def schema = new JsonSchema(pogoClass)
			schema.validate(jsonText) == null
		
		where:
			
			pogoClass       | jsonText
			Subject.class   | getSampleJsonDom("simple/subject.json")
			Predicate.class | getSampleJsonDom("simple/predicate.json")
			Command.class   | getSampleJsonDom("simple/command.json")
	}
	
	def "POGO from JSON ~ Subject"()
	{
		when:
			
			Subject probject = new Subject(getSampleJsonDom("simple/subject.json"))
		
		then:
			
			probject.kind == "Subject"
	}
	
	def "POGO from JSON ~ Predicate"()
	{
		when:
			
			Predicate probject = new Predicate(getSampleJsonDom("simple/predicate.json"))
		
		then:
			
			probject.kind == "Predicate"
	}
	
	def "POGO from JSON ~ Command"()
	{
		when:
			
			Command probject = new Command(getSampleJsonDom("simple/command.json"))
		
		then:
			
			probject.kind == "Command"
	}
	
	def "JSON round-trip ~ #refClass.getSimpleName() [UNROLL]"() { expect: true }
	
	@Unroll
	def "JSON round-trip ~ #refClass.getSimpleName()"()
	{
		when:
			
			JSONObject firstDom = new JSONObject(new JSONTokener(refText))
			Base firstObj = refClass.newInstance(firstDom)
		
		then:
			
			refClass.getSimpleName() == firstObj.getKind()
		
		when:
			
			String dupeText = JsonOutput.toJson(firstObj)
			JSONObject dupeDom = new JSONObject(new JSONTokener(dupeText))
		
			logger.debug("dupeText:\n${JsonOutput.prettyPrint(dupeText)}")
		
		then:
			
			def schema = new JsonSchema(refClass)
			schema.validate(dupeDom) == null
		
		when:
			
			Base dupeObj = refClass.newInstance(dupeDom)
		
		then:
			
			firstObj == dupeObj
			JsonOutput.toJson(firstObj) == JsonOutput.toJson(dupeObj)
		
		where:
			
			refClass        | refText
			Predicate.class | getSampleText('simple/predicate.json')
			Subject.class   | getSampleText('simple/subject.json')
			Command.class   | getSampleText('simple/command.json')
	}
	
	def "Command ~ constructors"()
	{
		when:
			
			Command fromText = new Command(getSampleText   ('simple/command.json'))
			Command fromDom  = new Command(getSampleJsonDom('simple/command.json'))
		
		then:
			
			fromText == fromDom
		
		when:
			
			InputStream stream     = CommandTest.class.getResourceAsStream('samples/simple/command.json')
			Command     fromStream = new Command(stream)
		
		then:
			
			fromText == fromStream
	}
}
