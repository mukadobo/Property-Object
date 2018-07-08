package com.mukadobo.json.schema.poc.command

import com.mukadobo.propertyobject.KindAndVersion
import groovy.json.JsonOutput
import groovy.transform.EqualsAndHashCode
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import org.json.JSONTokener

import java.lang.reflect.Method

@EqualsAndHashCode
class Command extends KindAndVersion.Base
{
	Boolean dryrun
	String  verbosity
	
	Map<String, KindAndVersion.Base> payload = new LinkedHashMap<>()
	
	Command(JSONObject jsonDom)
	{
		super(jsonDom)
		
		dryrun    = jsonDom.getBoolean("dryrun")
		verbosity = jsonDom.getEnum(Verbosity.class, "verbosity")
		
		JSONObject payloadDom = jsonDom.getJSONObject("payload")
		
		payloadDom.keySet().each {
			switch (it)
			{
				case "subject"  : this.mySubject  (new Subject  (payloadDom.getJSONObject(it))); break
				case "predicate": this.myPredicate(new Predicate(payloadDom.getJSONObject(it))); break
				
				default: throw new RuntimeException("unsupported payload key: '$it'")
			}
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
	
	private Subject   mySubject  () { payload.get("subject"  ) as Subject   }
	private Predicate myPredicate() { payload.get("predicate") as Predicate }
	
	private void mySubject  (Subject   subject  ) { payload.put("subject"  , subject  ) }
	private void myPredicate(Predicate predicate) { payload.put("predicate", predicate) }
	
	Result perform()
	{
		Subject   subject   = mySubject()
		Predicate predicate = myPredicate()
		
		String subjectSpecies = null
		Class  subjectClass   = null
		Method subjectPerform = null
		Result performResult  = null

		try
		{
			subjectSpecies = subject.getSpecies()
			subjectClass   = Class.forName(subjectSpecies)
			subjectPerform = subjectClass.getMethod("perform", Predicate.class)
			performResult  = subjectPerform.invoke(subject, predicate) as Result
			
		}
		catch (ClassNotFoundException e)
		{
			return new Result(
				Result.Code.FAILURE,
				"Subject can't perform action",
				"Subject species class not found: ${subjectSpecies}",
				e
			)
		}
		catch (NoSuchMethodException e)
		{
			return new Result(
				Result.Code.FAILURE,
				"Subject can't perform action",
				"Method not found for subject species: ${subjectSpecies}.perform(Predicate)",
				e
			)
		}
		catch(Throwable e)
		{
			return new Result(
				Result.Code.FAILURE,
				"Subject can't perform action",
				"General error: ${e}",
				e
			)
		}
		
		new Result (Result.Code.SUCCESS, "SUMMARY", "DETAIL", null)
	}
	
	static class Result
	{
		final Code      code
		final String    summary
		final String    detail
		final Throwable cause
		
		Result(Code code, String summary, String detail, Throwable cause)
		{
			this.code    = code
			this.summary = summary
			this.detail  = detail
			this.cause   = cause
		}
		
		static enum Code
		{
			SUCCESS,
			FAILURE
		}
		
		String toString()
		{
			JsonOutput.toJson(this)
		}
	}
}
