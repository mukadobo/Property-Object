package com.mukadobo.propertyobject

import spock.lang.Specification

class PropertySchemeTest extends Specification
{
	def "YAML to JSON - all value primitives"()
	{
		expect:
		PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:
		yamlText  | jsonText
		'a: A'    | '{"a" : "A"}'
		'a: 0'    | '{"a" : 0}'
		'a: true' | '{"a" : true}'
		'a: 2.7'  | '{"a" : 2.7}'
		'a: null' | '{"a" : null}'
	}


}
