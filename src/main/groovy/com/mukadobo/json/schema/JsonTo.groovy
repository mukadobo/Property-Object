package com.mukadobo.json.schema

import org.json.JSONArray
import org.json.JSONObject

class JsonTo
{
	static Object pogoFromDom(Object jsonDom)
	{
		switch (jsonDom)
		{
			case JSONObject:   pogoFromDomObject(jsonDom as JSONObject); break
			case JSONArray:    listFromDomArray (jsonDom as JSONArray ); break
			
			default: jsonDom
		}
	}
	
	static Object pogoFromDomObject(JSONObject jsonDomObject)
	{
		if (EntityObject.Base.canTransformFrom(jsonDomObject))
		{
			EntityObject.Base.factory(jsonDomObject)
		}
		else
		{
			Map<String, Object> map = new LinkedHashMap<>()
			
			for (String key : jsonDomObject.keySet())
			{
				Object value = pogoFromDom(jsonDomObject.get(key))
				
				map.put(key, value)
			}
			
			map
		}
	}
	
	static List<Object> listFromDomArray(JSONArray jsonDomArray)
	{
		List<Object> list = new LinkedList<>()
		for (Object raw : (jsonDomArray as JSONArray))
		{
			Object cooked = pogoFromDom(raw)
			
			list.add(cooked)
		}
		
		list
	}
}
