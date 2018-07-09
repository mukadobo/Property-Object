package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.json.JSONTokener

import java.lang.reflect.Method

/**
 * TODO:
 * - Result.getNullable**() is a hack (for easy JsonOutput). Do it right!
 *
 */
@EqualsAndHashCode
class Command extends EntityObject.Base
{
	/**
	 * Payload items that perform action are not required to implement this interface - they
	 * merely need to have method with the signature. Using the interface, however, helps
	 * keep performers "neat" (and allows IDE support... :)
	 */
	interface Performer
	{
		Command.Result perform(Map args)
	}
	
	Boolean dryrun
	String  verbosity
	
	Map<String, EntityObject> payload = new LinkedHashMap<>()
	
	Command(JSONObject jsonDom)
	{
		super(jsonDom)
		
		dryrun    = jsonDom.getBoolean("dryrun")
		verbosity = jsonDom.getEnum(Verbosity.class, "verbosity")
		
		JSONObject payloadDom = jsonDom.getJSONObject("payload")
		
		payloadDom.keySet().each { key ->
			
			EntityObject item = EntityObject.Base.factory(payloadDom.getJSONObject(key))
			
			payload.put(key, item)
		}
	}
	
	Command(String jsonText)
	{
		this(new JSONObject(new JSONTokener(jsonText)))
	}
	
	
	Command(InputStream stream)
	{
		this(IOUtils.toString(stream, "UTF-8"))
	}
	
	private EntityObject mySubject  () { payload.get("subject"  ) as EntityObject }
	private EntityObject myPredicate() { payload.get("predicate") as EntityObject }
	
	private void mySubject  (EntityObject subject  ) { payload.put("subject"  , subject  ) }
	private void myPredicate(EntityObject predicate) { payload.put("predicate", predicate) }
	
	Result perform()
	{
		Map<String, Method> performers = payload.collectEntries { k, v ->
			try
			{
				[(k) : v.getClass().getMethod("perform", Map.class)]
			}
			catch (NoSuchMethodException ignored)
			{
				[:]
			}
		} as Map<String, Method>
		
		if (performers.isEmpty())
		{
			return Result.failure(
				summary: "Command payload has nothing with #.perform(Map)",
				detail : "No #.perform(Map) found for any payload items: ${payload.keySet()}",
			)
		}
		
		if (performers.size() > 1)
		{
			return Result.failure(
				summary: "Command payload has multiple items with #.perform(Map)",
				detail : "Found ${performers.size()} payload items with #.perform(Map): ${performers.keySet()}",
				)
		}
		
		Map<String, Object> performArgs = ([:] << payload).findAll {
			! performers.get(it.key)
		}
		
		Map.Entry<String, Method> performEntry = performers.entrySet().stream().findFirst().get()
		
		String       performKey    = performEntry.key
		Method       performMethod = performEntry.value
		EntityObject performer     = payload.get(performKey)
		Result       performResult

		try
		{
			performResult  = performMethod.invoke(performer, performArgs) as Result
		}
		catch(Throwable e)
		{
			// a bit convoluted, but quiets erroneous IDE warning
			
			Map args = [
				summary : "Subject can't perform action",
				detail  : "General error: ${e}",
				cause   : e
			]
			
			return new Result(args, Result.Status.FAILURE)
		}
		
		performResult
	}
	
	static class Result
	{
		final Status status
		final String summary
		final String detail
		
		final Optional<Throwable> cause
		final Optional<Object>    product
		
		Result(Map args = [:], Status status)
		{
			this.status  = status
			this.summary = args.get("summary", this.status)
			this.detail  = args.get("detail" , this.detail)
			this.cause   = Optional.ofNullable(args.get("cause", (Throwable) null))
			this.product = Optional.ofNullable(args.get("product"))
		}
		
		static enum Status
		{
			SUCCESS,
			FAILURE
		}
		
		static Result failure(Map args = [:])
		{
			new Result(args, Result.Status.FAILURE)
		}
		
		static Result success(Map args = [:])
		{
			new Result(args, Result.Status.SUCCESS)
		}
		
		// These "getNullable**()" are so the JsonOutput works. Probably a better way, but tech-debt is a good thing!!!
		
		Throwable getNullableCause  () {
			cause  .isPresent() ?
				cause  .get() :
				null }
		Object    getNullableProduct() {
			product.isPresent() ?
				product.get() :
				null }
		
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}
