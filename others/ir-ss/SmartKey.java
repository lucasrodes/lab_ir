package ir;

import java.lang.Comparable;
public class SmartKey implements Comparable<Object>
{

	private String token;
	private int priority;

	public SmartKey(String token, int priority)
	{
		this.priority = priority;
		this.token = token;
	}

	public String getToken(){
		return this.token;
	}


	/* Return >0 iif */
	@Override
	public int compareTo(Object o)
	{
		if( !(o instanceof SmartKey))
			throw new ClassCastException("Impossible to compare");
		SmartKey skey = (SmartKey) o;
		return priority - skey.priority;
	}

	@Override
	public boolean equals(Object o)
	{
		if( !(o instanceof SmartKey))
			throw new ClassCastException("Impossible to compare");
		SmartKey skey = (SmartKey) o;
		return skey.token.equals(this.token);
	}
}