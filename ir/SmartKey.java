public class SmartKey implements java.lang.Comparator
{

	private String token;
	private int priority;

	public SmartKey(String token, int priority)
	{
		this.priority = priority;
		this.token = token;
	}


	/* Return >0 iif */
	public int compareTo(Object o)
	{
		if( !(o instanceof SmartKey))
			throw new ClassCastException("Impossible to compare");
		SmartKey skey = (SmartKey) o;
		return priority - skey.priority;
	}
}