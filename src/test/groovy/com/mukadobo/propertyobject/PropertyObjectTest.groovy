package com.mukadobo.propertyobject

import spock.lang.Specification

class PropertyObjectTest extends Specification
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

	def "YAML to JSON - Simple Lists"()
	{
		expect:
		PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:
		yamlText     | jsonText
		'[]'         | '[]'
		'["a"]'      | '["a"]'
		'[1]'        | '[1]'
		'[null]'     | '[null]'
		'[k: null]'  | '[{"k" : null}]'
		'[0,1,2,3]'  | '[0,1,2,3]'
	}

	def "YAML to JSON - Nested Lists"()
	{
		expect:
		PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:
		yamlText         | jsonText
		'[[]]'           | '[[]]'
		'[[0]]'          | '[[0]]'
		'[[0,1]]'        | '[[0,1]]'
		'[[0,1],[],[2]]' | '[[0,1],[],[2]]'
	}

	def "YAML to JSON - Simple Maps"()
	{
		expect:
		PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:
		yamlText     | jsonText
		'{}'         | '{}'
		'{a}'        | '{"a" : null}'
		'{a: }'      | '{"a" : null}'
		'{a: null}'  | '{"a" : null}'
		'{a: "A"}'   | '{"a" : "A"}'
		'{a: 0}'     | '{"a" : 0}'
		'{a: 2.7}'   | '{"a" : 2.7}'
	}

	def "YAML to JSON - Nested Maps"()
	{
		expect:
		PropertyObject.fromYaml(yamlText).toJson() == jsonText

		where:
		yamlText                       | jsonText
		'{a0: {}}'                     | '{"a0" : {}}'
		'{a0: {}, b0:{}}'              | '{"a0" : {}, "b0" : {}}'
		'{a0: {a1: A1}, b0:{b1: B1}}'  | '{"a0" : {"a1" : "A1"}, "b0" : {"b1" : "B1"}}'
	}

}