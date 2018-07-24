package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import com.mukadobo.json.schema.JsonTo
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.json.JSONTokener

import java.lang.reflect.Method
import java.util.stream.Stream
/**
 * TODO:
 * - Result.getNullable**() is a hack (for easy JsonOutput). Do it right!
 *
 */
@EqualsAndHashCode
class Command extends EntityObject.Base
{
	/**
	 * Payload items that perform an action are not required to implement this interface - they
	 * merely need to have method with the signature. Using the interface, however, helps
	 * keep performers "neat" (and allows IDE support... !-)
	 */
	interface Performer
	{
		Command.Result perform(Map args)
	}
	
	List<String> notes
	Boolean      dryrun
	String       verbosity
	
	Map<String, Object> payload = new LinkedHashMap<>()
	
	Command(JSONObject jsonDom)
	{
		super(jsonDom, Command.class)
		
		notes     = (jsonDom.has("notes") ? jsonDom.optJSONArray("notes").toList() : []) as List<String>
		dryrun    = jsonDom.optBoolean("dryrun", false)
		verbosity = jsonDom.optEnum(Verbosity.class, "verbosity", Verbosity.INFO)
		
		JSONObject payloadDom = jsonDom.getJSONObject("payload")
		
		payloadDom.keySet().each { key ->
			
			Object item = JsonTo.pogoFromDom(payloadDom.get(key))
			
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
				summary: "No payload item with #perform(Map)",
				detail : "No #perform(Map) found for any available payload items: ${payload.keySet()}",
			)
		}
		
		if (performers.size() > 1)
		{
			return Result.failure(
				summary: "Multiple payload items with #perform(Map)",
				detail : "Found ${performers.size()} payload items with #perform(Map): ${performers.keySet()}",
			)
		}
		
		Map.Entry<String, Method> performEntry = performers.entrySet().stream().findFirst().get()
		
		String       performKey    = performEntry.key
		Method       performMethod = performEntry.value
		EntityObject performer     = payload.get(performKey) as EntityObject
		Result       performResult

		try
		{
			Map<String, Object> performerArgs = [:] << payload << [
			    notes     : notes,
				dryrun    : dryrun,
				verbosity : verbosity,
			]
			
			performResult  = performMethod.invoke(performer, performerArgs) as Result
		}
		catch(Throwable e)
		{
			// a bit convoluted, but quiets erroneous IDE warning
			
			Map args = [
				summary : "Subject #perform() failure",
				detail  : "General error: ${e}",
				cause   : e
			]
			
			return new Result(args, Result.Status.FAILURE)
		}
		
		performResult
	}
	
	static class Result
	{
		final Status                  status
		final String                  summary
		final String                  detail
		final Map<String, Object>     logs
		final Optional<Object>        product
		final Optional<Object>        cause
		final List<String>            codePoint
		
		private static final String[] CODE_POINT_CLASS_PREFIX_SKIPS = [
			'com.intellij.junit4.',
			'com.intellij.rt.',
			'java.lang.Thread.',
			'java_lang_Thread$getStackTrace$.',
			'java.lang.reflect.',
			'org.codehaus.groovy.reflection.',
			'org.codehaus.groovy.runtime.',
			'org.junit.runner.',
			'org.junit.runners.',
			'org.spockframework.runtime.',
			'org.spockframework.util.ReflectionUtil.',
			'sun.reflect.',
			'com.mukadobo.json.schema.poc.command.Command$Result'
		]
		
		Result(Map args = [:], Status status)
		{
			StackTraceElement[]       steArray  = Thread.currentThread().getStackTrace()
			Stream<StackTraceElement> steStream = Stream.of(steArray) as Stream<StackTraceElement>
			List<StackTraceElement>   steList   = steStream
				.skip(2)
				.filter {
					if (it.getLineNumber() < 0)
					{
						false
					}
					else
					{
						String steClassName = it.getClassName() + "."
						
						! Stream.of(CODE_POINT_CLASS_PREFIX_SKIPS).filter { steClassName.startsWith(it as String) }.findFirst().isPresent()
					}
				}
				.limit(100)
				.collect()
			
			List<String> steTexts = steList.stream()
				.map {
					"at ${it.getClassName()}.${it.getMethodName()}(${it.getFileName()}:${it.getLineNumber()})"
				}
				.collect()

			this.status    = status
			this.summary   = args.get("summary", this.status)
			this.detail    = args.get("detail" , this.summary)
			this.cause     = Optional.ofNullable(args.get("cause"))
			this.codePoint = steTexts
			this.product   = Optional.ofNullable(args.get("product"))
			this.logs      = args.get("logs")
		}
		
		static enum Status
		{
			SUCCESS,
			FAILURE
		}
		
		static Result failure(String summary)
		{
			new Result(Result.Status.FAILURE, summary: summary)
		}
		
		static Result failure(Map args = [:])
		{
			new Result(args, Result.Status.FAILURE)
		}
		
		static Result success(String summary)
		{
			new Result(Result.Status.SUCCESS, summary: summary)
		}
		
		static Result success(Map args = [:])
		{
			new Result(args, Result.Status.SUCCESS)
		}
		
		static Result nyi(String what)
		{
			new Result(Result.Status.FAILURE, summary : "NYI: $what")
		}
		
		// These getters are so the JsonOutput works. Probably a better way, but tech-debt is a good thing!!!
		
		Object getCause()   { cause  .isPresent() ? cause  .get() : null }
		Object getProduct() { product.isPresent() ? product.get() : null }
		
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}
