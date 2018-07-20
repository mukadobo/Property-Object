package com.mukadobo.json.schema

import groovy.json.JsonOutput
import org.apache.commons.io.IOUtils
import org.apache.log4j.Logger
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Specification
import spock.lang.Unroll

class SubEntity extends EntityObject.Base
{
	SubEntity(JSONObject jsonDom)
	{
		super(jsonDom)
	}
}

class EntityObjectTest extends Specification
{
	static private Logger logger = Logger.getLogger(EntityObjectTest.class)
	
	def "validation ~ #qualifier, ok: #isGood (UNROLL)"() { expect: true }
	@Unroll
	def "validation ~ #qualifier, ok: #isGood"()
	{
		setup:
		
			JsonSchema  schema = new JsonSchema(EntityObject.class)
		
		when:
		
			String      path     = "samples/EntityObject+${qualifier}.json"
			InputStream stream   = this.getClass().getResourceAsStream(path)
			String      jsonText = IOUtils.toString(stream, "UTF-8")
			JSONObject  jsonDom  = new JSONObject(new JSONTokener(jsonText))
		
			Exception   oops     = schema.validate(jsonDom, true, 2000, false)
		
		then:
		
			isGood ? (oops == null) : (oops != null)
		
		when:
		
			oops = null
			try
			{
				new SubEntity(jsonDom)
			}
			catch (Exception e)
			{
				oops = e
			}
		
		then:
			
			isGood ? (oops == null) : (oops != null)
		
		where:
			
			isGood | qualifier
			false  | "bad+kind"
			false  | "bad+uuid"
			false  | "bad+version"
			false  | "missing+kind"
			true   | "missing+name"
			true   | "missing+uuid"
			true   | "missing+version"
			true   | "zero"
	}
	
	def "round trip via JSON ~ #qualifier (UNROLL)"() { expect: true }
	@Unroll
	def "round trip via JSON ~ #qualifier"()
	{
		when:
			
			String       path     = "samples/EntityObject+${qualifier}.json"
			InputStream  stream   = this.getClass().getResourceAsStream(path)
			String       jsonText = IOUtils.toString(stream, "UTF-8")
			JSONObject   jsonDom  = new JSONObject(new JSONTokener(jsonText))
		
			EntityObject firstObj = new SubEntity(jsonDom)
		
		then:
			
			EntityObject.class.getCanonicalName() == firstObj.getKind()
		
		when:
			
			String     dupeText = JsonOutput.toJson(firstObj)
			JSONObject dupeDom  = new JSONObject(new JSONTokener(dupeText))
			
			logger.debug("dupeText:\n${JsonOutput.prettyPrint(dupeText)}")
		
		then:
			
			def schema = new JsonSchema(EntityObject.class)
			schema.validate(dupeDom) == null
		
		when:
			
			SubEntity dupeObj = new SubEntity(dupeDom)
		
		then:
			
			firstObj == dupeObj
			JsonOutput.toJson(firstObj) == JsonOutput.toJson(dupeObj)
		
		where:
			
			isGood | qualifier
			true   | "missing+name"
			true   | "missing+uuid"
			true   | "zero"
	}
}
