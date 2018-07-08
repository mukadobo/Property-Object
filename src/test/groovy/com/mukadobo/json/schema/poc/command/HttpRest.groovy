package com.mukadobo.json.schema.poc.command

import org.json.JSONObject

class HttpRest
{
	HttpRest(JSONObject jsonDom)
	{
	
	}

	Command.Result perform(Predicate predicate)
	{
		String summary = "${this.getClass().getSimpleName()}.perform(): NYI"
		
		new Command.Result(Command.Result.Code.SUCCESS, summary, summary, null)
	}
	
}
