package KCAAgent;

import java.util.Collection;
import java.util.LinkedList;

import agent.AgentID;

public class Action
{
	enum ActionType {
		
		INFORM,

		FREE,

//		REQUEST,
		
	}
	
	ActionType	type;
	Fact		relatedFact	= null;
//	Data		relatedData	= null;
	AgentID		targetAgent	= null;
	
	public Action()
	{
		type = ActionType.FREE;
	}
	
	public Action(Fact fact, AgentID agent)
	{
		type = ActionType.INFORM;
		relatedFact = fact;
		targetAgent = agent;
	}
	
//	public Action(Data data, AgentID agent)
//	{
//		type = ActionType.REQUEST;
//		relatedData = data;
//		targetAgent = agent;
//	}
	
	public Collection<Action> toCollection()
	{
		Collection<Action> ret = new LinkedList<Action>();
		ret.add(this);
		return ret;
	}
	
	@Override
	public String toString()
	{
		String content = "";
		switch(type)
		{
		case FREE:
			content = "FREE";
			break;
		case INFORM:
			content = relatedFact + ">" + targetAgent;
			break;
//		case REQUEST:
//			content = relatedData + ">" + targetAgent;
//			break;
		}
		return type.toString() + "." + content;
	}
}
