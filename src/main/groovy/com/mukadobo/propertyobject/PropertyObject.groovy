package com.mukadobo.propertyobject

import org.jboss.dmr.ModelNode
import org.yaml.snakeyaml.Yaml

class PropertyObject
{
	private final ModelNode rootNode

	private PropertyObject(ModelNode rootNode)
	{
		this.rootNode = rootNode
	}

	static PropertyObject fromYaml(String yamlText)
	{
		new PropertyObject(convert(new Yaml().load(yamlText)))
	}

	String toJson()
	{
		rootNode.toJSONString(true)
	}

	private static ModelNode convert(Map<String, Object> map)
	{
		ModelNode nodeObject = new ModelNode()

		nodeObject.get("x")
		nodeObject.remove("x")

		map.each { k, v ->
			nodeObject.get(k).set(convert(v))
		}

		nodeObject
	}

	private static ModelNode convert(List<Object> list)
	{
		ModelNode nodeObject = new ModelNode()

		nodeObject.get(0)
		nodeObject.remove(0)

		list.each { v ->
			nodeObject.add(convert(v))
		}

		nodeObject
	}

	private static Object convert(Object primative)
	{
		primative != null ? primative : new ModelNode()
	}

}
