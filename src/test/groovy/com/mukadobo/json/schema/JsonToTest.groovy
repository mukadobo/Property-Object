package com.mukadobo.json.schema

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import spock.lang.Shared
import spock.lang.Specification

class JsonToTest extends Specification
{
	@Shared
	JSONObject sampleDom = null
	
	static class EntityForTest extends EntityObject.Base
	{
		String thing
		
		EntityForTest(JSONObject jsonDom)
		{
			super(jsonDom)
			
			this.thing = jsonDom.getString("thing")
		}
	}
	
	void setup()
	{
		String sampleText = '''\
		{
			"pogo" : {
				"p.s" : "P.S",
				"p.n" : 1.414,
				"p.m" : { "p.m.s1" : "P.M.S1",   "p.m.s2" : "P.M.S2" }
			},
			
			"entity" : {
				"kind"  : "com.mukadobo.json.schema.JsonToTest$EntityForTest",
				"thing" : "THINGY"
			},
			
			"array" : [
				"BBB",
				3.141,
				{ "s.a1" : "S.A1" }
			],
			
			"string"  : "STRING",
			"number"  : 2.718,
			"boolean" : true,
			"null"    : null
		}
		'''.stripIndent()
		
		sampleDom = new JSONObject(new JSONTokener(sampleText))
	}
	
	def "PogoFromDom"()
	{
		when:
		
			Object domItem = sampleDom[key]
		
			Object result = JsonTo.pogoFromDom(domItem)
		
			
		then:
			
			result.toString() == text
		
		where:
			
			key       | text
			"string"  | "STRING"
			"number"  | "2.718"
			"boolean" | "true"
			"null"    | "null"
	}
	
	def "PogoFromDomObject"()
	{
		when:
			
			JSONObject domItem = sampleDom[key] as JSONObject
			
			Object result = JsonTo.pogoFromDom(domItem)
		
		then:
			
			refClass.isAssignableFrom(result.getClass())
			result.toString() == text
		
		where:
			
			key       | refClass            | text
			"pogo"    | Map.class           | '[p.n:1.414, p.s:P.S, p.m:[p.m.s1:P.M.S1, p.m.s2:P.M.S2]]'
			"entity"  | EntityForTest.class | '{"kind":"com.mukadobo.json.schema.JsonToTest$EntityForTest","version":"0.0.0","thing":"THINGY","uuid":"00000000-0000-0000-0000-000000000000","name":null}'
	}
	
	def "ListFromDomArray"()
	{
		when:
			
			JSONArray domItem = sampleDom[key] as JSONArray
			
			List<Object> result = JsonTo.listFromDomArray(domItem)
		
		
		then:
			
			result.toString() == text
		
		where:
			
			key       | text
			"array"   | "[BBB, 3.141, [s.a1:S.A1]]"
	}
}
