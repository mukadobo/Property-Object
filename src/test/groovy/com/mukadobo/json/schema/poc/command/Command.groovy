package com.mukadobo.json.schema.poc.command

import com.mukadobo.json.schema.EntityObject
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.json.JSONTokener

import java.lang.reflect.Method

@EqualsAndHashCode
class Command extends EntityObject.Base
{
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
		EntityObject subject   = mySubject()
		EntityObject predicate = myPredicate()
		
		Class  subjectClass
		Method subjectPerform
		Result performResult

		try
		{
			subjectClass   = subject.getClass()
			subjectPerform = subjectClass.getMethod("perform", Predicate.class)
			performResult  = subjectPerform.invoke(subject, predicate) as Result
		}
		catch (NoSuchMethodException e)
		{
			return new Result(
				Result.Status.FAILURE,
				summary : "Subject can't perform action",
				detaul  : "Method not found for subject: ${subjectClass}.perform(Predicate)",
				cause   : e
			)
		}
		catch(Throwable e)
		{
			return new Result(
				Result.Status.FAILURE,
				summary : "Subject can't perform action",
				detaul  : "General error: ${e}",
				cause   : e
			)
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
		
		Throwable getNullableCause  () { cause   ? cause  .get() : null }
		Throwable getNullableProduct() { product ? product.get() : null }
		
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}
